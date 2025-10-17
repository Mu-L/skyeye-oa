/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.milestone.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.milestone.classenum.MilestoneAuthEnum;
import com.skyeye.milestone.classenum.MilestoneStateEnum;
import com.skyeye.milestone.dao.MilestoneDao;
import com.skyeye.milestone.entity.Milestone;
import com.skyeye.milestone.service.MilestoneService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: MilestoneServiceImpl
 * @Description: 里程碑管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/14 20:16
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "里程碑管理", groupName = "里程碑管理", flowable = true, teamAuth = true)
public class MilestoneServiceImpl extends SkyeyeBusinessServiceImpl<MilestoneDao, Milestone> implements MilestoneService {

    @Override
    public Class getAuthEnumClass() {
        return MilestoneAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(MilestoneAuthEnum.ADD.getKey(), MilestoneAuthEnum.EDIT.getKey(), MilestoneAuthEnum.DELETE.getKey(),
            MilestoneAuthEnum.REVOKE.getKey(), MilestoneAuthEnum.INVALID.getKey(), MilestoneAuthEnum.SUBMIT_TO_APPROVAL.getKey(), MilestoneAuthEnum.LIST.getKey(),
            MilestoneAuthEnum.EXECUTING.getKey(), MilestoneAuthEnum.COMPLETED.getKey(), MilestoneAuthEnum.CLOSE.getKey(), MilestoneAuthEnum.MY_CREATE.getKey());
    }

    @Override
    protected QueryWrapper<Milestone> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Milestone> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Milestone::getObjectId), commonPageInfo.getObjectId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(Milestone::getObjectKey), commonPageInfo.getObjectKey());
        if (StrUtil.equals("myCreate", commonPageInfo.getType())) {
            Milestone milestone = ReflectUtil.newInstance(clazz);
            milestone.setObjectId(commonPageInfo.getObjectId());
            milestone.setObjectKey(commonPageInfo.getObjectKey());
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            checkAuthPermission(milestone, userId, CommonNumConstants.NUM_TEN);
            queryWrapper.eq(MybatisPlusUtil.toColumns(Milestone::getCreateId), userId);
        }
        return queryWrapper;
    }

    @Override
    public Milestone selectById(String id) {
        Milestone milestone = super.selectById(id);
        // 负责人
        if (CollectionUtil.isNotEmpty(milestone.getResponsibleId())) {
            milestone.setResponsibleMation(iAuthUserService.queryDataMationByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(milestone.getResponsibleId())));
        }

        return milestone;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void executionMilestone(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Milestone milestone = selectById(id);
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        checkAuthPermission(milestone, userId, CommonNumConstants.NUM_SEVEN);

        if (StrUtil.equals(FlowableStateEnum.PASS.getKey(), milestone.getState())) {
            // 审核通过可以开始执行
            UpdateWrapper<Milestone> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(Milestone::getState), MilestoneStateEnum.EXECUTING.getKey());
            update(updateWrapper);
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void complateMilestone(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Milestone milestone = selectById(id);
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        checkAuthPermission(milestone, userId, CommonNumConstants.NUM_EIGHT);

        if (StrUtil.equals(MilestoneStateEnum.EXECUTING.getKey(), milestone.getState())) {
            // 执行中状态下可以执行完成
            UpdateWrapper<Milestone> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(Milestone::getState), MilestoneStateEnum.COMPLETED.getKey());
            update(updateWrapper);
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void closeMilestone(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Milestone milestone = selectById(id);
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        checkAuthPermission(milestone, userId, CommonNumConstants.NUM_NINE);

        if (StrUtil.equals(MilestoneStateEnum.COMPLETED.getKey(), milestone.getState())) {
            // 执行完成状态下可以关闭
            UpdateWrapper<Milestone> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(Milestone::getState), MilestoneStateEnum.CLOSE.getKey());
            update(updateWrapper);
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    public void queryAllExecutingMilestoneList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String objectId = map.get("objectId").toString();
        if (StrUtil.isEmpty(objectId)) {
            return;
        }
        QueryWrapper<Milestone> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Milestone::getObjectId), objectId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Milestone::getState), MilestoneStateEnum.EXECUTING.getKey());
        List<Milestone> milestoneList = list(queryWrapper);
        outputObject.setBeans(milestoneList);
        outputObject.settotal(milestoneList.size());
    }

    @Override
    public void queryAllApprovalMilestoneList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String objectId = map.get("objectId").toString();
        QueryWrapper<Milestone> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Milestone::getObjectId), objectId);
        List<String> states = Arrays.asList(FlowableStateEnum.DRAFT.getKey(),
            FlowableStateEnum.IN_EXAMINE.getKey(), FlowableStateEnum.REJECT.getKey(), FlowableStateEnum.REVOKE.getKey());
        queryWrapper.notIn(MybatisPlusUtil.toColumns(Milestone::getState), states);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Milestone::getStartTime));
        List<Milestone> milestoneList = list(queryWrapper);
        milestoneList.forEach(milestone -> {
            milestone.setPId(CommonNumConstants.NUM_ZERO.toString());
        });
        outputObject.setBeans(milestoneList);
        outputObject.settotal(milestoneList.size());
    }
}
