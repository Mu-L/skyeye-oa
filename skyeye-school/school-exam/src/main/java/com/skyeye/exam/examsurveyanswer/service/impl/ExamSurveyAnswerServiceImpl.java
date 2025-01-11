package com.skyeye.exam.examsurveyanswer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
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
import com.skyeye.exam.examsurveyanswer.dao.ExamSurveyAnswerDao;
import com.skyeye.exam.examsurveyanswer.entity.ExamSurveyAnswer;
import com.skyeye.exam.examsurveyanswer.service.ExamSurveyAnswerService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ExamSurveyAnswerServiceImpl
 * @Description: 试卷回答信息表管理服务层
 * @author: skyeye云系列--lyj
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "试卷回答信息表管理", groupName = "试卷回答信息表管理")
public class ExamSurveyAnswerServiceImpl extends SkyeyeBusinessServiceImpl<ExamSurveyAnswerDao, ExamSurveyAnswer> implements ExamSurveyAnswerService {

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

    @Override
    protected void createPrepose(ExamSurveyAnswer entity) {
        if (entity.getBgAnDate().isAfter(entity.getEndAnDate())) {
            throw new CustomException("开始时间不能大于结束时间");
        }
        if (entity.getMarkStartTime().isAfter(entity.getMarkEndTime())){
            throw new CustomException("阅卷开始时间不能大于结束时间");
        }
    }

    @Override
    protected void createPostpose(ExamSurveyAnswer entity, String userId) {
        String surveyId = entity.getSurveyId();
        Integer size = examAnRadioService.selectRadioBySurveyId(surveyId).size();
        Integer size1 = examAnScoreService.selectBySurveyId(surveyId).size();
        Integer size2 = examAnYesnoService.selectBySurveyId(surveyId).size();
        Integer size3 = examAnAnswerService.selectBySurveyId(surveyId).size();
        Integer size4 = examAnCheckboxService.slectBySurveyId(surveyId).size();
        Integer size5 = examAnChenCheckboxService.selectBySurveyId(surveyId).size();
        Integer size6 = examAnChenFbkService.selectBySurveyId(surveyId).size();
        Integer size7 = examAnChenRadioService.selectBySurveyId(surveyId).size();
        Integer size8 = examAnChenScoreService.selectBySurveyId(surveyId).size();
        Integer size9 = examAnCompChenRadioService.selectBySurveyId(surveyId).size();
        Integer size10 = examAnDfilllankService.selectBySurveyId(surveyId).size();
        Integer size11 = examAnEnumquService.selectBySurveyId(surveyId).size();
        Integer size12 = examAnFillblankService.selectBySurveyId(surveyId).size();
        Integer size13 = examAnOrderService.selectBySurveyId(surveyId).size();
        Integer total = size + size1 + size2 + size3 + size4 + size5 + size6 + size7 + size8 + size9 + size10 + size11 + size12 + size13;
        entity.setCompleteNum(total);
    }

    @Override
    public void queryMySurveyAnswerList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String createId = map.get("createId").toString();
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), createId);
        List<ExamSurveyAnswer> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    public ExamSurveyAnswer queryWhetherExamIngByStuId(String userId, String id) {
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), id);
        return getOne(queryWrapper);
    }

    @Override
    public List<ExamSurveyAnswer> querySurveyAnswer(String surveyId, String answerId, String userId) {
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), surveyId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getId), answerId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), userId);
        return list(queryWrapper);
    }

    @Override
    public List<ExamSurveyAnswer> queryNoOrYesSurveyAnswerList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String state = map.get("state").toString();
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getState), state);
        return list(queryWrapper);
    }
}
