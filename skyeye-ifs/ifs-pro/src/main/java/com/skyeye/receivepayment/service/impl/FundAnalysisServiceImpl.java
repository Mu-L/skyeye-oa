package com.skyeye.receivepayment.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.receivepayment.classenum.ReceivePaymentKeyEnum;
import com.skyeye.receivepayment.dao.FundAnalysisDao;
import com.skyeye.receivepayment.entity.FundAnalysis;
import com.skyeye.receivepayment.entity.ReceivePayment;
import com.skyeye.receivepayment.service.FundAnalysisService;
import com.skyeye.receivepayment.service.ReceivePaymentService;
import com.skyeye.rest.crm.payment.service.ICrmPaymentCollectionService;
import com.skyeye.rest.crm.receivable.service.ICrmReceivableService;
import com.skyeye.rest.erp.payable.service.IErpPayableService;
import com.skyeye.rest.erp.payment.service.IErpPaymentCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: FundAnalysisServiceImpl
 * @Description: 收付款管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/4 16:23
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "资金分析管理", groupName = "资金分析管理")
public class FundAnalysisServiceImpl extends SkyeyeBusinessServiceImpl<FundAnalysisDao, FundAnalysis> implements FundAnalysisService {

    @Autowired
    private ReceivePaymentService receivePaymentService;

    @Autowired
    private ICrmPaymentCollectionService iCrmPaymentCollectionService;

    @Autowired
    private IErpPaymentCollectionService iErpPaymentCollectionService;

    @Autowired
    private ICrmReceivableService iCrmReceivableService;

    @Autowired
    private IErpPayableService iErpPayableService;


    @Override
    public void writeFundAnalysisRecord(String tenantId) {
        // 取出前30天的收付款审批成功的记录
        List<ReceivePayment> record = receivePaymentService.getBeforeThirtyDaysReceivePayment(tenantId);
        if (CollectionUtil.isEmpty(record)) {
            return;
        }
        // 根据--objectKey分组
        Map<String, List<ReceivePayment>> map = record.stream().collect(Collectors.groupingBy(ReceivePayment::getObjectKey));
        List<FundAnalysis> beans = new ArrayList<>();
        // 遍历map，将数据写入到资金分析表中
        map.forEach((k, v) -> {
            // k:objectKey
            // 每个客户/供应商--- 对应的 应收/回款 -- 应付/付款
            // 将v按照objectId分组-- objectKey :客户/供应商
            Map<String, List<ReceivePayment>> crmOrErpMap = v.stream().collect(Collectors.groupingBy(ReceivePayment::getObjectId));
            crmOrErpMap.forEach((k1, v1) -> {
                // k1:客户id、供应商id
                // v1： 应收/回款--- 应付/付款
                // 根据fromKey分组
                Map<String, List<ReceivePayment>> fromKeyMap = v1.stream().collect(Collectors.groupingBy(ReceivePayment::getFromKey));
                // 计算每个map中prince总金额
                if (CollectionUtil.isEmpty(fromKeyMap)) {
                    return;
                }
                fromKeyMap.forEach((k2, v2) -> {
                    // k2:fromKey:
                    // v2:应收/回款--- 应付/付款
                    String price = String.valueOf(CommonNumConstants.NUM_ZERO);
                    for (ReceivePayment receivePayment : v2) {
                        price = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                                StrUtil.isEmpty(receivePayment.getPrice()) ? "0" : receivePayment.getPrice(),
                                price);
                    }
                    FundAnalysis fundAnalysis = new FundAnalysis();
                    fundAnalysis.setObjectKey(k);
                    fundAnalysis.setFromId(v2.get(CommonNumConstants.NUM_ZERO).getFromId());
                    fundAnalysis.setFromKey(k2);
                    fundAnalysis.setObjectId(k1);
                    fundAnalysis.setPrice(price);
                    fundAnalysis.setCreateTime(DateUtil.getPointTime(DateUtil.YYYY_MM));
                    beans.add(fundAnalysis);
                });

            });

        });
        createEntity(beans, null);
    }

    @Override
    public void queryFundAnalysis(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<FundAnalysis> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(FundAnalysis::getCreateTime), DateUtil.getPointTime(DateUtil.YYYY_MM));
        List<FundAnalysis> bean = list(queryWrapper);
        if (CollectionUtil.isEmpty(bean)) {
            return;
        }// 1. 按fromKey分组
        Map<String, List<FundAnalysis>> fromKeyMap = bean.stream().collect(Collectors.groupingBy(FundAnalysis::getFromKey));
        List<FundAnalysis> beans = new ArrayList<>();
        fromKeyMap.forEach((kk, vv) -> {
            // kk: 应付,付款  应收,回款
            // 设置：应付,付款  应收,回款信息
            List<String> fromIds = vv.stream().map(FundAnalysis::getFromId).distinct().collect(Collectors.toList());
            String fromId = String.join(StrUtil.COMMA, fromIds);
            // 应付,付款  应收,回款信息
            List<Map<String, Object>> paymentCollection = new ArrayList<>();

            if (ReceivePaymentKeyEnum.ERP_PAYMENT_KEY.getKey().equals(kk)) {
                // 付款信息
                paymentCollection = iErpPaymentCollectionService.queryPaymentCollectionById(fromId);
            } else if (ReceivePaymentKeyEnum.ERP_PURCHASE_ORDER_KEY.getKey().equals(kk)) {
                // 应付事项
                paymentCollection = iErpPayableService.queryPayableByIds(fromId);
            } else if (ReceivePaymentKeyEnum.CRM_RECEIVE_KEY.getKey().equals(kk)) {
                // 应收事项
                paymentCollection = iCrmReceivableService.queryReceivableByIds(fromId);
            } else if (ReceivePaymentKeyEnum.CRM_RECEIVE_PAYMENT_KEY.getKey().equals(kk)) {
                // 回款
                paymentCollection = iCrmPaymentCollectionService.queryPaymentCollectionById(fromId);
            }

            Map<String, Map<String, Object>> paymentMap = paymentCollection.stream().collect(Collectors.toMap(m -> m.get("id").toString(), m -> m));
            List<FundAnalysis> collect = vv.stream().map(item -> {
                item.setFromMation(paymentMap.getOrDefault(item.getFromId(), new HashMap<>()));
                return item;
            }).collect(Collectors.toList());
            beans.addAll(collect);
        });
        outputObject.settotal(beans.size());
        outputObject.setBeans(beans);
    }

}
