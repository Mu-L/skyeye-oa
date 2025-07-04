/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.assets.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeFlowableServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableChildStateEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.MapUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.assets.classenum.PurchaseOrderStateEnum;
import com.skyeye.eve.assets.classenum.PurchasePutFromType;
import com.skyeye.eve.assets.classenum.PurchaseReturnFromType;
import com.skyeye.eve.assets.dao.AssetPurchaseDao;
import com.skyeye.eve.assets.entity.AssetPurchase;
import com.skyeye.eve.assets.entity.AssetPurchaseLink;
import com.skyeye.eve.assets.entity.AssetPurchasePut;
import com.skyeye.eve.assets.entity.AssetPurchaseReturn;
import com.skyeye.eve.assets.service.*;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.project.service.IProProjectService;
import org.openxmlformats.schemas.drawingml.x2006.chart.STRotY;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: AssetPurchaseServiceImpl
 * @Description: 资产采购单服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/18 23:29
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "资产采购", groupName = "资产模块", flowable = true)
public class AssetPurchaseServiceImpl extends SkyeyeFlowableServiceImpl<AssetPurchaseDao, AssetPurchase> implements AssetPurchaseService {

    @Autowired
    private AssetPurchaseLinkService assetPurchaseLinkService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private AssetPurchasePutService assetPurchasePutService;

    @Autowired
    private AssetPurchaseReturnService assetPurchaseReturnService;

    @Autowired
    private IProProjectService iProProjectService;

    @Override
    public QueryWrapper<AssetPurchase> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<AssetPurchase> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AssetPurchase::getIdKey), getServiceClassName());
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AssetPurchase::getProjectId), commonPageInfo.getObjectId());
            String lastMonth = DateUtil.getLastMonthDate();
            queryWrapper.apply("DATE_FORMAT(" + MybatisPlusUtil.toColumns(AssetPurchase::getCreateTime) + ", '%Y-%m') = {0}", lastMonth);
            queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(AssetPurchase::getCreateTime));
            queryWrapper.and(w -> {
                w.eq(MybatisPlusUtil.toColumns(AssetPurchase::getState), FlowableStateEnum.PASS.getKey())
                        .or().eq(MybatisPlusUtil.toColumns(AssetPurchase::getState), PurchaseOrderStateEnum.COMPLETED.getKey());
            });
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iProProjectService.setMationForMap(beans, "projectId", "projectMation");
        return beans;
    }

    @Override
    public void validatorEntity(AssetPurchase entity) {
        checkOrderItem(entity.getPurchaseLinks());
        getTotalPrice(entity);
    }

    @Override
    public void createPrepose(AssetPurchase entity) {
        entity.setIdKey(getServiceClassName());
        super.createPrepose(entity);
    }

    @Override
    public void writeChild(AssetPurchase entity, String userId) {
        assetPurchaseLinkService.saveLinkList(entity.getId(), entity.getPurchaseLinks());
        super.writeChild(entity, userId);
    }

    private void checkOrderItem(List<AssetPurchaseLink> assetPurchaseLinks) {
        List<String> assetIds = assetPurchaseLinks.stream()
                .map(bean -> String.format(Locale.ROOT, "%s-%s", bean.getFromId(), bean.getAssetId())).distinct()
                .collect(Collectors.toList());
        if (assetPurchaseLinks.size() != assetIds.size()) {
            throw new CustomException("单据中不允许相同来源的同一资产信息");
        }
    }

    private void getTotalPrice(AssetPurchase entity) {
        String totalPrice = "0";
        // 计算关联的资产总价
        for (AssetPurchaseLink purchaseLink : entity.getPurchaseLinks()) {
            String amountOfMoney = CalculationUtil.multiply(CommonNumConstants.NUM_TWO, String.valueOf(purchaseLink.getPurchaseNum()), purchaseLink.getUnitPrice());
            purchaseLink.setAmountOfMoney(amountOfMoney);
            totalPrice = CalculationUtil.add(totalPrice, amountOfMoney);
        }
        entity.setAllPrice(totalPrice);
    }

    @Override
    public void submitToApprovalPostpose(String id, String processInstanceId) {
        super.submitToApprovalPostpose(id, processInstanceId);
        assetPurchaseLinkService.editStateByPId(id, FlowableChildStateEnum.IN_EXAMINE.getKey());
    }

    @Override
    public AssetPurchase getDataFromDb(String id) {
        AssetPurchase assetPurchase = super.getDataFromDb(id);
        List<AssetPurchaseLink> assetPurchaseLinks = assetPurchaseLinkService.selectByPId(assetPurchase.getId());
        assetPurchase.setPurchaseLinks(assetPurchaseLinks);
        return assetPurchase;
    }

    @Override
    public AssetPurchase selectById(String id) {
        AssetPurchase assetPurchase = super.selectById(id);
        // 获取资产信息
        assetService.setDataMation(assetPurchase.getPurchaseLinks(), AssetPurchaseLink::getAssetId);
        assetPurchase.getPurchaseLinks().forEach(bean -> {
            bean.setStateName(FlowableChildStateEnum.getStateName(bean.getState()));
        });
        // 资产来源信息
        iSysDictDataService.setDataMation(assetPurchase.getPurchaseLinks(), AssetPurchaseLink::getFromId);
        assetPurchase.setStateName(FlowableStateEnum.getStateName(assetPurchase.getState()));
        iAuthUserService.setName(assetPurchase, "createId", "createName");
        iProProjectService.setDataMation(assetPurchase, AssetPurchase::getProjectId);
        return assetPurchase;
    }

    @Override
    public void revokePostpose(AssetPurchase entity) {
        super.revokePostpose(entity);
        assetPurchaseLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.DRAFT.getKey());
    }

    @Override
    public void approvalEndIsSuccess(AssetPurchase entity) {
        // 修改子单据状态
        assetPurchaseLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.ADEQUATE.getKey());
    }

    @Override
    public void approvalEndIsFailed(AssetPurchase entity) {
        assetPurchaseLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.REJECT.getKey());
    }

    @Override
    public void queryAssetPurchaseOrderTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        AssetPurchase assetPurchase = selectById(id);
        // 获取已经下达采购退货单的资产信息
        Map<String, Integer> returnExecuteNum = assetPurchaseReturnService.calcAssetNumByFromId(assetPurchase.getId());
        // 获取已经下达采购入库单的资产信息
        Map<String, Integer> putExecuteNum = assetPurchasePutService.calcAssetNumByFromId(assetPurchase.getId());
        assetPurchase.getPurchaseLinks().forEach(purchaseLink -> {
            // 采购订单数量 - 已入库数量 - 已退货数量
            Integer surplusNum = purchaseLink.getPurchaseNum()
                    - (putExecuteNum.containsKey(purchaseLink.getAssetId()) ? putExecuteNum.get(purchaseLink.getAssetId()) : 0)
                    - (returnExecuteNum.containsKey(purchaseLink.getAssetId()) ? returnExecuteNum.get(purchaseLink.getAssetId()) : 0);
            // 设置未下达采购入库单/采购退货单的资产数量
            purchaseLink.setPurchaseNum(surplusNum);
        });

        // 过滤掉数量为0的进行生成采购入库单/退货单
        assetPurchase.setPurchaseLinks(assetPurchase.getPurchaseLinks().stream()
                .filter(purchaseLink -> purchaseLink.getPurchaseNum() > 0).collect(Collectors.toList()));
        outputObject.setBean(assetPurchase);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertAssetPurchaseOrderToTurnPut(InputObject inputObject, OutputObject outputObject) {
        AssetPurchasePut assetPurchasePut = inputObject.getParams(AssetPurchasePut.class);
        // 获取采购单状态
        AssetPurchase order = selectById(assetPurchasePut.getId());
        if (ObjectUtil.isEmpty(order)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过/部分完成的可以转到采购入库单
        if (FlowableStateEnum.PASS.getKey().equals(order.getState()) || PurchaseOrderStateEnum.PARTIALLY_COMPLETED.getKey().equals(order.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            assetPurchasePut.setFromId(assetPurchasePut.getId());
            assetPurchasePut.setFromTypeId(PurchasePutFromType.PURCHASE_ORDER.getKey());
            assetPurchasePut.setId(StrUtil.EMPTY);
            assetPurchasePutService.createEntity(assetPurchasePut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达采购入库单.");
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertAssetPurchaseOrderToReturns(InputObject inputObject, OutputObject outputObject) {
        AssetPurchaseReturn assetPurchaseReturn = inputObject.getParams(AssetPurchaseReturn.class);
        // 获取采购单状态
        AssetPurchase order = selectById(assetPurchaseReturn.getId());
        if (ObjectUtil.isEmpty(order)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过/部分完成的可以转到采购退货单
        if (FlowableStateEnum.PASS.getKey().equals(order.getState()) || PurchaseOrderStateEnum.PARTIALLY_COMPLETED.getKey().equals(order.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            assetPurchaseReturn.setFromId(assetPurchaseReturn.getId());
            assetPurchaseReturn.setFromTypeId(PurchaseReturnFromType.PURCHASE_ORDER.getKey());
            assetPurchaseReturn.setId(StrUtil.EMPTY);
            assetPurchaseReturnService.createEntity(assetPurchaseReturn, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达采购退货单.");
        }
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
        QueryWrapper<AssetPurchase> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, ids);
        List<AssetPurchase> entityList = list(queryWrapper);
        Map<String, AssetPurchase> entityMap = entityList.stream().collect(Collectors.toMap(AssetPurchase::getId, bean -> bean));
        for (Map<String, Object> bean : beans) {
            if (!MapUtil.checkKeyIsNull(bean, idKey)) {
                AssetPurchase entity = entityMap.get(bean.get(idKey).toString());
                if (ObjectUtil.isEmpty(entity)) {
                    continue;
                }
                bean.put(mationKey, entity);
            }
        }
    }

    @Override
    public void queryLastMonthAssetPurchaseCost(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<AssetPurchase> queryWrapper = new QueryWrapper<>();
        //获取上个月日期
        String lastMonth = DateUtil.getLastMonthDate();
        queryWrapper.apply("DATE_FORMAT("+MybatisPlusUtil.toColumns(AssetPurchase::getCreateTime)+", '%Y-%m') = {0}",lastMonth);
        queryWrapper.isNotNull(MybatisPlusUtil.toColumns(AssetPurchase::getProjectId));
        queryWrapper.eq(MybatisPlusUtil.toColumns(AssetPurchase::getIdKey), getServiceClassName());
        List<AssetPurchase> bean = list(queryWrapper);
        List<Map<String,Object>> result = new ArrayList<>();
        if(CollectionUtil.isEmpty(bean)){
            outputObject.setBeans(result);
            return;
        }
        // 根据projectId分组
        Map<String, List<AssetPurchase>> groupMap = bean.stream().collect(Collectors.groupingBy(AssetPurchase::getProjectId));
        for (Map.Entry<String, List<AssetPurchase>> entry : groupMap.entrySet()) {
            Map<String,Object> map = new HashMap<>();
            String price = String.valueOf(CommonNumConstants.NUM_ZERO);
            map.put("projectId",entry.getKey());
            for (AssetPurchase assetPurchase : entry.getValue()) {
                price = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                        StrUtil.isEmpty(assetPurchase.getAllPrice()) ? "0" : assetPurchase.getAllPrice(),
                        price);
            }
            map.put("price",price);
            result.add(map);
        }
        outputObject.setBeans(result);
    }
}
