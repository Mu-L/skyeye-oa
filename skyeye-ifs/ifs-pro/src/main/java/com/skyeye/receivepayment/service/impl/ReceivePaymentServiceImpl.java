package com.skyeye.receivepayment.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
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
    private IContactsService iContactsService;

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
    }

    @Override
    public QueryWrapper<ReceivePayment> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ReceivePayment> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectKey())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ReceivePayment::getObjectKey), commonPageInfo.getObjectKey());
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        return beans;
    }

    private List<Map<String, Object>> setInfoMap(List<Map<String, Object>> mapList, List<Map<String, Object>> beans) {
        Map<String, Map<String, Object>> paymentMap = mapList.stream().collect(Collectors.toMap(m -> m.get("id").toString(), m -> m));
        for (Map<String, Object> bean : beans) {
            String fromId = bean.get("fromId").toString();
            if (paymentMap.containsKey(fromId)) {
                Map<String, Object> payment = paymentMap.get(fromId);
                bean.put("fromMation", payment);
                bean.put("name", payment.get("oddNumber"));
            }
        }
        return beans;
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

    private List<ReceivePayment> setInfo(List<ReceivePayment> list) {
        return list;
    }

    private List<ReceivePayment> setInfo(List<Map<String, Object>> map, List<ReceivePayment> list) {
        Map<String, Map<String, Object>> paymentMap = map.stream().collect(Collectors.toMap(m -> m.get("id").toString(), m -> m));
        List<ReceivePayment> beans = list.stream().map(item -> {
            item.setFromMation(paymentMap.getOrDefault(item.getFromId(), new HashMap<>()));
            item.setName(item.getOddNumber());
            return item;
        }).collect(Collectors.toList());
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
