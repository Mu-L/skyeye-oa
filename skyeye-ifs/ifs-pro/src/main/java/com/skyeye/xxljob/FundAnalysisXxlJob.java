package com.skyeye.xxljob;

import com.skyeye.receivepayment.service.FundAnalysisService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName: FundAnalysisXxlJob
 * @Description: 资金分析记录
 * @author: skyeye云系列--卫志强
 * @date: 2023/10/11 19:20
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class FundAnalysisXxlJob {

    @Autowired
    private  FundAnalysisService fundAnalysisService;

    /**
     * 定时任务
     * 每月一次
     * 计算每个客户/供应商的月度资金分析记录
     * */

    @XxlJob("writeFundAnalysisRecord")
    public void writeFundAnalysisRecord() {
        fundAnalysisService.writeFundAnalysisRecord();
    }
}
