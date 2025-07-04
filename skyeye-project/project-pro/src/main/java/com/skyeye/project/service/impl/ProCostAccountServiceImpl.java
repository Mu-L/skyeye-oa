package com.skyeye.project.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
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
import java.util.List;
import java.util.Map;

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
        if(Double.parseDouble(entity.getTotalPrice())<0){
            throw new CustomException("总金额不能小于0");
        }
    }

    @Override
    public void createPrepose(CostAccount entity) {
        super.createPrepose(entity);
        if (entity.getCostType() == ProAddFlagEnum.HAND_ADD.getKey()) {
            String pointTime = DateUtil.getPointTime(DateUtil.YYYY_MM);
            entity.setCreateTime(pointTime);
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
        if (StrUtil.isEmpty(commonPageInfo.getType())) {
            throw new CustomException("成本类型不能为空");
        }
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        String lastMonth = DateUtil.getLastMonthDate();
        QueryWrapper<CostAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CostAccount::getCostType), Integer.parseInt(commonPageInfo.getType()));
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(CostAccount::getTotalPrice));
        queryWrapper.eq(MybatisPlusUtil.toColumns(CostAccount::getCreateTime), lastMonth);
        List<CostAccount> bean = list(queryWrapper);
        proProjectService.setDataMation(bean, CostAccount::getProjectId);
        outputObject.setBeans(bean);
        outputObject.settotal(page.getTotal());
    }
}
