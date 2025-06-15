/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.business.service.SkyeyeErpOrderService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.eve.flowable.classenum.FormSubType;
import com.skyeye.exception.CustomException;
import com.skyeye.jedis.util.RedisLock;
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
     * @param type       出入库类型，参考#DepotPutOutType
     */
    @Override
    public void editMaterialNormsDepotStock(String depotId, String materialId, String normsId, Integer operNumber, int type) {
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
            int depotAllStock = ObjectUtil.isNotEmpty(materialNorms.getDepotTock()) ? materialNorms.getDepotTock().getAllStock() : CommonNumConstants.NUM_ZERO;
            depotAllStock = getNewStockNum(type, operNumber, depotAllStock);
            // 减去初始库存
            if (CollectionUtil.isNotEmpty(materialNorms.getNormsStock())) {
                MaterialNormsStock materialNormsStock = materialNorms.getNormsStock().stream()
                    .filter(normsStock -> StrUtil.equals(normsStock.getDepotId(), depotId)).findFirst().orElse(null);
                if (ObjectUtil.isNotEmpty(materialNormsStock)) {
                    depotAllStock = depotAllStock - materialNormsStock.getStock();
                }
            }
            materialNormsStockService.saveMaterialNormsOrderStock(materialId, depotId, normsId, depotAllStock);
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

    private int getNewStockNum(int type, int changeNumber, int depotAllStock) {
        if (type == DepotPutOutType.PUT.getKey()) {
            // 入库
            depotAllStock = depotAllStock + changeNumber;
        } else if (type == DepotPutOutType.OUT.getKey()) {
            // 出库
            depotAllStock = depotAllStock - changeNumber;
        }
        if (depotAllStock < 0) {
            throw new CustomException("当前库存不足，无法操作.");
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
        String processInstanceId = map.get("processInstanceId").toString();
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        try {
            Class<?> clazz = Class.forName(serviceClassName);
            SkyeyeErpOrderService skyeyeErpOrderService = (SkyeyeErpOrderService) SpringUtils.getBean(clazz);
            skyeyeErpOrderService.revoke(processInstanceId, userId);
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
        String id = map.get("id").toString();
        String modelKey = map.get("modelKey").toString();
        String approvalId = map.get("approvalId").toString();
        try {
            Class<?> clazz = Class.forName(serviceClassName);
            SkyeyeErpOrderService skyeyeErpOrderService = (SkyeyeErpOrderService) SpringUtils.getBean(clazz);
            skyeyeErpOrderService.submitToApproval(id, FormSubType.SUB_FLOWABLE.getKey(), approvalId, modelKey);
        } catch (Exception ex) {
            throw new RuntimeException("orderSubmitToApproval error", ex);
        }
    }

}
