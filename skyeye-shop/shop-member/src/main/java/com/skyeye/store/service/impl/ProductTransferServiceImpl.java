/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.erp.service.IMaterialNormsService;
import com.skyeye.erp.service.IMaterialService;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.shopmaterialnorms.sevice.IShopMaterialNormsService;
import com.skyeye.store.dao.ProductTransferDao;
import com.skyeye.store.entity.ProductTransfer;
import com.skyeye.store.entity.ShopStore;
import com.skyeye.store.service.ProductTransferService;
import com.skyeye.store.service.ShopStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ProductTransferServiceImpl
 * @Description: 门店产品调拨申请服务层
 * @author: skyeye云系列--卫志强
 * @date: 2025/01/XX XX:XX
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "门店产品调拨", groupName = "门店产品调拨", flowable = true)
public class ProductTransferServiceImpl extends SkyeyeBusinessServiceImpl<ProductTransferDao, ProductTransfer> implements ProductTransferService {

    @Autowired
    private ShopStoreService shopStoreService;

    @Autowired
    private IMaterialService iMaterialService;

    @Autowired
    private IMaterialNormsService iMaterialNormsService;

    @Autowired
    private IShopMaterialNormsService iShopMaterialNormsService;

    @Override
    public void validatorEntity(ProductTransfer entity) {
        if (StrUtil.isEmpty(entity.getMaterialId()) || StrUtil.isEmpty(entity.getNormsId()) || StrUtil.isEmpty(entity.getOperNumber())) {
            throw new CustomException("产品、规格、调拨数量不能为空");
        }
        if (StrUtil.isEmpty(entity.getFromStoreId()) || StrUtil.isEmpty(entity.getToStoreId())) {
            throw new CustomException("原门店和目标门店不能为空");
        }
        if (StrUtil.equals(entity.getFromStoreId(), entity.getToStoreId())) {
            throw new CustomException("原门店和目标门店相同，无需调拨");
        }
        if (CalculationUtil.compareTo(entity.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), CommonNumConstants.NUM_TWO, RoundingMode.UP) <= 0) {
            throw new CustomException("调拨数量必须大于0");
        }
    }

    @Override
    protected QueryWrapper<ProductTransfer> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ProductTransfer> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProductTransfer::getCreateId), userId);
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        setMaterialAndStoreMation(beans);
        return beans;
    }

    @Override
    public ProductTransfer selectById(String id) {
        ProductTransfer productTransfer = super.selectById(id);
        iMaterialService.setDataMation(productTransfer, ProductTransfer::getMaterialId);
        iMaterialNormsService.setDataMation(productTransfer, ProductTransfer::getNormsId);
        ShopStore fromStore = shopStoreService.selectById(productTransfer.getFromStoreId());
        if (fromStore != null) {
            productTransfer.setFromStoreMation(fromStore);
        }
        ShopStore toStore = shopStoreService.selectById(productTransfer.getToStoreId());
        if (toStore != null) {
            productTransfer.setToStoreMation(toStore);
        }
        productTransfer.setStateName(FlowableStateEnum.getStateName(productTransfer.getState()));
        iAuthUserService.setName(productTransfer, "createId", "createName");
        return productTransfer;
    }

    /**
     * 设置产品、规格、门店信息
     */
    private void setMaterialAndStoreMation(List<Map<String, Object>> beans) {
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        iMaterialService.setMationForMap(beans, "materialId", "materialMation");
        iMaterialNormsService.setMationForMap(beans, "normsId", "normsMation");
        List<String> storeIds = beans.stream()
            .flatMap(bean -> {
                List<String> ids = new java.util.ArrayList<>();
                ids.add(bean.get("fromStoreId").toString());
                ids.add(bean.get("toStoreId").toString());
                return ids.stream();
            }).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(storeIds)) {
            List<ShopStore> shopStores = shopStoreService.selectByIds(storeIds.toArray(new String[]{}));
            Map<String, ShopStore> storeMap = shopStores.stream()
                .collect(Collectors.toMap(ShopStore::getId, shopStore -> shopStore));
            beans.forEach(bean -> {
                bean.put("fromStoreMation", storeMap.get(bean.get("fromStoreId").toString()));
                bean.put("toStoreMation", storeMap.get(bean.get("toStoreId").toString()));
            });
        }
    }

    @Override
    protected void approvalEndIsSuccess(ProductTransfer entity) {
        iShopMaterialNormsService.executeStoreProductTransfer(
            entity.getMaterialId(),
            entity.getNormsId(),
            entity.getOperNumber(),
            entity.getFromStoreId(),
            entity.getToStoreId());
    }

}
