package com.skyeye.product.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.business.service.SkyeyeErpOrderItemService;
import com.skyeye.business.service.impl.SkyeyeErpOrderServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.CorrespondentEnterEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.depot.classenum.DepotPutFromType;
import com.skyeye.depot.classenum.DepotPutState;
import com.skyeye.depot.entity.DepotPut;
import com.skyeye.depot.service.DepotPutService;
import com.skyeye.entity.ErpOrderItem;
import com.skyeye.exception.CustomException;
import com.skyeye.farm.service.FarmService;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.product.dao.ProductReturnInStockDao;
import com.skyeye.product.entity.ProductReturnInStock;
import com.skyeye.product.service.ProductReturnInStockService;
import com.skyeye.product.service.ProductReturnService;
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
@SkyeyeService(name = "归还入库", groupName = "归还入库", flowable = true)
public class ProductReturnInStockServiceImpl extends SkyeyeErpOrderServiceImpl<ProductReturnInStockDao, ProductReturnInStock> implements ProductReturnInStockService {

    @Autowired
    private SkyeyeErpOrderItemService skyeyeErpOrderItemService;

    @Autowired
    private DepotPutService depotPutService;

    @Autowired
    private IProProjectService iProProjectService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private FarmService farmService;

    @Autowired
    private ProductReturnService productReturnService;

    @Override
    public QueryWrapper<ProductReturnInStock> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ProductReturnInStock> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ProductReturnInStock::getHolderId), commonPageInfo.getHolderId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getFromId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ProductReturnInStock::getFromId), commonPageInfo.getFromId());
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageData(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageData(inputObject);
        productReturnService.setMationForMap(beans, "fromId", "fromMation");
        farmService.setMationForMap(beans, "farmId", "farmMation");
        iProProjectService.setMationForMap(beans, "projectId", "projectMation");
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
    public void validatorEntity(ProductReturnInStock entity) {
        List<ErpOrderItem> erpOrderItemList = entity.getErpOrderItemList();
        if (CollectionUtil.isEmpty(erpOrderItemList)) {
            throw new CustomException("该归还入库订单没有商品信息");
        }
        entity.setOtherState(DepotPutState.NEED_PUT.getKey());
        checkMaterialNorms(entity, false);
    }

    private void checkMaterialNorms(ProductReturnInStock entity, boolean setData) {
        if (StrUtil.isEmpty(entity.getFromId())) {
            return;
        }
        // 当前归还入库订单的商品数量
        Map<String, Integer> orderNormsNum = entity.getErpOrderItemList().stream()
            .collect(Collectors.toMap(ErpOrderItem::getNormsId, ErpOrderItem::getOperNumber));
        // 获取已经下达归还入库订单的商品信息
        Map<String, Integer> executeNum = calcMaterialNormsNumByFromId(entity.getFromId());
        List<String> inSqlNormsId = new ArrayList<>(executeNum.keySet());
        if (entity.getFromTypeId() == DepotPutFromType.LOANIN.getKey()) {
            // 归还入库单
            checkAndUpdateFromState(entity, setData, orderNormsNum, executeNum, inSqlNormsId);
        }
    }

    private void checkAndUpdateFromState(
        ProductReturnInStock entity,
        boolean setData,
        Map<String, Integer> orderNormsNum,
        Map<String, Integer> executeNum,
        List<String> inSqlNormsId) {
        if (CollectionUtil.isEmpty(entity.getErpOrderItemList())) {
            throw new CustomException("该归还入库订单没有商品信息");
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
    public ProductReturnInStock selectById(String id) {
        ProductReturnInStock productReturnInStock = super.selectById(id);
        Map<String, Integer> stringIntegerMap = depotPutService.calcMaterialNormsNumByFromId(productReturnInStock.getId());
        super.setOrCheckOperNumber(productReturnInStock.getErpOrderItemList(), true, stringIntegerMap);
        productReturnInStock.setErpOrderItemList(productReturnInStock.getErpOrderItemList().stream()
            .filter(erpOrderItem -> erpOrderItem.getOperNumber() > 0).collect(Collectors.toList()));
        materialNormsService.setDataMation(productReturnInStock.getErpOrderItemList(), ErpOrderItem::getNormsId);
        materialService.setDataMation(productReturnInStock.getErpOrderItemList(), ErpOrderItem::getMaterialId);
        productReturnService.setDataMation(productReturnInStock, ProductReturnInStock::getFromId);
        if (productReturnInStock.getHolderKey().equals(CorrespondentEnterEnum.CUSTOM.getKey())) {
            iCustomerService.setDataMation(productReturnInStock, ProductReturnInStock::getHolderId);
        } else {
            supplierService.setDataMation(productReturnInStock, ProductReturnInStock::getHolderId);
        }
        return productReturnInStock;
    }

    @Override
    public void deletePostpose(String id) {
        skyeyeErpOrderItemService.deleteByPId(id);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertProductReturnInStockToInDepot(InputObject inputObject, OutputObject outputObject) {
        DepotPut depotPut = inputObject.getParams(DepotPut.class);
        ProductReturnInStock productReturnInStock = selectById(depotPut.getId());
        if (ObjectUtil.isEmpty(productReturnInStock)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转到仓库出库单
        if (FlowableStateEnum.PASS.getKey().equals(productReturnInStock.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            depotPut.setFromId(depotPut.getId());
            depotPut.setFromTypeId(DepotPutFromType.LOANIN.getKey());
            depotPut.setId(StrUtil.EMPTY);
            depotPutService.createEntity(depotPut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达仓库入库单.");
        }
    }
}
