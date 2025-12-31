/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.payment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.payment.classenum.PaymentHistoryState;
import com.skyeye.eve.payment.dao.WagesPaymentHistoryDao;
import com.skyeye.eve.payment.entity.WagesPaymentHistory;
import com.skyeye.eve.payment.service.WagesPaymentHistoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: WagesPaymentHistoryServiceImpl
 * @Description: 薪资发放历史管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 23:34
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "薪资发放历史", groupName = "薪资发放历史")
public class WagesPaymentHistoryServiceImpl extends SkyeyeBusinessServiceImpl<WagesPaymentHistoryDao, WagesPaymentHistory> implements WagesPaymentHistoryService {

    private void queryList(CommonPageInfo pageInfo, OutputObject outputObject) {
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryWagesPaymentHistoryList(pageInfo);
        // 设置员工信息
        List<String> staffIds = beans.stream().map(bean -> bean.get("staffId").toString())
            .filter(staffId -> StrUtil.isNotEmpty(staffId)).distinct().collect(Collectors.toList());
        Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(staffIds);
        beans.forEach(bean -> {
            String staffId = bean.get("staffId").toString();
            bean.put("staffMation", staffMap.get(staffId));
        });
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 获取所有已发放薪资发放历史列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryAllGrantWagesPaymentHistoryList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        pageInfo.setState(PaymentHistoryState.ISSUED.getKey().toString());
        queryList(pageInfo, outputObject);
    }

    /**
     * 获取我的薪资发放历史列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryMyWagesPaymentHistoryList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        pageInfo.setStaffId(inputObject.getLogParams().get("staffId").toString());
        queryList(pageInfo, outputObject);
    }

    /**
     * 获取所有待发放薪资列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryAllNotGrantWagesPaymentHistoryList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        pageInfo.setState(PaymentHistoryState.UNISSUED.getKey().toString());
        queryList(pageInfo, outputObject);
    }

    @Override
    public List<WagesPaymentHistory> queryWagesPaymentHistoryByState(Integer state) {
        QueryWrapper<WagesPaymentHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(WagesPaymentHistory::getState), state);
        List<WagesPaymentHistory> paymentHistories = list(queryWrapper);
        return paymentHistories;
    }

    @Override
    public void editWagesPaymentHistoryState(String staffId, String payMonth, Integer state) {
        UpdateWrapper<WagesPaymentHistory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(WagesPaymentHistory::getStaffId), staffId);
        updateWrapper.eq(MybatisPlusUtil.toColumns(WagesPaymentHistory::getPayMonth), payMonth);
        updateWrapper.set(MybatisPlusUtil.toColumns(WagesPaymentHistory::getState), state);
        updateWrapper.set(MybatisPlusUtil.toColumns(WagesPaymentHistory::getGrantTime), DateUtil.getTimeAndToString());
        update(updateWrapper);
    }

    @Override
    public void queryWagesStaffPaymentDetail(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String staffId = map.get("staffId").toString();
        String payMonth = map.get("payMonth").toString();
        QueryWrapper<WagesPaymentHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(WagesPaymentHistory::getStaffId), staffId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(WagesPaymentHistory::getPayMonth), payMonth);
        WagesPaymentHistory wagesPaymentHistory = getOne(queryWrapper, false);
        outputObject.setBean(wagesPaymentHistory);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }
}
