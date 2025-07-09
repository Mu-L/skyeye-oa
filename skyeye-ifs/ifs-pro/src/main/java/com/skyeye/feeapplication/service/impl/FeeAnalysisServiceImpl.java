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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        // 每月执行一次
        //获取上个月所有审批通过的费用申请单
        String month = DateUtil.getLastMonthDate();
        List<FeeApplication> feeApplicationsList = feeApplicationService.queryFeeApplicationList(month);
        FeeAnalysis feeAnalysis = new FeeAnalysis();
        feeAnalysis.setPeriodTime(month);
        String price = "0";
        feeAnalysis.setPrice(price);
        // 计算费用总和
        for (FeeApplication feeApplication : feeApplicationsList) {
            price = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                    StrUtil.isEmpty(feeApplication.getPrice()) ? "0" : feeApplication.getPrice(),
                    price);
        }
        feeAnalysis.setPrice(price);
        feeAnalysis.setCreateTime(DateUtil.getPointTime(DateUtil.YYYY_MM_DD_HH_MM_SS));
        createEntity(feeAnalysis, null);
    }

    @Override
    public void queryFeeAnalysis(InputObject inputObject, OutputObject outputObject) {
        String year = inputObject.getParams().get("year").toString();
        String startPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_ZERO + CommonNumConstants.NUM_ONE;
        String endPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_TWELVE;
        QueryWrapper<FeeAnalysis> queryWrapper = new QueryWrapper<>();
        queryWrapper.between(MybatisPlusUtil.toColumns(FeeAnalysis::getPeriodTime), startPeriod, endPeriod);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(FeeAnalysis::getPeriodTime));
        List<FeeAnalysis> bean = list(queryWrapper);
        // 按periodTime分组
        Map<String, List<FeeAnalysis>> map = bean.stream().collect(Collectors.groupingBy(FeeAnalysis::getPeriodTime));
        // 循环12个月
        List<FeeAnalysis> beans = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            String time = year + StrUtil.DASHED + (i < 10 ? "0" + i : i);
            if (map.containsKey(time)) {
                List<FeeAnalysis> list = map.get(time);
                beans.add(list.get(CommonNumConstants.NUM_ZERO));
            } else {
                FeeAnalysis feeAnalysis = new FeeAnalysis();
                feeAnalysis.setPeriodTime(time);
                feeAnalysis.setPrice("0");
                beans.add(feeAnalysis);
            }
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }
}

