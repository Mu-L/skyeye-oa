/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pick.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.business.service.impl.SkyeyeErpOrderServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotPutFromType;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.depot.classenum.DepotPutState;
import com.skyeye.depot.entity.DepotPut;
import com.skyeye.depot.service.DepotPutService;
import com.skyeye.entity.ErpOrderItem;
import com.skyeye.exception.CustomException;
import com.skyeye.farm.service.FarmService;
import com.skyeye.machinprocedure.classenum.MachinProcedureAcceptChildType;
import com.skyeye.material.classenum.MaterialInOrderType;
import com.skyeye.material.classenum.MaterialNormsCodeInDepot;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.entity.MaterialNormsCode;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.pick.classenum.OutLetState;
import com.skyeye.pick.classenum.PickNormsCodeUseState;
import com.skyeye.pick.classenum.ReturnPutFromType;
import com.skyeye.pick.dao.ReturnPutDao;
import com.skyeye.pick.entity.PickChild;
import com.skyeye.pick.entity.ReturnMaterial;
import com.skyeye.pick.entity.ReturnPut;
import com.skyeye.pick.service.DepartmentStockService;
import com.skyeye.pick.service.ReturnMaterialService;
import com.skyeye.pick.service.ReturnPutService;
import com.skyeye.util.ErpOrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ReturnPutServiceImpl
 * @Description: 退料入库单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/26 21:05
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "退料入库单", groupName = "物料单", flowable = true)
public class ReturnPutServiceImpl extends SkyeyeErpOrderServiceImpl<ReturnPutDao, ReturnPut> implements ReturnPutService {

    @Autowired
    private ReturnMaterialService returnMaterialService;

    @Autowired
    private DepartmentStockService departmentStockService;

    @Autowired
    private DepotPutService depotPutService;

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private FarmService farmService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        // 设置退料需求单
        returnMaterialService.setOrderMationByFromId(beans, "fromId", "fromMation");
        return beans;
    }

    @Override
    public void validatorEntity(ReturnPut entity) {
        entity.setOtherState(DepotPutState.NEED_PUT.getKey());
        checkMaterialNorms(entity, false);
        checkNormsCodeAndSave(entity, true);
    }

    @Override
    public void createPrepose(ReturnPut entity) {
        super.createPrepose(entity);
        entity.setType(DepotPutOutType.PUT.getKey());
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
        });
    }

    @Override
    public void writePostpose(ReturnPut entity, String userId) {
        // 保存单据子表关联的条形码编号信息
        super.saveErpOrderItemCode(entity);
        super.writePostpose(entity, userId);
    }

    @Override
    public void deletePostpose(String id) {
        // 删除关联的编码信息
        super.deleteErpOrderItemCodeById(id);
    }

    @Override
    public ReturnPut getDataFromDb(String id) {
        ReturnPut returnPut = super.getDataFromDb(id);
        // 查询单据子表关联的条形码编号信息
        queryErpOrderItemCodeById(returnPut);
        return returnPut;
    }

    @Override
    public ReturnPut selectById(String id) {
        ReturnPut returnPut = super.selectById(id);
        // 部门
        iDepmentService.setDataMation(returnPut, ReturnPut::getDepartmentId);
        // 车间
        farmService.setDataMation(returnPut, ReturnPut::getFarmId);
        if (returnPut.getFromTypeId() == ReturnPutFromType.RETURN_PUT.getKey()) {
            // 退料需求单
            returnMaterialService.setDataMation(returnPut, ReturnPut::getFromId);
        }
        return returnPut;
    }

    private void checkMaterialNorms(ReturnPut entity, boolean setData) {
        if (StrUtil.isEmpty(entity.getFromId())) {
            return;
        }
        // 当前退料入库单的商品数量
        Map<String, String> orderNormsNum = entity.getErpOrderItemList().stream()
            .collect(Collectors.toMap(ErpOrderItem::getNormsId, ErpOrderItem::getOperNumber));
        // 获取已经下达退料入库单的商品信息
        Map<String, String> executeNum = calcMaterialNormsNumByFromId(entity.getFromId());
        List<String> inSqlNormsId = new ArrayList<>(executeNum.keySet());
        if (entity.getFromTypeId() == ReturnPutFromType.RETURN_PUT.getKey()) {
            // 退料需求单
            checkAndUpdateFromState(entity, setData, orderNormsNum, executeNum, inSqlNormsId);
        }
    }

    private void checkAndUpdateFromState(ReturnPut entity, boolean setData, Map<String, String> orderNormsNum, Map<String, String> executeNum, List<String> inSqlNormsId) {
        ReturnMaterial returnMaterial = returnMaterialService.selectById(entity.getFromId());
        if (CollectionUtil.isEmpty(returnMaterial.getPickChildList())) {
            throw new CustomException("该退料单下未包含商品.");
        }
        List<String> fromNormsIds = returnMaterial.getPickChildList().stream()
            .map(PickChild::getNormsId).collect(Collectors.toList());
        super.checkIdFromOrderMaterialNorms(fromNormsIds, inSqlNormsId);

        returnMaterial.getPickChildList().forEach(pickChild -> {
            // 退料需求单数量 - 当前单据数量 - 已经下达退料入库单的数量
            String surplusNum = ErpOrderUtil.checkOperNumber(pickChild.getNeedNum(), pickChild.getNormsId(), orderNormsNum, executeNum);
            if (setData) {
                pickChild.setNeedNum(surplusNum);
            }
        });
        // 和当前部门/车间的库存做对比
        checkDepartStockWhetherOutstrip(entity.getDepartmentId(), entity.getFarmId(), entity.getErpOrderItemList());
        if (setData) {
            // 过滤掉剩余数量为0的商品
            List<PickChild> pickChildList = returnMaterial.getPickChildList().stream()
                .filter(pickChild -> CalculationUtil.compareTo(pickChild.getNeedNum(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
                .collect(Collectors.toList());
            // 如果该退料需求单的商品已经全部生成了退料入库单，那说明已经完成
            if (CollectionUtil.isEmpty(pickChildList)) {
                returnMaterialService.editOtherState(returnMaterial.getId(), OutLetState.COMPLATE_OUTLET.getKey());
            } else {
                returnMaterialService.editOtherState(returnMaterial.getId(), OutLetState.PARTIAL_OUTLET.getKey());
            }
        }
    }

    @Override
    public void approvalEndIsSuccess(ReturnPut entity) {
        entity = selectById(entity.getId());
        // 修改来源单据信息
        checkMaterialNorms(entity, true);
        // 校验并修改条形码信息
        checkNormsCodeAndSave(entity, false);
    }

    private void checkDepartStockWhetherOutstrip(String departmentId, String farmId, List<ErpOrderItem> erpOrderItemList) {
        List<String> normsIds = erpOrderItemList.stream().map(ErpOrderItem::getNormsId).collect(Collectors.toList());
        Map<String, String> normsDepartmentStock = departmentStockService.queryNormsDepartmentStock(departmentId, farmId, normsIds);
        for (ErpOrderItem bean : erpOrderItemList) {
            // 部门库存存量
            String departMentTock = normsDepartmentStock.get(bean.getNormsId());
            if (StrUtil.isEmpty(departMentTock)) {
                departMentTock = CommonNumConstants.NUM_ZERO.toString();
            }
            // 单据数量 小于 仓储数量
            String subtractResult = CalculationUtil.subtract(departMentTock, bean.getOperNumber(), ErpConstants.NUM_AFTER_DOT);
            if (CalculationUtil.compareTo(subtractResult, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) < 0) {
                throw new CustomException("单据储量小于仓储数量，请确认");
            }
        }
    }

    /**
     * 校验商品规格条形码与单据明细的参数是否匹配
     * 从部门/车间的库存入库到仓库的库存
     *
     * @param entity
     * @param onlyCheck 是否只进行校验，true：是；false：否
     */
    protected List<String> checkNormsCodeAndSave(ReturnPut entity, Boolean onlyCheck) {
        List<String> materialIdList = entity.getErpOrderItemList().stream().map(ErpOrderItem::getMaterialId).distinct().collect(Collectors.toList());
        List<String> normsIdList = entity.getErpOrderItemList().stream().map(ErpOrderItem::getNormsId).distinct().collect(Collectors.toList());
        Map<String, Material> materialMap = materialService.selectMapByIds(materialIdList);
        Map<String, MaterialNorms> normsMap = materialNormsService.selectMapByIds(normsIdList);
        // 所有需要进行入库的条形码编码
        List<String> allNormsCodeList = new ArrayList<>();
        int allCodeNum = checkErpOrderItemDetail(entity, materialMap, normsMap, allNormsCodeList);
        if (CollectionUtil.isNotEmpty(allNormsCodeList)) {
            allNormsCodeList = allNormsCodeList.stream().distinct().collect(Collectors.toList());
            if (allCodeNum != allNormsCodeList.size()) {
                throw new CustomException("商品明细中存在相同的条形码编号，请确认");
            }
            // 1. 和数据库中条形码的状态做对比
            //  1.1 从数据库查询出库状态的条形码信息，
            //  1.2 只有部门信息不为空的说明没有退料，才可以进行退料。部门信息为空，说明该条形码还未进行领料
            List<MaterialNormsCode> materialNormsCodeList = materialNormsCodeService.queryMaterialNormsCodeByCodeNum(StrUtil.EMPTY, allNormsCodeList,
                MaterialNormsCodeInDepot.OUTBOUND.getKey());
            materialNormsCodeList = materialNormsCodeList.stream()
                .filter(bean -> StrUtil.isNotEmpty(bean.getDepartmentId()) && StrUtil.equals(entity.getDepartmentId(), bean.getDepartmentId()))
                .collect(Collectors.toList());
            //  1.3 如果车间不为空，则需要获取过滤出当前车间的库存
            if (StrUtil.isNotEmpty(entity.getFarmId())) {
                materialNormsCodeList = materialNormsCodeList.stream()
                    .filter(bean -> StrUtil.isNotEmpty(bean.getFarmId()) && StrUtil.equals(entity.getFarmId(), bean.getFarmId()))
                    .collect(Collectors.toList());
            }
            //  1.4 只有未使用/不是正常使用(入报废等)的可以退料
            materialNormsCodeList = materialNormsCodeList.stream()
                .filter(bean -> bean.getPickUseState() == PickNormsCodeUseState.WAIT_USE.getKey() ||
                    MachinProcedureAcceptChildType.NORMAL.getKey() != bean.getPickState())
                .collect(Collectors.toList());
            List<String> inSqlNormsCodeList = materialNormsCodeList.stream().map(MaterialNormsCode::getCodeNum).collect(Collectors.toList());
            // 获取所有前端传递过来的条形码信息，求差集(在入参中有，但是在数据库中不包含的条形码信息)
            List<String> diffList = allNormsCodeList.stream()
                .filter(num -> !inSqlNormsCodeList.contains(num)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(diffList)) {
                throw new CustomException(
                    String.format(Locale.ROOT, "编码【%s】不存在或不存在【部门/车间】仓库中或已被使用，请确认", Joiner.on(CommonCharConstants.COMMA_MARK).join(diffList)));
            }
            if (!onlyCheck) {
                // 批量修改条形码信息
                materialNormsCodeList.forEach(materialNormsCode -> {
                    materialNormsCode.setDepartmentId(StrUtil.EMPTY);
                    materialNormsCode.setFarmId(StrUtil.EMPTY);
                    materialNormsCode.setPickUseState(null);
                });
                materialNormsCodeService.updateEntityPick(materialNormsCodeList);
            }
        }
        if (!onlyCheck) {
            // 修改部门/车间的库存
            entity.getErpOrderItemList().forEach(erpOrderItem -> {
                departmentStockService.updateDepartmentStock(entity.getDepartmentId(), entity.getFarmId(), erpOrderItem.getMaterialId(),
                    erpOrderItem.getNormsId(), erpOrderItem.getOperNumber(), DepotPutOutType.OUT.getKey());
            });
        }
        return allNormsCodeList;
    }

    @Override
    public void queryReturnPutTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        ReturnPut returnPut = selectById(id);
        // 该退料入库单下的已经下达仓库入库单(审核通过)的数量
        Map<String, String> depotNumMap = depotPutService.calcMaterialNormsNumByFromId(returnPut.getId());
        // 设置未下达商品数量-----退料入库单数量 - 已入库数量
        super.setOrCheckOperNumber(returnPut.getErpOrderItemList(), true, depotNumMap);
        // 过滤掉数量为0的商品信息
        returnPut.setErpOrderItemList(returnPut.getErpOrderItemList().stream()
            .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
            .collect(Collectors.toList()));
        outputObject.setBean(returnPut);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void insertReturnPutToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        DepotPut depotPut = inputObject.getParams(DepotPut.class);
        // 获取退料入库单状态
        ReturnPut returnPut = selectById(depotPut.getId());
        if (ObjectUtil.isEmpty(returnPut)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转到仓库入库单
        if (FlowableStateEnum.PASS.getKey().equals(returnPut.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            depotPut.setFromId(depotPut.getId());
            depotPut.setFromTypeId(DepotPutFromType.RETURN_PUT.getKey());
            depotPut.setId(StrUtil.EMPTY);
            depotPutService.createEntity(depotPut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达仓库入库单.");
        }
    }
}
