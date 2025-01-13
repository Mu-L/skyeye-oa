package com.skyeye.exam.examsurveyanswer.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.SchoolService;
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
import com.skyeye.exam.examsurveydirectory.entity.ExamSurveyDirectory;
import com.skyeye.exam.examsurveyquanswer.service.ExamSurveyQuAnswerService;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.wall.certification.rest.ICertificationRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private ExamSurveyQuAnswerService examSurveyQuAnswerService;

    @Autowired
    private ICertificationRest iCertificationRest;

    @Autowired
    private SchoolService schoolService;

    @Override
    protected void createPrepose(ExamSurveyAnswer entity) {
        LocalDateTime bgAnDate = entity.getBgAnDate();
        //进行空指针判断
        if (bgAnDate == null) {
            throw new CustomException("开始时间不能为空");
        }
        if (entity.getBgAnDate().isAfter(entity.getEndAnDate())) {
            throw new CustomException("开始时间不能大于结束时间");
        }
    }

    @Override
    protected void updatePrepose(ExamSurveyAnswer entity) {
        LocalDateTime bgAnDate = entity.getBgAnDate();
        LocalDateTime endAnDate = entity.getEndAnDate();
        LocalDateTime markStartTime = entity.getMarkStartTime();
        LocalDateTime markEndTime = entity.getMarkEndTime();
        //进行空指针判断
        if (endAnDate == null) {
            throw new CustomException("结束时间不能为空");
        }
        if (markStartTime == null || markEndTime == null) {
            throw new CustomException("批阅开始时间或结束时间不能为空");
        }
        Duration duration = Duration.between(bgAnDate, endAnDate); // 计算时间差
        if (duration.isNegative()) {
            throw new CustomException("开始时间不能大于结束时间");
        }
        // 将时间差转换为总小时数（浮点数）
        float totalHours = (float) duration.toHours() + (float) duration.toMinutes() / 60.0f + (float) duration.toMillis() / 3600000.0f;
        entity.setTotalTime(totalHours); // 设置时间差到totalTime属性
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
        if (total.equals(entity.getQuNum())) {
            entity.setIsComplete(CommonNumConstants.NUM_ONE);
        } else if (total < entity.getQuNum()) {
            throw new CustomException("未完成所有题目");
        }
        if (entity.getHandleState().equals(CommonNumConstants.NUM_ONE) && entity.getState().equals(CommonNumConstants.NUM_TWO)) {
            Integer fraction = examSurveyQuAnswerService.selectFractionBySurveyId(entity.getSurveyId());
            entity.setMarkFraction(fraction);
        }
    }

    @Override
    public void queryMySurveyAnswerList(InputObject inputObject, OutputObject outputObject) {
        String createId = InputObject.getLogParamsStatic().get("id").toString();
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

    @Override
    public void querySurveyAnswerBySurveyId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String surveyId = commonPageInfo.getHolderId();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), surveyId);
        List<ExamSurveyAnswer> list = list(queryWrapper);
        List<String> stuNoList = list.stream().map(ExamSurveyAnswer::getNo).distinct().collect(Collectors.toList());
        List<Map<String, Object>> userList = ExecuteFeignClient.get(() ->
                iCertificationRest.queryUserByStudentNumber(Joiner.on(CommonCharConstants.COMMA_MARK).join(stuNoList))).getRows();
        //根据学号分别设置到对应的试卷回答信息中
        for (ExamSurveyAnswer examSurveyAnswer : list) {
            examSurveyAnswer.setSchoolMation(schoolService.selectById(examSurveyAnswer.getSchoolId()));
            for (Map<String, Object> user : userList) {
                if (examSurveyAnswer.getNo().equals(user.get("studentNumber"))) {
                    examSurveyAnswer.setStuMation(user);
                }
            }
        }
        iAuthUserService.setName(list,"createId","createName");
        outputObject.setBeans(list);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void queryFilterApprovedSurveys(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getState),CommonNumConstants.NUM_TWO);
        extracted(commonPageInfo, queryWrapper);
        if(StrUtil.isNotEmpty(commonPageInfo.getKeyword())){

        }
    }

    @Override
    public void queryFilterToBeReviewedSurveys(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getState),CommonNumConstants.NUM_ONE);
        extracted(commonPageInfo, queryWrapper);
        List<ExamSurveyAnswer> beans = list(queryWrapper);
        // 学生名字
        // 学号
        // 试卷名
    }

    private static void extracted(CommonPageInfo commonPageInfo, QueryWrapper<ExamSurveyAnswer> queryWrapper) {
        // 学校
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderKey())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSchoolId), commonPageInfo.getHolderKey());
        }
        // 院校
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getFacultyId), commonPageInfo.getHolderId());
        }
        // 专业
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectKey())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getMajorId), commonPageInfo.getObjectKey());
        }
    }

}
