/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.loan.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeFlowableServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.loan.classenum.LoanBorrowTypeEnum;
import com.skyeye.loan.classenum.LoanPaidStateEnum;
import com.skyeye.loan.dao.LoanBorrowDao;
import com.skyeye.loan.entity.LoanBorrow;
import com.skyeye.loan.service.LoanBorrowService;
import com.skyeye.loan.service.UserLoanService;
import com.skyeye.organization.service.IDepmentService;
import com.xingyuv.http.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: LoanBorrowServiceImpl
 * @Description: 借款单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/5 14:17
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "借款单", groupName = "借款单", flowable = true)
public class LoanBorrowServiceImpl extends SkyeyeFlowableServiceImpl<LoanBorrowDao, LoanBorrow> implements LoanBorrowService {

    @Autowired
    private UserLoanService userLoanService;

    @Autowired
    private IDepmentService iDepmentService;

    @Override
    public void validatorEntity(LoanBorrow entity) {
        super.validatorEntity(entity);
        if (entity.getBorrowType() == LoanBorrowTypeEnum.DEPARTMENT.getKey() && StringUtil.isEmpty(entity.getDepartmentId())) {
            throw new CustomException("请选择借款部门");
        }
    }

    @Override
    public QueryWrapper<LoanBorrow> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<LoanBorrow> queryWrapper = super.getQueryWrapper(commonPageInfo);
        // 我创建的
        queryWrapper.eq(MybatisPlusUtil.toColumns(LoanBorrow::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iDepmentService.setMationForMap(beans, "departmentId", "departmentMation");
        iAuthUserService.setMationForMap(beans, "applicantId", "applicantMation");
        return beans;
    }

    @Override
    public LoanBorrow selectById(String id) {
        LoanBorrow loanBorrow = super.selectById(id);
        iSysDictDataService.setDataMation(loanBorrow, LoanBorrow::getPayTypeId);
        iDepmentService.setDataMation(loanBorrow, LoanBorrow::getDepartmentId);
        iAuthUserService.setDataMation(loanBorrow, LoanBorrow::getApplicantId);
        return loanBorrow;
    }

    @Override
    public void approvalEndIsSuccess(LoanBorrow entity) {
        userLoanService.calcUserLoanPrice(entity.getCreateId(), entity.getPrice(), true);
    }

    @Override
    public void updateLoanBorrowStatePrice(String loanBorrowId, String price) {
        LoanBorrow loanBorrow = selectById(loanBorrowId);
        price = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                StrUtil.isEmpty(loanBorrow.getPaidPrice()) ? "0" : loanBorrow.getPaidPrice(),
                price);
        UpdateWrapper<LoanBorrow> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, loanBorrowId);
        updateWrapper.set(MybatisPlusUtil.toColumns(LoanBorrow::getPaidPrice), price);
        if (Double.parseDouble(price) >= Double.parseDouble(loanBorrow.getPrice())) {
            updateWrapper.set(MybatisPlusUtil.toColumns(LoanBorrow::getState), LoanPaidStateEnum.PAID.getKey());
        } else if (Double.parseDouble(price) > CommonNumConstants.NUM_ZERO) {
            updateWrapper.set(MybatisPlusUtil.toColumns(LoanBorrow::getState), LoanPaidStateEnum.PART_PAID.getKey());
        } else {
            updateWrapper.set(MybatisPlusUtil.toColumns(LoanBorrow::getState), LoanPaidStateEnum.NOT_PAID.getKey());
        }
        update(updateWrapper);
        refreshCache(loanBorrowId);
    }

    @Override
    public void queryLoanBorrowTypePie(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<LoanBorrow> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(LoanBorrow::getCreateTime));
        List<LoanBorrow> bean = list(queryWrapper);
        List<Map<String ,Object>>result = new ArrayList<>();
        if(CollectionUtil.isEmpty(bean)){
            return;
        }
        // 按借款类型分组求出对应数量
        Map<Integer, Long> map = bean.stream().collect(Collectors.groupingBy(LoanBorrow::getBorrowType, Collectors.counting()));
        LoanBorrowTypeEnum[] types = LoanBorrowTypeEnum.values();
        for (LoanBorrowTypeEnum type : types) {
            Long count = map.getOrDefault(type.getKey(), 0L);
            Map<String,Object> resultItem = new HashMap<>();
            BigDecimal percent = new BigDecimal(count).divide(new BigDecimal(bean.size()), 2, RoundingMode.HALF_UP);
            resultItem.put("name", type.getValue());
            resultItem.put("value", percent.multiply(new BigDecimal(100)) + "%");
            result.add(resultItem);
        }
        outputObject.setBeans(result);
    }

    @Override
    public void queryLoanBorrowDeptPie(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<LoanBorrow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LoanBorrow::getBorrowType), LoanBorrowTypeEnum.DEPARTMENT.getKey());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(LoanBorrow::getCreateTime));
        List<LoanBorrow> bean = list(queryWrapper);
        if(CollectionUtil.isEmpty(bean)){
            return;
        }
        iDepmentService.setDataMation(bean,LoanBorrow::getDepartmentId);
        // 根据部门id分组
        Map<String,List<LoanBorrow>> map = bean.stream().collect(Collectors.groupingBy(LoanBorrow::getDepartmentId));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<LoanBorrow>> entry : map.entrySet()) {
            String departmentName = entry.getValue().get(CommonNumConstants.NUM_ZERO).getDepartmentMation().get("name").toString();
            BigDecimal percent = new BigDecimal(entry.getValue().size()).divide(new BigDecimal(bean.size()), 2, RoundingMode.HALF_UP);
            Map<String, Object> deptInfo = new HashMap<>();
            deptInfo.put("name", departmentName);
            deptInfo.put("pie", percent.multiply(new BigDecimal(100)) + "%");
            result.add(deptInfo);
        }

        outputObject.setBeans(result);
    }

    /**
     * 时间格式 YYYY-MM
     * */
    @Override
    public List<LoanBorrow> queryLoanBorrowList(String time) {
        QueryWrapper<LoanBorrow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LoanBorrow::getState), FlowableStateEnum.PASS.getKey());
        queryWrapper.apply("date_format(" + MybatisPlusUtil.toColumns(LoanBorrow::getCreateTime) + ", '%Y-%m') = {0}", time);
        return list(queryWrapper);
    }
}
