package com.skyeye.receivepayment.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.CorrespondentEnterEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.receivepayment.dao.FundAnalysisDao;
import com.skyeye.receivepayment.entity.FundAnalysis;
import com.skyeye.receivepayment.entity.ReceivePayment;
import com.skyeye.receivepayment.service.FundAnalysisService;
import com.skyeye.receivepayment.service.ReceivePaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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

                // 根据支付方式typeId分组
                Map<String, List<ReceivePayment>> typeIdKeyMap = v1.stream().collect(Collectors.groupingBy(ReceivePayment::getTypeId));
                // 计算每个map中prince总金额
                if (CollectionUtil.isEmpty(typeIdKeyMap)) {
                    return;
                }
                typeIdKeyMap.forEach((k2, v2) -> {
                    // k2:typeId:
                    // v2:应收/回款--- 应付/付款
                    String price = String.valueOf(CommonNumConstants.NUM_ZERO);
                    for (ReceivePayment receivePayment : v2) {
                        price = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                                StrUtil.isEmpty(receivePayment.getPrice()) ? "0" : receivePayment.getPrice(),
                                price);
                    }
                    FundAnalysis fundAnalysis = new FundAnalysis();
                    fundAnalysis.setObjectId(k1);
                    fundAnalysis.setObjectKey(k);
                    fundAnalysis.setTypeId(k2);
                    fundAnalysis.setPrice(price);
                    fundAnalysis.setCreateTime(DateUtil.getPointTime(DateUtil.YYYY_MM));
                    beans.add(fundAnalysis);
                });

            });

        });
        createEntity(beans, null);
    }

    @Override
    public void queryFundPercentage(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String year = params.get("year").toString();
        String month = (String) params.get("month");
        QueryWrapper<FundAnalysis> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(month)) {
            year = year + StrUtil.DASHED + month;
            queryWrapper.eq(MybatisPlusUtil.toColumns(FundAnalysis::getCreateTime), year);
        } else {
            queryWrapper.apply("DATE_FORMAT(" + MybatisPlusUtil.toColumns(FundAnalysis::getCreateTime) + "%Y) = {0}", year);
        }
        List<FundAnalysis> bean = list(queryWrapper);
        if (CollectionUtil.isEmpty(bean)) {
            return;
        }
        // 根据objectKey分组
        Map<String, List<FundAnalysis>> map = bean.stream().collect(Collectors.groupingBy(FundAnalysis::getObjectKey));
        Map<String, Object> result = new HashMap<>();
        map.forEach((k, v) -> {
            String totalPrice = "0";
            for (FundAnalysis fundAnalysis : v) {
                totalPrice = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                        StrUtil.isEmpty(fundAnalysis.getPrice()) ? "0" : fundAnalysis.getPrice(),
                        totalPrice);
            }
            if (CorrespondentEnterEnum.CUSTOM.getKey().equals(k)) {
                result.put("customer", totalPrice);
            } else {
                result.put("supplier", totalPrice);
            }
        });
        outputObject.setBean(result);
    }

    @Override
    public void queryFundTypePercentage(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String year = params.get("year").toString();
        String month = (String) params.get("month");
        QueryWrapper<FundAnalysis> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(month)) {
            year = year + StrUtil.DASHED + month;
            queryWrapper.eq(MybatisPlusUtil.toColumns(FundAnalysis::getCreateTime), year);
        } else {
            queryWrapper.apply("DATE_FORMAT(" + MybatisPlusUtil.toColumns(FundAnalysis::getCreateTime) + "%Y) = {0}", year);
        }
        List<FundAnalysis> bean = list(queryWrapper);
        if (CollectionUtil.isEmpty(bean)) {
            return;
        }
        // 根据objectKey分组
        Map<String, List<FundAnalysis>> objectKeyMap = bean.stream().collect(Collectors.groupingBy(FundAnalysis::getObjectKey));
        Map<String, Object> result = new HashMap<>();
        objectKeyMap.forEach((key, value) -> {
            List<Map<String, Object>> temp = new ArrayList<>();
            //根据typeId分组并求数量
            Map<String, Long> typeIdMap = value.stream().map(FundAnalysis::getTypeId).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            typeIdMap.forEach((typeId, count) -> {
                Map<String, Object> map = new HashMap<>();
                map.put("typeId", typeId);
                map.put("count", count);
                temp.add(map);
            });
            if (CorrespondentEnterEnum.CUSTOM.getKey().equals(key)) {
                result.put(CorrespondentEnterEnum.CUSTOM.getValue(), temp);
            } else {
                result.put(CorrespondentEnterEnum.SUPPLIER.getValue(), temp);
            }
        });
        outputObject.setBean(result);
    }

    @Override
    public void queryFundMetrics(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String year = params.get("year").toString();
        String month = (String) params.get("month");
        String objectKey = params.get("objectKey").toString();
        List<Map<String, Object>> beans;
        // 如果传入了月份，则计算该月的本期和上期
        if (StrUtil.isNotEmpty(month)) {
            String startPeriod = year + StrUtil.DASHED + month; // 本期开始时间
            String endPeriod = year + StrUtil.DASHED + month;  // 本期结束时间
            // 计算上期时间
            int yearInt = Integer.parseInt(year);
            int monthInt = Integer.parseInt(month);
            String startPreviousPeriod;
            String endPreviousPeriod;

            if (monthInt == CommonNumConstants.NUM_ONE) {
                // 如果是1月，则上期是去年12月
                startPreviousPeriod = (yearInt - CommonNumConstants.NUM_ONE) + StrUtil.DASHED + CommonNumConstants.NUM_TWELVE;
                endPreviousPeriod = (yearInt - CommonNumConstants.NUM_ONE) + StrUtil.DASHED + CommonNumConstants.NUM_TWELVE;
            } else {
                // 如果不是1月，则上期是上个月
                startPreviousPeriod = yearInt + StrUtil.DASHED + String.format("%02d", monthInt - 1);
                endPreviousPeriod = yearInt + StrUtil.DASHED + String.format("%02d", monthInt - 1);
            }
            beans = extracted(startPeriod, endPeriod, startPreviousPeriod, endPreviousPeriod, objectKey);

        } else {
            // 如果没有传入月份，则计算全年的本期和上期
            String startPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_ZERO + CommonNumConstants.NUM_ONE; // 本期开始时间
            String endPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_TWELVE;  // 本期结束时间

            String startPreviousPeriod = (Integer.parseInt(year) - CommonNumConstants.NUM_ONE) + StrUtil.DASHED + CommonNumConstants.NUM_ZERO + CommonNumConstants.NUM_ONE; // 上期开始时间
            String endPreviousPeriod = (Integer.parseInt(year) - CommonNumConstants.NUM_ONE) + StrUtil.DASHED + CommonNumConstants.NUM_TWELVE;  // 上期结束时间
            beans = extracted(startPeriod, endPeriod, startPreviousPeriod, endPreviousPeriod, objectKey);
        }
        outputObject.setBeans(beans);
    }

    private List<Map<String, Object>> extracted(String startPeriod, String endPeriod, String startPreviousPeriod, String endPreviousPeriod, String objectKey) {
        List<Map<String, Object>> currentResult = new ArrayList<>();
        List<Map<String, Object>> lastResult = new ArrayList<>();
        List<Map<String, Object>> result = new ArrayList<>();
        // 计算本期
        QueryWrapper<FundAnalysis> currentWrapper = new QueryWrapper<>();
        currentWrapper.eq(MybatisPlusUtil.toColumns(FundAnalysis::getObjectKey), objectKey);
        currentWrapper.between(MybatisPlusUtil.toColumns(FundAnalysis::getCreateTime), startPeriod, endPeriod);
        List<FundAnalysis> currentList = list(currentWrapper);
        if (CollectionUtil.isNotEmpty(currentList)) {
            //typeId分组
            Map<String, List<FundAnalysis>> currentMap = currentList.stream().collect(Collectors.groupingBy(FundAnalysis::getTypeId));
            currentMap.forEach((k, v) -> {
                String totalPrice = "0";
                for (FundAnalysis fundAnalysis : v) {
                    totalPrice = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                            StrUtil.isEmpty(fundAnalysis.getPrice()) ? "0" : fundAnalysis.getPrice(),
                            totalPrice);
                }
                Map<String, Object> map = new HashMap<>();
                map.put("typeId", k);
                map.put("totalPrice", totalPrice);
                currentResult.add(map);
            });
        }

        // 计算上期
        QueryWrapper<FundAnalysis> previousWrapper = new QueryWrapper<>();
        previousWrapper.eq(MybatisPlusUtil.toColumns(FundAnalysis::getObjectKey), objectKey);
        previousWrapper.between(MybatisPlusUtil.toColumns(FundAnalysis::getCreateTime), startPreviousPeriod, endPreviousPeriod);
        List<FundAnalysis> previousList = list(previousWrapper);
        if (CollectionUtil.isNotEmpty(previousList)) {
            Map<String, List<FundAnalysis>> previousMap = previousList.stream().collect(Collectors.groupingBy(FundAnalysis::getTypeId));
            previousMap.forEach((k, v) -> {
                String totalPrice = "0";
                for (FundAnalysis fundAnalysis : v) {
                    totalPrice = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                            StrUtil.isEmpty(fundAnalysis.getPrice()) ? "0" : fundAnalysis.getPrice(),
                            totalPrice);
                }
                Map<String, Object> map = new HashMap<>();
                map.put("typeId", k);
                map.put("totalPrice", totalPrice);
                lastResult.add(map);
            });
        }
        // 如果本期和上期 计算的发票金额列表都为空--则没数据
        if (CollectionUtil.isEmpty(lastResult) && CollectionUtil.isEmpty(currentResult)) {
            return result;
        }
        // 哪个列表更长先循环哪个
        if (lastResult.size() > currentResult.size()) {
            for (Map<String, Object> lastMap : lastResult) {
                Map<String, Object> map = new HashMap<>();
                map.put("typeId", lastMap.get("typeId"));
                map.put("lastTotalPrice", lastMap.get("totalPrice"));
                for (Map<String, Object> currentMap : currentResult) {
                    if (lastMap.get("typeId").equals(currentMap.get("typeId"))) {
                        map.put("currentTotalPrice", currentMap.get("totalPrice"));
                    }
                }
                if (!map.containsKey("currentTotalPrice")) {
                    map.put("currentTotalPrice", 0);
                }
                // 变动值
                double changePrice = Double.parseDouble(map.get("currentTotalPrice").toString()) - Double.parseDouble(map.get("lastTotalPrice").toString());
                map.put("changePrice", changePrice <= 0 ? changePrice : String.format("%+f", changePrice));
                // 环比=（本期-上期）/上期*100%
                double ringRatio = 0;
                if (Double.parseDouble(map.get("lastTotalPrice").toString()) != 0) {
                    ringRatio = changePrice / Double.parseDouble(map.get("lastTotalPrice").toString()) * 100;
                }
                map.put("ringRatio", String.format("%.2f", ringRatio) + "%");
                result.add(map);
            }
        } else {
            for (Map<String, Object> currentMap : currentResult) {
                Map<String, Object> map = new HashMap<>();
                map.put("typeId", currentMap.get("typeId"));
                map.put("currentTotalPrice", currentMap.get("totalPrice"));
                for (Map<String, Object> lastMap : lastResult) {
                    if (currentMap.get("typeId").equals(lastMap.get("typeId"))) {
                        map.put("lastTotalPrice", lastMap.get("totalPrice"));
                    }
                }
                if (!map.containsKey("lastTotalPrice")) {
                    map.put("lastTotalPrice", 0);
                }// 变动值
                double changePrice = Double.parseDouble(map.get("currentTotalPrice").toString()) - Double.parseDouble(map.get("lastTotalPrice").toString());
                map.put("changePrice", changePrice <= 0 ? changePrice : String.format("%+f", changePrice));
                // 环比=（本期-上期）/上期*100%
                double ringRatio;
                if (Double.parseDouble(map.get("lastTotalPrice").toString()) != 0) {
                    ringRatio = changePrice / Double.parseDouble(map.get("lastTotalPrice").toString()) * 100;
                } else {
                    ringRatio = 100;
                }
                map.put("ringRatio", String.format("%.2f", ringRatio) + "%");
                result.add(map);
            }
        }
        return result;
    }

}
