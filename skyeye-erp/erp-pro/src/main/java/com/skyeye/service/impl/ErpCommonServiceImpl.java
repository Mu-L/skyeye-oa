/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.business.service.SkyeyeErpOrderService;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.exception.CustomException;
import com.skyeye.jedis.util.RedisLock;
import com.skyeye.material.classenum.MaterialNormsStockType;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.entity.MaterialNormsStock;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialNormsStockService;
import com.skyeye.service.ErpCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.Map;

/**
 * @ClassName: ErpCommonServiceImpl
 * @Description: ERP公共服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:42
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ErpCommonServiceImpl implements ErpCommonService {

    private static Logger LOGGER = LoggerFactory.getLogger(ErpCommonServiceImpl.class);

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private MaterialNormsStockService materialNormsStockService;

    /**
     * 获取单据详情信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryDepotHeadDetailsMationById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String serviceClassName = map.get("serviceClassName").toString();
        String id = map.get("id").toString();
        try {
            Class<?> clazz = Class.forName(serviceClassName);
            SkyeyeErpOrderService skyeyeErpOrderService = (SkyeyeErpOrderService) SpringUtils.getBean(clazz);
            Object object = skyeyeErpOrderService.selectById(id);
            outputObject.setBean(object);
            outputObject.settotal(CommonNumConstants.NUM_ONE);
        } catch (Exception ex) {
            throw new RuntimeException("queryDepotHeadDetailsMationById error", ex);
        }
    }

    /**
     * 删除单据信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void deleteErpOrderById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String serviceClassName = map.get("serviceClassName").toString();
        String id = map.get("id").toString();
        try {
            Class<?> clazz = Class.forName(serviceClassName);
            SkyeyeErpOrderService skyeyeErpOrderService = (SkyeyeErpOrderService) SpringUtils.getBean(clazz);
            skyeyeErpOrderService.deleteById(id);
        } catch (Exception ex) {
            throw new RuntimeException("deleteErpOrderById error", ex);
        }
    }

    /**
     * 修改商品规格库存
     *
     * @param depotId    仓库id
     * @param materialId 商品id
     * @param normsId    规格id
     * @param operNumber 变化数量
     * @param type       出入库类型， {@link DepotPutOutType}
     * @param stockType  库存类型， {@link MaterialNormsStockType}
     */
    @Override
    public void editMaterialNormsDepotStock(String depotId, String materialId, String normsId, String operNumber, int type, int stockType) {
        String lockKey = String.format("%s_%s", depotId, normsId);
        RedisLock lock = new RedisLock(lockKey);
        try {
            if (!lock.lock()) {
                // 加锁失败
                throw new CustomException("增减库存失败，当前并发量较大，请稍后再次尝试.");
            }
            LOGGER.info("get lock success, lockKey is {}.", lockKey);
            // 查询规格库存信息
            MaterialNorms materialNorms = materialNormsService.queryMaterialNorms(normsId, depotId);
            String depotAllStock = ObjectUtil.isNotEmpty(materialNorms.getDepotTock()) ? materialNorms.getDepotTock().getAllStock() : CommonNumConstants.NUM_ZERO.toString();
            depotAllStock = getNewStockNum(type, operNumber, depotAllStock, stockType);

            if (CollectionUtil.isNotEmpty(materialNorms.getNormsStock()) && stockType == MaterialNormsStockType.ORDER_STOCK.getKey()) {
                // 只有现有库存类型的，才去减去初始库存
                MaterialNormsStock materialNormsStock = materialNorms.getNormsStock().stream()
                    .filter(normsStock -> StrUtil.equals(normsStock.getDepotId(), depotId)).findFirst().orElse(null);
                if (ObjectUtil.isNotEmpty(materialNormsStock)) {
                    depotAllStock = CalculationUtil.subtract(depotAllStock, materialNormsStock.getStock(), ErpConstants.NUM_AFTER_DOT);
                }
            }

            materialNormsStockService.saveMaterialNormsStock(materialId, depotId, normsId, depotAllStock, stockType);
            LOGGER.info("editMaterialNormsDepotStock is success.");
        } catch (Exception ee) {
            LOGGER.warn("editMaterialNormsDepotStock error, because {}", ee);
            if (ee instanceof CustomException) {
                throw new CustomException(ee.getMessage());
            }
            throw new RuntimeException(ee.getMessage());
        } finally {
            lock.unlock();
        }
    }

    private String getNewStockNum(int type, String changeNumber, String depotAllStock, int stockType) {
        if (type == DepotPutOutType.PUT.getKey()) {
            // 入库
            depotAllStock = CalculationUtil.add(depotAllStock, changeNumber, ErpConstants.NUM_AFTER_DOT);
        } else if (type == DepotPutOutType.OUT.getKey()) {
            // 出库
            depotAllStock = CalculationUtil.subtract(depotAllStock, changeNumber, ErpConstants.NUM_AFTER_DOT);
        }
        if (CalculationUtil.compareTo(depotAllStock, CommonNumConstants.NUM_ZERO.toString(), 0, RoundingMode.UP) < 0) {
            if (stockType == MaterialNormsStockType.ORDER_STOCK.getKey()) {
                // 只有现有库存类型的，才提示库存不足
                throw new CustomException("当前库存不足，无法操作.");
            } else if (stockType == MaterialNormsStockType.IN_TRANSIT_STOCK.getKey() || stockType == MaterialNormsStockType.ALLOCATED_STOCK.getKey()) {
                // 在途物料 || 已分配物料 小于0时，设置为0
                depotAllStock = CommonNumConstants.NUM_ZERO.toString();
            }
        }
        return depotAllStock;
    }

    /**
     * erp相关单据撤销审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void editDepotHeadToRevoke(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String serviceClassName = map.get("serviceClassName").toString();
        try {
            Class<?> clazz = Class.forName(serviceClassName);
            SkyeyeErpOrderService skyeyeErpOrderService = (SkyeyeErpOrderService) SpringUtils.getBean(clazz);
            skyeyeErpOrderService.revoke(inputObject, outputObject);
        } catch (Exception ex) {
            throw new RuntimeException("editDepotHeadToRevoke error", ex);
        }
    }

    /**
     * 订单信息提交审核
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void orderSubmitToApproval(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String serviceClassName = map.get("serviceClassName").toString();
        try {
            Class<?> clazz = Class.forName(serviceClassName);
            SkyeyeErpOrderService skyeyeErpOrderService = (SkyeyeErpOrderService) SpringUtils.getBean(clazz);
            skyeyeErpOrderService.submitToApproval(inputObject, outputObject);
        } catch (Exception ex) {
            throw new RuntimeException("orderSubmitToApproval error", ex);
        }
    }

}
