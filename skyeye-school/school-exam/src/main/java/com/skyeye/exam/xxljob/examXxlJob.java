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
    private ExamAnRadioService examAnRadioService;

    @Autowired
    private ExamAnScoreService examAnScoreService;

    @Autowired
    private ExamAnYesnoService examAnYesnoService;

    @Autowired
    private ExamAnAnswerService examAnAnswerService;

    @Autowired
    private ExamAnCheckboxService examAnCheckboxService;

    @Autowired
    private ExamAnChenCheckboxService examAnChenCheckboxService;

    @Autowired
    private ExamAnChenFbkService examAnChenFbkService;

    @Autowired
    private ExamAnChenRadioService examAnChenRadioService;

    @Autowired
    private ExamAnChenScoreService examAnChenScoreService;

    @Autowired
    private ExamAnCompChenRadioService examAnCompChenRadioService;

    @Autowired
    private ExamAnDfilllankService examAnDfilllankService;

    @Autowired
    private ExamAnEnumquService examAnEnumquService;

    @Autowired
    private ExamAnFillblankService examAnFillblankService;

    @Autowired
    private ExamAnOrderService examAnOrderService;

    @Autowired
    private ExamSurveyAnswerService examSurveyAnswerService;

    @XxlJob("examZoreXxlJob")
    public void examZoreXxlJob() {
        String param = XxlJobHelper.getJobParam();
        ExamSurveyAnswer paramMap = JSONUtil.toBean(JSONUtil.toJsonStr(param), ExamSurveyAnswer.class);
        String id = paramMap.getId();
        String surveyId = paramMap.getSurveyId();
        Long size = examAnRadioService.selectRadioBySurveyId(surveyId,id);
        Long size1 = examAnScoreService.selectBySurveyId(surveyId,id);
        Long size2 = examAnYesnoService.selectBySurveyId(surveyId,id);
        Long size3 = examAnAnswerService.selectBySurveyId(surveyId,id);
        Long size4 = examAnCheckboxService.slectBySurveyId(surveyId,id);
        Long size5 = examAnChenCheckboxService.selectBySurveyId(surveyId,id);
        Long size6 = examAnChenFbkService.selectBySurveyId(surveyId,id);
        Long size7 = examAnChenRadioService.selectBySurveyId(surveyId,id);
        Long size8 = examAnChenScoreService.selectBySurveyId(surveyId,id);
        Long size9 = examAnCompChenRadioService.selectBySurveyId(surveyId,id);
        Long size10 = examAnDfilllankService.selectBySurveyId(surveyId,id);
        Long size11 = examAnEnumquService.selectBySurveyId(surveyId,id);
        Long size12 = examAnFillblankService.selectBySurveyId(surveyId,id);
        Long size13 = examAnOrderService.selectBySurveyId(surveyId,id);
        long total = size + size1 + size2 + size3 + size4 + size5 + size6 + size7 + size8 + size9 + size10 + size11 + size12 + size13;
        if ((int) total == CommonNumConstants.NUM_ZERO){
            examSurveyAnswerService.updateMarkFraction(id);
        }
    }
}
