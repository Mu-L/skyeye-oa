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
import com.skyeye.eve.entity.School;
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
import com.skyeye.exam.examsurveydirectory.service.ExamSurveyDirectoryService;
import com.skyeye.exam.examsurveymarkexam.entity.ExamSurveyMarkExam;
import com.skyeye.exam.examsurveymarkexam.service.ExamSurveyMarkExamService;
import com.skyeye.exam.examsurveyquanswer.service.ExamSurveyQuAnswerService;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.wall.certification.rest.ICertificationRest;
import com.skyeye.school.common.entity.UserOrStudent;
import com.skyeye.school.common.service.SchoolCommonService;
import com.skyeye.school.exam.service.ExamDirectoryAnService;
import com.skyeye.school.faculty.entity.Faculty;
import com.skyeye.school.faculty.service.FacultyService;
import com.skyeye.school.major.entity.Major;
import com.skyeye.school.major.service.MajorService;
import com.skyeye.school.subject.entity.SubjectClasses;
import com.skyeye.school.subject.entity.SubjectClassesStu;
import com.skyeye.school.subject.service.SubjectClassesService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
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
public class ExamSurveyAnswerServiceImpl extends SkyeyeBusinessServiceImpl<ExamSurveyAnswerDao, ExamSurveyAnswer> implements ExamSurveyAnswerService, ExamDirectoryAnService {

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

    @Autowired
    private SubjectClassesService subjectClassesService;

    @Autowired
    private ExamSurveyMarkExamService examSurveyMarkExamService;

    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    @Autowired
    private SchoolCommonService schoolCommonService;

    @Override
    protected void createPrepose(ExamSurveyAnswer entity) {
        String id = InputObject.getLogParamsStatic().get("id").toString();
        String surveyId = entity.getSurveyId();
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), surveyId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), id);
        if (CollectionUtil.isNotEmpty(list(queryWrapper))) {
            throw new CustomException("该试卷已回答,请勿重复回答");
        }
        String bgAnDate = entity.getBgAnDate();
        String endAnDate = entity.getEndAnDate();
        if (StrUtil.isNotEmpty(bgAnDate) && StrUtil.isNotEmpty(endAnDate)) {
            boolean compare = DateUtil.compare(bgAnDate, endAnDate);
            if (!compare) {
                throw new CustomException("开始时间不能大于结束时间");
            }
        }
    }

    @Override
    protected void updatePrepose(ExamSurveyAnswer entity) {
        String bgAnDate = entity.getBgAnDate();
        String endAnDate = entity.getEndAnDate();
        ExamSurveyAnswer examSurveyAnswer = selectById(entity.getId());
        // 判断是否结束
        if (StrUtil.isNotEmpty(examSurveyAnswer.getId())) {
            if (StrUtil.isNotEmpty(examSurveyAnswer.getEndAnDate())) {
                throw new CustomException("该试卷已结束,请勿修改");
            }
        }
        String markStartTime = entity.getMarkStartTime();
        String markEndTime = entity.getMarkEndTime();
        if (StrUtil.isNotEmpty(markStartTime) && StrUtil.isNotEmpty(markEndTime)) {
            boolean compare = DateUtil.compare(markStartTime, markEndTime);
            if (!compare) {
                throw new CustomException("开始时间不能大于结束时间");
            }
        }
        if (StrUtil.isNotEmpty(bgAnDate) && StrUtil.isNotEmpty(endAnDate)) {
            boolean compare = DateUtil.compare(bgAnDate, endAnDate);
            if (!compare) {
                throw new CustomException("开始时间不能大于结束时间");
            }
            String distanceHMS = getDistanceHMS(bgAnDate, endAnDate);
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
            }
            if (entity.getHandleState().equals(CommonNumConstants.NUM_ONE) && entity.getState().equals(CommonNumConstants.NUM_TWO)) {
                Integer fraction = examSurveyQuAnswerService.selectFractionBySurveyId(entity.getSurveyId());
                entity.setMarkFraction(fraction);
            }
        }

    }

    public static String getDistanceHMS(String bgAnDate, String endAnDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(bgAnDate, formatter);
        LocalDateTime end = LocalDateTime.parse(endAnDate, formatter);
        Duration duration = Duration.between(start, end);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }


    @Override
    protected void deletePreExecution(ExamSurveyAnswer entity) {
        String surveyId = entity.getSurveyId();
        String createId = entity.getCreateId();
        examAnRadioService.deleteBySurAndCreateId(surveyId, createId);
        examAnScoreService.deleteBySurAndCreateId(surveyId, createId);
        examAnYesnoService.deleteBySurAndCreateId(surveyId, createId);
        examAnAnswerService.deleteBySurAndCreateId(surveyId, createId);
        examAnCheckboxService.deleteBySurAndCreateId(surveyId, createId);
        examAnChenCheckboxService.deleteBySurAndCreateId(surveyId, createId);
        examAnChenFbkService.deleteBySurAndCreateId(surveyId, createId);
        examAnChenRadioService.deleteBySurAndCreateId(surveyId, createId);
        examAnChenScoreService.deleteBySurAndCreateId(surveyId, createId);
        examAnCompChenRadioService.deleteBySurAndCreateId(surveyId, createId);
        examAnDfilllankService.deleteBySurAndCreateId(surveyId, createId);
        examAnEnumquService.deleteBySurAndCreateId(surveyId, createId);
        examAnFillblankService.deleteBySurAndCreateId(surveyId, createId);
        examAnOrderService.deleteBySurAndCreateId(surveyId, createId);
    }

    @Override
    public ExamSurveyAnswer selectById(String id) {
        ExamSurveyAnswer examSurveyAnswer = super.selectById(id);
        if (StrUtil.isNotEmpty(examSurveyAnswer.getEndAnDate())){
            throw new CustomException("该试卷已回答结束，不能查看");
        }
        String surveyId = examSurveyAnswer.getSurveyId();
        String studentId = examSurveyAnswer.getCreateId();
        ExamSurveyDirectory examSurveyDirectory = examSurveyDirectoryService.selectBySurAndStuId(surveyId, studentId);
        examSurveyAnswer.setSurveyMation(examSurveyDirectory);
        return examSurveyAnswer;
    }

    @Override
    public void queryMySurveyAnswerList(InputObject inputObject, OutputObject outputObject) {
        String createId = inputObject.getLogParams().get("id").toString();
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), createId);
        queryWrapper.isNull(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getEndAnDate))
            .or()
            .eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getEndAnDate), "");
        List<ExamSurveyAnswer> examSurveyAnswerList = list(queryWrapper);
        List<String> collect = examSurveyAnswerList.stream().map(ExamSurveyAnswer::getSurveyId).collect(Collectors.toList());
        //试卷id及信息
        Map<String, List<ExamSurveyDirectory>> stringListMap = examSurveyDirectoryService.querySurveyListByIds(collect, createId);
        for (ExamSurveyAnswer examSurveyAnswer : examSurveyAnswerList) {
            examSurveyAnswer.setSurveysMation(stringListMap.get(examSurveyAnswer.getSurveyId()));
        }
        outputObject.setBeans(examSurveyAnswerList);
        outputObject.settotal(examSurveyAnswerList.size());
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

    @Override
    public void queryAllSurveyList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        String userId = inputObject.getLogParams().get("id").toString();
        List<ExamSurveyMarkExam> examSurveyMarkExams = examSurveyMarkExamService.selectByUserId(userId);
        List<String> surveyIds = examSurveyMarkExams.stream().map(ExamSurveyMarkExam::getSurveyId).collect(Collectors.toList());
        Map<String, List<ExamSurveyDirectory>> stringListMap = examSurveyDirectoryService.querySurveyListByIds(surveyIds, userId);
        List<ExamSurveyDirectory> examSurveyDirectories = stringListMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        outputObject.setBeans(examSurveyDirectories);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void querySurveyAnswerBySurveyId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String surveyId = commonPageInfo.getHolderId();
        String state = commonPageInfo.getState();
        Integer starts = Integer.valueOf(state);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), surveyId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getState), starts);
        List<ExamSurveyAnswer> list = list(queryWrapper);
        List<String> stuNoList = list.stream().map(ExamSurveyAnswer::getStudentNumber).distinct().collect(Collectors.toList());
        List<Map<String, Object>> userList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(stuNoList)) {
            userList = ExecuteFeignClient.get(() ->
                iCertificationRest.queryUserByStudentNumber(Joiner.on(CommonCharConstants.COMMA_MARK).join(stuNoList))).getRows();
        }
        List<String> schoolIds = list.stream().map(ExamSurveyAnswer::getSchoolId).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        List<String> surveyIds = list.stream().map(ExamSurveyAnswer::getSurveyId).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        List<String> facultyIds = list.stream().map(ExamSurveyAnswer::getFacultyId).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        List<String> majorIds = list.stream().map(ExamSurveyAnswer::getMajorId).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());

        Map<String, List<School>> schoolMap = schoolIds.isEmpty() ? new HashMap<>() : schoolService.selectByIdList(schoolIds);
        Map<String, ExamSurveyDirectory> surveyMap = surveyIds.isEmpty() ? new HashMap<>() : examSurveyDirectoryService.selectMapBysurveyIds(surveyIds);
        Map<String, List<Faculty>> facultyMap = facultyIds.isEmpty() ? new HashMap<>() : facultyService.selectByIdList(facultyIds);
        Map<String, List<Major>> majorMap = majorIds.isEmpty() ? new HashMap<>() : majorService.selectByIdList(majorIds);
        Map<String, Map<String, Object>> userMap = userList.stream()
            .filter(user -> user.get("studentNumber") != null) // 过滤空学号
            .collect(Collectors.toMap(
                user -> user.get("studentNumber").toString(),
                Function.identity(),
                (oldValue, newValue) -> oldValue
            ));

        for (ExamSurveyAnswer answer : list) {
            List<School> schools = schoolMap.getOrDefault(answer.getSchoolId(), Collections.emptyList());
            answer.setSchoolMation(schools.isEmpty() ? null : schools.get(CommonNumConstants.NUM_ZERO));
            answer.setSurveyMation(surveyMap.get(answer.getSurveyId()));
            List<Faculty> faculties = facultyMap.getOrDefault(answer.getFacultyId(), Collections.emptyList());
            answer.setFacultyMation(faculties.isEmpty() ? null : faculties.get(CommonNumConstants.NUM_ZERO));
            List<Major> majors = majorMap.getOrDefault(answer.getMajorId(), Collections.emptyList());
            answer.setMajorMation(majors.isEmpty() ? null : majors.get(CommonNumConstants.NUM_ZERO));
            String studentNumber = answer.getStudentNumber();
            answer.setStuMation(StrUtil.isNotBlank(studentNumber) ? userMap.get(studentNumber) : null);
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
        String state = commonPageInfo.getState();
        Integer IntState = Integer.valueOf(state);
        String userId = inputObject.getLogParams().get("id").toString();
        List<ExamSurveyMarkExam> examSurveyMarkExams = examSurveyMarkExamService.selectByUserId(userId);
        List<String> surveyIds = examSurveyMarkExams.stream().map(ExamSurveyMarkExam::getSurveyId).collect(Collectors.toList());
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), surveyIds);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getState), IntState);
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
        if (CollectionUtil.isEmpty(map)) {
            return Collections.emptyMap();
        }
        return map;
    }

    @Override
    public Map<String, Integer> queryAlreadyAnswerNum(List<String> directoryIds) {
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), directoryIds);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getState), CommonNumConstants.NUM_TWO);
        List<ExamSurveyAnswer> list = list(queryWrapper);
        Map<String, List<ExamSurveyAnswer>> collect = list.stream().collect(Collectors.groupingBy(ExamSurveyAnswer::getSurveyId));
        Map<String, Integer> map = new HashMap<>();
        for (Map.Entry<String, List<ExamSurveyAnswer>> entry : collect.entrySet()) {
            map.put(entry.getKey(), entry.getValue().size());
        }
        if (CollectionUtil.isEmpty(map)) {
            return Collections.emptyMap();
        }
        return map;
    }

    @Override
    public void IsTakeSurveyAnswer(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        String surveyId = inputObject.getParams().get("surveyId").toString();
        ExamSurveyDirectory examSurveyDirectory = examSurveyDirectoryService.selectById(surveyId);
        if (ObjectUtil.isEmpty(examSurveyDirectory)) {
            throw new CustomException("试卷不存在");
        }
        String classId = examSurveyDirectory.getClassId();
        String[] split = classId.split(",");
        List<String> classIds = Arrays.asList(split);
        String subjectId = examSurveyDirectory.getSubjectId();
        boolean yesOrNo = false;
        UserOrStudent userOrStudent = schoolCommonService.queryUserOrStudent(userId);
        if (ObjectUtil.isEmpty(userOrStudent)) {
            throw new CustomException("用户不存在");
        }
        if (userOrStudent.getUserOrStudent()) {
            Map<String, Object> dataMation = userOrStudent.getDataMation();
            String no = null;
            if (dataMation != null) {
                Object noObject = dataMation.get("no");
                if (noObject != null) {
                    no = noObject.toString();
                } else {
                    no = dataMation.get("studentNumber").toString();
                }
            }
            List<SubjectClasses> subjectClassesByObjectIdAndClassesIds = subjectClassesService.getSubjectClassesByObjectIdAndClassesIds(subjectId, classIds);
            List<String> subLinkIds = subjectClassesByObjectIdAndClassesIds.stream().map(SubjectClasses::getId).collect(Collectors.toList());
            List<SubjectClassesStu> subjectClassesStuList = subjectClassesStuService.queryListBySubClassLinkIds(subLinkIds);
            List<String> studentNumber = subjectClassesStuList.stream().map(SubjectClassesStu::getStuNo).collect(Collectors.toList());
            if (studentNumber.contains(no)) {
                yesOrNo = true;
            }
        } else {
            List<SubjectClasses> subjectClassesByObjectIdAndClassesIds = subjectClassesService.getSubjectClassesByObjectIdAndClassesIds(subjectId, classIds);
            List<String> createIds = subjectClassesByObjectIdAndClassesIds.stream().map(SubjectClasses::getCreateId).collect(Collectors.toList());
            if (createIds.contains(userId)) {
                yesOrNo = true;
            }
        }
        outputObject.setBean(yesOrNo);
    }

    @Override
    public Long queryClassExamSurveyAnswerNum(String classId) {
        // 获取试卷id
        List<String> directorIds = examSurveyDirectoryService.queryDirectoryIdsByClassId(classId);
        // 获取参与人次
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getIsComplete), CommonNumConstants.NUM_ONE);
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), directorIds);
        return count(queryWrapper);
    }

    @Override
    public List<ExamSurveyAnswer> selectSurveyIdByUserId(String userId) {
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), userId);
        queryWrapper.isNotNull(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getEndAnDate));
        return list(queryWrapper);
    }

    @Override
    public List<ExamSurveyAnswer> selectSurveyIdByteacherId(String userId) {
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), userId);
        return list(queryWrapper);
    }

    @Override
    public Map<String, Long> queryClassExamSurveyAnswerNumByStuIds(String classesId, List<String> stuIds) {
        List<String> directorIds = examSurveyDirectoryService.queryDirectoryIdsByClassId(classesId);
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getIsComplete),CommonNumConstants.NUM_ONE);
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), directorIds);
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), stuIds);
        List<ExamSurveyAnswer> list = list(queryWrapper);
        if(CollectionUtil.isEmpty(list)){
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.groupingBy(ExamSurveyAnswer::getCreateId, Collectors.counting()));
    }

    @Override
    public void querySurveyBySurveyIdAndUserId(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        String surveyId = inputObject.getParams().get("surveyId").toString();
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), surveyId)
            .eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), userId);
        ExamSurveyAnswer examSurveyAnswer = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(examSurveyAnswer)) {
            ExamSurveyDirectory examSurveyDirectory = examSurveyDirectoryService.selectBySurAndStuId(surveyId, userId);
            examSurveyAnswer.setSurveyMation(examSurveyDirectory);
            outputObject.setBean(examSurveyAnswer);
            outputObject.settotal(1);
        }
    }

    private void extracted(OutputObject outputObject, QueryWrapper<ExamSurveyAnswer> queryWrapper, CommonPageInfo commonPageInfo, Integer page, Integer limit) {
        List<ExamSurveyAnswer> beans = list(queryWrapper); // 获取所有的已批阅回答者信息
        for (ExamSurveyAnswer bean : beans) {
            UserOrStudent userOrStudent = schoolCommonService.queryUserOrStudent(bean.getCreateId());
            bean.setUserMation(userOrStudent);
        }
        // 设置信息：
        List<String> stuNoList = beans.stream().map(ExamSurveyAnswer::getStudentNumber).collect(Collectors.toList());
        List<Map<String, Object>> userList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(stuNoList)) {
            userList = ExecuteFeignClient.get(() ->
                iCertificationRest.queryUserByStudentNumber(Joiner.on(CommonCharConstants.COMMA_MARK).join(stuNoList))).getRows();
        }
        List<String> schoolIds = beans.stream().map(ExamSurveyAnswer::getSchoolId).distinct().collect(Collectors.toList());
        List<String> surveyIds = beans.stream().map(ExamSurveyAnswer::getSurveyId).distinct().collect(Collectors.toList());
        List<String> facultyIds = beans.stream().map(ExamSurveyAnswer::getFacultyId).distinct().collect(Collectors.toList());
        List<String> majorIds = beans.stream().map(ExamSurveyAnswer::getMajorId).distinct().collect(Collectors.toList());
        Map<String, School> schoolMap = schoolService.selectMapByIds(schoolIds);
        Map<String, ExamSurveyDirectory> surveyMap = examSurveyDirectoryService.selectMapBysurveyIds(surveyIds);
        Map<String, Faculty> facultyMap = facultyService.selectMapByIds(facultyIds);
        Map<String, Major> majorMap = majorService.selectMapByIds(majorIds);
        Map<String, Map<String, Object>> userMap = userList.stream()
            .collect(Collectors.toMap(
                user -> user.get("studentNumber").toString(),
                Function.identity(),
                (oldValue, newValue) -> oldValue  // 保留第一个值，忽略后续重复值
            ));
        for (ExamSurveyAnswer answer : beans) {
            answer.setSchoolMation(schoolMap.get(answer.getSchoolId()));
            answer.setSurveyMation(surveyMap.get(answer.getSurveyId()));
            answer.setFacultyMation(facultyMap.get(answer.getFacultyId()));
            answer.setMajorMation(majorMap.get(answer.getMajorId()));
            answer.setStuMation(userMap.get(answer.getStudentNumber()));
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
