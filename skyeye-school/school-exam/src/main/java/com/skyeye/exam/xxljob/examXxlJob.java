package com.skyeye.exam.xxljob;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.exam.examananswer.service.ExamAnAnswerService;
import com.skyeye.exam.examancheckbox.service.ExamAnCheckboxService;
import com.skyeye.exam.examanchencheckbox.service.ExamAnChenCheckboxService;
import com.skyeye.exam.examanchenfbk.service.ExamAnChenFbkService;
import com.skyeye.exam.examanchenradio.service.ExamAnChenRadioService;
import com.skyeye.exam.examanchenscore.service.ExamAnChenScoreService;
import com.skyeye.exam.examancompchenradio.service.ExamAnCompChenRadioService;
import com.skyeye.exam.examandfillblank.service.ExamAnDfilllankService;
import com.skyeye.exam.examanenumqu.service.ExamAnEnumquService;
import com.skyeye.exam.examanfillblank.service.ExamAnFillblankService;
import com.skyeye.exam.examanorder.service.ExamAnOrderService;
import com.skyeye.exam.examanradio.service.ExamAnRadioService;
import com.skyeye.exam.examanscore.service.ExamAnScoreService;
import com.skyeye.exam.examanyesno.service.ExamAnYesnoService;
import com.skyeye.exam.examsurveyanswer.entity.ExamSurveyAnswer;
import com.skyeye.exam.examsurveyanswer.service.ExamSurveyAnswerService;
import com.skyeye.exam.examsurveydirectory.entity.ExamSurveyDirectory;
import com.skyeye.exam.examsurveydirectory.service.ExamSurveyDirectoryService;
import com.skyeye.exam.examsurveyquanswer.service.ExamSurveyQuAnswerService;
import com.skyeye.school.subject.entity.SubjectClasses;
import com.skyeye.school.subject.service.SubjectClassesService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName: ExamXxlJob
 * @Description: 试卷打分
 * @author: skyeye云系列--卫志强
 * @date: 2025/4/15 19:20
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class examXxlJob {

    @Autowired
    private SubjectClassesService subjectClassesService;

    @XxlJob("examZoreXxlJob")
    public void examZoreXxlJob() {
        String param = XxlJobHelper.getJobParam();
        ExamSurveyDirectory paramMap = JSONUtil.toBean(JSONUtil.toJsonStr(param), ExamSurveyDirectory.class);
        String realStartTime = paramMap.getRealStartTime();
        String subjectId = paramMap.getSubjectId();
        String classId = paramMap.getClassId();
        SubjectClasses subjectClasses = subjectClassesService.selectIdBySubAndClassId(subjectId, classId);

    }
}
