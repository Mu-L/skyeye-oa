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
import com.skyeye.depot.service.ErpDepotService;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.exception.CustomException;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.repair.classenum.EquipmentRepairAuditOpinion;
import com.skyeye.repair.classenum.EquipmentRepairOrderState;
import com.skyeye.repair.classenum.EquipmentRepairUrgency;
import com.skyeye.repair.dao.EquipmentRepairOrderDao;
import com.skyeye.repair.entity.EquipmentRepairAcceptance;
import com.skyeye.repair.entity.EquipmentRepairAuditDispatch;
import com.skyeye.repair.entity.EquipmentRepairEvaluate;
import com.skyeye.repair.entity.EquipmentRepairFaultReport;
import com.skyeye.repair.entity.EquipmentRepairOrder;
import com.skyeye.repair.entity.EquipmentRepairResult;
import com.skyeye.repair.service.EquipmentRepairOrderService;
import com.skyeye.repair.entity.EquipmentSparePartRequisition;
import com.skyeye.repair.entity.EquipmentSparePartRequisitionDetail;
import com.skyeye.repair.service.EquipmentSparePartRequisitionService;
import com.skyeye.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 设备维修单服务层（分阶段编辑接口，列表流程对齐工单）
 */
@Service
@SkyeyeService(name = "设备维修单", groupName = "设备维修")
public class EquipmentRepairOrderServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentRepairOrderDao, EquipmentRepairOrder>
    implements EquipmentRepairOrderService {

    @Autowired
    private EquipmentSparePartRequisitionService equipmentSparePartRequisitionService;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private ErpDepotService erpDepotService;

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
            if (StrUtil.equals(state, "myCreate")) {
                queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getCreateId), userId);
            } else if (StrUtil.equals(state, String.valueOf(EquipmentRepairOrderState.PENDING_ORDERS.getKey()))) {
                queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), EquipmentRepairOrderState.PENDING_ORDERS.getKey())
                    .and(wrapper -> wrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getServiceUserId), userId)
                        .or().isNull(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getServiceUserId))
                        .or().eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getServiceUserId), StrUtil.EMPTY));
            } else if (StrUtil.equals(state, String.valueOf(EquipmentRepairOrderState.BE_COMPLETED.getKey()))) {
                queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getServiceUserId), userId)
                    .eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), EquipmentRepairOrderState.BE_COMPLETED.getKey());
            } else if (StrUtil.equals(state, String.valueOf(EquipmentRepairOrderState.AUDIT.getKey()))) {
                queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), EquipmentRepairOrderState.AUDIT.getKey())
                    .eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getUserId), userId);
            } else if (StrUtil.equals(state, String.valueOf(EquipmentRepairOrderState.BE_DISPATCHED.getKey()))
                || StrUtil.equals(state, String.valueOf(EquipmentRepairOrderState.BE_EVALUATED.getKey()))
                || StrUtil.equals(state, String.valueOf(EquipmentRepairOrderState.COMPLATE.getKey()))) {
                queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), Integer.parseInt(state));
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
        order.setSparePartRequisitionList(equipmentSparePartRequisitionService.selectByPId(id));
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
        if (CollectionUtil.isEmpty(order.getSparePartRequisitionList())) {
            return order;
        }
        erpDepotService.setDataMation(order.getSparePartRequisitionList(), EquipmentSparePartRequisition::getDepotId);
        iAuthUserService.setDataMation(order.getSparePartRequisitionList(), EquipmentSparePartRequisition::getUserId);

        List<EquipmentSparePartRequisitionDetail> allDetailList = order.getSparePartRequisitionList().stream()
            .filter(bean -> CollectionUtil.isNotEmpty(bean.getDetailList()))
            .flatMap(bean -> bean.getDetailList().stream())
            .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(allDetailList)) {
            materialService.setDataMation(allDetailList, EquipmentSparePartRequisitionDetail::getMaterialId);
            materialNormsService.setDataMation(allDetailList, EquipmentSparePartRequisitionDetail::getNormsId);
        }
        return order;
    }

    @Override
    public void createPrepose(EquipmentRepairOrder entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business);
        entity.setOddNumber(oddNumber);
        entity.setUserId(InputObject.getLogParamsStatic().get("id").toString());
        entity.setReportTime(DateUtil.getTimeAndToString());
        if (entity.getUrgencyLevel() == null) {
            entity.setUrgencyLevel(EquipmentRepairUrgency.NORMAL.getKey());
        }
        entity.setState(EquipmentRepairOrderState.BE_DISPATCHED.getKey());
        entity.setServiceTime(null);
        entity.setServiceUserId(null);
    }

    @Override
    protected void validatorEntity(EquipmentRepairOrder entity) {
        if (StrUtil.isNotEmpty(entity.getId())) {
            throw new CustomException("请使用分阶段编辑接口");
        }
        validateFaultReportRequired(entity.getFaultBrief(), entity.getFaultPhoto(), entity.getFaultVideo());
    }

    @Override
    public void saveOrUpdateEntity(InputObject inputObject, OutputObject outputObject) {
        throw new CustomException("请使用分阶段接口：insertEquipmentRepairOrder / editEquipmentRepairFaultReport 等");
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertEquipmentRepairOrder(InputObject inputObject, OutputObject outputObject) {
        EquipmentRepairFaultReport params = inputObject.getParams(EquipmentRepairFaultReport.class);
        EquipmentRepairOrder entity = new EquipmentRepairOrder();
        entity.setEquipmentId(params.getEquipmentId());
        entity.setFaultBrief(params.getFaultBrief());
        entity.setFaultPhoto(params.getFaultPhoto());
        entity.setFaultVideo(params.getFaultVideo());
        entity.setUrgencyLevel(params.getUrgencyLevel());
        validatorEntity(entity);
        createPrepose(entity);
        String userId = inputObject.getLogParams().get("id").toString();
        createEntity(entity, userId);
        outputObject.setBean(selectById(entity.getId()));
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editEquipmentRepairFaultReport(InputObject inputObject, OutputObject outputObject) {
        EquipmentRepairFaultReport params = inputObject.getParams(EquipmentRepairFaultReport.class);
        EquipmentRepairOrder dbOrder = requireOrder(params.getId());
        assertOrderState(dbOrder,
            EquipmentRepairOrderState.BE_DISPATCHED.getKey(),
            EquipmentRepairOrderState.PENDING_ORDERS.getKey());
        validateFaultReportRequired(params.getFaultBrief(), params.getFaultPhoto(), params.getFaultVideo());

        UpdateWrapper<EquipmentRepairOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, params.getId());
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getEquipmentId), params.getEquipmentId());
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getFaultBrief), params.getFaultBrief());
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getFaultPhoto), params.getFaultPhoto());
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getFaultVideo), params.getFaultVideo());
        if (params.getUrgencyLevel() != null) {
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getUrgencyLevel), params.getUrgencyLevel());
        }
        update(updateWrapper);
        refreshCache(params.getId());
        outputObject.setBean(selectById(params.getId()));
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editEquipmentRepairAuditDispatch(InputObject inputObject, OutputObject outputObject) {
        EquipmentRepairAuditDispatch params = inputObject.getParams(EquipmentRepairAuditDispatch.class);
        EquipmentRepairOrder dbOrder = requireOrder(params.getId());
        assertOrderState(dbOrder,
            EquipmentRepairOrderState.BE_DISPATCHED.getKey(),
            EquipmentRepairOrderState.PENDING_ORDERS.getKey());
        validateAuditOpinion(params.getAuditOpinion());

        UpdateWrapper<EquipmentRepairOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, params.getId());
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getAuditOpinion), params.getAuditOpinion());
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getUrgencyLevel), params.getUrgencyLevel());
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getFaultType), params.getFaultType());
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getRepairTeam), params.getRepairTeam());
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getResponseHours), params.getResponseHours());
        applyDispatchStateOnUpdate(updateWrapper, params.getServiceUserId(), params.getServiceTime());
        update(updateWrapper);
        refreshCache(params.getId());
        outputObject.setBean(selectById(params.getId()));
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editEquipmentRepairResult(InputObject inputObject, OutputObject outputObject) {
        EquipmentRepairResult params = inputObject.getParams(EquipmentRepairResult.class);
        EquipmentRepairOrder dbOrder = requireOrder(params.getId());
        assertOrderState(dbOrder, EquipmentRepairOrderState.BE_COMPLETED.getKey());
        validateRepairResultStage(params, dbOrder);

        String supplierId = params.getSupplierId();
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
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), EquipmentRepairOrderState.BE_EVALUATED.getKey());
        if (StrUtil.isEmpty(params.getRepairFinishTime())) {
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getRepairFinishTime), DateUtil.getTimeAndToString());
        }
        update(updateWrapper);
        refreshCache(params.getId());
        outputObject.setBean(selectById(params.getId()));
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editEquipmentRepairEvaluate(InputObject inputObject, OutputObject outputObject) {
        EquipmentRepairEvaluate params = inputObject.getParams(EquipmentRepairEvaluate.class);
        EquipmentRepairOrder dbOrder = requireOrder(params.getId());
        assertOrderState(dbOrder, EquipmentRepairOrderState.BE_EVALUATED.getKey());
        validateEvaluateStage(params);

        UpdateWrapper<EquipmentRepairOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, params.getId());
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getEvaluateTypeId), params.getEvaluateTypeId());
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getEvaluateContent), params.getEvaluateContent());
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), EquipmentRepairOrderState.AUDIT.getKey());
        update(updateWrapper);
        refreshCache(params.getId());
        outputObject.setBean(selectById(params.getId()));
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editEquipmentRepairAcceptance(InputObject inputObject, OutputObject outputObject) {
        EquipmentRepairAcceptance params = inputObject.getParams(EquipmentRepairAcceptance.class);
        EquipmentRepairOrder dbOrder = requireOrder(params.getId());
        assertOrderState(dbOrder, EquipmentRepairOrderState.AUDIT.getKey());
        assertRepairInitiator(dbOrder);
        validateAcceptanceStage(params);

        UpdateWrapper<EquipmentRepairOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, params.getId());
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getIsFixed), params.getIsFixed());
        if (WhetherEnum.DISABLE_USING.getKey().equals(params.getIsFixed())) {
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), EquipmentRepairOrderState.BE_COMPLETED.getKey());
        } else {
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), EquipmentRepairOrderState.COMPLATE.getKey());
        }
        update(updateWrapper);
        refreshCache(params.getId());
        if (WhetherEnum.ENABLE_USING.getKey().equals(params.getIsFixed())
            && params.getEquipmentStatus() != null) {
            equipmentService.editEquipmentStateById(dbOrder.getEquipmentId(), params.getEquipmentStatus());
        }
        outputObject.setBean(selectById(params.getId()));
    }

    private EquipmentRepairOrder requireOrder(String id) {
        EquipmentRepairOrder order = selectById(id);
        if (order == null) {
            throw new CustomException("数据不存在");
        }
        return order;
    }

    private void assertOrderState(EquipmentRepairOrder order, Integer... states) {
        for (Integer expected : states) {
            if (ObjectUtil.equal(order.getState(), expected)) {
                return;
            }
        }
        throw new CustomException("该数据状态已改变，请刷新页面！");
    }

    private void validateFaultReportRequired(String faultBrief, String faultPhoto, String faultVideo) {
        if (StrUtil.isEmpty(faultBrief)) {
            throw new CustomException("请填写故障描述");
        }
        if (StrUtil.isEmpty(faultPhoto)) {
            throw new CustomException("请上传故障情况拍照");
        }
        if (StrUtil.isEmpty(faultVideo)) {
            throw new CustomException("请上传故障情况视频");
        }
    }

    private void validateAuditOpinion(Integer auditOpinion) {
        if (auditOpinion == null) {
            return;
        }
        if (!EquipmentRepairAuditOpinion.REPAIR_NOW.getKey().equals(auditOpinion)
            && !EquipmentRepairAuditOpinion.OUTSOURCE.getKey().equals(auditOpinion)) {
            throw new CustomException("审核意见只能选择立即维修或转委外");
        }
    }

    private void validateRepairResultStage(EquipmentRepairResult params, EquipmentRepairOrder dbOrder) {
        if (WhetherEnum.DISABLE_USING.getKey().equals(params.getIsRepaired())) {
            if (params.getCancelReason() == null) {
                throw new CustomException("请选择作废原因");
            }
            return;
        }
        if (EquipmentRepairAuditOpinion.OUTSOURCE.getKey().equals(dbOrder.getAuditOpinion())
            && StrUtil.isEmpty(params.getSupplierId())) {
            throw new CustomException("请选择供应商");
        }
        if (WhetherEnum.ENABLE_USING.getKey().equals(params.getIsReplaceSpare())) {
            List<EquipmentSparePartRequisition> dbList = equipmentSparePartRequisitionService.selectByPId(params.getId());
            if (CollectionUtil.isEmpty(dbList)) {
                throw new CustomException("已选择更换备件，请至少添加一条备件领用单");
            }
        }
    }

    private void validateEvaluateStage(EquipmentRepairEvaluate params) {
        if (StrUtil.isEmpty(params.getEvaluateContent())) {
            throw new CustomException("请填写评价内容");
        }
    }

    private void validateAcceptanceStage(EquipmentRepairAcceptance params) {
        if (params.getIsFixed() == null) {
            throw new CustomException("请选择是否修复");
        }
        if (WhetherEnum.ENABLE_USING.getKey().equals(params.getIsFixed()) && params.getEquipmentStatus() == null) {
            throw new CustomException("请选择设备状态");
        }
    }

    private void assertRepairInitiator(EquipmentRepairOrder order) {
        String currentUserId = InputObject.getLogParamsStatic().get("id").toString();
        if (!StrUtil.equals(order.getUserId(), currentUserId)) {
            throw new CustomException("仅报修人可进行结果验收与审核操作");
        }
    }

    private void applyDispatchStateOnUpdate(UpdateWrapper<EquipmentRepairOrder> updateWrapper, String serviceUserId, String serviceTime) {
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), EquipmentRepairOrderState.PENDING_ORDERS.getKey());
        if (StrUtil.isEmpty(serviceUserId)) {
            // 审核派工未指定负责人：进入待接单池，接单时再写入 serviceUserId
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getServiceUserId), null);
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getServiceTime), null);
            return;
        }
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getServiceUserId), serviceUserId);
        if (StrUtil.isNotEmpty(serviceTime)) {
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getServiceTime), serviceTime);
        } else {
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getServiceTime), DateUtil.getTimeAndToString());
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
        if (repairOrder == null) {
            throw new CustomException("数据不存在");
        }
        String currentUserId = InputObject.getLogParamsStatic().get("id").toString();

        if (ObjectUtil.equal(repairOrder.getState(), EquipmentRepairOrderState.BE_DISPATCHED.getKey())) {
            throw new CustomException("请先完成审核派工");
        }
        if (ObjectUtil.equal(repairOrder.getState(), EquipmentRepairOrderState.PENDING_ORDERS.getKey())) {
            if (StrUtil.isNotEmpty(repairOrder.getServiceUserId())
                && !StrUtil.equals(currentUserId, repairOrder.getServiceUserId())) {
                throw new CustomException("只有维修负责人可以接单");
            }
            UpdateWrapper<EquipmentRepairOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, repairOrder.getId());
            updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), EquipmentRepairOrderState.BE_COMPLETED.getKey());
            if (StrUtil.isEmpty(repairOrder.getServiceUserId())) {
                updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getServiceUserId), currentUserId);
                updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getServiceTime), DateUtil.getTimeAndToString());
            }
            update(updateWrapper);
            refreshCache(repairOrder.getId());
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
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
    public void queryAllEquipmentRepairOrderList(InputObject inputObject, OutputObject outputObject) {
        List<EquipmentRepairOrder> list = list();
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }
}
