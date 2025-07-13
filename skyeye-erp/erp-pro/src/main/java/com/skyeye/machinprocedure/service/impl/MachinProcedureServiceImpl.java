/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machinprocedure.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.machinprocedure.classenum.MachinProcedureFarmState;
import com.skyeye.machinprocedure.classenum.MachinProcedureState;
import com.skyeye.machinprocedure.dao.MachinProcedureDao;
import com.skyeye.machinprocedure.entity.MachinProcedure;
import com.skyeye.machinprocedure.entity.MachinProcedureFarm;
import com.skyeye.machinprocedure.service.MachinProcedureFarmService;
import com.skyeye.machinprocedure.service.MachinProcedureService;
import com.skyeye.procedure.service.WorkProcedureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: MachinProcedureServiceImpl
 * @Description: 加工单子单据工序信息服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/24 15:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "加工单子单据工序信息", groupName = "加工单管理")
public class MachinProcedureServiceImpl extends SkyeyeBusinessServiceImpl<MachinProcedureDao, MachinProcedure> implements MachinProcedureService {

    @Autowired
    private MachinProcedureFarmService machinProcedureFarmService;

    @Autowired
    private WorkProcedureService workProcedureService;

    @Override
    public void deleteByParentId(String parentId) {
        QueryWrapper<MachinProcedure> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedure::getParentId), parentId);
        remove(queryWrapper);
    }

    @Override
    public MachinProcedure selectById(String id) {
        MachinProcedure machinProcedure = super.selectById(id);
        List<MachinProcedureFarm> machinProcedureFarmList = machinProcedureFarmService.queryAllMachinProcedureFarmByMachinProcedureId(id);
        machinProcedure.setMachinProcedureFarmList(machinProcedureFarmList);
        return machinProcedure;
    }

    @Override
    public List<MachinProcedure> selectByIds(String... ids) {
        List<MachinProcedure> machinProcedureList = super.selectByIds(ids);
        workProcedureService.setDataMation(machinProcedureList, MachinProcedure::getProcedureId);
        return machinProcedureList;
    }

    @Override
    public void saveList(String parentId, List<MachinProcedure> machinProcedureList) {
        deleteByParentId(parentId);
        if (CollectionUtil.isNotEmpty(machinProcedureList)) {
            for (MachinProcedure machinProcedure : machinProcedureList) {
                machinProcedure.setParentId(parentId);
                machinProcedure.setState(MachinProcedureState.WAIT_STARTED.getKey());
            }
            createEntity(machinProcedureList, StrUtil.EMPTY);
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void setMachinProcedureById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();

        String machinProcedureFarmListStr = params.get("machinProcedureFarmList").toString();
        List<MachinProcedureFarm> machinProcedureFarmList = new ArrayList<>();
        if (StrUtil.isNotEmpty(machinProcedureFarmListStr)) {
            machinProcedureFarmList = JSONUtil.toList(machinProcedureFarmListStr, MachinProcedureFarm.class);
        }
        params.put("machinProcedureFarmList", null);
        MachinProcedure machinProcedure = JSONUtil.toBean(JSONUtil.toJsonStr(params), MachinProcedure.class);

        UpdateWrapper<MachinProcedure> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, machinProcedure.getId());
        updateWrapper.set(MybatisPlusUtil.toColumns(MachinProcedure::getPlanStartTime), machinProcedure.getPlanStartTime());
        updateWrapper.set(MybatisPlusUtil.toColumns(MachinProcedure::getPlanEndTime), machinProcedure.getPlanEndTime());
        updateWrapper.set(MybatisPlusUtil.toColumns(MachinProcedure::getActualStartTime), machinProcedure.getActualStartTime());
        updateWrapper.set(MybatisPlusUtil.toColumns(MachinProcedure::getActualEndTime), machinProcedure.getActualEndTime());
        update(updateWrapper);
        MachinProcedure machinProcedureMation = selectById(machinProcedure.getId());
        // 处理车间任务信息
        machinProcedureFarmList.forEach(machinProcedureFarm -> {
            machinProcedureFarm.setMachinId(machinProcedureMation.getParentId());
            machinProcedureFarm.setMachinProcedureId(machinProcedureMation.getId());
            machinProcedureFarm.setState(MachinProcedureFarmState.WAIT_RECEIVE.getKey());
        });
        // 获取除了【待接收】状态之外的其他车间任务
        List<MachinProcedureFarm> oldMachinProcedureFarmList = machinProcedureFarmService.queryMachinProcedureFarmByMachinProcedureId(machinProcedure.getId());
        List<String> inSqlIds = oldMachinProcedureFarmList.stream().map(MachinProcedureFarm::getId).collect(Collectors.toList());
        // 过滤掉已经存在的不是待接收状态的车间任务
        machinProcedureFarmList = machinProcedureFarmList.stream()
            .filter(machinProcedureFarm -> !inSqlIds.contains(machinProcedureFarm.getId()))
            .collect(Collectors.toList());
        // 删除原有的车间任务
        machinProcedureFarmService.deleteMachinProcedureFarmByMachinProcedureId(machinProcedure.getId());
        // 保存车间任务信息
        String userId = inputObject.getLogParams().get("id").toString();
        machinProcedureFarmService.createEntity(machinProcedureFarmList, userId);
    }

    @Override
    public Map<String, MachinProcedure> queryMachinProcedureMapByMachinId(String machinId) {
        QueryWrapper<MachinProcedure> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedure::getParentId), machinId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(MachinProcedure::getOrderBy));
        List<MachinProcedure> machinProcedureList = list(queryWrapper);
        Map<String, MachinProcedure> machinProcedureMap = machinProcedureList.stream().collect(Collectors.toMap(bean ->
            String.format(Locale.ROOT, "%s-%s-%s-%s-%s-%s",
                bean.getChildId(), bean.getBomChildId(), bean.getMaterialId(), bean.getNormsId(), bean.getWayProcedureId(), bean.getProcedureId()), bean -> bean));
        return machinProcedureMap;
    }

    @Override
    public void editStateById(String id, Integer state) {
        UpdateWrapper<MachinProcedure> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(MachinProcedure::getState), state);
        update(updateWrapper);
    }

    /**
     * 判断当前工序的前置工序是否部分完成/全部完成
     *
     * @param machinProcedureId 当前加工单子单据工序id
     * @return true: 前置工序全部完成/部分完成; false: 前置工序未完成
     */
    @Override
    public boolean checkPrevMachinProcedureIsCompleted(String machinProcedureId) {
        // 查询当前加工单子单据工序的信息
        MachinProcedure machinProcedure = selectById(machinProcedureId);
        // 查询当前加工单子单据工序的前置工序
        QueryWrapper<MachinProcedure> queryWrapper = new QueryWrapper<>();
        // 主要的四个条件：加工单id、子单据id、bom子单据id、工艺路线id
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedure::getParentId), machinProcedure.getParentId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedure::getChildId), machinProcedure.getChildId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedure::getBomChildId), machinProcedure.getBomChildId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedure::getWayProcedureId), machinProcedure.getWayProcedureId());

        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedure::getOrderBy), machinProcedure.getOrderBy() - 1);
        MachinProcedure prevMachinProcedure = getOne(queryWrapper, false);
        // 判断前置工序是否完成
        if (prevMachinProcedure == null) {
            // 前置工序不存在，说明当前工序是第一个工序，可以开始
            return true;
        }
        if (prevMachinProcedure.getState() == MachinProcedureState.PARTIAL_COMPLETION.getKey()
            || prevMachinProcedure.getState() == MachinProcedureState.ALL_COMPLETED.getKey()) {
            // 前置工序部分完成/全部完成，可以开始当前工序
            return true;
        }
        return false;
    }

    @Override
    public List<MachinProcedure> queryListByMachinId(String machinId) {
        if (StrUtil.isEmpty(machinId)) {
            return new ArrayList<>();
        }
        QueryWrapper<MachinProcedure> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedure::getParentId), machinId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(MachinProcedure::getOrderBy));
        return list(queryWrapper);
    }

    /**
     * 根据加工单子单据工序信息id查询同一加工单子单据工序信息
     * @param machinProcedureId 加工单子单据工序信息id
     * @return 同一加工单子单据工序信息
     */
    @Override
    public List<MachinProcedure> querySameListById(String machinProcedureId) {
        if (StrUtil.isEmpty(machinProcedureId)) {
            return new ArrayList<>();
        }
        MachinProcedure procedure = getById(machinProcedureId);
        QueryWrapper<MachinProcedure> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedure::getChildId), procedure.getChildId());
        List<MachinProcedure> list = list(queryWrapper);
        return list;
    }

}
