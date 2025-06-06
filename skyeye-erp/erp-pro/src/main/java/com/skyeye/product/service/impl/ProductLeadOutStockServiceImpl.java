package com.skyeye.product.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.business.service.impl.SkyeyeErpOrderServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
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
import com.skyeye.product.classenum.ProductLeadFromType;
import com.skyeye.product.dao.ProductLeadOutStockDao;
import com.skyeye.product.entity.ProductLeadOutStock;
import com.skyeye.product.service.ProductLeadOutStockService;
import com.skyeye.product.service.ProductLeadService;
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
    public void validatorEntity(ProductLeadOutStock entity) {
        entity.setOtherState(DepotOutState.NEED_OUT.getKey());
        checkMaterialNorms(entity, false);
    }

    @Autowired
    private DepotOutService depotOutService;

    @Autowired
    private ProductLeadService productLeadService;

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
        if (entity.getFromTypeId() == ProductLeadFromType.LOANOUT.getKey()) {
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
        ProductLeadOutStock productLeadOutStock = selectById(entity.getFromId());
        if (CollectionUtil.isEmpty(productLeadOutStock.getErpOrderItemList())) {
            throw new CustomException("该借出出库订单没有商品信息");
        }
        List<String> fromNormsIds = productLeadOutStock.getErpOrderItemList().stream().map(ErpOrderItem::getNormsId).collect(Collectors.toList());
        super.checkIdFromOrderMaterialNorms(fromNormsIds, inSqlNormsId);
        productLeadOutStock.getErpOrderItemList().forEach(productLeadChild -> {
                Integer operNumber = ErpOrderUtil.checkOperNumber(productLeadChild.getOperNumber(), productLeadChild.getNormsId(), orderNormsNum, executeNum);
                if (setData) {
                    productLeadChild.setOperNumber(operNumber);
                }
            }
        );
    }

    @Override
    public void queryProductLeadOutStockById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        ProductLeadOutStock productLeadOutStock = selectById(id);
        // 该补料出库单下的已经下达仓库出库单(审核通过)的数量
        Map<String, Integer> depotNumMap = depotOutService.calcMaterialNormsNumByFromId(productLeadOutStock.getId());
        // 设置未下达商品数量-----补料出库单数量 - 已出库数量
        super.setOrCheckOperNumber(productLeadOutStock.getErpOrderItemList(), true, depotNumMap);
        // 过滤掉数量为0的商品信息
        productLeadOutStock.setErpOrderItemList(productLeadOutStock.getErpOrderItemList().stream()
            .filter(erpOrderItem -> erpOrderItem.getOperNumber() > 0).collect(Collectors.toList()));
        outputObject.setBean(productLeadOutStock);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertProductLeadOutStockToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        DepotOut depotOut = inputObject.getParams(DepotOut.class);
        // 获取补料出库单状态
        ProductLeadOutStock productLeadOutStock = selectById(depotOut.getId());
        if (ObjectUtil.isEmpty(productLeadOutStock)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转到仓库出库单
        if (FlowableStateEnum.PASS.getKey().equals(productLeadOutStock.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            depotOut.setFromId(depotOut.getId());
            depotOut.setFromTypeId(DepotOutFromType.PATCH_OUTLET.getKey());
            depotOut.setId(StrUtil.EMPTY);
            depotOutService.createEntity(depotOut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达仓库出库单.");
        }
    }
}
