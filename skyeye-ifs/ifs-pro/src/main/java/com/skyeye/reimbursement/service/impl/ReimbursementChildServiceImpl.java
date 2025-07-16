/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.reimbursement.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeLinkDataServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.feeapplication.entity.FeeApplication;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.reimbursement.dao.ReimbursementChildDao;
import com.skyeye.reimbursement.entity.Reimbursement;
import com.skyeye.reimbursement.entity.ReimbursementChild;
import com.skyeye.reimbursement.service.ReimbursementChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ReimbursementChildServiceImpl
 * @Description: 报销订单子内容服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/4 16:23
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ReimbursementChildServiceImpl extends SkyeyeLinkDataServiceImpl<ReimbursementChildDao, ReimbursementChild> implements ReimbursementChildService {

    @Autowired
    private IDepmentService iDepmentService;

    @Override
    public String calcOrderAllTotalPrice(List<ReimbursementChild> orderItemList) {
        String totalPrice = "0";
        for (ReimbursementChild orderItem : orderItemList) {
            // 计算子单据总价：单价相加
            totalPrice = CalculationUtil.add(totalPrice, orderItem.getPrice());
        }
        return totalPrice;
    }

    @Override
    @IgnoreTenant
    public List<Map<String, Object>> queryReimbursementAnalysis(String startPeriod, String endPeriod) {
        MPJLambdaWrapper<ReimbursementChild> wrapper = JoinWrappers.lambda("t",ReimbursementChild.class)
                .innerJoin(Reimbursement.class, "i", Reimbursement::getId, ReimbursementChild::getParentId)
                .selectAs(Reimbursement::getDepartmentId,ReimbursementChild::getDepartmentId)
                .selectAll(ReimbursementChild.class);
        wrapper.apply("date_format(" + MybatisPlusUtil.toColumns(ReimbursementChild::getOccurTime) + ", '%Y-%m') >= {0}", startPeriod);
        wrapper.apply("date_format(" + MybatisPlusUtil.toColumns(ReimbursementChild::getOccurTime) + ", '%Y-%m') <= {0}", endPeriod);

        if(tenantEnable){
            String tenantId = TenantContext.getTenantId();
            wrapper.eq("t." + CommonConstants.TENANT_ID_FIELD, tenantId);
            wrapper.eq("i." + CommonConstants.TENANT_ID_FIELD, tenantId);
        }
        List<ReimbursementChild> bean = skyeyeBaseMapper.selectJoinList(ReimbursementChild.class,wrapper);
        List<Map<String, Object>> result = new ArrayList<>();
        if(CollectionUtil.isEmpty(bean)){
            return result;
        }
        iSysDictDataService.setDataMation(bean, ReimbursementChild::getReimburseProId);
        //根据部门id分组
        Map<String, List<ReimbursementChild>> departMap = bean.stream().collect(Collectors.groupingBy(ReimbursementChild::getDepartmentId));
        for (Map.Entry<String, List<ReimbursementChild>> entry : departMap.entrySet()) {
            //根据报销项目id分组
            Map<String, List<ReimbursementChild>> map = entry.getValue().stream().collect(Collectors.groupingBy(ReimbursementChild::getReimburseProId));
            Map<String,Object> tempMap = new HashMap<>();
            List<Map<String, Object>> temp = new ArrayList<>();
            for (Map.Entry<String, List<ReimbursementChild>> childEntry : map.entrySet()) {
                String reimburseProName = childEntry.getValue().get(CommonNumConstants.NUM_ZERO).getReimburseProMation().get("dictName").toString();
                String price = "0";
                for (ReimbursementChild reimbursementChild : childEntry.getValue()) {
                    price = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                            StrUtil.isEmpty(reimbursementChild.getPrice()) ? "0" : reimbursementChild.getPrice(),
                            price);
                }
                Map<String, Object> deptInfo = new HashMap<>();
                deptInfo.put("name", reimburseProName);
                deptInfo.put("price", price);
                temp.add(deptInfo);
            }
            tempMap.put("departmentId", entry.getKey());
            tempMap.put("childList", temp);
            result.add(tempMap);
        }
        iDepmentService.setMationForMap(result,"departmentId","departmentMation");
        return result;
    }

}
