/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeFlowableServiceImpl;
import com.skyeye.bom.entity.Bom;
import com.skyeye.bom.entity.BomChild;
import com.skyeye.bom.service.BomService;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.MapUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.machin.classenum.MachinFromType;
import com.skyeye.machin.classenum.MachinPickStateEnum;
import com.skyeye.machin.dao.MachinDao;
import com.skyeye.machin.entity.Machin;
import com.skyeye.machin.entity.MachinChild;
import com.skyeye.machin.service.MachinChildService;
import com.skyeye.machin.service.MachinService;
import com.skyeye.machinprocedure.classenum.MachinProcedureState;
import com.skyeye.machinprocedure.entity.MachinProcedure;
import com.skyeye.machinprocedure.entity.MachinProcedureAccept;
import com.skyeye.machinprocedure.entity.MachinProcedureFarm;
import com.skyeye.machinprocedure.service.MachinProcedureFarmService;
import com.skyeye.machinprocedure.service.MachinProcedureService;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.pick.classenum.PickFromType;
import com.skyeye.pick.entity.PatchMaterial;
import com.skyeye.pick.entity.RequisitionMaterial;
import com.skyeye.pick.entity.ReturnMaterial;
import com.skyeye.pick.service.PatchMaterialService;
import com.skyeye.pick.service.RequisitionMaterialService;
import com.skyeye.pick.service.ReturnMaterialService;
import com.skyeye.procedure.entity.WayProcedure;
import com.skyeye.procedure.entity.WayProcedureChild;
import com.skyeye.procedure.service.WayProcedureService;
import com.skyeye.production.classenum.ProductionChildType;
import com.skyeye.production.classenum.ProductionMachinOrderState;
import com.skyeye.production.entity.Production;
import com.skyeye.production.entity.ProductionChild;
import com.skyeye.production.service.ProductionService;
import com.skyeye.util.ErpOrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: MachinServiceImpl
 * @Description: 加工单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:47
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "加工单管理", groupName = "加工单管理", flowable = true)
public class MachinServiceImpl extends SkyeyeFlowableServiceImpl<MachinDao, Machin> implements MachinService {

    @Autowired
    private ProductionService productionService;

    @Autowired
    private MachinChildService machinChildService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private BomService bomService;

    @Autowired
    private WayProcedureService wayProcedureService;

    @Autowired
    private MachinProcedureService machinProcedureService;

    @Autowired
    private MachinProcedureFarmService machinProcedureFarmService;

    @Autowired
    private RequisitionMaterialService requisitionMaterialService;

    @Autowired
    private PatchMaterialService patchMaterialService;

    @Autowired
    private ReturnMaterialService returnMaterialService;

    @Override
    public List<Map<String, Object>> queryPageData(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageData(inputObject);
        iDepmentService.setMationForMap(beans, "departmentId", "departmentMation");
        // 生产计划单
        productionService.setOrderMationByFromId(beans, "fromId", "fromMation");
        return beans;
    }

    @Override
    public void validatorEntity(Machin entity) {
        checkMaterialNorms(entity, false);
    }

    @Override
    public void createPrepose(Machin entity) {
        super.createPrepose(entity);
        entity.setPickState(MachinPickStateEnum.NOT_PICKED.getKey());
    }

    @Override
    public void writePostpose(Machin entity, String userId) {
        // 保存子单据信息
        machinChildService.saveList(entity.getId(), entity.getMachinChildList());
        super.writePostpose(entity, userId);
    }

    @Override
    public void writeChild(Machin entity, String userId) {
        refreshCache(entity.getId());
        super.writeChild(entity, userId);
    }

    @Override
    public Machin getDataFromDb(String id) {
        Machin machin = super.getDataFromDb(id);
        // 查询子单据信息
        machin.setMachinChildList(machinChildService.selectByParentId(machin.getId()));
        return machin;
    }

    @Override
    public List<Machin> getDataFromDb(List<String> idList) {
        List<Machin> machinList = super.getDataFromDb(idList);
        List<String> machinIdList = machinList.stream().map(Machin::getId).collect(Collectors.toList());
        Map<String, List<MachinChild>> listMap = machinChildService.selectMapByParentId(machinIdList);
        machinList.forEach(machin -> {
            machin.setMachinChildList(listMap.get(machin.getId()));
        });
        return machinList;
    }

    @Override
    public Machin selectById(String id) {
        Machin machin = super.selectById(id);
        // 部门信息
        iDepmentService.setDataMation(machin, Machin::getDepartmentId);
        // 配件商品规格信息
        materialService.setDataMation(machin.getMachinChildList(), MachinChild::getMaterialId);
        materialNormsService.setDataMation(machin.getMachinChildList(), MachinChild::getNormsId);
        if (machin.getFromTypeId() == MachinFromType.PRODUCTION.getKey()) {
            // 生产计划单
            productionService.setDataMation(machin, Machin::getFromId);
        }
        // 设置工序/工序信息,车间任务
        Map<String, MachinProcedure> machinProcedureMap = machinProcedureService.queryMachinProcedureMapByMachinId(id);
        Map<String, List<MachinProcedureFarm>> procedureFarmMap = machinProcedureFarmService.queryMachinProcedureFarmMapByMachinId(id);
        // 查询bom方案
        List<String> bomIds = machin.getMachinChildList().stream().filter(bean -> StrUtil.isNotEmpty(bean.getBomId()))
            .map(MachinChild::getBomId).distinct().collect(Collectors.toList());
        Map<String, Bom> bomMap = bomService.selectMapByIds(bomIds);

        // 获取规格对应的所有bom信息
        List<String> normsId = machin.getMachinChildList().stream()
            .map(MachinChild::getNormsId).distinct().collect(Collectors.toList());
        Map<String, List<Bom>> listMap = bomService.getBomListByNormsId(normsId.toArray(new String[]{}));
        machin.getMachinChildList().forEach(machinChild -> {
            machinChild.setBomList(listMap.get(machinChild.getNormsId()));
            // 判断产品的所有工序是否已完成
            Boolean[] checkComplateFlag = new Boolean[]{true};
            // 最后一个工序所完成的数量
            Integer[] lastProcedureNum = new Integer[]{0};
            // 1. 设置加工单子单据bom清单的工序信息
            if (StrUtil.isNotEmpty(machinChild.getBomId())) {
                Bom bom = bomMap.get(machinChild.getBomId());
                bom.getBomChildList().forEach(bomChild -> {
                    WayProcedure bomWayProcedure = resetMachinProcedure(bomChild.getWayProcedureId(), bomChild.getMaterialId(), bomChild.getNormsId(),
                        machinChild.getId(), bomChild.getId(), machinProcedureMap, procedureFarmMap, checkComplateFlag, lastProcedureNum);
                    bomChild.setWayProcedureMation(bomWayProcedure);
                });
                machinChild.setBomMation(bom);
            }

            // 2. 设置加工单子单据的工序信息
            WayProcedure wayProcedure = resetMachinProcedure(machinChild.getWayProcedureId(), machinChild.getMaterialId(), machinChild.getNormsId(),
                machinChild.getId(), StrUtil.EMPTY, machinProcedureMap, procedureFarmMap, checkComplateFlag, lastProcedureNum);
            machinChild.setWayProcedureMation(wayProcedure);
            machinChild.setCheckComplateFlag(checkComplateFlag[0]);
            machinChild.setLastProcedureNum(lastProcedureNum[0]);
        });

        return machin;
    }

    private WayProcedure resetMachinProcedure(String wayProcedureId, String materialId, String normsId, String childId,
                                              String bomChildId, Map<String, MachinProcedure> machinProcedureMap,
                                              Map<String, List<MachinProcedureFarm>> procedureFarmMap, Boolean[] checkComplateFlag,
                                              Integer[] lastProcedureNum) {
        if (StrUtil.isNotEmpty(wayProcedureId)) {
            // 获取工艺信息
            WayProcedure wayProcedure = wayProcedureService.selectById(wayProcedureId);
            wayProcedure.getWorkProcedureList().forEach(wayProcedureChild -> {
                String key = String.format(Locale.ROOT, "%s-%s-%s-%s-%s-%s",
                    childId, bomChildId, materialId, normsId, wayProcedureId, wayProcedureChild.getProcedureId());
                MachinProcedure machinProcedure = machinProcedureMap.get(key);
                if (machinProcedure.getState() == MachinProcedureState.WAIT_STARTED.getKey()) {
                    // 工序未开始
                    checkComplateFlag[0] = false;
                }
                // 设置工序关联的车间任务
                List<MachinProcedureFarm> machinProcedureFarmList = procedureFarmMap.get(machinProcedure.getId());
                // 计算已经完成的数量
                lastProcedureNum[0] = calcMachinProcedureFarmPassNum(machinProcedureFarmList);
                machinProcedure.setMachinProcedureFarmList(machinProcedureFarmList);
                wayProcedureChild.setMachinProcedureMation(machinProcedure);
            });
            return wayProcedure;
        }
        return null;
    }

    private Integer calcMachinProcedureFarmPassNum(List<MachinProcedureFarm> machinProcedureFarmList) {
        if (CollectionUtil.isEmpty(machinProcedureFarmList)) {
            return CommonNumConstants.NUM_ZERO;
        }
        // 获取已经审批通过的工序验收单
        List<MachinProcedureAccept> machinProcedureAcceptList = machinProcedureFarmList.stream()
            .filter(bean -> CollectionUtil.isNotEmpty(bean.getMachinProcedureAcceptList()))
            .flatMap(bean -> bean.getMachinProcedureAcceptList().stream())
            .filter(bean -> StrUtil.equals(bean.getState(), FlowableStateEnum.PASS.getKey())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(machinProcedureAcceptList)) {
            return CommonNumConstants.NUM_ZERO;
        }
        return machinProcedureAcceptList.stream().collect(Collectors.summingInt(MachinProcedureAccept::getQualifiedNum));
    }

    @Override
    public void deletePreExecution(String id) {
        Machin machin = selectById(id);
        if (!FlowableStateEnum.DRAFT.getKey().equals(machin.getState())
            && !FlowableStateEnum.REJECT.getKey().equals(machin.getState())
            && !FlowableStateEnum.REVOKE.getKey().equals(machin.getState())) {
            throw new CustomException("只有草稿、驳回、撤销状态的可删除.");
        }
    }

    @Override
    public void deletePostpose(String id) {
        // 删除子单据信息
        machinChildService.deleteByParentId(id);
    }

    @Override
    public void approvalEndIsSuccess(Machin entity) {
        entity = selectById(entity.getId());
        // 修改来源单据的状态信息
        checkMaterialNorms(entity, true);
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
        QueryWrapper<Machin> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, ids);
        List<Machin> machinList = list(queryWrapper);
        Map<String, Machin> machinMap = machinList.stream()
            .collect(Collectors.toMap(Machin::getId, bean -> bean));
        for (Map<String, Object> bean : beans) {
            if (!MapUtil.checkKeyIsNull(bean, idKey)) {
                Machin entity = machinMap.get(bean.get(idKey).toString());
                if (ObjectUtil.isEmpty(entity)) {
                    continue;
                }
                bean.put(mationKey, entity);
            }
        }
    }

    private void checkMaterialNorms(Machin entity, boolean setData) {
        if (StrUtil.isEmpty(entity.getFromId())) {
            return;
        }
        // 当前加工单的商品数量
        Map<String, Integer> orderNormsNum = entity.getMachinChildList().stream()
            .collect(Collectors.toMap(MachinChild::getNormsId, MachinChild::getOperNumber));
        // 获取同一个来源单据下已经审批通过的加工单的商品信息
        Map<String, Integer> executeNum = calcMaterialNormsNumByFromId(entity.getFromId());
        List<String> inSqlNormsId = new ArrayList<>(executeNum.keySet());
        if (entity.getFromTypeId() == MachinFromType.PRODUCTION.getKey()) {
            // 生产计划单
            Production production = productionService.selectById(entity.getFromId());
            // 获取需要【加工】的商品
            List<ProductionChild> productionChildList = production.getProductionChildList().stream()
                .filter(bean -> bean.getProductionType() == ProductionChildType.SELF_CONTROL.getKey()).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(productionChildList)) {
                throw new CustomException("该生产计划单下未包含需要加工的商品.");
            }
            List<String> fromNormsIds = productionChildList.stream()
                .map(ProductionChild::getNormsId).collect(Collectors.toList());
            // 求差集(生产计划单不包含的商品)
            List<String> diffList = inSqlNormsId.stream()
                .filter(num -> !fromNormsIds.contains(num)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(diffList)) {
                List<MaterialNorms> materialNormsList = materialNormsService.selectByIds(diffList.toArray(new String[]{}));
                List<String> normsNames = materialNormsList.stream().map(MaterialNorms::getName).collect(Collectors.toList());
                throw new CustomException(String.format(Locale.ROOT, "该生产计划单下未包含如下商品规格：【%s】.",
                    Joiner.on(CommonCharConstants.COMMA_MARK).join(normsNames)));
            }
            productionChildList.forEach(productionChild -> {
                // 生产计划单数量 - 当前加工单数量 - 已经审批通过的加工单数量
                Integer surplusNum = ErpOrderUtil.checkOperNumber(productionChild.getOperNumber(), productionChild.getNormsId(),
                    orderNormsNum, executeNum);
                if (setData) {
                    productionChild.setOperNumber(surplusNum);
                }
            });
            if (setData) {
                // 过滤掉剩余数量为0的商品
                productionChildList = productionChildList.stream()
                    .filter(productionChild -> productionChild.getOperNumber() > 0).collect(Collectors.toList());
                // 该生产计划单的商品已经全部下达了加工单
                if (CollectionUtil.isEmpty(productionChildList)) {
                    productionService.editMachinOrderState(production.getId(), ProductionMachinOrderState.COMPLATE_ISSUE.getKey());
                } else {
                    productionService.editMachinOrderState(production.getId(), ProductionMachinOrderState.PARTIAL_ISSUE.getKey());
                }
            }
        }
    }

    @Override
    public void editPickStateById(String id, String pickState) {
        UpdateWrapper<Machin> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Machin::getPickState), pickState);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public Map<String, Integer> calcMaterialNormsNumByFromId(String fromId) {
        QueryWrapper<Machin> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(CommonConstants.ID);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Machin::getFromId), fromId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Machin::getState), FlowableStateEnum.PASS.getKey());
        List<Machin> machinList = list(queryWrapper);
        List<String> ids = machinList.stream().map(Machin::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return new HashMap<>();
        }
        List<MachinChild> machinChildList = machinChildService.selectByParentId(ids);
        Map<String, Integer> collect = machinChildList.stream()
            .collect(Collectors.groupingBy(MachinChild::getNormsId, Collectors.summingInt(MachinChild::getOperNumber)));
        return collect;
    }

    @Override
    public void setMachinMationByFromId(List<Map<String, Object>> beans, String idKey, String mationKey) {
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        List<String> ids = beans.stream().filter(bean -> !MapUtil.checkKeyIsNull(bean, idKey))
            .map(bean -> bean.get(idKey).toString()).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        QueryWrapper<Machin> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, ids);
        List<Machin> machinList = list(queryWrapper);
        Map<String, Machin> machinMap = machinList.stream()
            .collect(Collectors.toMap(Machin::getId, bean -> bean));
        for (Map<String, Object> bean : beans) {
            if (!MapUtil.checkKeyIsNull(bean, idKey)) {
                Machin entity = machinMap.get(bean.get(idKey).toString());
                if (ObjectUtil.isEmpty(entity)) {
                    continue;
                }
                bean.put(mationKey, entity);
            }
        }
    }

    @Override
    public void queryMachinForGanttById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        Machin machin = selectById(id);
        // 生成唯一的主键id
        machin.getMachinChildList().forEach(machinChild -> {
            String machinChildId = ToolUtil.getSurFaceId();
            machinChild.setNewId(machinChildId);
            if (StrUtil.isNotEmpty(machinChild.getBomId())) {
                Bom bom = machinChild.getBomMation();
                Map<String, String> newMaterialIdMap = new HashMap<>();
                bom.getBomChildList().forEach(bomChild -> {
                    newMaterialIdMap.put(bomChild.getMaterialId(), ToolUtil.getSurFaceId());
                });
                bom.getBomChildList().forEach(bomChild -> {
                    String bomChildId = ToolUtil.getSurFaceId();
                    bomChild.setNewId(bomChildId);
                    if (StrUtil.equals(bomChild.getParentId(), CommonNumConstants.NUM_ZERO.toString())) {
                        bomChild.setNewParentId(machinChildId);
                    } else {
                        bomChild.setNewParentId(newMaterialIdMap.get(bomChild.getMaterialId()));
                    }
                    if (ObjectUtil.isNotEmpty(bomChild.getWayProcedureMation())) {
                        bomChild.getWayProcedureMation().getWorkProcedureList().forEach(wayProcedureChild -> {
                            wayProcedureChild.setNewId(ToolUtil.getSurFaceId());
                        });
                    }
                });
            }

            if (ObjectUtil.isNotEmpty(machinChild.getWayProcedureMation())) {
                machinChild.getWayProcedureMation().getWorkProcedureList().forEach(wayProcedureChild -> {
                    wayProcedureChild.setNewId(ToolUtil.getSurFaceId());
                });
            }
        });

        Map<String, Object> mathinTime = new HashMap<>();
        List<String> dateArray = new ArrayList<>();
        machin.getMachinChildList().forEach(machinChild -> {
            dateArray.add(machinChild.getPlanStartTime());
            dateArray.add(machinChild.getPlanEndTime());
        });
        showResult(dateArray.toArray(new String[]{}), mathinTime);
        // 构造数据
        List<Map<String, Object>> node = new ArrayList<>();
        List<Map<String, Object>> link = new ArrayList<>();
        machin.getMachinChildList().forEach(machinChild -> {
            // 产品信息
            Map<String, Object> materialNode = getNode(machinChild.getNewId(), machinChild.getMaterialMation().getName(), CommonNumConstants.NUM_ZERO.toString(),
                machinChild.getPlanStartTime(), machinChild.getPlanEndTime(), true, machinChild);
            node.add(materialNode);
            resetMachinProcedure(machinChild.getWayProcedureMation(), node, link, machinChild.getNewId(), mathinTime, materialNode);
            // bom清单
            if (StrUtil.isNotEmpty(machinChild.getBomId())) {
                Bom bom = machinChild.getBomMation();
                bom.getBomChildList().forEach(bomChild -> {
                    Map<String, Object> childMaterialNode = getNode(bomChild.getNewId(), bomChild.getMaterialMation().getName(), bomChild.getNewParentId(),
                        machinChild.getPlanStartTime(), machinChild.getPlanEndTime(), true, bomChild);
                    node.add(childMaterialNode);
                    link.add(getLink(bomChild.getNewParentId(), bomChild.getNewId()));
                    link.get(link.size() - 1).put("color", "#009688");
                    resetMachinProcedure(bomChild.getWayProcedureMation(), node, link, bomChild.getNewId(), mathinTime, childMaterialNode);
                });
            }
        });

        node.forEach(item -> {
            if (!MapUtil.checkKeyIsNull(item, "start_date")) {
                dateArray.add(item.get("start_date").toString());
            }
            if (!MapUtil.checkKeyIsNull(item, "end_date")) {
                dateArray.add(item.get("end_date").toString());
            }
        });
        showResult(dateArray.toArray(new String[]{}), mathinTime);

        Map<String, Object> retult = new HashMap<>();
        retult.put("node", node);
        retult.put("link", link);
        retult.put("mathinTime", mathinTime);
        outputObject.setBean(retult);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private static void showResult(String[] dateArray, Map<String, Object> mathinTime) {
        Map<String, Integer> dateMap = new TreeMap<>();
        int i, arrayLen;
        arrayLen = dateArray.length;
        for (i = 0; i < arrayLen; i++) {
            String dateKey = dateArray[i];
            if (dateMap.containsKey(dateKey)) {
                int value = dateMap.get(dateKey) + 1;
                dateMap.put(dateKey, value);
            } else {
                dateMap.put(dateKey, 1);
            }
        }
        Set<String> keySet = dateMap.keySet();
        String[] sorttedArray = new String[keySet.size()];
        Iterator<String> iter = keySet.iterator();
        int index = 0;
        while (iter.hasNext()) {
            String key = iter.next();
            sorttedArray[index++] = key;
        }
        int sorttedArrayLen = sorttedArray.length;
        mathinTime.put("start_date", sorttedArray[0]);
        mathinTime.put("end_date", sorttedArray[sorttedArrayLen - 1]);
    }

    private void resetMachinProcedure(WayProcedure wayProcedureMation, List<Map<String, Object>> node,
                                      List<Map<String, Object>> link, String parentId, Map<String, Object> mathinTime,
                                      Map<String, Object> materialNode) {
        if (ObjectUtil.isEmpty(wayProcedureMation)) {
            return;
        }
        String prveId = parentId;
        // 获取工艺信息
        for (WayProcedureChild wayProcedureChild : wayProcedureMation.getWorkProcedureList()) {
            MachinProcedure machinProcedure = wayProcedureChild.getMachinProcedureMation();
            if (ObjectUtil.isEmpty(machinProcedure)) {
                node.add(getNode(wayProcedureChild.getNewId(), wayProcedureChild.getProcedureMation().getName(), parentId,
                    mathinTime.get("start_date").toString(), mathinTime.get("end_date").toString(), false, machinProcedure));
                node.get(node.size() - 1).put("notAddWorkProcedure", true);
            } else {
                node.add(getNode(wayProcedureChild.getNewId(), wayProcedureChild.getProcedureMation().getName(), parentId,
                    machinProcedure.getPlanStartTime(), machinProcedure.getPlanEndTime(), false, machinProcedure));
                List<String> dateArray = new ArrayList<>();
                if (StrUtil.isNotEmpty(machinProcedure.getPlanStartTime())) {
                    dateArray.add(machinProcedure.getPlanStartTime());
                }
                if (StrUtil.isNotEmpty(machinProcedure.getPlanEndTime())) {
                    dateArray.add(machinProcedure.getPlanEndTime());
                }
                dateArray.add(materialNode.get("start_date").toString());
                dateArray.add(materialNode.get("end_date").toString());
                showResult(dateArray.toArray(new String[]{}), materialNode);
            }
            link.add(getLink(prveId, wayProcedureChild.getNewId()));
            link.get(link.size() - 1).put("color", "#FFB800");
            prveId = wayProcedureChild.getNewId();
        }
    }

    private Map<String, Object> getNode(String id, String name, String parentId, String startTime, String endTime, Boolean type, Object object) {
        Map<String, Object> retult = new HashMap<>();
        retult.put("id", id);
        retult.put("text", name);
        retult.put("parent", parentId);
        if (type) {
            retult.put("types", "project");
        }
        retult.put("start_date", startTime);
        retult.put("end_date", endTime);
        retult.put("open", true);
        retult.put("data", object);
        return retult;
    }

    private Map<String, Object> getLink(String id, String parentId) {
        Map<String, Object> retult = new HashMap<>();
        retult.put("id", id + "CC");
        retult.put("source", id);
        retult.put("target", parentId);
        retult.put("type", CommonNumConstants.NUM_ZERO);
        return retult;
    }

    @Override
    public void queryMachinTransRequestById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        Machin machin = selectById(id);
        // 获取需要的原材料
        List<BomChild> needRawMaterial = getNeedRawMaterial(machin);
        // 获取已经领料的数量
        Map<String, Integer> requestNum = requisitionMaterialService.calcMaterialNormsNumByFromId(id);
        // 获取已经补料的数量
        Map<String, Integer> patchNum = patchMaterialService.calcMaterialNormsNumByFromId(id);
        // 设置未申领的原材料信息
        needRawMaterial.forEach(machinChild -> {
            // 设置未下达领料单/补料单的商品数量-----原材料需求数量 - 已领料的数量 - 已补料的数量
            Integer surplusNum = machinChild.getNeedNum()
                - (requestNum.containsKey(machinChild.getNormsId()) ? requestNum.get(machinChild.getNormsId()) : 0)
                - (patchNum.containsKey(machinChild.getNormsId()) ? patchNum.get(machinChild.getNormsId()) : 0);
            if (surplusNum < 0) {
                // 超出需求数量，设置为0，方便补料时进行补料
                surplusNum = 0;
            }
            // 设置未下达领料单/补料单的数量
            machinChild.setNeedNum(surplusNum);
        });
        // 不需要过滤掉数量为0的商品信息，方便补料时进行补料
        machin.setNeedRawMaterialList(needRawMaterial);

        outputObject.setBean(machin);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 获取需要的原材料
     *
     * @param machin 加工单
     * @return
     */
    private List<BomChild> getNeedRawMaterial(Machin machin) {
        List<BomChild> needRawMaterial = new ArrayList<>();
        for (MachinChild machinChild : machin.getMachinChildList()) {
            if (StrUtil.isNotEmpty(machinChild.getBomId())) {
                Bom bom = machinChild.getBomMation();
                List<BomChild> bomChildList = new ArrayList<>(bom.getBomChildList());
                bomChildList.forEach(bomChild -> {
                    // 计算需要的原材料数量 = 订单数量 / bom方案制造的数量 * bom子项的需求数量
                    String divide = CalculationUtil.divide(String.valueOf(machinChild.getOperNumber()), String.valueOf(bom.getMakeNum()), CommonNumConstants.NUM_TWO);
                    divide = CalculationUtil.multiply(divide, String.valueOf(bomChild.getNeedNum()), CommonNumConstants.NUM_ZERO);
                    bomChild.setNeedNum(Integer.parseInt(divide));
                });
                needRawMaterial.addAll(bomChildList);
            }
        }
        // 根据规格id去重并合并所需数量
        Map<String, BomChild> needRawMaterialMap = new HashMap<>();
        needRawMaterial.forEach(bomChild -> {
            if (needRawMaterialMap.containsKey(bomChild.getNormsId())) {
                BomChild existBomChild = needRawMaterialMap.get(bomChild.getNormsId());
                existBomChild.setNeedNum(existBomChild.getNeedNum() + bomChild.getNeedNum());
            } else {
                needRawMaterialMap.put(bomChild.getNormsId(), bomChild);
            }
        });
        needRawMaterial = new ArrayList<>(needRawMaterialMap.values());
        return needRawMaterial;
    }

    @Override
    public void insertMachinToPickRequest(InputObject inputObject, OutputObject outputObject) {
        RequisitionMaterial requisitionMaterial = inputObject.getParams(RequisitionMaterial.class);
        // 获取加工单状态
        Machin order = selectById(requisitionMaterial.getId());
        if (ObjectUtil.isEmpty(order)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以进行下达领料单/补料单
        if (FlowableStateEnum.PASS.getKey().equals(order.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            requisitionMaterial.setFromId(requisitionMaterial.getId());
            requisitionMaterial.setFromTypeId(PickFromType.MACHIN.getKey());
            requisitionMaterial.setId(StrUtil.EMPTY);
            requisitionMaterialService.createEntity(requisitionMaterial, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达领料单.");
        }
    }

    @Override
    public void insertMachinToPickPatch(InputObject inputObject, OutputObject outputObject) {
        PatchMaterial patchMaterial = inputObject.getParams(PatchMaterial.class);
        // 获取加工单状态
        Machin order = selectById(patchMaterial.getId());
        if (ObjectUtil.isEmpty(order)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以进行下达领料单/补料单
        if (FlowableStateEnum.PASS.getKey().equals(order.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            patchMaterial.setFromId(patchMaterial.getId());
            patchMaterial.setFromTypeId(PickFromType.MACHIN.getKey());
            patchMaterial.setId(StrUtil.EMPTY);
            patchMaterialService.createEntity(patchMaterial, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达补料单.");
        }
    }

    @Override
    public void queryMachinTransReturnById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        Machin machin = selectById(id);
        // 获取已经领料的数量
        Map<String, Integer> requestNum = requisitionMaterialService.calcMaterialNormsNumByFromId(id);
        // 获取已经补料的数量
        Map<String, Integer> patchNum = patchMaterialService.calcMaterialNormsNumByFromId(id);
        // 获取已经退料的数量
        Map<String, Integer> returnNum = returnMaterialService.calcMaterialNormsNumByFromId(id);
        machin.getMachinChildList().forEach(machinChild -> {
            // 设置未下达退料单的商品数量-----已领料的数量 + 已补料的数量 - 订单数量 - 已退料的数量
            Integer surplusNum = (requestNum.containsKey(machinChild.getNormsId()) ? requestNum.get(machinChild.getNormsId()) : 0)
                + (patchNum.containsKey(machinChild.getNormsId()) ? patchNum.get(machinChild.getNormsId()) : 0)
                - machinChild.getLastProcedureNum()
                - (returnNum.containsKey(machinChild.getNormsId()) ? returnNum.get(machinChild.getNormsId()) : 0);
            // 设置未下达退料单的数量
            machinChild.setOperNumber(surplusNum);
        });
        outputObject.setBean(machin);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void insertMachinToPickReturn(InputObject inputObject, OutputObject outputObject) {
        ReturnMaterial returnMaterial = inputObject.getParams(ReturnMaterial.class);
        // 获取加工单状态
        Machin order = selectById(returnMaterial.getId());
        if (ObjectUtil.isEmpty(order)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以进行下达退料单
        if (FlowableStateEnum.PASS.getKey().equals(order.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            returnMaterial.setFromId(returnMaterial.getId());
            returnMaterial.setFromTypeId(PickFromType.MACHIN.getKey());
            returnMaterial.setId(StrUtil.EMPTY);
            returnMaterialService.createEntity(returnMaterial, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达退料单.");
        }
    }

    @Override
    public boolean checkIsLastProcedure(String machinId, String childId, String bomChildId, String wayProcedureId, String materialId, String normsId, String procedureId) {
        if (StrUtil.isEmpty(machinId) || StrUtil.isEmpty(childId)
            || StrUtil.isEmpty(materialId) || StrUtil.isEmpty(normsId) || StrUtil.isEmpty(procedureId)) {
            return false;
        }
        Machin machin = selectById(machinId);
        if (ObjectUtil.isEmpty(machin)) {
            return false;
        }
        // 获取子单据信息
        MachinChild machinChild = machin.getMachinChildList().stream().filter(m -> m.getId().equals(childId)).findFirst().orElse(null);
        if (ObjectUtil.isEmpty(machinChild)) {
            return false;
        }
        if (ObjectUtil.isNotEmpty(machinChild.getWayProcedureMation())) {
            // 判断是否为最后一道工序
            if (StrUtil.isNotEmpty(bomChildId)) {
                // 如果bom子件清单的id不为空 && 加工单子单据有绑定工艺，那么就一定不是最后一道工序
                return false;
            }
            int lastIndex = machinChild.getWayProcedureMation().getWorkProcedureList().size() - 1;
            WayProcedureChild wayProcedureChild = machinChild.getWayProcedureMation().getWorkProcedureList().get(lastIndex);
            if (StrUtil.equals(machinChild.getMaterialId(), materialId) && StrUtil.equals(machinChild.getNormsId(), normsId)
                && StrUtil.equals(machinChild.getWayProcedureId(), wayProcedureId) && StrUtil.equals(wayProcedureChild.getProcedureId(), procedureId)) {
                return true;
            } else {
                return false;
            }
        }

        if (StrUtil.isNotEmpty(machinChild.getBomId())) {
            Bom bom = machinChild.getBomMation();
            // 获取bom子件清单中绑定了工艺的id
            List<BomChild> bomChildList = bom.getBomChildList().stream().filter(bc -> StrUtil.isNotEmpty(bc.getWayProcedureId())).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(bomChildList)) {
                // 如果没有绑定工艺，那么就一定是最后一道工序
                return true;
            }
            int lastIndex = bomChildList.size() - 1;
            BomChild bomChild = bomChildList.get(lastIndex);
            if (StrUtil.equals(machinChild.getMaterialId(), materialId) && StrUtil.equals(machinChild.getNormsId(), normsId)
                && StrUtil.equals(machinChild.getWayProcedureId(), wayProcedureId) && StrUtil.equals(bomChild.getId(), bomChildId)) {
                // 判断是否为最后一道工序
                int lastProcedureIndex = machinChild.getWayProcedureMation().getWorkProcedureList().size() - 1;
                WayProcedureChild wayProcedureChild = machinChild.getWayProcedureMation().getWorkProcedureList().get(lastProcedureIndex);
                if (StrUtil.equals(wayProcedureChild.getProcedureId(), procedureId)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        return false;
    }
}
