/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.invoice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeFlowableServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;

import com.skyeye.contract.service.SupplierContractService;
import com.skyeye.eve.service.IAreaService;

import com.skyeye.invoice.classenum.ErpInvoiceAuthEnum;
import com.skyeye.invoice.dao.SupplierInvoiceDao;

import com.skyeye.invoice.entity.SupplierInvoice;


import com.skyeye.invoice.service.SupplierInvoiceHeaderService;
import com.skyeye.invoice.service.SupplierInvoiceService;

import com.skyeye.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: SupplierInvoiceServiceImpl
 * @Description: 发票服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/3 19:53
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "发票管理", groupName = "发票管理", teamAuth = true, flowable = true)
public class SupplierInvoiceServiceImpl extends SkyeyeFlowableServiceImpl<SupplierInvoiceDao, SupplierInvoice> implements SupplierInvoiceService {

    @Autowired
    private SupplierContractService supplierContractService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private SupplierInvoiceHeaderService supplierInvoiceHeaderService;

    @Autowired
    private IAreaService iAreaService;

    @Override
    public Class getAuthEnumClass() {
        return ErpInvoiceAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(ErpInvoiceAuthEnum.ADD.getKey(), ErpInvoiceAuthEnum.EDIT.getKey(), ErpInvoiceAuthEnum.DELETE.getKey(),
                ErpInvoiceAuthEnum.REVOKE.getKey(), ErpInvoiceAuthEnum.INVALID.getKey(), ErpInvoiceAuthEnum.SUBMIT_TO_APPROVAL.getKey(),
                ErpInvoiceAuthEnum.LIST.getKey());
    }

    @Override
    public QueryWrapper<SupplierInvoice> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<SupplierInvoice> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SupplierInvoice::getObjectId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        supplierContractService.setMationForMap(beans, "contractId", "contractMation");
        paymentService.setMationForMap(beans, "paymentCollectionId", "paymentCollectionMation");
        supplierInvoiceHeaderService.setMationForMap(beans, "invoiceHeaderId", "invoiceHeaderMation");
        return beans;
    }

    @Override
    public SupplierInvoice selectById(String id) {
        SupplierInvoice invoice = super.selectById(id);
        // 合同信息
        supplierContractService.setDataMation(invoice, SupplierInvoice::getContractId);
        // 付款信息
        paymentService.setDataMation(invoice, SupplierInvoice::getPaymentCollectionId);
        // 发票抬头
        supplierInvoiceHeaderService.setDataMation(invoice, SupplierInvoice::getInvoiceHeaderId);
        iAreaService.setDataMation(invoice, SupplierInvoice::getProvinceId);
        iAreaService.setDataMation(invoice, SupplierInvoice::getCityId);
        iAreaService.setDataMation(invoice, SupplierInvoice::getAreaId);
        iAreaService.setDataMation(invoice, SupplierInvoice::getTownshipId);
        return invoice;
    }

    @Override
    public void approvalEndIsSuccess(SupplierInvoice entity) {
        // 修改付款的开票金额
        paymentService.updateInvoicePrice(entity.getPaymentCollectionId(), entity.getPrice());
    }

}
