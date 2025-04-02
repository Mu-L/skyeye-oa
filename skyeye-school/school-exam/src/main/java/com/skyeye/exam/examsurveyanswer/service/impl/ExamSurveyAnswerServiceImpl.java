package com.skyeye.exam.examsurveyanswer.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.examquestion.entity.Question;
import com.skyeye.eve.examquestion.service.QuestionService;
import com.skyeye.eve.service.SchoolService;
import com.skyeye.exam.examananswer.entity.ExamAnAnswer;
import com.skyeye.exam.examananswer.service.ExamAnAnswerService;
import com.skyeye.exam.examancheckbox.entitiy.ExamAnCheckbox;
import com.skyeye.exam.examancheckbox.service.ExamAnCheckboxService;
import com.skyeye.exam.examanchencheckbox.entity.ExamAnChenCheckbox;
import com.skyeye.exam.examanchencheckbox.service.ExamAnChenCheckboxService;
import com.skyeye.exam.examanchenfbk.entity.ExamAnChenFbk;
import com.skyeye.exam.examanchenfbk.service.ExamAnChenFbkService;
import com.skyeye.exam.examanchenradio.entity.ExamAnChenRadio;
import com.skyeye.exam.examanchenradio.service.ExamAnChenRadioService;
import com.skyeye.exam.examanchenscore.entity.ExamAnChenScore;
import com.skyeye.exam.examanchenscore.service.ExamAnChenScoreService;
import com.skyeye.exam.examancompchenradio.entity.ExamAnCompChenRadio;
import com.skyeye.exam.examancompchenradio.service.ExamAnCompChenRadioService;
import com.skyeye.exam.examandfillblank.entity.ExamAnDfillblank;
import com.skyeye.exam.examandfillblank.service.ExamAnDfilllankService;
import com.skyeye.exam.examanenumqu.entity.ExamAnEnumqu;
import com.skyeye.exam.examanenumqu.service.ExamAnEnumquService;
import com.skyeye.exam.examanfillblank.entity.ExamAnFillblank;
import com.skyeye.exam.examanfillblank.service.ExamAnFillblankService;
import com.skyeye.exam.examanorder.entity.ExamAnOrder;
import com.skyeye.exam.examanorder.service.ExamAnOrderService;
import com.skyeye.exam.examanradio.entity.ExamAnRadio;
import com.skyeye.exam.examanradio.service.ExamAnRadioService;
import com.skyeye.exam.examanscore.entity.ExamAnScore;
import com.skyeye.exam.examanscore.service.ExamAnScoreService;
import com.skyeye.exam.examanyesno.entity.ExamAnYesno;
import com.skyeye.exam.examanyesno.service.ExamAnYesnoService;
import com.skyeye.exam.examsurveyanswer.dao.ExamSurveyAnswerDao;
import com.skyeye.exam.examsurveyanswer.entity.ExamSurveyAnswer;
import com.skyeye.exam.examsurveyanswer.service.ExamSurveyAnswerService;
import com.skyeye.exam.examsurveydirectory.entity.ExamSurveyDirectory;
import com.skyeye.exam.examsurveydirectory.service.ExamSurveyDirectoryService;
import com.skyeye.exam.examsurveymarkexam.entity.ExamSurveyMarkExam;
import com.skyeye.exam.examsurveymarkexam.service.ExamSurveyMarkExamService;
import com.skyeye.exam.examsurveyquanswer.service.ExamSurveyQuAnswerService;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.wall.certification.rest.ICertificationRest;
import com.skyeye.school.faculty.service.FacultyService;
import com.skyeye.school.major.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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

    @Autowired
    private ExamSurveyDirectoryService examSurveyDirectoryService;

    @Autowired
    private MajorService majorService;

    @Autowired
    private FacultyService facultyService;

    @Override
    protected void createPrepose(ExamSurveyAnswer entity) {
        String bgAnDate = entity.getBgAnDate();
        String endAnDate = entity.getEndAnDate();
        if (StrUtil.isNotEmpty(bgAnDate) && StrUtil.isNotEmpty(endAnDate)) {
            boolean compare = DateUtil.compare(bgAnDate, endAnDate);
            if (compare) {
                throw new CustomException("开始时间不能大于结束时间");
            }
        }
    }

    @Override
    protected void updatePrepose(ExamSurveyAnswer entity) {
        String bgAnDate = entity.getBgAnDate();
        String endAnDate = entity.getEndAnDate();
        if (endAnDate == null) {
            throw new CustomException("结束时间不能为空");
        }
        String distanceHMS = DateUtil.getDistanceHMS(bgAnDate, endAnDate);
        entity.setTotalTime(distanceHMS);
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
    public ExamSurveyAnswer selectById(String id) {
        ExamSurveyAnswer examSurveyAnswer = super.selectById(id);
        String surveyId = examSurveyAnswer.getSurveyId();
        List<ExamAnRadio> examAnRadioList = examAnRadioService.selectRadioBySurveyId(surveyId);
        List<ExamAnScore> examAnScoreList = examAnScoreService.selectBySurveyId(surveyId);
        List<ExamAnYesno> examAnYesnoList = examAnYesnoService.selectBySurveyId(surveyId);
        List<ExamAnAnswer> examAnAnswerList = examAnAnswerService.selectBySurveyId(surveyId);
        List<ExamAnCheckbox> examAnCheckboxList = examAnCheckboxService.slectBySurveyId(surveyId);
        List<ExamAnChenCheckbox> examAnChenCheckboxList = examAnChenCheckboxService.selectBySurveyId(surveyId);
        List<ExamAnChenFbk> examAnChenFbkList = examAnChenFbkService.selectBySurveyId(surveyId);
        List<ExamAnChenRadio> examAnChenRadioList = examAnChenRadioService.selectBySurveyId(surveyId);
        List<ExamAnChenScore> examAnChenScoreList = examAnChenScoreService.selectBySurveyId(surveyId);
        List<ExamAnCompChenRadio> examAnCompChenRadioList = examAnCompChenRadioService.selectBySurveyId(surveyId);
        List<ExamAnDfillblank> examAnDfillblankList = examAnDfilllankService.selectBySurveyId(surveyId);
        List<ExamAnEnumqu> examAnEnumquList = examAnEnumquService.selectBySurveyId(surveyId);
        List<ExamAnFillblank> examAnFillblankList = examAnFillblankService.selectBySurveyId(surveyId);
        List<ExamAnOrder> examAnOrderList = examAnOrderService.selectBySurveyId(surveyId);
        examSurveyAnswer.setExamAnRadioList(examAnRadioList);
        examSurveyAnswer.setExamAnScoreList(examAnScoreList);
        examSurveyAnswer.setExamAnYesnoList(examAnYesnoList);
        examSurveyAnswer.setExamAnAnswerList(examAnAnswerList);
        examSurveyAnswer.setExamAnCheckboxList(examAnCheckboxList);
        examSurveyAnswer.setExamAnChenCheckboxList(examAnChenCheckboxList);
        examSurveyAnswer.setExamAnChenFbkList(examAnChenFbkList);
        examSurveyAnswer.setExamAnChenRadioList(examAnChenRadioList);
        examSurveyAnswer.setExamAnChenScoreList(examAnChenScoreList);
        examSurveyAnswer.setExamAnCompChenRadioList(examAnCompChenRadioList);
        examSurveyAnswer.setExamAnDfillblankList(examAnDfillblankList);
        examSurveyAnswer.setExamAnEnumquList(examAnEnumquList);
        examSurveyAnswer.setExamAnFillblankList(examAnFillblankList);
        examSurveyAnswer.setExamAnOrderList(examAnOrderList);
        return examSurveyAnswer;
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
        queryWrapper.eq(CommonConstants.ID, answerId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), userId);
        return list(queryWrapper);
    }

    @Autowired
    private ExamSurveyMarkExamService examSurveyMarkExamService;

    @Override
    public void queryNoOrYesSurveyAnswerList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        String userId = inputObject.getLogParams().get("id").toString();
        List<ExamSurveyMarkExam> examSurveyMarkExams = examSurveyMarkExamService.selectByUserId(userId);
        List<String> surveyIds = examSurveyMarkExams.stream().map(ExamSurveyMarkExam::getSurveyId).collect(Collectors.toList());
        List<ExamSurveyDirectory> examSurveyDirectories = examSurveyDirectoryService.querySurveyListByIds(surveyIds);
        outputObject.setBeans(examSurveyDirectories);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void querySurveyAnswerBySurveyId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String surveyId = commonPageInfo.getHolderId();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), surveyId);
        List<ExamSurveyAnswer> list = list(queryWrapper);
        List<String> stuNoList = list.stream().map(ExamSurveyAnswer::getStudentNumber).distinct().collect(Collectors.toList());
        List<Map<String, Object>> userList = ExecuteFeignClient.get(() ->
            iCertificationRest.queryUserByStudentNumber(Joiner.on(CommonCharConstants.COMMA_MARK).join(stuNoList))).getRows();
        //根据学号分别设置到对应的试卷回答信息中
        for (ExamSurveyAnswer examSurveyAnswer : list) {
            examSurveyAnswer.setSchoolMation(schoolService.selectById(examSurveyAnswer.getSchoolId()));
            examSurveyAnswer.setSurveyMation(examSurveyDirectoryService.selectById(examSurveyAnswer.getSurveyId()));
            examSurveyAnswer.setFacultyMation(facultyService.selectById(examSurveyAnswer.getFacultyId()));
            examSurveyAnswer.setMajorMation(majorService.selectById(examSurveyAnswer.getMajorId()));
            for (Map<String, Object> user : userList) {
                if (examSurveyAnswer.getStudentNumber().equals(user.get("studentNumber"))) {
                    examSurveyAnswer.setStuMation(user);
                }
            }
        }
        iAuthUserService.setName(list, "createId", "createName");
        outputObject.setBeans(list);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void queryFilterApprovedSurveys(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Integer page = commonPageInfo.getPage();
        Integer limit = commonPageInfo.getLimit();
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getState), CommonNumConstants.NUM_TWO);
        extracted(outputObject, queryWrapper, commonPageInfo, page, limit);
    }

    @Override
    public void queryFilterToBeReviewedSurveys(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Integer page = commonPageInfo.getPage();
        Integer limit = commonPageInfo.getLimit();
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getState), CommonNumConstants.NUM_ONE);
        extracted(outputObject, queryWrapper, commonPageInfo, page, limit);
    }

    @Override
    public Map<String, Integer> queryAnswerNum(List<String> directoryIds) {
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), directoryIds);
        List<ExamSurveyAnswer> list = list(queryWrapper);
        Map<String, List<ExamSurveyAnswer>> collect = list.stream().collect(Collectors.groupingBy(ExamSurveyAnswer::getSurveyId));
        Map<String, Integer> map = new HashMap<>();
        for (Map.Entry<String, List<ExamSurveyAnswer>> entry : collect.entrySet()) {
            map.put(entry.getKey(), entry.getValue().size());
        }
        if(CollectionUtil.isEmpty(map)){
            return Collections.emptyMap();
        }
        return map;
    }

    @Override
    public void querySurveyBySurveyIdAndUserId(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        String surveyId = inputObject.getParams().get("surveyId").toString();
        ExamSurveyDirectory examSurveyDirectory = examSurveyDirectoryService.selectById(surveyId);
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), surveyId)
            .eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), userId);
        ExamSurveyAnswer examSurveyAnswer = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(examSurveyAnswer)){
            examSurveyAnswer.setSurveyMation(examSurveyDirectory);
            outputObject.setBean(examSurveyAnswer);
            outputObject.settotal(1);
        }
    }

    private void extracted(OutputObject outputObject, QueryWrapper<ExamSurveyAnswer> queryWrapper, CommonPageInfo commonPageInfo, Integer page, Integer limit) {
        List<ExamSurveyAnswer> beans = list(queryWrapper); // 获取所有的已批阅信息
        // 设置信息：
        List<String> stuNoList = beans.stream().map(ExamSurveyAnswer::getStudentNumber).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(stuNoList)) {
            List<Map<String, Object>> userList = ExecuteFeignClient.get(() ->
                iCertificationRest.queryUserByStudentNumber(Joiner.on(CommonCharConstants.COMMA_MARK).join(stuNoList))).getRows();
            for (ExamSurveyAnswer examSurveyAnswer : beans) {
                examSurveyAnswer.setSchoolMation(schoolService.selectById(examSurveyAnswer.getSchoolId()));
                examSurveyAnswer.setSurveyMation(examSurveyDirectoryService.selectById(examSurveyAnswer.getSurveyId()));
                examSurveyAnswer.setFacultyMation(facultyService.selectById(examSurveyAnswer.getFacultyId()));
                examSurveyAnswer.setMajorMation(majorService.selectById(examSurveyAnswer.getMajorId()));
                for (Map<String, Object> user : userList) {
                    if (examSurveyAnswer.getStudentNumber().equals(user.get("studentNumber"))) {
                        examSurveyAnswer.setStuMation(user);
                    }
                }
            }
        }
        // 学校
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderKey())) {
            beans = beans.stream().filter(examSurveyAnswer -> examSurveyAnswer.getSchoolId().equals(commonPageInfo.getHolderKey())).collect(Collectors.toList());
        }
        // 院校
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            beans = beans.stream().filter(examSurveyAnswer -> examSurveyAnswer.getFacultyId().equals(commonPageInfo.getHolderId())).collect(Collectors.toList());
        }
        // 专业
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectKey())) {
            beans = beans.stream().filter(examSurveyAnswer -> examSurveyAnswer.getMajorId().equals(commonPageInfo.getObjectKey())).collect(Collectors.toList());
        }
        // 学号
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            beans = beans.stream().filter(examSurveyAnswer -> {
                Map<String, Object> stuMation = examSurveyAnswer.getStuMation();
                return StrUtil.contains((String) stuMation.get("studentNumber"), commonPageInfo.getObjectId());
            }).collect(Collectors.toList());
        }
        // 是否包含模糊搜索学生名字
        if (StrUtil.isNotEmpty(commonPageInfo.getType())) {
            beans = beans.stream().filter(examSurveyAnswer -> {
                Map<String, Object> stuMation = examSurveyAnswer.getStuMation();
                return StrUtil.contains((String) stuMation.get("realName"), commonPageInfo.getType());
            }).collect(Collectors.toList());
        }
        // 试卷名
        if (StrUtil.isNotEmpty(commonPageInfo.getKeyword())) {
            beans = beans.stream().filter(examSurveyAnswer -> {
                ExamSurveyDirectory surveyMation = examSurveyAnswer.getSurveyMation();
                return StrUtil.contains(surveyMation.getSurveyName(), commonPageInfo.getKeyword());
            }).collect(Collectors.toList());
        }
        // 将筛选后端beans按分页参数返回
        int fromIndex = (page - 1) * limit;
        if (fromIndex >= beans.size()) {
            outputObject.setBeans(new ArrayList<>());
            outputObject.settotal(CommonNumConstants.NUM_ONE);
        }
        int toIndex = Math.min(fromIndex + limit, beans.size());
        outputObject.setBeans(beans.subList(fromIndex, toIndex));
        outputObject.settotal(beans.size());
    }

}
