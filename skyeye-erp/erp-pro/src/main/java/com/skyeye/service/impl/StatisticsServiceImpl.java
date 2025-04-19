/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.base.handler.enclosure.helper.ServiceBeanToEntityHelper;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.CorrespondentEnterEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.crm.service.ICustomerService;
import com.skyeye.dao.StatisticsDao;
import com.skyeye.depot.classenum.DepotOutFromType;
import com.skyeye.depot.classenum.DepotPutFromType;
import com.skyeye.depot.service.ErpDepotService;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.service.StatisticsService;
import com.skyeye.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: StatisticsServiceImpl
 * @Description: erp统计服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 12:11
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private StatisticsDao statisticsDao;

    @Autowired
    protected MaterialService materialService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    protected ErpDepotService erpDepotService;

    @Autowired
    protected ICustomerService iCustomerService;

    @Autowired
    protected SupplierService supplierService;

    /**
     * 入库明细
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryWarehousingDetails(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        pageInfo.setObjectBusiness(DepotPutFromType.getAllIdKeys());
        getBeans(outputObject, pageInfo);
    }

    private void getBeans(OutputObject outputObject, CommonPageInfo pageInfo) {
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        pageInfo.setState(FlowableStateEnum.PASS.getKey());
        List<Map<String, Object>> beans = statisticsDao.queryErpOrderItem(pageInfo);
        materialService.setMationForMap(beans, "materialId", "materialMation");
        materialNormsService.setMationForMap(beans, "normsId", "normsMation");
        erpDepotService.setMationForMap(beans, "depotId", "depotMation");
        beans.forEach(bean -> {
            Object idKeyObj = bean.get("idKey");
            if (idKeyObj != null && !idKeyObj.toString().trim().isEmpty()) {
                String idKey = idKeyObj.toString();
                String serviceName = null;
                try {
                    serviceName = ServiceBeanToEntityHelper.getServiceName(idKey);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                bean.put("serviceName", serviceName);
            } else {
                bean.put("serviceName", null);
            }
        });
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 出库明细
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryOutgoingDetails(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        pageInfo.setObjectBusiness(DepotOutFromType.getAllIdKeys());
        getBeans(outputObject, pageInfo);
    }

    /**
     * 进货统计
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryInComimgDetails(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        // ERP入库操作相关单据类型的集合
        List<String> subTypeList = Arrays.asList(DepotPutFromType.PURCHASE_PUT.getIdKey(),
            DepotPutFromType.SEAL_RETURNS.getIdKey(),
            DepotPutFromType.RETAIL_RETURNS.getIdKey(),
            DepotPutFromType.OTHER_WARE_HOUS.getIdKey());

        // ERP退货入库操作相关单据类型的集合
        List<String> returnSubTypeList = Arrays.asList(DepotPutFromType.SEAL_RETURNS.getIdKey(),
            DepotPutFromType.RETAIL_RETURNS.getIdKey());
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        List<Map<String, Object>> beans = getPointSubTypeOrderStatistics(commonPageInfo, subTypeList, returnSubTypeList);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    private List<Map<String, Object>> getPointSubTypeOrderStatistics(CommonPageInfo commonPageInfo, List<String> allSubType, List<String> returnSubTyoe) {
        commonPageInfo.setObjectBusiness(allSubType);
        List<Map<String, Object>> beans = statisticsDao.queryPointSubTypeOrder(commonPageInfo);

        commonPageInfo.setObjectBusiness(returnSubTyoe);
        List<Map<String, Object>> returnBeans = statisticsDao.queryPointSubTypeOrder(commonPageInfo);
        Map<String, Map<String, Object>> returnMaps = returnBeans.stream().collect(Collectors.toMap(bean -> bean.get("normsId").toString(), bean -> bean));

        // 设置退货入库的数量信息
        beans.forEach(bean -> {
            String normsId = bean.get("normsId").toString();
            Map<String, Object> returnBean = returnMaps.get(normsId);
            if (CollectionUtils.isEmpty(returnBean)) {
                bean.put("returnCurrentTock", CommonNumConstants.NUM_ZERO);
                bean.put("returnCurrentTockMoney", CommonNumConstants.NUM_ZERO);
            } else {
                bean.put("returnCurrentTock", returnBean.get("currentTock"));
                bean.put("returnCurrentTockMoney", returnBean.get("currentTockMoney"));
            }
        });
        // 获取商品规格信息
        materialService.setMationForMap(beans, "materialId", "materialMation");
        materialNormsService.setMationForMap(beans, "normsId", "normsMation");
        return beans;
    }

    /**
     * 销售统计
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void querySalesDetails(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        // ERP出库操作相关单据类型的集合
        List<String> subTypeList = Arrays.asList(DepotOutFromType.SEAL_OUTLET.getIdKey(),
            DepotOutFromType.PURCHASE_RETURNS.getIdKey(),
            DepotOutFromType.RETAIL_OUTLET.getIdKey(),
            DepotOutFromType.OTHER_OUTLET.getIdKey());

        // ERP退货出库操作相关单据类型的集合
        List<String> returnSubTypeList = Arrays.asList(DepotOutFromType.PURCHASE_RETURNS.getIdKey());
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        List<Map<String, Object>> beans = getPointSubTypeOrderStatistics(commonPageInfo, subTypeList, returnSubTypeList);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 客户对账
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryCustomerReconciliationDetails(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        pageInfo.setState(FlowableStateEnum.PASS.getKey());
        pageInfo.setHolderKey(CorrespondentEnterEnum.CUSTOM.getKey());
        List<Map<String, Object>> beans = statisticsDao.queryErpOrderListByIdKey(pageInfo);
        iCustomerService.setMationForMap(beans, "holderId", "holderMation");
        beans.forEach(bean -> {
            String idKey = bean.get("idKey").toString();
            String serviceName = ServiceBeanToEntityHelper.getServiceName(idKey);
            bean.put("serviceName", serviceName);
        });
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 供应商对账
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void querySupplierReconciliationDetails(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        pageInfo.setState(FlowableStateEnum.PASS.getKey());
        pageInfo.setHolderKey(CorrespondentEnterEnum.SUPPLIER.getKey());
        List<Map<String, Object>> beans = statisticsDao.queryErpOrderListByIdKey(pageInfo);
        supplierService.setMationForMap(beans, "holderId", "holderMation");
        beans.forEach(bean -> {
            String idKey = bean.get("idKey").toString();
            String serviceName = ServiceBeanToEntityHelper.getServiceName(idKey);
            bean.put("serviceName", serviceName);
        });
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

}
