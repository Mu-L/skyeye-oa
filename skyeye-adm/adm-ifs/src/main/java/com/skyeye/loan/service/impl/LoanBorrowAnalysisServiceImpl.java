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
import com.skyeye.loan.dao.LoanBorrowAnalysisDao;
import com.skyeye.loan.entity.LoanBorrow;
import com.skyeye.loan.entity.LoanBorrowAnalysis;
import com.skyeye.loan.service.LoanBorrowAnalysisService;
import com.skyeye.loan.service.LoanBorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: LoanBorrowAnalysisServiceImpl
 * @Description: 借款单分析实现层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/5 14:16
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "借款单分析", groupName = "借款单分析")
public class LoanBorrowAnalysisServiceImpl extends SkyeyeBusinessServiceImpl<LoanBorrowAnalysisDao, LoanBorrowAnalysis> implements LoanBorrowAnalysisService {

    @Autowired
    private LoanBorrowService loanBorrowService;

    @Override
    public void writeLoanBorrowAnalysisRecord() {
        // 每月执行一次
        //获取上个月所有审批通过的费用申请单
        String month = DateUtil.getLastMonthDate();
        List<LoanBorrow> loanBorrowList = loanBorrowService.queryLoanBorrowList(month);
        LoanBorrowAnalysis loanBorrowAnalysis = new LoanBorrowAnalysis();
        loanBorrowAnalysis.setPeriodTime(month);
        String price = "0";
        loanBorrowAnalysis.setPrice(price);
        // 计算费用总和
        for (LoanBorrow loanBorrow : loanBorrowList) {
            price = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                    StrUtil.isEmpty(loanBorrow.getPrice()) ? "0" : loanBorrow.getPrice(),
                    price);
        }
        loanBorrowAnalysis.setPrice(price);
        loanBorrowAnalysis.setCreateTime(DateUtil.getPointTime(DateUtil.YYYY_MM_DD_HH_MM_SS));
        createEntity(loanBorrowAnalysis, null);
    }

    @Override
    public void queryLoanBorrowAnalysis(InputObject inputObject, OutputObject outputObject) {
        String year = inputObject.getParams().get("year").toString();
        String startPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_ZERO + CommonNumConstants.NUM_ONE;
        String endPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_TWELVE;
        QueryWrapper<LoanBorrowAnalysis> queryWrapper = new QueryWrapper<>();
        queryWrapper.between(MybatisPlusUtil.toColumns(LoanBorrowAnalysis::getPeriodTime), startPeriod, endPeriod);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(LoanBorrowAnalysis::getPeriodTime));
        List<LoanBorrowAnalysis> bean = list(queryWrapper);
        // 按periodTime分组
        Map<String, List<LoanBorrowAnalysis>> map = bean.stream().collect(Collectors.groupingBy(LoanBorrowAnalysis::getPeriodTime));
        // 循环12个月
        List<LoanBorrowAnalysis> beans = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            String time = year + StrUtil.DASHED + (i < 10 ? "0" + i : i);
            if (map.containsKey(time)) {
                List<LoanBorrowAnalysis> list = map.get(time);
                beans.add(list.get(CommonNumConstants.NUM_ZERO));
            } else {
                LoanBorrowAnalysis loanBorrowAnalysis = new LoanBorrowAnalysis();
                loanBorrowAnalysis.setPeriodTime(time);
                loanBorrowAnalysis.setPrice("0");
                beans.add(loanBorrowAnalysis);
            }
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

}
