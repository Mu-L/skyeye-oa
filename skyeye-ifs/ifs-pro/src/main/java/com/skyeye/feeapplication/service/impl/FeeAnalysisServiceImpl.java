package com.skyeye.feeapplication.service.impl;

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
import com.skyeye.feeapplication.dao.FeeAnalysisDao;
import com.skyeye.feeapplication.entity.FeeAnalysis;
import com.skyeye.feeapplication.entity.FeeApplication;
import com.skyeye.feeapplication.service.FeeAnalysisService;
import com.skyeye.feeapplication.service.FeeApplicationService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @ClassName: FeeAnalysisServiceImpl
 * @Description: 费用申请分析服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/5 14:17
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "费用申请分析", groupName = "费用申请分析")
public class FeeAnalysisServiceImpl extends SkyeyeBusinessServiceImpl<FeeAnalysisDao, FeeAnalysis> implements FeeAnalysisService {

    @Autowired
    private FeeApplicationService feeApplicationService;

    @Override
    public void writeFeeAnalysisRecord() {
        // 每年执行一次
        //获取今年所有审批通过的费用申请单
        int year = Integer.parseInt(DateUtil.getPointTime(DateUtil.YYYY)) - CommonNumConstants.NUM_ONE;
        List<FeeApplication> feeApplicationsList = feeApplicationService.queryFeeApplicationListByYear(year);
        // 按格式化createTime字段分组，格式为YYYY-MM
        Map<String, List<FeeApplication>> feeAnalysisMap = feeApplicationsList.stream().collect(Collectors.groupingBy(feeApplication -> feeApplication.getCreateTime().substring(0, 7)));
        // 循环12个月
        List<FeeAnalysis> feeAnalysisList = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            String key = year + StrUtil.DASHED + (i < 10 ? "0" + i : i);
            FeeAnalysis feeAnalysis = new FeeAnalysis();
            feeAnalysis.setYearMonth(key);
            String price = "0";
            feeAnalysis.setPrice(price);
            if (feeAnalysisMap.containsKey(key)) {
                List<FeeApplication> feeApplicationList = feeAnalysisMap.get(key);
                // 计算费用总和
                for (FeeApplication feeApplication : feeApplicationList) {
                    price = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                            StrUtil.isEmpty(feeApplication.getPrice()) ? "0" : feeApplication.getPrice(),
                            price);
                }
                feeAnalysis.setPrice(price);
            }
            feeAnalysis.setCreateTime(DateUtil.getPointTime(DateUtil.YYYY_MM_DD_HH_MM_SS));
            feeAnalysisList.add(feeAnalysis);
        }
        createEntity(feeAnalysisList,null);
    }

    @Override
    public void queryFeeAnalysis(InputObject inputObject, OutputObject outputObject) {
        String year = inputObject.getParams().get("year").toString();
        String startPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_ZERO + CommonNumConstants.NUM_ONE;
        String endPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_TWELVE;
        QueryWrapper<FeeAnalysis> queryWrapper = new QueryWrapper<>();
        queryWrapper.between(MybatisPlusUtil.toColumns(FeeAnalysis::getYearMonth),startPeriod,endPeriod);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(FeeAnalysis::getYearMonth));
        List<FeeAnalysis> beans = list(queryWrapper);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }
}

