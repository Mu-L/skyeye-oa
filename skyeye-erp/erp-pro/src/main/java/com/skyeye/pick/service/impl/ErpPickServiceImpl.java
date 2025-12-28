/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pick.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.MapUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.service.ErpDepotService;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.farm.service.FarmService;
import com.skyeye.machin.service.MachinService;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.pick.entity.PickChild;
import com.skyeye.pick.entity.common.Pick;
import com.skyeye.pick.service.ErpPickService;
import com.skyeye.pick.service.PickChildService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ErpPickServiceImpl
 * @Description: 领料申请单、补料申请单、退料申请单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
public class ErpPickServiceImpl<D extends SkyeyeBaseMapper<T>, T extends Pick> extends SkyeyeBusinessServiceImpl<D, T> implements ErpPickService<T> {

    @Autowired
    private MachinService machinService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private PickChildService pickChildService;

    @Autowired
    private ErpDepotService erpDepotService;

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private FarmService farmService;

    @Override
    public QueryWrapper<T> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<T> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Pick::getIdKey), getServiceClassName());
        if (StrUtil.equals(commonPageInfo.getType(), "department")) {
            // 我所在部门
            String departmentId = InputObject.getLogParamsStatic().get("departmentId").toString();
            queryWrapper.eq(MybatisPlusUtil.toColumns(Pick::getDepartmentId), departmentId);
        } else if (StrUtil.equals(commonPageInfo.getType(), "farm")) {
            // 指定车间
            String departmentId = InputObject.getLogParamsStatic().get("departmentId").toString();
            queryWrapper.eq(MybatisPlusUtil.toColumns(Pick::getDepartmentId), departmentId);
            queryWrapper.eq(MybatisPlusUtil.toColumns(Pick::getFarmId), commonPageInfo.getObjectId());
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        machinService.setMationForMap(beans, "fromId", "fromMation");
        farmService.setMationForMap(beans, "farmId", "farmMation");
        iDepmentService.setMationForMap(beans, "departmentId", "departmentMation");
        return beans;
    }

    @Override
    public void createPrepose(T entity) {
        super.createPrepose(entity);
        entity.setIdKey(getServiceClassName());
        // 设置商品为使用中
        entity.getPickChildList().forEach(pickChild -> {
            materialService.setUsed(pickChild.getMaterialId());
        });
    }

    @Override
    public void writePostpose(T entity, String userId) {
        pickChildService.saveList(entity.getId(), entity.getPickChildList(), userId);
        super.writePostpose(entity, userId);
    }

    @Override
    public T getDataFromDb(String id) {
        T pick = super.getDataFromDb(id);
        List<PickChild> pickChildList = pickChildService.selectByParentId(id);
        pick.setPickChildList(pickChildList);
        return pick;
    }

    @Override
    public T selectById(String id) {
        T pick = super.selectById(id);
        // 仓库
        erpDepotService.setDataMation(pick.getPickChildList(), PickChild::getDepotId);
        // 加工单
        machinService.setDataMation(pick, T::getFromId);

        // 获取产品/规格信息
        materialService.setDataMation(pick.getPickChildList(), PickChild::getMaterialId);
        materialNormsService.setDataMation(pick.getPickChildList(), PickChild::getNormsId);

        // 部门
        iDepmentService.setDataMation(pick, T::getDepartmentId);
        // 车间
        farmService.setDataMation(pick, T::getFarmId);
        return pick;
    }

    @Override
    public void deletePostpose(String id) {
        // 删除子单据信息
        pickChildService.deleteByParentId(id);
    }

    @Override
    public void setOrderMationByFromId(List<Map<String, Object>> beans, String idKey, String mationKey) {
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        List<String> ids = beans.stream().filter(bean -> !MapUtil.checkKeyIsNull(bean, idKey))
            .map(bean -> bean.get(idKey).toString()).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, ids);
        List<T> entityList = list(queryWrapper);
        Map<String, T> entityMap = entityList.stream().collect(Collectors.toMap(Pick::getId, bean -> bean));
        for (Map<String, Object> bean : beans) {
            if (!MapUtil.checkKeyIsNull(bean, idKey)) {
                T entity = entityMap.get(bean.get(idKey).toString());
                if (ObjectUtil.isEmpty(entity)) {
                    continue;
                }
                bean.put(mationKey, entity);
            }
        }
    }

    @Override
    public void editOtherState(String id, Integer otherState) {
        UpdateWrapper<T> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Pick::getOtherState), otherState);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public Map<String, String> calcMaterialNormsNumByFromId(String fromId) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(CommonConstants.ID);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Pick::getFromId), fromId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Pick::getIdKey), getServiceClassName());
        // 只查询审批通过的单据
        queryWrapper.eq(MybatisPlusUtil.toColumns(Pick::getState), FlowableStateEnum.PASS.getKey());
        List<T> entityList = list(queryWrapper);
        List<String> ids = entityList.stream().map(Pick::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return new HashMap<>();
        }
        List<PickChild> pickChildList = pickChildService.queryPickChildListByParentIds(ids);
        Map<String, String> collect = pickChildList.stream()
            .collect(Collectors.groupingBy(
                PickChild::getNormsId,
                Collectors.reducing(
                    CommonNumConstants.NUM_ZERO.toString(),
                    PickChild::getNeedNum,
                    (sum, needNum) -> CalculationUtil.add(
                        ErpConstants.NUM_AFTER_DOT,
                        StrUtil.isEmpty(sum) ? CommonNumConstants.NUM_ZERO.toString() : sum,
                        StrUtil.isEmpty(needNum) ? CommonNumConstants.NUM_ZERO.toString() : needNum
                    )
                )
            ));
        return collect;
    }

}
