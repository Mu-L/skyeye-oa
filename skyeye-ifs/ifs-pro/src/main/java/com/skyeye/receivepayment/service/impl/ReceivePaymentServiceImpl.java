package com.skyeye.receivepayment.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeFlowableServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.contacts.service.IContactsService;
import com.skyeye.receivepayment.classenum.ReceivePaymentKeyEnum;
import com.skyeye.receivepayment.dao.ReceivePaymentDao;
import com.skyeye.receivepayment.entity.ReceivePayment;
import com.skyeye.receivepayment.service.ReceivePaymentService;
import com.skyeye.rest.crm.contract.service.ICrmContractService;
import com.skyeye.rest.crm.payment.service.ICrmPaymentCollectionService;
import com.skyeye.rest.erp.contract.service.IErpContractService;
import com.skyeye.rest.erp.payment.service.IErpPaymentCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ReceivePaymentServiceImpl
 * @Description: 收付款管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/4 16:23
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "收付款管理", groupName = "收付款管理")
public class ReceivePaymentServiceImpl extends SkyeyeFlowableServiceImpl<ReceivePaymentDao, ReceivePayment> implements ReceivePaymentService {


    @Autowired
    private IContactsService iContactsService;

    @Autowired
    private ICrmContractService iCrmContractService;

    @Autowired
    private IErpContractService iErpContractService;

    @Autowired
    private ICrmPaymentCollectionService iCrmPaymentCollectionService;

    @Autowired
    private IErpPaymentCollectionService iErpPaymentCollectionService;


    @Override
    public void createPrepose(ReceivePayment entity) {
        if(StrUtil.isNotEmpty(entity.getId())){
            entity.setFromId(entity.getId());
            entity.setId(StrUtil.EMPTY);
            entity.setFromKey(entity.getServiceClassName());
        }
    }

    @Override
    public QueryWrapper<ReceivePayment> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ReceivePayment> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ReceivePayment::getObjectId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    public void queryReceivePaymentList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ReceivePayment> queryWrapper = getQueryWrapper(commonPageInfo);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ReceivePayment::getCreateTime));
        List<ReceivePayment> list = list(queryWrapper);
        List<ReceivePayment> beans = setInfo(list);
        outputObject.settotal(page.getTotal());
        outputObject.setBeans(beans);
    }
    
    private List<ReceivePayment> setInfo(List<ReceivePayment> list){
        if(CollectionUtil.isEmpty(list)){
            return list;
        }
        // 获取通过逗号隔开的ids
        String contractIds = list.stream().map(ReceivePayment::getContractId).collect(Collectors.joining(StrUtil.COMMA));
        String fromIds = list.stream().map(ReceivePayment::getFromId).collect(Collectors.joining(StrUtil.COMMA));
        List<Map<String, Object>> contractMation;
        // 回款信息
        List<Map<String, Object>> paymentCollection;
        if (ReceivePaymentKeyEnum.CRM_PAYMENT_KEY.getKey().equals(list.get(CommonNumConstants.NUM_ZERO).getObjectKey())) {
            // 查询合同信息
            contractMation = iCrmContractService.queryCrmContractByIds(contractIds);
            // 回款信息
            paymentCollection = iCrmPaymentCollectionService.queryPaymentCollectionById(fromIds);
        } else {
            // 查询合同信息
            contractMation = iErpContractService.querySupplierContractByIds(contractIds);
            // 付款信息
            paymentCollection = iErpPaymentCollectionService.queryPaymentCollectionById(fromIds);
        }
        // 转为map结构，健contractId
        Map<String, Map<String, Object>> contractMap = contractMation.stream().collect(Collectors.toMap(m -> m.get("id").toString(), m -> m));
        // 转为map结构，健fromId
        Map<String, Map<String, Object>> paymentMap = paymentCollection.stream().collect(Collectors.toMap(m -> m.get("id").toString(), m -> m));
        List<ReceivePayment> beans = list.stream().map(item -> {
            item.setContractMation(contractMap.getOrDefault(item.getContractId(), new HashMap<>()));
            item.setFromMation(paymentMap.getOrDefault(item.getFromId(), new HashMap<>()));
            item.setName(item.getOddNumber());
            return item;
        }).collect(Collectors.toList());
        iContactsService.setDataMation(beans,ReceivePayment::getContactId);
        return beans;
    }

    @Override
    public void queryReceivePaymentByContractId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String contractId = map.get("contractId").toString();
        if (StrUtil.isEmpty(contractId)) {
            return;
        }
        QueryWrapper<ReceivePayment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ReceivePayment::getContractId), contractId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ReceivePayment::getState), FlowableStateEnum.PASS.getKey());
        List<ReceivePayment> list = list(queryWrapper);
        List<ReceivePayment> beans = setInfo(list);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    @Override
    public ReceivePayment selectById(String id) {
        ReceivePayment receivePayment = super.selectById(id);
        iContactsService.setDataMation(receivePayment, ReceivePayment::getContactId);
        // 查询合同信息
        List<Map<String, Object>> contractMation;
        // 回款信息
        List<Map<String, Object>> paymentCollection;
        if (ReceivePaymentKeyEnum.CRM_PAYMENT_KEY.getKey().equals(receivePayment.getObjectKey())) {
            // 查询合同信息
              contractMation = iCrmContractService.queryCrmContractByIds(receivePayment.getContractId());
            // 回款信息
             paymentCollection = iCrmPaymentCollectionService.queryPaymentCollectionById(receivePayment.getFromId());
        } else {
            // 查询合同信息
           contractMation = iErpContractService.querySupplierContractByIds(receivePayment.getContractId());
            // 付款信息
           paymentCollection = iErpPaymentCollectionService.queryPaymentCollectionById(receivePayment.getFromId());
        }
        receivePayment.setContractMation(CollectionUtil.isEmpty(contractMation) ? new HashMap<>() : contractMation.get(CommonNumConstants.NUM_ZERO));
        receivePayment.setFromMation(CollectionUtil.isEmpty(paymentCollection) ? new HashMap<>() : paymentCollection.get(CommonNumConstants.NUM_ZERO));
        return receivePayment;
    }
}
