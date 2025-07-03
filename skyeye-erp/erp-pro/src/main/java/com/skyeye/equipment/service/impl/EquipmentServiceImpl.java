/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipment.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
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
import com.skyeye.equipment.dao.EquipmentDao;
import com.skyeye.equipment.entity.Equipment;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.exception.CustomException;
import com.skyeye.farm.service.FarmService;
import com.skyeye.rest.project.service.IProProjectService;
import com.skyeye.whole.entity.WholeOrderOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: EquipmentServiceImpl
 * @Description: 设备管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/17 21:15
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "设备管理", groupName = "设备管理")
public class EquipmentServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentDao, Equipment> implements EquipmentService {

    @Autowired
    private FarmService farmService;

    @Autowired
    private IProProjectService iProProjectService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        farmService.setMationForMap(beans, "farmId", "farmMation");
        iProProjectService.setMationForMap(beans, "projectId", "projectMation");
        return beans;
    }

    @Override
    protected void validatorEntity(Equipment entity) {
        super.validatorEntity(entity);
        if(Double.parseDouble(entity.getUnitPrice())<=0){
            throw new CustomException("单价不能为0或负数");
        }
    }

    @Override
    public Equipment selectById(String id) {
        Equipment equipment = super.selectById(id);
        farmService.setDataMation(equipment, Equipment::getFarmId);
        iProProjectService.setDataMation(equipment, Equipment::getProjectId);
        return equipment;
    }

    @Override
    public void queryAllEquipmentList(InputObject inputObject, OutputObject outputObject) {
        List<Equipment> equipmentList = list();
        outputObject.setBeans(equipmentList);
        outputObject.settotal(equipmentList.size());
    }

    @Override
    public void queryLastMonthEquipmentCost(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<Equipment> queryWrapper = new QueryWrapper<>();
        //获取上个月日期
        String lastMonth = DateUtil.getLastMonthDate();
        queryWrapper.apply("DATE_FORMAT("+MybatisPlusUtil.toColumns(Equipment::getBuyTime)+", '%Y-%m') = ?",lastMonth);
        queryWrapper.isNotNull(MybatisPlusUtil.toColumns(Equipment::getProjectId));
        List<Equipment> bean = list(queryWrapper);

        if(CollectionUtil.isEmpty(bean)){
            return;
        }
           // 根据projectId分组
        Map<String, List<Equipment>> groupMap = bean.stream().collect(Collectors.groupingBy(Equipment::getProjectId));
        List<Map<String,Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<Equipment>> entry : groupMap.entrySet()) {
            Map<String,Object> map = new HashMap<>();
            String price = String.valueOf(CommonNumConstants.NUM_ZERO);
            map.put("projectId",entry.getKey());
            for (Equipment equipment : entry.getValue()) {
                price = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                        StrUtil.isEmpty(equipment.getUnitPrice()) ? "0" : equipment.getUnitPrice(),
                        price);
            }
            map.put("price",price);
            result.add(map);
        }
        outputObject.setBeans(result);
    }

    @Override
    public void queryLastMonthEquipmentList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        if(StrUtil.isEmpty(commonPageInfo.getObjectId())){
            throw new CustomException("项目id不能为空");
        }
        String lastMonth = DateUtil.getLastMonthDate();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<Equipment> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply("DATE_FORMAT("+MybatisPlusUtil.toColumns(Equipment::getBuyTime)+", '%Y-%m') = ?",lastMonth);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Equipment::getProjectId),commonPageInfo.getObjectId());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Equipment::getUnitPrice));
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Equipment::getCreateTime));
        List<Equipment> bean = list(queryWrapper);
        farmService.setDataMation(bean, Equipment::getFarmId);
        iProProjectService.setDataMation(bean, Equipment::getProjectId);
        outputObject.setBeans(bean);
        outputObject.settotal(page.getTotal());
    }

}
