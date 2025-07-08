/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.reimbursement.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.base.business.service.impl.SkyeyeLinkDataServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.reimbursement.dao.ReimbursementChildDao;
import com.skyeye.reimbursement.entity.ReimbursementChild;
import com.skyeye.reimbursement.service.ReimbursementChildService;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    public void queryReimbursementAnalysis(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String year = params.get("year").toString();
        String month = (String) params.get("month");
        if (StrUtil.isNotEmpty(month)){
            int yearInt = Integer.parseInt(year);
            int monthInt = Integer.parseInt(month);
            String startPeriod=year + StrUtil.DASHED + month;
            String endPeriod = year + StrUtil.DASHED + month;
            if (monthInt == CommonNumConstants.NUM_ONE) {
                // 如果是1月，则上期是去年12月
                startPeriod = (yearInt - CommonNumConstants.NUM_ONE) + StrUtil.DASHED + CommonNumConstants.NUM_TWELVE;
                endPeriod = (yearInt - CommonNumConstants.NUM_ONE) + StrUtil.DASHED + CommonNumConstants.NUM_TWELVE;
            }
            List<Map<String, Object>> result = getBeans(startPeriod, endPeriod);
            outputObject.setBeans(result);
        }else {
            String startPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_ZERO + CommonNumConstants.NUM_ONE; // 本期开始时间
            String endPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_TWELVE;  // 本期结束时间
            List<Map<String, Object>> result = getBeans(startPeriod, endPeriod);
            outputObject.setBeans(result);
        }
    }

    private  List<Map<String, Object>> getBeans(String startPeriod, String endPeriod) {
        QueryWrapper<ReimbursementChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply("date_format(" + MybatisPlusUtil.toColumns(ReimbursementChild::getOccurTime) + ", '%Y-%m') >= {0}", startPeriod)
                .apply("date_format(" + MybatisPlusUtil.toColumns(ReimbursementChild::getOccurTime) + ", '%Y-%m') <= {0}", endPeriod);
        List<ReimbursementChild> bean = list(queryWrapper);
        List<Map<String, Object>> result = new ArrayList<>();
        if(CollectionUtil.isEmpty(bean)){
            return result;
        }
        iSysDictDataService.setDataMation(bean, ReimbursementChild::getReimburseProId);
        // 按reimburseProId分组
        Map<String, List<ReimbursementChild>> map = bean.stream().collect(Collectors.groupingBy(ReimbursementChild::getReimburseProId));
        //求占比
        for (Map.Entry<String, List<ReimbursementChild>> entry : map.entrySet()) {
            String reimburseProName = entry.getValue().get(CommonNumConstants.NUM_ZERO).getReimburseProMation().get("dictName").toString();
            BigDecimal percent = new BigDecimal(entry.getValue().size()).divide(new BigDecimal(bean.size()), 2, RoundingMode.HALF_UP);
            Map<String, Object> deptInfo = new HashMap<>();
            deptInfo.put("name", reimburseProName);
            deptInfo.put("pie", percent.multiply(new BigDecimal(100)) + "%");
            result.add(deptInfo);
        }
        return result;
    }

}
