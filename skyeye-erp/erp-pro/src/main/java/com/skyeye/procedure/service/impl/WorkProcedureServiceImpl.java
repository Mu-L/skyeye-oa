/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.procedure.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipment.entity.Equipment;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.exception.CustomException;
import com.skyeye.farm.entity.Farm;
import com.skyeye.farm.service.FarmService;
import com.skyeye.procedure.dao.WorkProcedureDao;
import com.skyeye.procedure.entity.WorkProcedure;
import com.skyeye.procedure.entity.WorkProcedureEquipment;
import com.skyeye.procedure.service.WorkProcedureEquipmentService;
import com.skyeye.procedure.service.WorkProcedureService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: WorkProcedureServiceImpl
 * @Description: 工序信息管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:49
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "工序管理", groupName = "工序管理", allowDynamicAttrKey = false)
public class WorkProcedureServiceImpl extends SkyeyeBusinessServiceImpl<WorkProcedureDao, WorkProcedure> implements WorkProcedureService {

    @Autowired
    private WorkProcedureEquipmentService workProcedureEquipmentService;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private FarmService farmService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iAuthUserService.setMationForMap(beans, "chargeId", "chargeMation");
        return beans;
    }

    @Override
    protected void validatorEntity(WorkProcedure entity) {
        QueryWrapper<WorkProcedure> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper ->
            wrapper.eq(MybatisPlusUtil.toColumns(WorkProcedure::getName), entity.getName())
                .or().eq(MybatisPlusUtil.toColumns(WorkProcedure::getNumber), entity.getNumber()));
        queryWrapper.eq(MybatisPlusUtil.toColumns(WorkProcedure::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        WorkProcedure checkWorkProcedure = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(checkWorkProcedure)) {
            throw new CustomException("this 【name/number】 is exist.");
        }
        // 校验设备的重复性
        List<String> equipmentIdList = entity.getWorkProcedureEquipmentList().stream()
            .filter(bean -> StrUtil.isNotEmpty(bean.getEquipmentId()))
            .map(WorkProcedureEquipment::getEquipmentId).distinct().collect(Collectors.toList());
        if (equipmentIdList.size() != entity.getWorkProcedureEquipmentList().size()) {
            throw new CustomException("存在相同的设备信息，请确认.");
        }
    }

    @Override
    public void writePostpose(WorkProcedure entity, String userId) {
        super.writePostpose(entity, userId);
        // 保存设备清单信息
        workProcedureEquipmentService.saveList(entity.getId(), entity.getWorkProcedureEquipmentList());
    }

    @Override
    public WorkProcedure getDataFromDb(String id) {
        WorkProcedure workProcedure = super.getDataFromDb(id);
        // 查询设备清单信息
        workProcedure.setWorkProcedureEquipmentList(workProcedureEquipmentService.selectByParentId(id));
        return workProcedure;
    }

    @Override
    public List<WorkProcedure> getDataFromDb(List<String> idList) {
        List<WorkProcedure> workProcedureList = super.getDataFromDb(idList);
        // 查询工序信息
        List<String> ids = workProcedureList.stream().map(WorkProcedure::getId).collect(Collectors.toList());
        Map<String, List<WorkProcedureEquipment>> workProcedureEquipmentMap = workProcedureEquipmentService.selectByParentId(ids);
        workProcedureList.forEach(workProcedure -> {
            String id = workProcedure.getId();
            workProcedure.setWorkProcedureEquipmentList(workProcedureEquipmentMap.get(id));
        });
        return workProcedureList;
    }

    @Override
    public WorkProcedure selectById(String id) {
        WorkProcedure workProcedure = super.selectById(id);
        iAuthUserService.setDataMation(workProcedure, WorkProcedure::getChargeId);
        // 设置设备信息
        equipmentService.setDataMation(workProcedure.getWorkProcedureEquipmentList(), WorkProcedureEquipment::getEquipmentId);
        return workProcedure;
    }

    @Override
    public List<WorkProcedure> selectByIds(String... ids) {
        List<WorkProcedure> workProcedures = super.selectByIds(ids);
        iAuthUserService.setDataMation(workProcedures, WorkProcedure::getChargeId);
        // 设置设备信息
        List<String> equipmentIds = workProcedures.stream()
            .filter(bean -> CollectionUtil.isNotEmpty(bean.getWorkProcedureEquipmentList()))
            .flatMap(norms -> norms.getWorkProcedureEquipmentList().stream()).map(WorkProcedureEquipment::getEquipmentId).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(equipmentIds)) {
            Map<String, Equipment> equipmentMap = equipmentService.selectMapByIds(equipmentIds);
            workProcedures.forEach(workProcedure -> {
                if (CollectionUtil.isEmpty(workProcedure.getWorkProcedureEquipmentList())) {
                    return;
                }
                workProcedure.getWorkProcedureEquipmentList().forEach(workProcedureEquipment -> {
                    workProcedureEquipment.setEquipmentMation(equipmentMap.get(workProcedureEquipment.getEquipmentId()));
                });
            });
        }
        return workProcedures;
    }

    /**
     * 查询所有工序列表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryAllWorkProcedureList(InputObject inputObject, OutputObject outputObject) {
        List<WorkProcedure> beans = queryAllData();
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    @Override
    public void queryExecuteFarmByWorkProcedureId(InputObject inputObject, OutputObject outputObject) {
        String workProcedureId = inputObject.getParams().get("workProcedureId").toString();
        // 查询设备信息，然后根据设备信息查询车间信息，如果设备信息为空/车间信息为空，则返回所有车间列表
        List<Farm> farmList = queryExecuteFarmByWorkProcedureId(workProcedureId);
        if (CollectionUtil.isEmpty(farmList)) {
            farmList = farmService.queryEnabledFarmList();
        }
        outputObject.setBeans(farmList);
        outputObject.settotal(farmList.size());
    }

    @Override
    public List<Farm> queryExecuteFarmByWorkProcedureId(String workProcedureId) {
        // 查询设备信息
        List<WorkProcedureEquipment> workProcedureEquipmentList = workProcedureEquipmentService.selectByParentId(workProcedureId);
        if (CollectionUtil.isEmpty(workProcedureEquipmentList)) {
            return CollectionUtil.newArrayList();
        }
        List<String> equipmentIdList = workProcedureEquipmentList.stream()
            .filter(bean -> StrUtil.isNotEmpty(bean.getEquipmentId()))
            .map(WorkProcedureEquipment::getEquipmentId).distinct().collect(Collectors.toList());
        List<Equipment> equipment = equipmentService.selectByIds(equipmentIdList.toArray(new String[]{}));
        // 查询车间信息
        List<String> farmIdList = equipment.stream().map(Equipment::getFarmId).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(farmIdList)) {
            return CollectionUtil.newArrayList();
        }
        List<Farm> farmList = farmService.selectByIds(farmIdList.toArray(new String[]{}));
        return farmList;
    }

}
