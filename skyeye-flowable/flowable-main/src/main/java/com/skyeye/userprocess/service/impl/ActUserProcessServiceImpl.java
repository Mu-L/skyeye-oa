/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.userprocess.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.entity.ActFlowMation;
import com.skyeye.eve.service.ActFlowService;
import com.skyeye.userprocess.dao.ActUserProcessDao;
import com.skyeye.userprocess.entity.ActUserProcess;
import com.skyeye.userprocess.service.ActUserProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ActUserProcessServiceImpl
 * @Description: 用户启动的流程管理服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2022/12/18 10:32
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "用户启动的流程", groupName = "用户启动的流程")
public class ActUserProcessServiceImpl extends SkyeyeBusinessServiceImpl<ActUserProcessDao, ActUserProcess> implements ActUserProcessService {

    @Autowired
    private ActFlowService actFlowService;

    /**
     * 根据流程实例id删除流程信息
     *
     * @param processInstanceId 流程实例id
     */
    @Override
    public void deleteByProcessInstanceId(String processInstanceId) {
        QueryWrapper<ActUserProcess> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ActUserProcess::getProcessInstanceId), processInstanceId);
        remove(queryWrapper);
    }

    /**
     * 保存流程信息
     *
     * @param processInstanceId 流程实例id
     * @param actFlowId         流程模型id
     * @param objectId          业务对象id
     * @param objectKey         业务对象的key
     */
    @Override
    public void saveActUserProcess(String processInstanceId, String actFlowId, String objectId, String objectKey, String appId) {
        ActUserProcess actUserProcess = new ActUserProcess();
        actUserProcess.setProcessInstanceId(processInstanceId);
        actUserProcess.setActFlowId(actFlowId);
        actUserProcess.setObjectId(objectId);
        actUserProcess.setObjectKey(objectKey);
        actUserProcess.setAppId(appId);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        createEntity(actUserProcess, userId);
    }

    /**
     * 根据流程实例id查询流程信息
     *
     * @param processInstanceId 流程实例id
     */
    @Override
    public ActUserProcess selectByProcessInstanceId(String processInstanceId) {
        QueryWrapper<ActUserProcess> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ActUserProcess::getProcessInstanceId), processInstanceId);
        ActUserProcess userProcess = getOne(queryWrapper);
        if (userProcess == null) {
            return new ActUserProcess();
        }
        iAuthUserService.setName(userProcess, "createId", "createName");
        // 获取工作流模型信息
        Map<String, ActFlowMation> actFlowMationMap = actFlowService.actIdToFlowNameByIds(Arrays.asList(userProcess.getActFlowId()));
        if (CollectionUtil.isNotEmpty(actFlowMationMap)) {
            ActFlowMation actFlowMation = actFlowMationMap.get(userProcess.getActFlowId());
            userProcess.setTitle(actFlowMation.getFlowName());
        }
        return userProcess;
    }

    @Override
    public Map<String, ActUserProcess> selectByProcessInstanceId(List<String> processInstanceIds) {
        if (CollectionUtil.isEmpty(processInstanceIds)) {
            return MapUtil.newHashMap();
        }
        QueryWrapper<ActUserProcess> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ActUserProcess::getProcessInstanceId), processInstanceIds);
        List<ActUserProcess> actUserProcessList = list(queryWrapper);
        iAuthUserService.setName(actUserProcessList, "createId", "createName");
        // 获取工作流模型信息
        List<String> actFlowIds = actUserProcessList.stream().map(ActUserProcess::getActFlowId).collect(Collectors.toList());
        Map<String, ActFlowMation> actFlowMationMap = actFlowService.actIdToFlowNameByIds(actFlowIds);
        actUserProcessList.forEach(actUserProcess -> {
            ActFlowMation actFlowMation = actFlowMationMap.get(actUserProcess.getActFlowId());
            if (ObjectUtil.isNotEmpty(actFlowMation)) {
                actUserProcess.setTitle(actFlowMation.getFlowName());
            }
        });
        return actUserProcessList.stream().collect(Collectors.toMap(ActUserProcess::getProcessInstanceId, bean -> bean));
    }
}
