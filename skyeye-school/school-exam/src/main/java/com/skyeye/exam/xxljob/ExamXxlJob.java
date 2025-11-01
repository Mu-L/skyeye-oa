package com.skyeye.exam.xxljob;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.eve.service.IQuartzService;
import com.skyeye.exam.examsurveydirectory.service.ExamSurveyDirectoryService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName: ExamXxlJob
 * @Description: 创建零分试卷的定时任务
 * @author: skyeye云系列--卫志强
 * @date: 2025/4/15 19:20
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class ExamXxlJob {

    @Autowired
    private ExamSurveyDirectoryService examSurveyDirectoryService;

    @Autowired
    private IQuartzService iQuartzService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @XxlJob("createExam")
    public void createExam() {
        String param = XxlJobHelper.getJobParam();
        Map<String, Object> paramMap = JSONUtil.toBean(JSONUtil.toJsonStr(param), null);
        String userId = paramMap.get("userId").toString();
        String examId = paramMap.get("objectId").toString();
        String tenantId = tenantEnable ? paramMap.get("tenantId").toString() : StrUtil.EMPTY;
        if (tenantEnable) {
            TenantContext.setTenantId(tenantId);
        }
        try {
            examSurveyDirectoryService.createNotSubStudent(examId, userId);
        } finally {
            iQuartzService.stopAndDeleteTaskQuartz(examId);// 删除任务
        }
    }

}
