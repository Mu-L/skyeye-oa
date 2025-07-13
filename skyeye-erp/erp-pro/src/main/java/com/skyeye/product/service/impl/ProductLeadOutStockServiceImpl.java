package com.skyeye.product.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.business.service.SkyeyeErpOrderItemService;
import com.skyeye.business.service.impl.SkyeyeErpOrderServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.CorrespondentEnterEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.depot.classenum.DepotOutFromType;
import com.skyeye.depot.classenum.DepotOutState;
import com.skyeye.depot.entity.DepotOut;
import com.skyeye.depot.service.DepotOutService;
import com.skyeye.entity.ErpOrderItem;
import com.skyeye.exception.CustomException;
import com.skyeye.farm.service.FarmService;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.product.dao.ProductLeadOutStockDao;
import com.skyeye.product.entity.ProductLeadOutStock;
import com.skyeye.product.service.ProductLeadOutStockService;
import com.skyeye.product.service.ProductLeadService;
import com.skyeye.rest.project.service.IProProjectService;
import com.skyeye.util.ErpOrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "借出出库", groupName = "借出出库", flowable = true)
public class ProductLeadOutStockServiceImpl extends SkyeyeErpOrderServiceImpl<ProductLeadOutStockDao, ProductLeadOutStock> implements ProductLeadOutStockService {

    @Autowired
    private SkyeyeErpOrderItemService skyeyeErpOrderItemService;

    @Autowired
    private DepotOutService depotOutService;

    @Autowired
    private IProProjectService iProProjectService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private ProductLeadService productLeadService;

    @Autowired
    private FarmService farmService;

    @Override
    public QueryWrapper<ProductLeadOutStock> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ProductLeadOutStock> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ProductLeadOutStock::getHolderId), commonPageInfo.getHolderId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getFromId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ProductLeadOutStock::getFromId), commonPageInfo.getFromId());
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageData(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageData(inputObject);
        productLeadService.setMationForMap(beans, "fromId", "fromMation");
        iProProjectService.setMationForMap(beans, "projectId", "projectMation");
        farmService.setMationForMap(beans, "farmId", "farmMation");
        beans.forEach(
            bean -> {
                String holderKey = bean.get("holderKey").toString();
                if (StrUtil.equals(holderKey, CorrespondentEnterEnum.CUSTOM.getKey())) {
                    iCustomerService.setMationForMap(bean, "holderId", "holderMation");
                } else {
                    supplierService.setMationForMap(bean, "holderId", "holderMation");
                }
            }
        );
        return beans;
    }

    @Override
    public void validatorEntity(ProductLeadOutStock entity) {
        List<ErpOrderItem> erpOrderItemList = entity.getErpOrderItemList();
        if (CollectionUtil.isEmpty(erpOrderItemList)) {
            throw new CustomException("该借出出库订单没有商品信息");
        }
        erpOrderItemList.forEach(
            e -> {
                String depotId = e.getDepotId();
                if (StrUtil.isEmpty(depotId)) {
                    throw new CustomException("商品[" + e.getMaterialMation().getName() + "]没有选择仓库");
                }
            }
        );
        entity.setOtherState(DepotOutState.NEED_OUT.getKey());
        checkMaterialNorms(entity, false);
    }

    private void checkMaterialNorms(ProductLeadOutStock entity, boolean setData) {
        if (StrUtil.isEmpty(entity.getFromId())) {
            return;
        }
        // 当前借出出库订单的商品数量
        Map<String, Integer> orderNormsNum = entity.getErpOrderItemList().stream()
            .collect(Collectors.toMap(ErpOrderItem::getNormsId, ErpOrderItem::getOperNumber));
        // 获取已经下达借出出库订单的商品信息
        Map<String, Integer> executeNum = calcMaterialNormsNumByFromId(entity.getFromId());
        List<String> inSqlNormsId = new ArrayList<>(executeNum.keySet());
        if (entity.getFromTypeId() == DepotOutFromType.LOANOUT.getKey()) {
            // 借出出库单
            checkAndUpdateFromState(entity, setData, orderNormsNum, executeNum, inSqlNormsId);
        }
    }

    private void checkAndUpdateFromState(
        ProductLeadOutStock entity,
        boolean setData,
        Map<String, Integer> orderNormsNum,
        Map<String, Integer> executeNum,
        List<String> inSqlNormsId) {
        if (CollectionUtil.isEmpty(entity.getErpOrderItemList())) {
            throw new CustomException("该借出出库订单没有商品信息");
        }
        List<String> fromNormsIds = entity.getErpOrderItemList().stream().map(ErpOrderItem::getNormsId).collect(Collectors.toList());
        super.checkIdFromOrderMaterialNorms(fromNormsIds, inSqlNormsId);
        entity.getErpOrderItemList().forEach(productLeadChild -> {
                Integer operNumber = ErpOrderUtil.checkOperNumber(productLeadChild.getOperNumber(), productLeadChild.getNormsId(), orderNormsNum, executeNum);
                if (setData) {
                    productLeadChild.setOperNumber(operNumber);
                }
            }
        );
    }

    @Override
    public ProductLeadOutStock selectById(String id) {
        ProductLeadOutStock productLeadOutStock = super.selectById(id);
        // 该出库单下的已经下达仓库出库单(审核通过)的数量
        Map<String, Integer> depotNumMap = depotOutService.calcMaterialNormsNumByFromId(productLeadOutStock.getId());
        // 设置未下达商品数量-----补料出库单数量 - 已出库数量
        super.setOrCheckOperNumber(productLeadOutStock.getErpOrderItemList(), true, depotNumMap);
        // 过滤掉数量为0的商品信息
        productLeadOutStock.setErpOrderItemList(productLeadOutStock.getErpOrderItemList().stream()
            .filter(erpOrderItem -> erpOrderItem.getOperNumber() > 0).collect(Collectors.toList()));
        productLeadService.setDataMation(productLeadOutStock, ProductLeadOutStock::getFromId);
        farmService.setDataMation(productLeadOutStock, ProductLeadOutStock::getFarmId);
        iProProjectService.setDataMation(productLeadOutStock, ProductLeadOutStock::getProjectId);
        materialNormsService.setDataMation(productLeadOutStock.getErpOrderItemList(), ErpOrderItem::getNormsId);
        materialService.setDataMation(productLeadOutStock.getErpOrderItemList(), ErpOrderItem::getMaterialId);
        if (productLeadOutStock.getHolderKey().equals(CorrespondentEnterEnum.CUSTOM.getKey())) {
            iCustomerService.setDataMation(productLeadOutStock, ProductLeadOutStock::getHolderId);
        } else {
            supplierService.setDataMation(productLeadOutStock, ProductLeadOutStock::getHolderId);
        }
        return productLeadOutStock;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertProductLeadOutStockToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        DepotOut depotOut = inputObject.getParams(DepotOut.class);
        // 获取借出出库单状态
        ProductLeadOutStock productLeadOutStock = selectById(depotOut.getId());
        if (ObjectUtil.isEmpty(productLeadOutStock)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转到仓库出库单
        if (FlowableStateEnum.PASS.getKey().equals(productLeadOutStock.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            depotOut.setFromId(depotOut.getId());
            depotOut.setFromTypeId(DepotOutFromType.LOANOUT.getKey());
            depotOut.setId(StrUtil.EMPTY);
            depotOutService.createEntity(depotOut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达仓库出库单.");
        }
    }

    @Override
    protected void approvalEndIsFailed(ProductLeadOutStock entity) {
        super.approvalEndIsFailed(entity);
        UpdateWrapper<ProductLeadOutStock> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, entity.getId());
        updateWrapper.set(CommonConstants.STATE, FlowableStateEnum.REJECT.getKey());
        update(updateWrapper);
    }

    @Override
    protected void approvalEndIsSuccess(ProductLeadOutStock entity) {
        super.approvalEndIsSuccess(entity);
        productLeadService.updateLeadType(entity.getFarmId());
    }


    @Override
    public List<ProductLeadOutStock> queryLeadByHolderId(String holderId) {
        QueryWrapper<ProductLeadOutStock> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProductLeadOutStock::getHolderId), holderId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProductLeadOutStock::getState), FlowableStateEnum.PASS.getKey());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ProductLeadOutStock::getCreateTime));
        List<ProductLeadOutStock> list = list(queryWrapper);
        List<String> productLeadOutStockIds = list.stream().map(ProductLeadOutStock::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(productLeadOutStockIds)) {
            return list;
        }
        List<ErpOrderItem> erpOrderItems = skyeyeErpOrderItemService.queryErpOrderItemByPIds(productLeadOutStockIds);
        Map<String, List<ErpOrderItem>> stringListMap = erpOrderItems.stream().collect(Collectors.groupingBy(ErpOrderItem::getParentId));
        list.forEach(productLeadOutStock ->
            productLeadOutStock.setErpOrderItemList(stringListMap.get(productLeadOutStock.getId())));
        return list(queryWrapper);
    }

    @Override
    public void deletePostpose(String id) {
        skyeyeErpOrderItemService.deleteByPId(id);
    }
}
