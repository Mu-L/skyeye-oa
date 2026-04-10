/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.afterseal.classenum.AfterSealState;
import com.skyeye.afterseal.classenum.SealSignState;
import com.skyeye.afterseal.classenum.SealSignWorkUnit;
import com.skyeye.afterseal.dao.SealSignDao;
import com.skyeye.afterseal.entity.AfterSeal;
import com.skyeye.afterseal.entity.SealSign;
import com.skyeye.afterseal.service.AfterSealService;
import com.skyeye.afterseal.service.ProjectInstallerCommissionService;
import com.skyeye.afterseal.service.SealSignService;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @ClassName: SealSignServiceImpl
 * @Description: 工人签到报工信息服务层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/24
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Service
@SkyeyeService(name = "工人签到报工信息", groupName = "工单管理")
public class SealSignServiceImpl extends SkyeyeBusinessServiceImpl<SealSignDao, SealSign> implements SealSignService {

    @Autowired
    private AfterSealService afterSealService;

    @Autowired
    private ProjectInstallerCommissionService projectInstallerCommissionService;

    @Override
    protected QueryWrapper<SealSign> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<SealSign> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SealSign::getObjectId), commonPageInfo.getObjectId());
        if (StrUtil.equals(commonPageInfo.getType(), "current")) {
            // 查询当前用户的签到记录
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            queryWrapper.eq(MybatisPlusUtil.toColumns(SealSign::getSignId), userId);
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iAuthUserService.setNameForMap(beans, "auditUserId", "auditUserName");
        iAuthUserService.setNameForMap(beans, "signId", "signName");
        return beans;
    }

    @Override
    protected void validatorEntity(SealSign entity) {
        AfterSeal afterSeal = afterSealService.selectById(entity.getObjectId());
        // 只有"待签到"或"待完成"状态的工单才能进行签到
        if (!StrUtil.equals(afterSeal.getState(), AfterSealState.BE_SIGNED.getKey())
            && !StrUtil.equals(afterSeal.getState(), AfterSealState.BE_COMPLETED.getKey())) {
            throw new CustomException("该工单状态不允许签到。");
        }

        // 检查一个人一天只能签到一次
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String today = DateUtil.getYmdTimeAndToString(); // 获取今天的日期，格式：yyyy-MM-dd

        QueryWrapper<SealSign> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SealSign::getObjectId), entity.getObjectId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(SealSign::getSignId), userId);
        queryWrapper.like(MybatisPlusUtil.toColumns(SealSign::getSignTime), today);
        queryWrapper.last("LIMIT 1");

        List<SealSign> existSignList = list(queryWrapper);
        if (existSignList != null && !existSignList.isEmpty()) {
            throw new CustomException("您今天已经签到过了，一天只能签到一次。");
        }
    }

    @Override
    protected void createPrepose(SealSign entity) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        entity.setSignId(userId);
        entity.setSignTime(DateUtil.getTimeAndToString());
        // 设置默认状态为"待报工"
        entity.setState(SealSignState.PENDING_WORK_REPORT.getKey());
    }

    @Override
    protected void createPostpose(SealSign entity, String userId) {
        // 修改工单信息为【待完成】
        afterSealService.updateStateById(entity.getObjectId(), AfterSealState.BE_COMPLETED.getKey());
    }

    /**
     * 报工：更新工时信息，状态改为"待审核"
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void reportWork(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> entity = inputObject.getParams();
        String id = entity.get("id").toString();
        String workHours = entity.get("workHours").toString();
        String workUnit = entity.get("workUnit").toString();

        SealSign existEntity = selectById(id);
        if (existEntity == null) {
            outputObject.setreturnMessage("签到记录不存在");
            return;
        }

        // 只有"待报工" 或者 "已驳回"状态才能报工
        if (!SealSignState.PENDING_WORK_REPORT.getKey().equals(existEntity.getState())
            && !SealSignState.REJECTED.getKey().equals(existEntity.getState())) {
            outputObject.setreturnMessage("当前状态不允许报工");
            return;
        }

        // 使用 UpdateWrapper 更新工时信息
        UpdateWrapper<SealSign> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(SealSign::getWorkHours), workHours);
        updateWrapper.set(MybatisPlusUtil.toColumns(SealSign::getWorkUnit), workUnit);
        updateWrapper.set(MybatisPlusUtil.toColumns(SealSign::getState), SealSignState.PENDING_AUDIT.getKey());
        update(updateWrapper);

        // 清除缓存
        clearCache(id);
    }

    /**
     * 审核：更新审核信息，状态改为"已通过"或"已驳回"
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void auditSign(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> entity = inputObject.getParams();
        String id = entity.get("id").toString();
        String state = entity.get("state").toString();
        String auditRemark = entity.get("auditRemark").toString();

        // 只有"待审核"状态才能审核
        SealSign existEntity = selectById(id);
        if (existEntity == null) {
            outputObject.setreturnMessage("签到记录不存在");
            return;
        }

        if (!SealSignState.PENDING_AUDIT.getKey().equals(existEntity.getState())) {
            outputObject.setreturnMessage("当前状态不允许审核");
            return;
        }

        // 验证审核状态值
        if (!state.equals(SealSignState.APPROVED.getKey()) && !state.equals(SealSignState.REJECTED.getKey())) {
            outputObject.setreturnMessage("审核结果无效");
            return;
        }

        // 使用 UpdateWrapper 更新审核信息
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        UpdateWrapper<SealSign> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(SealSign::getState), state);
        updateWrapper.set(MybatisPlusUtil.toColumns(SealSign::getAuditRemark), auditRemark);
        updateWrapper.set(MybatisPlusUtil.toColumns(SealSign::getAuditTime), DateUtil.getTimeAndToString());
        updateWrapper.set(MybatisPlusUtil.toColumns(SealSign::getAuditUserId), userId);
        update(updateWrapper);

        // 清除缓存
        clearCache(id);

        // 如果审核通过，自动计算提成
        if (state.equals(SealSignState.APPROVED.getKey())) {
            // 调用提成计算服务
            projectInstallerCommissionService.calculateCommission(existEntity.getObjectId());
        }
    }

    /**
     * 获取指定时间范围内所有已审核通过的签到报工的总工时（转换为小时）
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 总工时（小时）的字符串
     */
    @Override
    public String getAllFinishedWorkHours(String startTime, String endTime) {
        // 查询指定时间范围内所有已审核通过的签到报工记录
        QueryWrapper<SealSign> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SealSign::getState), SealSignState.APPROVED.getKey());

        // 如果指定了时间范围，直接使用签到时间过滤
        if (StrUtil.isNotEmpty(startTime) && StrUtil.isNotEmpty(endTime)) {
            queryWrapper.apply(MybatisPlusUtil.toColumns(SealSign::getSignTime) + " <= {0}", endTime)
                .apply(MybatisPlusUtil.toColumns(SealSign::getSignTime) + " >= {0}", startTime);
        }

        List<SealSign> sealSignList = list(queryWrapper);

        if (CollectionUtil.isEmpty(sealSignList)) {
            return "0";
        }

        // 将工时转换为小时并累加
        String totalWorkHours = "0";
        for (SealSign sealSign : sealSignList) {
            String workHours = sealSign.getWorkHours();
            String workUnit = sealSign.getWorkUnit();

            if (StrUtil.isEmpty(workHours)) {
                continue;
            }

            // 根据单位转换为小时
            String workHoursInHours = getHourTime(workUnit, workHours);

            // 累加
            totalWorkHours = CalculationUtil.add(CommonNumConstants.NUM_FOUR, totalWorkHours, workHoursInHours);
        }

        return totalWorkHours;
    }

    private static String getHourTime(String workUnit, String workHours) {
        String workHoursInHours;
        if (SealSignWorkUnit.HOUR.getKey().equals(workUnit)) {
            // 小时：直接使用
            workHoursInHours = workHours;
        } else if (SealSignWorkUnit.DAY.getKey().equals(workUnit)) {
            // 天转换为小时：乘以8
            workHoursInHours = CalculationUtil.multiply(CommonNumConstants.NUM_TWO, workHours, "8");
        } else {
            // 其他单位，默认按小时处理
            workHoursInHours = workHours;
        }
        return workHoursInHours;
    }

    /**
     * 获取指定时间范围内各用户已审核通过的签到报工的总工时（转换为小时）
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 按用户ID分组的工时（小时）Map
     */
    @Override
    public Map<String, String> getAllFinishedWorkHoursByUserId(String startTime, String endTime) {
        // 查询指定时间范围内所有已审核通过的签到报工记录
        QueryWrapper<SealSign> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SealSign::getState), SealSignState.APPROVED.getKey());

        // 如果指定了时间范围，直接使用签到时间过滤
        if (StrUtil.isNotEmpty(startTime) && StrUtil.isNotEmpty(endTime)) {
            queryWrapper.apply(MybatisPlusUtil.toColumns(SealSign::getSignTime) + " <= {0}", endTime)
                .apply(MybatisPlusUtil.toColumns(SealSign::getSignTime) + " >= {0}", startTime);
        }

        List<SealSign> sealSignList = list(queryWrapper);

        if (CollectionUtil.isEmpty(sealSignList)) {
            return new HashMap<>();
        }

        // 按用户ID分组，将工时转换为小时并累加
        Map<String, String> userWorkHoursMap = new HashMap<>();
        for (SealSign sealSign : sealSignList) {
            String signId = sealSign.getSignId();
            String workHours = sealSign.getWorkHours();
            String workUnit = sealSign.getWorkUnit();

            if (StrUtil.isEmpty(signId) || StrUtil.isEmpty(workHours)) {
                continue;
            }

            // 根据单位转换为小时
            String workHoursInHours = getHourTime(workUnit, workHours);

            // 累加该用户的工时
            String currentWorkHours = userWorkHoursMap.getOrDefault(signId, "0");
            userWorkHoursMap.put(signId, CalculationUtil.add(CommonNumConstants.NUM_FOUR, currentWorkHours, workHoursInHours));
        }

        return userWorkHoursMap;
    }

    /**
     * 获取指定时间范围内各用户已审核通过的签到报工的工单数量
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 按用户ID分组的工单数量Map
     */
    @Override
    public Map<String, Long> getOrderCountByUserId(String startTime, String endTime) {
        // 查询指定时间范围内所有已审核通过的签到报工记录
        QueryWrapper<SealSign> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SealSign::getState), SealSignState.APPROVED.getKey());

        // 如果指定了时间范围，直接使用签到时间过滤
        if (StrUtil.isNotEmpty(startTime) && StrUtil.isNotEmpty(endTime)) {
            queryWrapper.apply(MybatisPlusUtil.toColumns(SealSign::getSignTime) + " <= {0}", endTime)
                .apply(MybatisPlusUtil.toColumns(SealSign::getSignTime) + " >= {0}", startTime);
        }

        List<SealSign> sealSignList = list(queryWrapper);

        if (CollectionUtil.isEmpty(sealSignList)) {
            return new HashMap<>();
        }

        // 按用户ID分组，统计每个用户的不重复工单数量
        Map<String, Set<String>> userOrderIdMap = new HashMap<>();
        for (SealSign sealSign : sealSignList) {
            String signId = sealSign.getSignId();
            String objectId = sealSign.getObjectId();

            if (StrUtil.isEmpty(signId) || StrUtil.isEmpty(objectId)) {
                continue;
            }

            // 使用Set去重，确保每个工单只统计一次
            userOrderIdMap.computeIfAbsent(signId, k -> new HashSet<>()).add(objectId);
        }

        // 转换为数量Map
        Map<String, Long> userOrderCountMap = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : userOrderIdMap.entrySet()) {
            userOrderCountMap.put(entry.getKey(), (long) entry.getValue().size());
        }

        return userOrderCountMap;
    }

}
