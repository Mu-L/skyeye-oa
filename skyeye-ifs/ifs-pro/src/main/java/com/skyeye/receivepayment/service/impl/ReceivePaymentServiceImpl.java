package com.skyeye.receivepayment.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.base.business.service.impl.SkyeyeFlowableServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.CorrespondentEnterEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.contacts.service.IContactsService;
import com.skyeye.receivepayment.classenum.ReceivePaymentKeyEnum;
import com.skyeye.receivepayment.dao.ReceivePaymentDao;
import com.skyeye.receivepayment.entity.ReceivePayment;
import com.skyeye.receivepayment.service.ReceivePaymentService;
import com.skyeye.rest.crm.contract.service.ICrmContractService;
import com.skyeye.rest.crm.customer.service.ICrmCustomerService;
import com.skyeye.rest.crm.payment.service.ICrmPaymentCollectionService;
import com.skyeye.rest.erp.contract.service.IErpContractService;
import com.skyeye.rest.erp.payment.service.IErpPaymentCollectionService;
import com.skyeye.rest.erp.supplier.service.IErpSupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
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
public class ReceivePaymentServiceImpl extends SkyeyeBusinessServiceImpl<ReceivePaymentDao, ReceivePayment> implements ReceivePaymentService {

    @Autowired
    private ICrmPaymentCollectionService iCrmPaymentCollectionService;

    @Autowired
    private IErpPaymentCollectionService iErpPaymentCollectionService;

    @Autowired
    private ICrmCustomerService iCrmCustomerService;

    @Autowired
    private IErpSupplierService iErpSupplierService;

    @Autowired
    private ICrmContractService iCrmContractService;

    @Autowired
    private IErpContractService iErpContractService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @Override
    public void createPrepose(ReceivePayment entity) {
        super.createPrepose(entity);
        entity.setId(StrUtil.EMPTY);
        entity.setState(FlowableStateEnum.PASS.getKey());
    }

    @Override
    public QueryWrapper<ReceivePayment> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ReceivePayment> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectKey())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ReceivePayment::getObjectKey), commonPageInfo.getObjectKey());
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ReceivePayment::getCreateTime));
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        // 根据objectKey分组
        setMationForMaps(beans);
        return beans;
    }

    private void setMationForMaps(List<Map<String, Object>> beans) {
        Map<String, List<Map<String, Object>>> map = beans.stream().collect(Collectors.groupingBy(m -> m.get("objectKey").toString()));
        List<Map<String, Object>> erpBeans = map.getOrDefault(CorrespondentEnterEnum.SUPPLIER.getKey(), new ArrayList<>());
        List<Map<String, Object>> customerBeans = map.getOrDefault(CorrespondentEnterEnum.CUSTOM.getKey(), new ArrayList<>());
        if(CollectionUtil.isNotEmpty(erpBeans)){
            // 供应商信息
            List<String> supplierIdList = erpBeans.stream().map(m -> m.get("objectId").toString()).distinct().collect(Collectors.toList());
            String supplierIds = String.join(StrUtil.COMMA, supplierIdList);
            List<Map<String, Object>> supplierList = iErpSupplierService.querySupplierListByIds(supplierIds);
            Map<String,Map<String, Object>> supplierMap = supplierList.stream().collect(Collectors.toMap(m -> m.get("id").toString(), m -> m));
            // erp合同信息
            List<String> contractIdList = erpBeans.stream().map(m -> m.get("contractId").toString()).distinct().collect(Collectors.toList());
            String contractIds = String.join(StrUtil.COMMA, contractIdList);
            List<Map<String, Object>> erpContractMations = iErpContractService.querySupplierContractByIds(contractIds);
            Map<String,Map<String, Object>> erpContractMap = erpContractMations.stream().collect(Collectors.toMap(m -> m.get("id").toString(), m -> m));
            // 付款信息
            List<String> erpPayIdList = erpBeans.stream().map(m -> m.get("fromId").toString()).distinct().collect(Collectors.toList());
            String erpPayIds = String.join(StrUtil.COMMA, erpPayIdList);
            List<Map<String, Object>> erpPayMations = iErpPaymentCollectionService.queryPaymentCollectionById(erpPayIds);
            Map<String,Map<String, Object>> erpPayMap = erpPayMations.stream().collect(Collectors.toMap(m -> m.get("id").toString(), m -> m));
            // 设置信息
            for (Map<String, Object> item : beans) {
                if(CorrespondentEnterEnum.SUPPLIER.getKey().equals(item.get("objectKey").toString())){
                    item.put("objectMation", supplierMap.get(item.get("objectId").toString()));
                    item.put("contractMation", erpContractMap.get(item.get("contractId").toString()));
                    item.put("fromMation", erpPayMap.get(item.get("fromId").toString()));
                }
            }
        }
        if(CollectionUtil.isNotEmpty(customerBeans)){
           // 客户信息
            List<String> customerIdList = customerBeans.stream().map(m -> m.get("objectId").toString()).distinct().collect(Collectors.toList());
            String customerIds = String.join(StrUtil.COMMA, customerIdList);
            List<Map<String, Object>> supplierList = iCrmCustomerService.queryCustomerListByIds(customerIds);
            Map<String,Map<String, Object>> customerMap = supplierList.stream().collect(Collectors.toMap(m -> m.get("id").toString(), m -> m));
            // crm合同信息
            List<String> contractIdList = customerBeans.stream().map(m -> m.get("contractId").toString()).distinct().collect(Collectors.toList());
            String contractIds = String.join(StrUtil.COMMA, contractIdList);
            List<Map<String, Object>> crmContractMations = iCrmContractService.queryCrmContractByIds(contractIds);
            Map<String,Map<String, Object>> crmContractMap = crmContractMations.stream().collect(Collectors.toMap(m -> m.get("id").toString(), m -> m));
            // 回款款信息
            List<String> crmPayIdList = customerBeans.stream().map(m -> m.get("fromId").toString()).distinct().collect(Collectors.toList());
            String crmPayIds = String.join(StrUtil.COMMA, crmPayIdList);
            List<Map<String, Object>> crmPayMations = iCrmPaymentCollectionService.queryPaymentCollectionById(crmPayIds);
            Map<String,Map<String, Object>> crmPayMap = crmPayMations.stream().collect(Collectors.toMap(m -> m.get("id").toString(), m -> m));
            // 设置信息
            for (Map<String, Object> item : beans) {
                if(CorrespondentEnterEnum.CUSTOM.getKey().equals(item.get("objectKey").toString())){
                    item.put("objectMation", customerMap.get(item.get("objectId").toString()));
                    item.put("contractMation", crmContractMap.get(item.get("contractId").toString()));
                    item.put("fromMation", crmPayMap.get(item.get("fromId").toString()));
                }
            }
        }
    }


    @Override
    public List<ReceivePayment> getBeforeThirtyDaysReceivePayment(String tenantId) {
        //获取前三十天以内的日期
        String beforeDay = getBeforeOrFutureDay(-29);
        String today = DateUtil.getTimeAndToString();
        // 查询近三十天的记录
        QueryWrapper<ReceivePayment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ReceivePayment::getState), FlowableStateEnum.PASS.getKey())
                .between(MybatisPlusUtil.toColumns(ReceivePayment::getCreateTime), beforeDay, today)
                .orderByDesc(MybatisPlusUtil.toColumns(ReceivePayment::getCreateTime));
        if (tenantEnable) {
            queryWrapper.eq(CommonConstants.TENANT_ID_FIELD, tenantId);
        }
        return list(queryWrapper);
    }

    public String getBeforeOrFutureDay(int num) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, num);
        Date m = c.getTime();
        return format.format(m);
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
        // 转为List<Map<String, Object>>
        List<Map<String, Object>> beans = JSONUtil.toList(JSONUtil.toJsonStr(list),null);
        setMationForMaps(beans);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    @Override
    public ReceivePayment selectById(String id) {
        ReceivePayment receivePayment = super.selectById(id);
        // 回付款信息
        List<Map<String, Object>> paymentCollection = new ArrayList<>();

        if (CorrespondentEnterEnum.CUSTOM.getKey().equals(receivePayment.getObjectKey())) {
            List<Map<String, Object>> objectMation = iCrmCustomerService.queryCustomerListByIds(receivePayment.getObjectId());
            List<Map<String, Object>> contractMation = iCrmContractService.queryCrmContractByIds(receivePayment.getContractId());
            paymentCollection = iErpPaymentCollectionService.queryPaymentCollectionById(receivePayment.getFromId());
            receivePayment.setObjectMation(objectMation.get(CommonNumConstants.NUM_ZERO));
            receivePayment.setContractMation(contractMation.get(CommonNumConstants.NUM_ZERO));
        } else {
            List<Map<String, Object>> objectMation = iErpSupplierService.querySupplierListByIds(receivePayment.getObjectId());
            List<Map<String, Object>> contractMation = iErpContractService.querySupplierContractByIds(receivePayment.getContractId());
            receivePayment.setObjectMation(objectMation.get(CommonNumConstants.NUM_ZERO));
            receivePayment.setContractMation(contractMation.get(CommonNumConstants.NUM_ZERO));
            paymentCollection = iCrmPaymentCollectionService.queryPaymentCollectionById(receivePayment.getFromId());
        }
        receivePayment.setFromMation(CollectionUtil.isEmpty(paymentCollection) ? new HashMap<>() : paymentCollection.get(CommonNumConstants.NUM_ZERO));
        return receivePayment;
    }
}
