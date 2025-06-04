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
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.contacts.service.IContactsService;
import com.skyeye.receivepayment.classenum.ReceivePaymentKeyEnum;
import com.skyeye.receivepayment.dao.ReceivePaymentDao;
import com.skyeye.receivepayment.entity.ReceivePayment;
import com.skyeye.receivepayment.service.ReceivePaymentService;
import com.skyeye.rest.crm.payment.service.ICrmPaymentCollectionService;
import com.skyeye.rest.crm.receivable.service.ICrmReceivableService;
import com.skyeye.rest.erp.payable.service.IErpPayableService;
import com.skyeye.rest.erp.payment.service.IErpPaymentCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ReceivePaymentServiceImpl extends SkyeyeFlowableServiceImpl<ReceivePaymentDao, ReceivePayment> implements ReceivePaymentService {


    @Autowired
    private IContactsService iContactsService;

    @Autowired
    private ICrmPaymentCollectionService iCrmPaymentCollectionService;

    @Autowired
    private IErpPaymentCollectionService iErpPaymentCollectionService;

    @Autowired
    private ICrmReceivableService iCrmReceivableService;

    @Autowired
    private IErpPayableService iErpPayableService;


    @Override
    public void createPrepose(ReceivePayment entity) {
        if (StrUtil.isNotEmpty(entity.getId())) {
            entity.setFromId(entity.getId());
            entity.setId(StrUtil.EMPTY);
            entity.setFromKey(entity.getServiceClassName());
            entity.setState(FlowableStateEnum.PASS.getKey());
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

    @Override
    public List<ReceivePayment> getBeforeThirtyDaysReceivePayment() {
        //获取前三十天以内的日期
        String beforeDay = getBeforeOrFutureDay(-29);
        String today = DateUtil.getTimeAndToString();
        // 查询近三十天的记录
        QueryWrapper<ReceivePayment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ReceivePayment::getState), FlowableStateEnum.PASS.getKey())
                .between(MybatisPlusUtil.toColumns(ReceivePayment::getCreateTime), beforeDay, today)
                .orderByDesc(MybatisPlusUtil.toColumns(ReceivePayment::getCreateTime));
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
        if (CollectionUtil.isEmpty(list)) {
            return list;
        }
        // 根据fromKey分组，值为List<String> fromId
        Map<String, List<String>> fromKeyMap = list.stream().
                collect(Collectors.groupingBy(ReceivePayment::getFromKey, Collectors.mapping(ReceivePayment::getFromId, Collectors.toList())));
        // 回款fromId
        List<String> crmPaymentFromIds = fromKeyMap.getOrDefault(ReceivePaymentKeyEnum.CRM_RECEIVE_PAYMENT_KEY.getKey(), new ArrayList<>());
        // 应收fromId
        List<String> crmReceivableFromIds = fromKeyMap.getOrDefault(ReceivePaymentKeyEnum.CRM_RECEIVE_KEY.getKey(), new ArrayList<>());
        // 付款fromId
        List<String> erpPaymentOutFromIds = fromKeyMap.getOrDefault(ReceivePaymentKeyEnum.ERP_PAYMENT_KEY.getKey(), new ArrayList<>());
        // 应付fromId
        List<String> erpReceivableFromIds = fromKeyMap.getOrDefault(ReceivePaymentKeyEnum.ERP_PURCHASE_ORDER_KEY.getKey(), new ArrayList<>());
        List<ReceivePayment> beans = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(crmPaymentFromIds)) {
            String fromIds = String.join(StrUtil.COMMA, crmPaymentFromIds);
            List<Map<String, Object>> mapList = iCrmPaymentCollectionService.queryPaymentCollectionById(fromIds);
            beans = setInfo(mapList, list);
        }
        if (CollectionUtil.isNotEmpty(crmReceivableFromIds)) {
            String fromIds = String.join(StrUtil.COMMA, crmReceivableFromIds);
            List<Map<String, Object>> mapList = iCrmReceivableService.queryReceivableByIds(fromIds);
            beans = setInfo(mapList, list);
        }
        if (CollectionUtil.isNotEmpty(erpPaymentOutFromIds)) {
            String fromIds = String.join(StrUtil.COMMA, erpPaymentOutFromIds);
            List<Map<String, Object>> mapList = iErpPaymentCollectionService.queryPaymentCollectionById(fromIds);
            beans = setInfo(mapList, list);
        }
        if (CollectionUtil.isNotEmpty(erpReceivableFromIds)) {
            String fromIds = String.join(StrUtil.COMMA, erpReceivableFromIds);
            List<Map<String, Object>> mapList = iErpPayableService.queryPayableByIds(fromIds);
            beans = setInfo(mapList, list);
        }
        iContactsService.setDataMation(beans, ReceivePayment::getContactId);
        return beans;
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
        iContactsService.setDataMation(receivePayment, ReceivePayment::getContactId);
        // 回付款信息
        List<Map<String, Object>> paymentCollection = new ArrayList<>();

        if (ReceivePaymentKeyEnum.ERP_PAYMENT_KEY.getKey().equals(receivePayment.getFromKey())) {
            // 付款信息
            paymentCollection = iErpPaymentCollectionService.queryPaymentCollectionById(receivePayment.getFromId());
        } else if (ReceivePaymentKeyEnum.ERP_PURCHASE_ORDER_KEY.getKey().equals(receivePayment.getFromKey())) {
            // 应付事项
            paymentCollection = iErpPayableService.queryPayableByIds(receivePayment.getFromId());
        } else if (ReceivePaymentKeyEnum.CRM_RECEIVE_KEY.getKey().equals(receivePayment.getObjectKey())) {
            // 应收事项
            paymentCollection = iCrmReceivableService.queryReceivableByIds(receivePayment.getFromId());
        } else if (ReceivePaymentKeyEnum.CRM_RECEIVE_PAYMENT_KEY.getKey().equals(receivePayment.getObjectKey())) {
            // 回款
            paymentCollection = iCrmPaymentCollectionService.queryPaymentCollectionById(receivePayment.getFromId());
        }
        receivePayment.setFromMation(CollectionUtil.isEmpty(paymentCollection) ? new HashMap<>() : paymentCollection.get(CommonNumConstants.NUM_ZERO));
        return receivePayment;
    }

    @Override
    public void approvalEndIsSuccess(ReceivePayment entity) {
        // 审核成功
        if (entity.getFromKey().equals(ReceivePaymentKeyEnum.ERP_PURCHASE_ORDER_KEY.getKey())) {
            // 修改应付事项--修改已付金额
            iErpPayableService.updatePayableById(entity.getFromId(), entity.getPrice());
        } else if (entity.getFromKey().equals(ReceivePaymentKeyEnum.CRM_RECEIVE_KEY.getKey())) {
            // 修改回收事项---修改已回收金额
            iCrmReceivableService.updateReceivableById(entity.getFromId(), entity.getPrice());
        }
    }
}
