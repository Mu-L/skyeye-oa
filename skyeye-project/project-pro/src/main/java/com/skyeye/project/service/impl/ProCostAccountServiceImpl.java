package com.skyeye.project.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.project.classenum.ProAddFlagEnum;
import com.skyeye.project.classenum.ProCostAccountEnum;
import com.skyeye.project.dao.ProCostAccountDao;
import com.skyeye.project.entity.CostAccount;
import com.skyeye.project.service.ProCostAccountService;
import com.skyeye.project.service.ProProjectService;
import com.skyeye.rest.adm.articlespurchase.service.IAdmArticlePurchaseService;
import com.skyeye.rest.adm.assetpurchase.service.IAdmAssetPurchaseService;
import com.skyeye.rest.erp.equipment.service.IErpEquipmentService;
import com.skyeye.rest.erp.purchaseorder.service.IErpPurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ProCostAccountServiceImpl
 * @Description: 成本核算管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 13:04
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "成本核算管理", groupName = "成本核算管理")
public class ProCostAccountServiceImpl extends SkyeyeBusinessServiceImpl<ProCostAccountDao, CostAccount> implements ProCostAccountService {

    @Autowired
    private IErpEquipmentService iErpEquipmentService;

    @Autowired
    private IErpPurchaseOrderService iErpPurchaseOrderService;

    @Autowired
    private IAdmArticlePurchaseService iAdmArticlePurchaseService;

    @Autowired
    private IAdmAssetPurchaseService iAdmAssetPurchaseService;

    @Autowired
    private ProProjectService proProjectService;


    @Override
    public void validatorEntity(CostAccount entity) {
        super.validatorEntity(entity);
        if (Double.parseDouble(entity.getTotalPrice()) < 0) {
            throw new CustomException("总金额不能小于0");
        }
    }

    /**
     * 定时任务--计算成本核算
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void writeCostAccountRecord(String tenantId) {

        List<CostAccount> costAccountList = new ArrayList<>();
        //erp采购--材料成本
        List<Map<String, Object>> purchaseOrderList = iErpPurchaseOrderService.queryLastMonthPurchaseOrderCost();

        addCostAccountList(costAccountList, purchaseOrderList, ProCostAccountEnum.MATERIAL.getKey());
        // erp设备成本
        List<Map<String, Object>> equipmentCost = iErpEquipmentService.queryLastMonthEquipmentCost();
        addCostAccountList(costAccountList, equipmentCost, ProCostAccountEnum.EQUIPMENT.getKey());

        // 其他成本---行政的资产+用品
        // 用品采购
        List<Map<String, Object>> articlePurchaseCost = iAdmArticlePurchaseService.queryLastMonthAssetArticleCost();
        addCostAccountList(costAccountList, articlePurchaseCost, ProCostAccountEnum.OTHER.getKey());

        // 资产采购
        List<Map<String, Object>> assetPurchaseCost = iAdmAssetPurchaseService.queryLastMonthAssetPurchaseCost();
        addCostAccountList(costAccountList, assetPurchaseCost, ProCostAccountEnum.OTHER.getKey());

        createEntity(costAccountList, null);
    }

    private void addCostAccountList(List<CostAccount> costAccountList, List<Map<String, Object>> costList, Integer costType) {
        if (CollectionUtil.isNotEmpty(costList)) {
            String lastMonth = DateUtil.getLastMonthDate();
            for (Map<String, Object> map : costList) {
                CostAccount costAccount = new CostAccount();
                costAccount.setCostType(costType);
                costAccount.setProjectId(map.get("projectId").toString());
                costAccount.setTotalPrice(map.get("price").toString());
                costAccount.setAddFlag(ProAddFlagEnum.SYSTEM_ADD.getKey());
                costAccount.setCreateTime(lastMonth);
                costAccountList.add(costAccount);
            }
        }
    }

    @Override
    public void queryProCostAccountList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<CostAccount> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(commonPageInfo.getType())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(CostAccount::getCostType), Integer.parseInt(commonPageInfo.getType()));
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(CostAccount::getProjectId), commonPageInfo.getObjectId());
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(CostAccount::getTotalPrice));
        List<CostAccount> bean = list(queryWrapper);
        proProjectService.setDataMation(bean, CostAccount::getProjectId);
        iAuthUserService.setDataMation(bean, CostAccount::getCreateId);
        outputObject.setBeans(bean);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void queryCostAccountViews(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String startTime = params.get("startTime").toString();
        String endTime = params.get("endTime").toString();
        String projectId = params.get("projectId").toString();
        QueryWrapper<CostAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CostAccount::getProjectId), projectId);
        queryWrapper.apply(MybatisPlusUtil.toColumns(CostAccount::getCreateTime) + " >= {0}", startTime)
                .apply(MybatisPlusUtil.toColumns(CostAccount::getCreateTime) + " <= {0}", endTime);
        List<CostAccount> bean = list(queryWrapper);
        List<Map<String, Object>> beans = new ArrayList<>();
        if (CollectionUtil.isEmpty(bean)) {
            for (ProCostAccountEnum proCostAccountEnum : ProCostAccountEnum.values()){
                Map<String,Object> temp = new HashMap<>();
                temp.put("size",0);
                temp.put(proCostAccountEnum.getCode(),0);
                beans.add(temp);
            }
            Map<String,Object> temp = new HashMap<>();
            temp.put("size",0);
            temp.put("totalPrice",0);
            beans.add(temp);
            outputObject.setBean(beans);
            return;
        }
        // 根据costType分组
        Map<Integer, List<CostAccount>> group = bean.stream().collect(Collectors.groupingBy(CostAccount::getCostType));
        // 循环枚举ProCostAccountEnum
        for (ProCostAccountEnum proCostAccountEnum : ProCostAccountEnum.values()) {
            Map<String,Object> temp = new HashMap<>();
            List<CostAccount> list = group.getOrDefault(proCostAccountEnum.getKey(), new ArrayList<>());
            temp.put("size",list.size());
            if (CollectionUtil.isEmpty(list)) {
                temp.put(proCostAccountEnum.getCode(),0);
            }else {
                // 计算金额
                String price = String.valueOf(CommonNumConstants.NUM_ZERO);
                for (CostAccount costAccount : list) {
                    price = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                            StrUtil.isEmpty(costAccount.getTotalPrice()) ? "0" : costAccount.getTotalPrice(),
                            price);
                }
                temp.put(proCostAccountEnum.getCode(),price);
            }
           beans.add(temp);
        }
        // 计算总金额 循环result
        double totalPrice = bean.stream().mapToDouble(item -> Double.parseDouble(item.getTotalPrice())).sum();
        Map<String, Object> temp = new HashMap<>();
        temp.put("totalPrice", totalPrice);
        temp.put("size", bean.size());
        beans.add(temp);
        outputObject.setBeans(beans);
    }

    @Override
    public void queryAllProCostAccountList(InputObject inputObject, OutputObject outputObject) {
        String projectId = inputObject.getParams().get("projectId").toString();
        QueryWrapper<CostAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CostAccount::getProjectId), projectId);
        List<CostAccount> bean = list(queryWrapper);
        // 根据类型分组
        Map<Integer, List<CostAccount>> costAcooutMap = bean.stream().collect(Collectors.groupingBy(CostAccount::getCostType));
        List<Map<String, Object>> result = new ArrayList<>();
        for (ProCostAccountEnum proCostAccountEnum : ProCostAccountEnum.values()) {
            Map<String, Object> temp = new HashMap<>();
            List<CostAccount> list = costAcooutMap.getOrDefault(proCostAccountEnum.getKey(), new ArrayList<>());
            temp.put("costType", proCostAccountEnum.getKey());
            temp.put("costTypeName", proCostAccountEnum.getValue());
            temp.put("costList", list);
            result.add(temp);
        }
        outputObject.setBeans( result);
        outputObject.settotal(result.size());
    }

    @Override
    public CostAccount selectById(String id) {
        CostAccount costAccount = super.selectById(id);
        proProjectService.setDataMation(costAccount, CostAccount::getProjectId);
        iAuthUserService.setDataMation(costAccount, CostAccount::getCreateId);
        return costAccount;
    }
}
