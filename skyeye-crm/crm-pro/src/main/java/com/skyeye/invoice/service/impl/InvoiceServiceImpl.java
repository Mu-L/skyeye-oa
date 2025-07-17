/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.invoice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeFlowableServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.contract.service.CrmContractService;
import com.skyeye.eve.service.IAreaService;
import com.skyeye.invoice.classenum.CrmInvoiceAuthEnum;
import com.skyeye.invoice.dao.InvoiceDao;
import com.skyeye.invoice.entity.Invoice;
import com.skyeye.invoice.service.InvoiceHeaderService;
import com.skyeye.invoice.service.InvoiceService;
import com.skyeye.payment.service.PaymentCollectionService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public void queryAllInvoiceList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<Invoice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Invoice::getState), FlowableStateEnum.PASS.getKey());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Invoice::getCreateTime));
        List<Invoice> bean = list(queryWrapper);
        // 合同信息
        crmContractService.setDataMation(bean, Invoice::getContractId);
        // 回款信息
        paymentCollectionService.setDataMation(bean, Invoice::getPaymentCollectionId);
        // 发票抬头
        invoiceHeaderService.setDataMation(bean, Invoice::getInvoiceHeaderId);
        bean.forEach(item -> {
            item.setServiceClassName(getServiceClassName());
        });
        outputObject.setBeans(bean);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void queryInvoiceStatistics(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String year = (String) params.get("year");
        String month = (String) params.get("month");
        if (StrUtil.isNotEmpty(month)) {
            int yearInt = Integer.parseInt(year);
            int monthInt = Integer.parseInt(month);
            String startPeriod=year + StrUtil.DASHED + month;
            String endPeriod = year + StrUtil.DASHED + month;
            if (monthInt == CommonNumConstants.NUM_ONE) {
                // 如果是1月，则上期是去年12月
                startPeriod = (yearInt - CommonNumConstants.NUM_ONE) + StrUtil.DASHED + CommonNumConstants.NUM_TWELVE;
                endPeriod = (yearInt - CommonNumConstants.NUM_ONE) + StrUtil.DASHED + CommonNumConstants.NUM_TWELVE;
            }
            List<Map<String, Object>> beans = getBeans(startPeriod, endPeriod);
            outputObject.setBeans(beans);

        } else {
            String startPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_ZERO + CommonNumConstants.NUM_ONE; // 本期开始时间
            String endPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_TWELVE;  // 本期结束时间
            List<Map<String, Object>> beans = getBeans(startPeriod, endPeriod);
            outputObject.setBeans(beans);
        }
    }

    private List<Map<String, Object>> getBeans(String startPeriod, String endPeriod) {
        QueryWrapper<Invoice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Invoice::getState), FlowableStateEnum.PASS.getKey());
        queryWrapper.apply("date_format(" + MybatisPlusUtil.toColumns(Invoice::getCreateTime) + ", '%Y-%m') >= {0}", startPeriod)
                .apply("date_format(" + MybatisPlusUtil.toColumns(Invoice::getCreateTime) + ", '%Y-%m') <= {0}", endPeriod);
        List<Invoice> bean = list(queryWrapper);
        List<Map<String, Object>> beans = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(bean)) {
            Map<String, List<Invoice>> map = bean.stream().collect(Collectors.groupingBy(Invoice::getTypeId));
            for (Map.Entry<String, List<Invoice>> entry : map.entrySet()) {
                Map<String, Object> result = new HashMap<>();
                result.put("typeId", entry.getKey());
                result.put("count", entry.getValue().size());
                String price = String.valueOf(CommonNumConstants.NUM_ZERO);
                for (Invoice invoice : entry.getValue()) {
                    price = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                            StrUtil.isEmpty(invoice.getPrice()) ? "0" : invoice.getPrice(),
                            price);
                }
                result.put("price", price);
                beans.add(result);
            }
        }
        return beans;
    }

    @Override
    public void queryAllInvoicesLists(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<Invoice> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Invoice::getCreateTime));
        if (StrUtil.isNotEmpty(commonPageInfo.getKeyword())) {
            queryWrapper.like(MybatisPlusUtil.toColumns(Invoice::getOddNumber), commonPageInfo.getKeyword());
        }
        List<Invoice> bean = list(queryWrapper);
        // 合同信息
        crmContractService.setDataMation(bean, Invoice::getContractId);
        // 回款信息
        paymentCollectionService.setDataMation(bean, Invoice::getPaymentCollectionId);
        // 发票抬头
        invoiceHeaderService.setDataMation(bean, Invoice::getInvoiceHeaderId);
        iAuthUserService.setName(bean,"lastUpdateId","lastUpdateName");
        iAuthUserService.setName(bean,"createId","createName");
        bean.forEach(item -> {
            item.setServiceClassName(getServiceClassName());
        });
        outputObject.setBeans(bean);
        outputObject.settotal(page.getTotal());
    }
}
