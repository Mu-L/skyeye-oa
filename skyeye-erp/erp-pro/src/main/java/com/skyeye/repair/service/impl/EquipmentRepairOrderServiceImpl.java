/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.exception.CustomException;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.repair.classenum.EquipmentRepairAuditOpinion;
import com.skyeye.repair.classenum.EquipmentRepairOrderState;
import com.skyeye.repair.dao.EquipmentRepairOrderDao;
import com.skyeye.repair.entity.EquipmentRepairOrder;
import com.skyeye.repair.entity.EquipmentSparePartUsageDetail;
import com.skyeye.repair.service.EquipmentRepairOrderService;
import com.skyeye.repair.service.EquipmentSparePartUsageDetailService;
import com.skyeye.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: EquipmentRepairOrderServiceImpl
 * @Description: 设备维修单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "设备维修单", groupName = "设备维修")
public class EquipmentRepairOrderServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentRepairOrderDao, EquipmentRepairOrder>
    implements EquipmentRepairOrderService {

    @Autowired
    private EquipmentSparePartUsageDetailService equipmentSparePartUsageDetailService;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private SupplierService supplierService;

    @Override
    public QueryWrapper<EquipmentRepairOrder> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EquipmentRepairOrder> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String state = commonPageInfo.getState();

        if (StrUtil.isNotEmpty(state)) {
            // 报修人
            if (StrUtil.equals(state, "myCreate")) {
                queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getUserId), userId);
            } else if (StrUtil.equals(state, String.valueOf(EquipmentRepairOrderState.PENDING_ORDERS.getKey()))
                || StrUtil.equals(state, String.valueOf(EquipmentRepairOrderState.BE_COMPLETED.getKey()))) {
                // 待接单、待完工 - 仅维修负责人可见
                queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getServiceUserId), userId)
                    .eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), Integer.valueOf(state));
            } else if (StrUtil.equals(state, String.valueOf(EquipmentRepairOrderState.AUDIT.getKey()))) {
                // 待确认 - 仅报修人可见
                queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getUserId), userId)
                    .eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), EquipmentRepairOrderState.AUDIT.getKey());
            } else if (StrUtil.equals(state, String.valueOf(EquipmentRepairOrderState.BE_DISPATCHED.getKey()))
                || StrUtil.equals(state, String.valueOf(EquipmentRepairOrderState.BE_EVALUATED.getKey()))
                || StrUtil.equals(state, String.valueOf(EquipmentRepairOrderState.COMPLATE.getKey()))) {
                // 待派工、待评价、已完工 - 查询所有该状态的工单
                queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), Integer.valueOf(state));
            }
        }

        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getEquipmentId), commonPageInfo.getObjectId());
        }
        return queryWrapper;
    }

    @Override
    public EquipmentRepairOrder getDataFromDb(String id) {
        EquipmentRepairOrder order = super.getDataFromDb(id);
        order.setSparePartUsageList(equipmentSparePartUsageDetailService.selectByPId(id));
        return order;
    }

    @Override
    public EquipmentRepairOrder selectById(String id) {
        EquipmentRepairOrder order = super.selectById(id);
        if (order == null) {
            return null;
        }
        equipmentService.setDataMation(order, EquipmentRepairOrder::getEquipmentId);
        iAuthUserService.setDataMation(order, EquipmentRepairOrder::getUserId);
        iAuthUserService.setDataMation(order, EquipmentRepairOrder::getServiceUserId);
        supplierService.setDataMation(order, EquipmentRepairOrder::getSupplierId);
        if (CollectionUtil.isEmpty(order.getSparePartUsageList())) {
            return order;
        }
        materialService.setDataMation(order.getSparePartUsageList(), EquipmentSparePartUsageDetail::getMaterialId);
        materialNormsService.setDataMation(order.getSparePartUsageList(), EquipmentSparePartUsageDetail::getNormsId);
        return order;
    }

    @Override
    public void createPrepose(EquipmentRepairOrder entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business);
        entity.setOddNumber(oddNumber);
        entity.setUserId(InputObject.getLogParamsStatic().get("id").toString());
        entity.setReportTime(DateUtil.getTimeAndToString());
        if (StrUtil.isEmpty(entity.getServiceUserId())) {
            entity.setState(EquipmentRepairOrderState.BE_DISPATCHED.getKey());
            entity.setServiceTime(null);
        } else {
            entity.setState(EquipmentRepairOrderState.PENDING_ORDERS.getKey());
            entity.setServiceTime(DateUtil.getTimeAndToString());
        }
    }

    @Override
    protected void updatePrepose(EquipmentRepairOrder entity) {
        if (StrUtil.isEmpty(entity.getServiceUserId())) {
            entity.setState(EquipmentRepairOrderState.BE_DISPATCHED.getKey());
            entity.setServiceTime(null);
        } else {
            entity.setState(EquipmentRepairOrderState.PENDING_ORDERS.getKey());
            if (StrUtil.isEmpty(entity.getServiceTime())) {
                entity.setServiceTime(DateUtil.getTimeAndToString());
            }
        }
    }

    @Override
    protected void validatorEntity(EquipmentRepairOrder entity) {
        if (StrUtil.isNotEmpty(entity.getId())) {
            EquipmentRepairOrder dbOrder = selectById(entity.getId());
            if (ObjectUtil.equal(dbOrder.getState(), EquipmentRepairOrderState.BE_DISPATCHED.getKey())
                || ObjectUtil.equal(dbOrder.getState(), EquipmentRepairOrderState.PENDING_ORDERS.getKey())) {
                // 待派工、待接单可以进行编辑
            } else {
                throw new CustomException("该数据状态已改变，请刷新页面！");
            }
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editEquipmentRepairResult(InputObject inputObject, OutputObject outputObject) {
        EquipmentRepairOrder params = inputObject.getParams(EquipmentRepairOrder.class);
        EquipmentRepairOrder dbOrder = selectById(params.getId());
        if (ObjectUtil.equal(dbOrder.getState(), EquipmentRepairOrderState.BE_COMPLETED.getKey())) {
            validateRepairResultStage(params, dbOrder);

            String supplierId = params.getSupplierId();
            if (StrUtil.isEmpty(supplierId)) {
                supplierId = dbOrder.getSupplierId();
            }
            if (EquipmentRepairAuditOpinion.REPAIR_NOW.getKey().equals(dbOrder.getAuditOpinion())) {
                supplierId = null;
            }

            UpdateWrapper<EquipmentRepairOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, params.getId());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getIsRepaired), params.getIsRepaired());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getIsReplaceSpare), params.getIsReplaceSpare());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getFaultReason), params.getFaultReason());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getCancelReason), params.getCancelReason());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getRepairDesc), params.getRepairDesc());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getRepairFinishPhoto), params.getRepairFinishPhoto());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getRepairFinishTime), params.getRepairFinishTime());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getSupplierId), supplierId);
            if (StrUtil.isEmpty(params.getRepairFinishTime())) {
                updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getRepairFinishTime), DateUtil.getTimeAndToString());
            }
            update(updateWrapper);
            refreshCache(params.getId());
            outputObject.setBean(selectById(params.getId()));
        } else {
            throw new CustomException("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void completeEquipmentRepairOrderById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        EquipmentRepairOrder dbOrder = selectById(map.get("id").toString());
        if (ObjectUtil.equal(dbOrder.getState(), EquipmentRepairOrderState.BE_COMPLETED.getKey())) {
            validateRepairResultBeforeComplete(dbOrder);
            updateStateById(dbOrder.getId(), EquipmentRepairOrderState.BE_EVALUATED.getKey());
            outputObject.setBean(selectById(dbOrder.getId()));
        } else {
            throw new CustomException("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editEquipmentRepairEvaluate(InputObject inputObject, OutputObject outputObject) {
        EquipmentRepairOrder params = inputObject.getParams(EquipmentRepairOrder.class);
        EquipmentRepairOrder dbOrder = selectById(params.getId());
        if (ObjectUtil.equal(dbOrder.getState(), EquipmentRepairOrderState.BE_EVALUATED.getKey())) {
            validateEvaluateStage(params);

            UpdateWrapper<EquipmentRepairOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, params.getId());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getEvaluateTypeId), params.getEvaluateTypeId());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getEvaluateContent), params.getEvaluateContent());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), EquipmentRepairOrderState.AUDIT.getKey());
            update(updateWrapper);
            refreshCache(params.getId());
            outputObject.setBean(selectById(params.getId()));
        } else {
            throw new CustomException("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editEquipmentRepairAcceptance(InputObject inputObject, OutputObject outputObject) {
        EquipmentRepairOrder params = inputObject.getParams(EquipmentRepairOrder.class);
        Map<String, Object> map = inputObject.getParams();
        Integer equipmentStatus = map.get("equipmentStatus") != null
            ? Integer.valueOf(map.get("equipmentStatus").toString()) : null;
        EquipmentRepairOrder dbOrder = selectById(params.getId());
        if (ObjectUtil.equal(dbOrder.getState(), EquipmentRepairOrderState.AUDIT.getKey())) {
            if (!StrUtil.equals(dbOrder.getUserId(), InputObject.getLogParamsStatic().get("id").toString())) {
                throw new CustomException("仅报修人可进行结果确认操作");
            }
            validateAcceptanceStage(params.getIsFixed(), equipmentStatus);

            UpdateWrapper<EquipmentRepairOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, params.getId());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getIsFixed), params.getIsFixed());
            if (WhetherEnum.DISABLE_USING.getKey().equals(params.getIsFixed())) {
                equipmentSparePartUsageDetailService.revertAndDeleteByRepairOrderId(dbOrder.getId(), dbOrder.getServiceUserId());
                updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), EquipmentRepairOrderState.PENDING_ORDERS.getKey());
            } else {
                updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), EquipmentRepairOrderState.COMPLATE.getKey());
            }
            update(updateWrapper);
            refreshCache(params.getId());
            if (WhetherEnum.ENABLE_USING.getKey().equals(params.getIsFixed())) {
                equipmentService.editEquipmentStateById(dbOrder.getEquipmentId(), equipmentStatus);
            }
            outputObject.setBean(selectById(params.getId()));
        } else {
            throw new CustomException("该数据状态已改变，请刷新页面！");
        }
    }

    private void validateRepairResultBeforeComplete(EquipmentRepairOrder order) {
        if (order.getIsRepaired() == null) {
            throw new CustomException("请先填写维修结果");
        }
        validateRepairResultStage(order, order);
    }

    private void validateRepairResultStage(EquipmentRepairOrder params, EquipmentRepairOrder dbOrder) {
        if (WhetherEnum.DISABLE_USING.getKey().equals(params.getIsRepaired())) {
            if (params.getCancelReason() == null) {
                throw new CustomException("请选择作废原因");
            }
            return;
        }
        if (EquipmentRepairAuditOpinion.OUTSOURCE.getKey().equals(dbOrder.getAuditOpinion())
            && StrUtil.isEmpty(params.getSupplierId())
            && StrUtil.isEmpty(dbOrder.getSupplierId())) {
            throw new CustomException("请选择供应商");
        }
        if (WhetherEnum.ENABLE_USING.getKey().equals(params.getIsReplaceSpare())) {
            List<EquipmentSparePartUsageDetail> dbList = equipmentSparePartUsageDetailService.selectByPId(params.getId());
            if (CollectionUtil.isEmpty(dbList)) {
                throw new CustomException("已选择更换备件，请至少添加一条备件使用明细");
            }
        }
    }

    private void validateEvaluateStage(EquipmentRepairOrder params) {
        if (StrUtil.isEmpty(params.getEvaluateTypeId())) {
            throw new CustomException("请选择评价类型");
        }
    }

    private void validateAcceptanceStage(Integer isFixed, Integer equipmentStatus) {
        if (isFixed == null) {
            throw new CustomException("请选择是否修复");
        }
        if (WhetherEnum.ENABLE_USING.getKey().equals(isFixed) && equipmentStatus == null) {
            throw new CustomException("请选择设备状态");
        }
    }

    @Override
    public void deletePreExecution(EquipmentRepairOrder entity) {
        if (ObjectUtil.equal(entity.getState(), EquipmentRepairOrderState.BE_DISPATCHED.getKey())
            || ObjectUtil.equal(entity.getState(), EquipmentRepairOrderState.PENDING_ORDERS.getKey())) {
            // 待派工/待接单可以进行删除
        } else {
            throw new CustomException("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isEmpty(beans)) {
            return beans;
        }
        equipmentService.setMationForMap(beans, "equipmentId", "equipmentMation");
        iAuthUserService.setMationForMap(beans, "userId", "userMation");
        iAuthUserService.setMationForMap(beans, "serviceUserId", "serviceUserMation");
        supplierService.setMationForMap(beans, "supplierId", "supplierMation");
        return beans;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editEquipmentRepairWaitToWorkMation(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        String serviceUserId = map.get("serviceUserId").toString();
        EquipmentRepairOrder repairOrder = selectById(id);
        if (ObjectUtil.equal(repairOrder.getState(), EquipmentRepairOrderState.BE_DISPATCHED.getKey())) {
            UpdateWrapper<EquipmentRepairOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, repairOrder.getId());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), EquipmentRepairOrderState.PENDING_ORDERS.getKey());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getServiceUserId), serviceUserId);
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getServiceTime), DateUtil.getTimeAndToString());
            update(updateWrapper);
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void receivingEquipmentRepairOrderById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        EquipmentRepairOrder repairOrder = selectById(map.get("id").toString());
        if (ObjectUtil.equal(repairOrder.getState(), EquipmentRepairOrderState.PENDING_ORDERS.getKey())) {
            // 接单进入待完工
            updateStateById(repairOrder.getId(), EquipmentRepairOrderState.BE_COMPLETED.getKey());
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    public void queryEquipmentRepairMyBeCompleted(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<EquipmentRepairOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getServiceUserId),
            InputObject.getLogParamsStatic().get("id").toString());
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), EquipmentRepairOrderState.BE_COMPLETED.getKey());
        List<EquipmentRepairOrder> orderList = list(queryWrapper);
        outputObject.setBeans(orderList);
        outputObject.settotal(orderList.size());
    }

    @Override
    public void updateStateById(String id, Integer state) {
        UpdateWrapper<EquipmentRepairOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), state);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void writeEquipmentRepairSparePartUsage(InputObject inputObject, OutputObject outputObject) {
        EquipmentRepairOrder params = inputObject.getParams(EquipmentRepairOrder.class);
        equipmentSparePartUsageDetailService.saveByRepairOrderId(params.getId(), params.getSparePartUsageList());
        outputObject.setBean(selectById(params.getId()));
    }

    @Override
    public void queryAllEquipmentRepairOrderList(InputObject inputObject, OutputObject outputObject) {
        List<EquipmentRepairOrder> list = list();
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }
}
