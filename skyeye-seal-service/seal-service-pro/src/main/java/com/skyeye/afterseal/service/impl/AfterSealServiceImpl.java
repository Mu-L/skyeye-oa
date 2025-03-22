/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Joiner;
import com.skyeye.afterseal.classenum.AfterSealState;
import com.skyeye.afterseal.dao.AfterSealDao;
import com.skyeye.afterseal.entity.AfterSeal;
import com.skyeye.afterseal.service.AfterSealService;
import com.skyeye.afterseal.service.SealFaultService;
import com.skyeye.afterseal.service.SealFaultUseMaterialService;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.crm.service.ICustomerService;
import com.skyeye.erp.service.IMaterialService;
import com.skyeye.eve.rest.mq.JobMateMation;
import com.skyeye.eve.service.IJobMateMationService;
import com.skyeye.exception.CustomException;
import com.skyeye.worker.service.SealWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: SealSeServiceServiceImpl
 * @Description: 售后服务工单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 21:23
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "售后工单管理", groupName = "售后工单")
public class AfterSealServiceImpl extends SkyeyeBusinessServiceImpl<AfterSealDao, AfterSeal> implements AfterSealService {

    @Autowired
    private IJobMateMationService iJobMateMationService;

    @Autowired
    private ICustomerService iCustomerService;

    @Autowired
    private IMaterialService iMaterialService;

    @Autowired
    private SealWorkerService sealWorkerService;

    @Autowired
    private SealFaultUseMaterialService sealFaultUseMaterialService;

    @Autowired
    private SealFaultService sealFaultService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        setPageInfoOfType(pageInfo, inputObject.getLogParams().get("id").toString());
        List<Map<String, Object>> beans = skyeyeBaseMapper.querySealServiceOrderList(pageInfo);

        iCustomerService.setMationForMap(beans, "holderId", "holderMation");
        iAuthUserService.setMationForMap(beans, "declarationId", "declarationMation");
        iAuthUserService.setMationForMap(beans, "serviceUserId", "serviceUserMation");
        return beans;
    }

    private void setPageInfoOfType(CommonPageInfo pageInfo, String userId) {
        String state = pageInfo.getState();
        if (StrUtil.isEmpty(state)) {
            return;
        }
        if (StrUtil.equals(state, AfterSealState.BE_DISPATCHED.getKey())
            || StrUtil.equals(state, AfterSealState.BE_EVALUATED.getKey())
            || StrUtil.equals(state, AfterSealState.AUDIT.getKey())
            || StrUtil.equals(state, AfterSealState.COMPLATE.getKey())) {
            // 待派工，待评价，待审核，已完工的工单查询所有的
        } else {
            pageInfo.setCreateId(userId);
        }
    }

    @Override
    public AfterSeal selectById(String id) {
        AfterSeal afterSeal = super.selectById(id);

        iAuthUserService.setDataMation(afterSeal, AfterSeal::getDeclarationId);
        afterSeal.setServiceUserMation(sealWorkerService.selectByUserId(afterSeal.getServiceUserId()));
        if (CollectionUtil.isNotEmpty(afterSeal.getCooperationUserId())) {
            afterSeal.setCooperationUserMation(iAuthUserService.queryDataMationByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(afterSeal.getCooperationUserId())));
        }

        iMaterialService.setDataMation(afterSeal, AfterSeal::getProductId);
        iCustomerService.setDataMation(afterSeal, AfterSeal::getHolderId);
        return afterSeal;
    }

    @Override
    public void createPrepose(AfterSeal entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business);
        entity.setOddNumber(oddNumber);
        if (StrUtil.isEmpty(entity.getServiceUserId())) {
            entity.setState(AfterSealState.BE_DISPATCHED.getKey());
        } else {
            // 默认有接单人
            entity.setState(AfterSealState.PENDING_ORDERS.getKey());
            entity.setServiceTime(DateUtil.getTimeAndToString());
        }
        entity.setDeclarationId(InputObject.getLogParamsStatic().get("id").toString());
    }

    @Override
    protected void validatorEntity(AfterSeal entity) {
        if (StrUtil.isNotEmpty(entity.getId())) {
            AfterSeal afterSeal = selectById(entity.getId());
            if (StrUtil.equals(afterSeal.getState(), AfterSealState.BE_DISPATCHED.getKey())
                || StrUtil.equals(afterSeal.getState(), AfterSealState.PENDING_ORDERS.getKey())) {
                // 待派工，待接单可以进行编辑
            } else {
                throw new CustomException("该数据状态已改变，请刷新页面！");
            }
        }
    }

    @Override
    protected void updatePrepose(AfterSeal entity) {
        if (StrUtil.isEmpty(entity.getServiceUserId())) {
            entity.setState(AfterSealState.BE_DISPATCHED.getKey());
        } else {
            // 默认有接单人
            entity.setState(AfterSealState.PENDING_ORDERS.getKey());
            entity.setServiceTime(DateUtil.getTimeAndToString());
        }
    }

    @Override
    protected void writePostpose(AfterSeal entity, String userId) {
        super.writePostpose(entity, userId);

        sendDispatchWork(entity.getId(), userId);
    }

    private void sendDispatchWork(String id, String userId) {
        // 发送消息
        Map<String, Object> notice = new HashMap<>();
        notice.put("serviceId", id);
        notice.put("type", MqConstants.JobMateMationJobType.WATI_WORKER_SEND.getJobType());
        JobMateMation jobMateMation = new JobMateMation();
        jobMateMation.setJsonStr(JSONUtil.toJsonStr(notice));
        jobMateMation.setUserId(userId);
        iJobMateMationService.sendMQProducer(jobMateMation);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editSealSeServiceWaitToWorkMation(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        AfterSeal afterSeal = selectById(id);
        if (StrUtil.equals(afterSeal.getState(), AfterSealState.BE_DISPATCHED.getKey())) {
            // 待派工可以进行派工
            UpdateWrapper<AfterSeal> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, afterSeal.getId());
            updateWrapper.set(MybatisPlusUtil.toColumns(AfterSeal::getState), AfterSealState.PENDING_ORDERS.getKey());
            updateWrapper.set(MybatisPlusUtil.toColumns(AfterSeal::getServiceUserId), map.get("serviceUserId").toString());
            updateWrapper.set(MybatisPlusUtil.toColumns(AfterSeal::getCooperationUserId), map.get("cooperationUserId").toString());
            updateWrapper.set(MybatisPlusUtil.toColumns(AfterSeal::getServiceTime), DateUtil.getTimeAndToString());
            update(updateWrapper);
            // 派工成功mq消息任务
            sendDispatchWork(id, afterSeal.getCreateId());
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void receivingSealSeServiceOrderById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        AfterSeal afterSeal = selectById(map.get("id").toString());
        if (StrUtil.equals(afterSeal.getState(), AfterSealState.PENDING_ORDERS.getKey())) {
            // 待接单可以进行接单
            updateStateById(afterSeal.getId(), AfterSealState.BE_SIGNED.getKey());
            refreshCache(afterSeal.getId());
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    public void deletePreExecution(AfterSeal afterSeal) {
        if (StrUtil.equals(afterSeal.getState(), AfterSealState.BE_DISPATCHED.getKey())
            || StrUtil.equals(afterSeal.getState(), AfterSealState.PENDING_ORDERS.getKey())) {
            // 待派工/待接单可以进行删除
        } else {
            throw new CustomException("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    public void querySealSeServiceSignon(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<AfterSeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AfterSeal::getServiceUserId), InputObject.getLogParamsStatic().get("id").toString());
        queryWrapper.eq(MybatisPlusUtil.toColumns(AfterSeal::getState), AfterSealState.BE_COMPLETED.getKey());
        List<AfterSeal> afterSealList = list(queryWrapper);
        outputObject.setBeans(afterSealList);
        outputObject.settotal(afterSealList.size());
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void auditSealSeServiceOrderById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        AfterSeal afterSeal = selectById(map.get("id").toString());
        if (StrUtil.equals(afterSeal.getState(), AfterSealState.BE_COMPLETED.getKey())) {
            // 只有待完工状态下可以完工，修改为待评价状态
            updateStateById(afterSeal.getId(), AfterSealState.BE_EVALUATED.getKey());
            refreshCache(afterSeal.getId());
        } else {
            throw new CustomException("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    public void updateStateById(String id, String state) {
        UpdateWrapper<AfterSeal> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(AfterSeal::getState), state);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void finishSealSeServiceOrderById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        AfterSeal afterSeal = selectById(map.get("id").toString());
        if (StrUtil.equals(afterSeal.getState(), AfterSealState.AUDIT.getKey())) {
            // 待审核状态可以进行审核完工
            updateStateById(afterSeal.getId(), AfterSealState.COMPLATE.getKey());
            refreshCache(afterSeal.getId());
        } else {
            throw new CustomException("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    public void queryOverviewSealSeServiceOrder(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        // 查询售后服务总览信息
        Map<String, Object> resultMap = new HashMap<>();
        // 总工单数
        QueryWrapper<AfterSeal> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(tableSelectInfo.getStartTime()) && StrUtil.isNotEmpty(tableSelectInfo.getEndTime())) {
            queryWrapper.apply("date_format(" + MybatisPlusUtil.toColumns(AfterSeal::getCreateTime) + ", '%Y-%m-%d') <= date_format({0}, '%Y-%m-%d')", tableSelectInfo.getEndTime())
                .apply("date_format(" + MybatisPlusUtil.toColumns(AfterSeal::getCreateTime) + ", '%Y-%m-%d') >= date_format({0}, '%Y-%m-%d')", tableSelectInfo.getStartTime());
        }
        Long totalOrders = count(queryWrapper);
        resultMap.put("totalOrders", totalOrders);
        // 完成工单数
        queryWrapper.eq(MybatisPlusUtil.toColumns(AfterSeal::getState), AfterSealState.COMPLATE.getKey());
        resultMap.put("completedOrders", count(queryWrapper));
        // 配件使用数
        resultMap.put("useCount", sealFaultUseMaterialService.queryUseCount(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime()));
        // 平均处理时长
        if (totalOrders > CommonNumConstants.NUM_ZERO) {
            resultMap.put("avgProcessTime", CalculationUtil.divide(String.valueOf(sealFaultService.getAllFinishedServiceTime(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime())),
                String.valueOf(totalOrders), CommonNumConstants.NUM_TWO));
        } else {
            resultMap.put("avgProcessTime", CommonNumConstants.NUM_ZERO);
        }
        outputObject.setBean(resultMap);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private Map<String, Long> getAllFinishedServiceNum(List<String> userIds, String startTime, String endTime) {
        QueryWrapper<AfterSeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AfterSeal::getState), AfterSealState.COMPLATE.getKey());
        if (StrUtil.isNotEmpty(startTime) && StrUtil.isNotEmpty(endTime)) {
            queryWrapper.apply("date_format(" + MybatisPlusUtil.toColumns(AfterSeal::getCreateTime) + ", '%Y-%m-%d') <= date_format({0}, '%Y-%m-%d')", endTime)
                .apply("date_format(" + MybatisPlusUtil.toColumns(AfterSeal::getCreateTime) + ", '%Y-%m-%d') >= date_format({0}, '%Y-%m-%d')", startTime);
        }
        if (CollectionUtil.isNotEmpty(userIds)) {
            queryWrapper.in(MybatisPlusUtil.toColumns(AfterSeal::getServiceUserId), userIds);
        }
        List<AfterSeal> afterSealList = list(queryWrapper);
        return afterSealList.stream().collect(Collectors.groupingBy(AfterSeal::getServiceUserId, Collectors.counting()));
    }

    @Override
    public void querySealSeServiceOrderTypeStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        // 根据typeId进行分组统计
        QueryWrapper<AfterSeal> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(tableSelectInfo.getStartTime()) && StrUtil.isNotEmpty(tableSelectInfo.getEndTime())) {
            queryWrapper.apply("date_format(" + MybatisPlusUtil.toColumns(AfterSeal::getCreateTime) + ", '%Y-%m-%d') <= date_format({0}, '%Y-%m-%d')", tableSelectInfo.getEndTime())
                .apply("date_format(" + MybatisPlusUtil.toColumns(AfterSeal::getCreateTime) + ", '%Y-%m-%d') >= date_format({0}, '%Y-%m-%d')", tableSelectInfo.getStartTime());
        }
        queryWrapper.groupBy(MybatisPlusUtil.toColumns(AfterSeal::getTypeId));
        List<AfterSeal> resultList = list(queryWrapper);
        iSysDictDataService.setDataMation(resultList, AfterSeal::getTypeId);
        // 获取typeId对应的name
        Map<String, String> stringMap = resultList.stream().collect(Collectors.toMap(AfterSeal::getTypeId, bean -> {
            if (CollectionUtil.isNotEmpty(bean.getTypeMation())) {
                return bean.getTypeMation().get("dictName").toString();
            } else {
                return StrUtil.EMPTY;
            }
        }));

        Map<String, Long> collect = resultList.stream().collect(Collectors.groupingBy(AfterSeal::getTypeId, Collectors.counting()));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : collect.entrySet()) {
            // 如果没有对应的name，则跳过
            if (StrUtil.isBlank(stringMap.get(entry.getKey()))) {
                continue;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("name", stringMap.get(entry.getKey()));
            map.put("value", entry.getValue());
            result.add(map);
        }
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    @Override
    public void querySealSeServiceOrderTrendStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        String startTime, endTime;
        if (StrUtil.isNotEmpty(tableSelectInfo.getStartTime()) && StrUtil.isNotEmpty(tableSelectInfo.getEndTime())) {
            startTime = tableSelectInfo.getStartTime();
            endTime = tableSelectInfo.getEndTime();
        } else {
            startTime = DateUtil.formatDate2Str(DateUtil.getAfDate(DateUtil.getPointTime(DateUtil.getYmdTimeAndToString(), DateUtil.YYYY_MM_DD), -30, "d"),
                DateUtil.YYYY_MM_DD);
            endTime = DateUtil.getYmdTimeAndToString();
        }
        List<String> dayList = DateUtil.getDays(startTime, endTime);
        QueryWrapper<AfterSeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(MybatisPlusUtil.toColumns(AfterSeal::getCreateTime));
        queryWrapper.apply("date_format(" + MybatisPlusUtil.toColumns(AfterSeal::getCreateTime) + ", '%Y-%m-%d') <= date_format({0}, '%Y-%m-%d')", endTime)
            .apply("date_format(" + MybatisPlusUtil.toColumns(AfterSeal::getCreateTime) + ", '%Y-%m-%d') >= date_format({0}, '%Y-%m-%d')", startTime);
        queryWrapper.groupBy(MybatisPlusUtil.toColumns(AfterSeal::getCreateTime));
        // 1. 新增的工单
        List<AfterSeal> afterSealList = list(queryWrapper);
        // 根据createTime进行分组统计
        Map<String, Long> collect = afterSealList.stream().collect(Collectors.groupingBy(bean -> {
            Date pointTime = DateUtil.getPointTime(bean.getCreateTime(), DateUtil.YYYY_MM_DD);
            return DateUtil.formatDate2Str(pointTime, DateUtil.YYYY_MM_DD);
        }, Collectors.counting()));
        // 2. 完工的工单
        queryWrapper.eq(MybatisPlusUtil.toColumns(AfterSeal::getState), AfterSealState.COMPLATE.getKey());
        List<AfterSeal> afterSealList2 = list(queryWrapper);
        // 根据createTime进行分组统计
        Map<String, Long> collect2 = afterSealList2.stream().collect(Collectors.groupingBy(bean -> {
            Date pointTime = DateUtil.getPointTime(bean.getCreateTime(), DateUtil.YYYY_MM_DD);
            return DateUtil.formatDate2Str(pointTime, DateUtil.YYYY_MM_DD);
        }, Collectors.counting()));
        // 构建结果集
        Map<String, Object> resultMap = new HashMap<>();
        List<Long> allNewOrders = new ArrayList<>();
        List<Long> completedOrders = new ArrayList<>();
        Long defaultValue = Long.valueOf(CommonNumConstants.NUM_ZERO);
        for (String day : dayList) {
            allNewOrders.add(collect.getOrDefault(day, defaultValue) - collect2.getOrDefault(day, defaultValue));
            completedOrders.add(collect2.getOrDefault(day, defaultValue));
        }
        resultMap.put("allNewOrders", allNewOrders);
        resultMap.put("completedOrders", completedOrders);
        resultMap.put("dayList", dayList);

        outputObject.setBean(resultMap);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void querySealServiceOrderWorkerStats(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        sealWorkerService.queryPageList(inputObject, outputObject);
        List<Map<String, Object>> beans = (List<Map<String, Object>>) outputObject.getObject().get("rows");
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        List<String> userId = beans.stream().map(bean -> bean.get("userId").toString()).collect(Collectors.toList());
        // 查询工单数量
        Map<String, Long> allFinishedServiceNum = getAllFinishedServiceNum(userId, commonPageInfo.getStartTime(), commonPageInfo.getEndTime());
        // 查询工时
        Map<String, Double> finishedServiceTime = sealFaultService.getAllFinishedServiceTime(userId, commonPageInfo.getStartTime(), commonPageInfo.getEndTime());
        // 查询使用配件数
        Map<String, Long> useCount = sealFaultUseMaterialService.queryUseCountByUserId(userId, commonPageInfo.getStartTime(), commonPageInfo.getEndTime());
        // 合并数据
        Long defaultValue = Long.valueOf(CommonNumConstants.NUM_ZERO);
        Double defaultValueDouble = Double.valueOf(CommonNumConstants.NUM_ZERO);
        for (Map<String, Object> bean : beans) {
            String userIdStr = bean.get("userId").toString();
            // 完成工单数量
            bean.put("completedOrders", allFinishedServiceNum.getOrDefault(userIdStr, defaultValue));
            // 平均工时
            if (allFinishedServiceNum.getOrDefault(userIdStr, defaultValue) == 0) {
                bean.put("avgProcessTime", CommonNumConstants.NUM_ZERO);
            } else {
                bean.put("avgProcessTime", CalculationUtil.divide(String.valueOf(finishedServiceTime.getOrDefault(userIdStr, defaultValueDouble)),
                    allFinishedServiceNum.getOrDefault(userIdStr, defaultValue).toString(), CommonNumConstants.NUM_TWO));
            }
            // 配件使用数
            bean.put("totalParts", useCount.getOrDefault(userIdStr, defaultValue));
        }

        outputObject.setBeans(beans);
    }

}
