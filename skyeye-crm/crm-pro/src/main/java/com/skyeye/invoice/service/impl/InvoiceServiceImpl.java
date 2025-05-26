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
import com.skyeye.contract.service.CrmContractService;
import com.skyeye.eve.service.IAreaService;
import com.skyeye.invoice.classenum.CrmInvoiceAuthEnum;
import com.skyeye.invoice.dao.InvoiceDao;
import com.skyeye.invoice.entity.Invoice;
import com.skyeye.invoice.service.InvoiceHeaderService;
import com.skyeye.invoice.service.InvoiceService;
import com.skyeye.payment.service.PaymentCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: InvoiceServiceImpl
 * @Description: 发票服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/3 19:53
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "发票管理", groupName = "发票管理", teamAuth = true, flowable = true)
public class InvoiceServiceImpl extends SkyeyeFlowableServiceImpl<InvoiceDao, Invoice> implements InvoiceService {

    @Autowired
    private CrmContractService crmContractService;

    @Autowired
    private PaymentCollectionService paymentCollectionService;

    @Autowired
    private InvoiceHeaderService invoiceHeaderService;

    @Autowired
    private IAreaService iAreaService;

    @Override
    public Class getAuthEnumClass() {
        return CrmInvoiceAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(CrmInvoiceAuthEnum.ADD.getKey(), CrmInvoiceAuthEnum.EDIT.getKey(), CrmInvoiceAuthEnum.DELETE.getKey(),
                CrmInvoiceAuthEnum.REVOKE.getKey(), CrmInvoiceAuthEnum.SUBMIT_TO_APPROVAL.getKey(),
                CrmInvoiceAuthEnum.LIST.getKey());
    }

    @Override
    public QueryWrapper<Invoice> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Invoice> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Invoice::getObjectId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        crmContractService.setMationForMap(beans, "contractId", "contractMation");
        paymentCollectionService.setMationForMap(beans, "paymentCollectionId", "paymentCollectionMation");
        invoiceHeaderService.setMationForMap(beans, "invoiceHeaderId", "invoiceHeaderMation");
        return beans;
    }

    @Override
    public Invoice selectById(String id) {
        Invoice invoice = super.selectById(id);
        // 合同信息
        crmContractService.setDataMation(invoice, Invoice::getContractId);
        // 回款信息
        paymentCollectionService.setDataMation(invoice, Invoice::getPaymentCollectionId);
        // 发票抬头
        invoiceHeaderService.setDataMation(invoice, Invoice::getInvoiceHeaderId);
        iAreaService.setDataMation(invoice, Invoice::getProvinceId);
        iAreaService.setDataMation(invoice, Invoice::getCityId);
        iAreaService.setDataMation(invoice, Invoice::getAreaId);
        iAreaService.setDataMation(invoice, Invoice::getTownshipId);
        return invoice;
    }

    @Override
    public void approvalEndIsSuccess(Invoice entity) {
        // 修改回款的开票金额
        paymentCollectionService.updateInvoicePrice(entity.getPaymentCollectionId(), entity.getPrice());
    }

}
