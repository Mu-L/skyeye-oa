/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.payment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeFlowableServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.contract.service.SupplierContractService;
import com.skyeye.payable.service.PayableService;
import com.skyeye.payment.classenum.ErpPaymentAuthEnum;
import com.skyeye.payment.dao.PaymentDao;
import com.skyeye.payment.entity.Payment;
import com.skyeye.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: PaymentCollectionServiceImpl
 * @Description: 回款服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/2 20:34
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "供应商付款管理", groupName = "供应商付款管理", flowable = true, teamAuth = true)
public class PaymentServiceImpl extends SkyeyeFlowableServiceImpl<PaymentDao, Payment> implements PaymentService {

    @Autowired
    private SupplierContractService supplierContractService;

    @Autowired
    private PayableService payableService;

    @Override
    public Class getAuthEnumClass() {
        return ErpPaymentAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(ErpPaymentAuthEnum.ADD.getKey(), ErpPaymentAuthEnum.EDIT.getKey(), ErpPaymentAuthEnum.DELETE.getKey(),
                ErpPaymentAuthEnum.REVOKE.getKey(), ErpPaymentAuthEnum.SUBMIT_TO_APPROVAL.getKey(),
                ErpPaymentAuthEnum.LIST.getKey());
    }

    @Override
    public QueryWrapper<Payment> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Payment> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Payment::getObjectId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        supplierContractService.setMationForMap(beans, "contractId", "contractMation");
        return beans;
    }

    @Override
    public Payment selectById(String id) {
        Payment paymentCollection = super.selectById(id);
        paymentCollection.setName(paymentCollection.getOddNumber());
        // 合同信息
        supplierContractService.setDataMation(paymentCollection, Payment::getContractId);
        return paymentCollection;
    }

    @Override
    public void queryPaymentByContractId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String contractId = map.get("contractId").toString();
        if (StrUtil.isEmpty(contractId)) {
            return;
        }
        QueryWrapper<Payment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Payment::getContractId), contractId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Payment::getState), FlowableStateEnum.PASS.getKey());
        List<Payment> paymentCollections = list(queryWrapper);
        paymentCollections.forEach(paymentCollection -> {
            paymentCollection.setName(paymentCollection.getOddNumber());
        });
        outputObject.setBeans(paymentCollections);
        outputObject.settotal(paymentCollections.size());
    }

    @Override
    public void approvalEndIsSuccess(Payment entity) {
        // 修改合同的付款金额
        supplierContractService.updatePaymentPrice(entity.getContractId(), entity.getPrice());
        // 修改应付事项的已付款金额
        payableService.updateReceivablePaidPrice(entity.getPayableId(), entity.getPrice());
    }

    @Override
    public void updateInvoicePrice(String id, String invoicePrice) {
        Payment paymentCollection = selectById(id);
        String newInvoicePrice = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                StrUtil.isEmpty(paymentCollection.getInvoicePrice()) ? "0" : paymentCollection.getInvoicePrice(),
                invoicePrice);
        UpdateWrapper<Payment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Payment::getInvoicePrice), newInvoicePrice);
        update(updateWrapper);
        refreshCache(id);
        // 修改合同的开票金额
        supplierContractService.updateInvoicePrice(paymentCollection.getContractId(), invoicePrice);
    }

}
