package com.skyeye.loan.service.impl;

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
import com.skyeye.feeapplication.entity.FeeAnalysis;
import com.skyeye.loan.dao.LoanRepayAnalysisDao;
import com.skyeye.loan.entity.LoanBorrow;
import com.skyeye.loan.entity.LoanBorrowAnalysis;
import com.skyeye.loan.entity.LoanRepay;
import com.skyeye.loan.entity.LoanRepayAnalysis;
import com.skyeye.loan.service.LoanRepayAnalysisService;
import com.skyeye.loan.service.LoanRepayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: LoanRepayAnalysisServiceImpl
 * @Description: 还款单分析实现层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/5 14:16
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "还款单分析", groupName = "还款单分析")
public class LoanRepayAnalysisServiceImpl extends SkyeyeBusinessServiceImpl<LoanRepayAnalysisDao, LoanRepayAnalysis> implements LoanRepayAnalysisService {

    @Autowired
    private LoanRepayService loanRepayService;

    @Override
    public void writeLoanRepayAnalysisRecord() {
        // 每月执行一次
        //获取上个月所有审批通过的费用申请单
        String month = DateUtil.getLastMonthDate();
        List<LoanRepay> loanRepayList = loanRepayService.queryLoanRepayList(month);
        LoanRepayAnalysis loanRepayAnalysis = new LoanRepayAnalysis();
        String price = "0";
        loanRepayAnalysis.setPrice(price);
        loanRepayAnalysis.setPeriodTime(month);
        // 计算费用总和
        for (LoanRepay loanRepay : loanRepayList) {
            price = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                    StrUtil.isEmpty(loanRepay.getPrice()) ? "0" : loanRepay.getPrice(),
                    price);
        }
        loanRepayAnalysis.setPrice(price);
        loanRepayAnalysis.setCreateTime(DateUtil.getPointTime(DateUtil.YYYY_MM_DD_HH_MM_SS));
        createEntity(loanRepayAnalysis, null);
    }

    @Override
    public void queryLoanRepayAnalysis(InputObject inputObject, OutputObject outputObject) {
        String year = inputObject.getParams().get("year").toString();
        String startPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_ZERO + CommonNumConstants.NUM_ONE;
        String endPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_TWELVE;
        QueryWrapper<LoanRepayAnalysis> queryWrapper = new QueryWrapper<>();
        queryWrapper.between(MybatisPlusUtil.toColumns(LoanRepayAnalysis::getPeriodTime), startPeriod, endPeriod);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(LoanRepayAnalysis::getPeriodTime));
        List<LoanRepayAnalysis> bean = list(queryWrapper);
        // 按periodTime分组
        Map<String, List<LoanRepayAnalysis>> map = bean.stream().collect(Collectors.groupingBy(LoanRepayAnalysis::getPeriodTime));
        // 循环12个月
        List<LoanRepayAnalysis> beans = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            String time = year + StrUtil.DASHED + (i < 10 ? "0" + i : i);
            if (map.containsKey(time)) {
                List<LoanRepayAnalysis> list = map.get(time);
                beans.add(list.get(CommonNumConstants.NUM_ZERO));
            } else {
                LoanRepayAnalysis loanRepayAnalysis = new LoanRepayAnalysis();
                loanRepayAnalysis.setPeriodTime(time);
                loanRepayAnalysis.setPrice("0");
                beans.add(loanRepayAnalysis);
            }
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

}
