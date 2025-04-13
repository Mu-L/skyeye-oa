package com.skyeye.exam.examsurveydirectory.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.common.util.question.QuType;
import com.skyeye.eve.entity.School;
import com.skyeye.eve.examquestion.entity.Question;
import com.skyeye.eve.examquestion.service.QuestionService;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.eve.service.SchoolService;
import com.skyeye.exam.examquchckbox.entity.ExamQuCheckbox;
import com.skyeye.exam.examquchckbox.service.ExamQuCheckboxService;
import com.skyeye.exam.examquchencolumn.entity.ExamQuChenColumn;
import com.skyeye.exam.examquchencolumn.service.ExamQuChenColumnService;
import com.skyeye.exam.examquchenrow.entity.ExamQuChenRow;
import com.skyeye.exam.examquchenrow.service.ExamQuChenRowService;
import com.skyeye.exam.examquestionlogic.entity.ExamQuestionLogic;
import com.skyeye.exam.examquestionlogic.service.ExamQuestionLogicService;
import com.skyeye.exam.examqumultfillblank.entity.ExamQuMultiFillblank;
import com.skyeye.exam.examqumultfillblank.service.ExamQuMultiFillblankService;
import com.skyeye.exam.examquorderby.entity.ExamQuOrderby;
import com.skyeye.exam.examquorderby.service.ExamQuOrderbyService;
import com.skyeye.exam.examquradio.entity.ExamQuRadio;
import com.skyeye.exam.examquradio.service.ExamQuRadioService;
import com.skyeye.exam.examquscore.entity.ExamQuScore;
import com.skyeye.exam.examquscore.service.ExamQuScoreService;
import com.skyeye.exam.examsurveyanswer.entity.ExamSurveyAnswer;
import com.skyeye.exam.examsurveyanswer.service.ExamSurveyAnswerService;
import com.skyeye.exam.examsurveyclass.service.ExamSurveyClassService;
import com.skyeye.exam.examsurveydirectory.dao.ExamSurveyDirectoryDao;
import com.skyeye.exam.examsurveydirectory.entity.ExamSurveyDirectory;
import com.skyeye.exam.examsurveydirectory.service.ExamSurveyDirectoryService;
import com.skyeye.exam.examsurveymarkexam.entity.ExamSurveyMarkExam;
import com.skyeye.exam.examsurveymarkexam.service.ExamSurveyMarkExamService;
import com.skyeye.exception.CustomException;
import com.skyeye.school.common.entity.UserOrStudent;
import com.skyeye.school.common.service.SchoolCommonService;
import com.skyeye.school.exam.service.ExamService;
import com.skyeye.school.faculty.entity.Faculty;
import com.skyeye.school.faculty.service.FacultyService;
import com.skyeye.school.grade.entity.Classes;
import com.skyeye.school.grade.service.ClassesService;
import com.skyeye.school.major.entity.Major;
import com.skyeye.school.major.service.MajorService;
import com.skyeye.school.semester.service.SemesterService;
import com.skyeye.school.subject.entity.SubjectClasses;
import com.skyeye.school.subject.entity.SubjectClassesStu;
import com.skyeye.school.subject.service.SubjectClassesService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import com.skyeye.school.subject.service.SubjectService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ExamSurveyDirectoryServiceImpl
 * @Description: 试卷管理服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "试卷管理", groupName = "试卷管理")
public class ExamSurveyDirectoryServiceImpl extends SkyeyeBusinessServiceImpl<ExamSurveyDirectoryDao, ExamSurveyDirectory> implements ExamSurveyDirectoryService, ExamService {

    @Autowired
    private ExamSurveyClassService examSurveyClassService;

    @Autowired
    private ExamSurveyMarkExamService examSurveyMarkExamService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ExamQuRadioService examQuRadioService;

    @Autowired
    private ExamQuScoreService examquScoreService;

    @Autowired
    private ExamQuCheckboxService examQuCheckboxService;

    @Autowired
    private ExamQuMultiFillblankService examQuMultiFillblankService;

    @Autowired
    private ExamQuOrderbyService examQuOrderbyService;

    @Autowired
    private ExamQuChenColumnService examQuChenColumnService;


    @Autowired
    private ExamQuChenRowService examQuChenRowService;

    @Autowired
    private ExamQuestionLogicService examQuestionLogicService;

    @Autowired
    private ExamSurveyAnswerService examSurveyAnswerService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ClassesService classesService;

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private MajorService majorService;

    @Autowired
    private SubjectClassesService subjectClassesService;

    @Autowired
    private SchoolCommonService schoolCommonService;

    @Autowired
    private SubjectClassesStuService subjectClassesStuService;


    /**
     * 设置考试目录的方法
     *
     * @param inputObject  输入对象，包含请求参数
     * @param outputObject 输出对象，用于返回响应数据
     */
    @Override
    public void setUpExamDirectory(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString(); // 获取试卷ID
        ExamSurveyDirectory examSurveyDirectory = selectById(id);
        if (StrUtil.isEmpty(examSurveyDirectory.getId())) {
            throw new CustomException("该试卷信息不存在。");
        }
        if (ObjUtil.isNotEmpty(examSurveyDirectory)) {
            // 判断试卷是否未发布
            if (examSurveyDirectory.getSurveyState().equals(CommonNumConstants.NUM_ZERO)) {
                String belongId = examSurveyDirectory.getId();
                Integer fractionNumber = getFractionNumber(belongId);
                if (fractionNumber == null || fractionNumber == 0) {
                    throw new CustomException("该试卷没有题目，请添加题目。");
                }
                if (fractionNumber != 0) {
                    UpdateWrapper<ExamSurveyDirectory> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.eq(CommonConstants.ID, id);
                    updateWrapper.set(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSurveyState), CommonNumConstants.NUM_ONE);
                    update(updateWrapper);
                }
            } else {
                throw new CustomException("该试卷已发布，请刷新数据。");
            }
        } else {
            throw new CustomException("该试卷信息不存在。");
        }
    }

    @NotNull
    private Integer getFractionNumber(String belongId) {
        List<Question> questions = questionService.QueryQuestionByBelongId(belongId);
        // 判断是否有题目
        int fraction = 0;
        // 题目总数
        int questionNum = 0;
        if (CollectionUtil.isNotEmpty(questions)) {
            for (Question question : questions) {
                int questionType = question.getQuType();
                if (questionType != QuType.PAGETAG.getIndex() && questionType != QuType.PARAGRAPH.getIndex()) {
                    fraction += question.getFraction();
                    questionNum++;
                }
            }
        }
        // 总分数
        UpdateWrapper<ExamSurveyDirectory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, belongId);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getFraction), fraction);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSurveyQuNum), questionNum);
        update(updateWrapper);
        return fraction;
    }

    /**
     * 参加考试的方法
     *
     * @param inputObject  输入对象，包含请求参数
     * @param outputObject 输出对象，用于返回响应数据
     * @return 允许参加考试时返回考试目录信息
     */
    @Override
    public ExamSurveyDirectory takeExam(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        // 是否可以参加考试，true：可以；false：不可以
        boolean yesOrNo = false;
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String id = map.get("id").toString();
        ExamSurveyDirectory examSurveyDirectory = selectById(id);
        if (StrUtil.isEmpty(examSurveyDirectory.getId())) {
            throw new CustomException("该试卷信息不存在。");
        }
        // 判断试卷是否存在
        if (ObjUtil.isEmpty(examSurveyDirectory)) {
            throw new CustomException("该试卷不存在");
        }
        // 判断试卷是否发布
        if (examSurveyDirectory.getSurveyState().equals(CommonNumConstants.NUM_ONE)) {
            ExamSurveyAnswer examSurveyAnswer = examSurveyAnswerService.queryWhetherExamIngByStuId(userId, id); // 查询用户是否已经参加过该考试
            if (ObjUtil.isNotEmpty(examSurveyAnswer)) {
                throw new CustomException("您已参加过该考试");
            } else {
                yesOrNo = true;
            }
        } else {
            throw new CustomException("该试卷未发布");
        }
        if (yesOrNo) {
            return examSurveyDirectory;
        } else {
            throw new CustomException("您不具备该考试权限");
        }
    }

    /**
     * 复制考试目录的方法
     *
     * @param inputObject  输入对象，包含请求参数
     * @param outputObject 输出对象，用于返回响应数据
     */
    @Override
    public void copyExamDirectory(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String examDirectoryId = map.get("id").toString();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String surveyName = map.get("surveyName").toString();
        ExamSurveyDirectory examSurveyDirectory = selectById(examDirectoryId);
        List<ExamSurveyMarkExam> examSurveyMarkExamList = examSurveyMarkExamService.getExamSurveyMarkExamList(examDirectoryId);
        String userIdJoin = Joiner.on(CommonCharConstants.COMMA_MARK).join(examSurveyMarkExamList.stream().map(ExamSurveyMarkExam::getUserId).collect(Collectors.toList()));
        examSurveyDirectory.setSurveyModel(CommonNumConstants.NUM_ONE);
        examSurveyDirectory.setSurveyState(CommonNumConstants.NUM_ZERO);
        examSurveyDirectory.setCreateId(userId);
        examSurveyDirectory.setCreateTime(DateUtil.getTimeAndToString());
        examSurveyDirectory.setReaderList(userIdJoin);
        examSurveyDirectory.setId(ToolUtil.randomStr(6, 12));
        examSurveyDirectory.setQuestionMation(new ArrayList<>());
        if (StrUtil.isEmpty(surveyName)) {
            examSurveyDirectory.setSurveyName(examSurveyDirectory.getSurveyName() + "_副本");
        } else {
            examSurveyDirectory.setSurveyName(surveyName);
        }
        createEntity(examSurveyDirectory, userId);
        List<Question> questionList = questionService.QueryQuestionByBelongId(examDirectoryId);
        if (CollectionUtil.isNotEmpty(questionList)) {
            List<String> questionIdList = questionList.stream().map(Question::getId).collect(Collectors.toList());
            Map<String, List<ExamQuestionLogic>> stringListMap = examQuestionLogicService.selectByQuestionIds(questionIdList);
            Map<String, List<ExamQuRadio>> stringListMap1 = examQuRadioService.selectByQuestionIds(questionIdList);
            Map<String, List<ExamQuScore>> stringListMap2 = examquScoreService.selectByQuestionIds(questionIdList);
            Map<String, List<ExamQuCheckbox>> stringListMap3 = examQuCheckboxService.selectByQuestionIds(questionIdList);
            Map<String, List<ExamQuMultiFillblank>> stringListMap4 = examQuMultiFillblankService.selectByQuestionIds(questionIdList);
            Map<String, List<ExamQuOrderby>> stringListMap5 = examQuOrderbyService.selectByQuestionIds(questionIdList);
            Map<String, List<ExamQuChenColumn>> stringListMap6 = examQuChenColumnService.selectByQuestionIds(questionIdList);
            Map<String, List<ExamQuChenRow>> stringListMap7 = examQuChenRowService.selectByQuestionIds(questionIdList);
            for (Question question : questionList) {
                String id = question.getId();
                question.setCopyFromId(id);
                List<ExamQuestionLogic> examQuestionLogics = stringListMap.get(id);
                question.setQuestionLogic(examQuestionLogics);
                List<ExamQuRadio> examQuRadioList = stringListMap1.get(id);
                question.setRadioTd(examQuRadioList);
                List<ExamQuScore> examQuScoreList = stringListMap2.get(id);
                question.setScoreTd(examQuScoreList);
                List<ExamQuCheckbox> examQuCheckboxList = stringListMap3.get(id);
                question.setCheckboxTd(examQuCheckboxList);
                question.setCheckboxTd(examQuCheckboxList);
                List<ExamQuMultiFillblank> multiFillblanks = stringListMap4.get(id);
                question.setMultifillblankTd(multiFillblanks);
                List<ExamQuOrderby> examQuOrderbyList = stringListMap5.get(id);
                question.setOrderByTd(examQuOrderbyList);
                List<ExamQuChenColumn> examQuChenColumnList = stringListMap6.get(id);
                question.setColumnTd(examQuChenColumnList);
                List<ExamQuChenRow> examQuChenRows = stringListMap7.get(id);
                question.setRowTd(examQuChenRows);
                question.setBelongId(examSurveyDirectory.getId());
            }
            questionService.createEntity(questionList, userId);
            examSurveyDirectory.setQuestionMation(questionList);
        }
        outputObject.setBean(examSurveyDirectory);
        outputObject.settotal(1);
    }

    /**
     * 创建/更新题目前的操作
     *
     * @param examSurveyDirectory 考试目录对象
     */
    @Override
    public void validatorEntity(ExamSurveyDirectory examSurveyDirectory) {
        super.validatorEntity(examSurveyDirectory);
        String realStartTime = examSurveyDirectory.getRealStartTime();
        String realEndTime = examSurveyDirectory.getRealEndTime();
        if (StrUtil.isNotEmpty(realStartTime) && StrUtil.isNotEmpty(realEndTime)) {
            boolean compareTime = DateUtil.compareTime(realStartTime, realEndTime);
            if (compareTime) {
                throw new CustomException("实际开始时间不能晚于实际结束时间");
            }
        }
    }

    /**
     * 创建考试目录后的后置操作
     *
     * @param entity 考试目录对象
     * @param userId 创建者ID
     */
    @Override
    public void createPostpose(ExamSurveyDirectory entity, String userId) {
        String id = entity.getId();
        String reader = entity.getReaderList();
        List<String> readerIds = Arrays.asList(reader.split(","));
        String classId = entity.getClassId();
        List<String> classIds = Arrays.asList(classId.split(","));
        examSurveyClassService.createExamSurveyClass(id, classIds, userId);
        examSurveyMarkExamService.createExamSurveyMarkExam(id, readerIds, userId);
        List<Question> questionList = entity.getQuestionMation();
        if (CollectionUtil.isNotEmpty(questionList)) {
            for (Question question : questionList) {
                question.setBelongId(id);
            }
            questionService.createEntity(questionList, userId);
        }
    }

    @Override
    public void updatePostpose(ExamSurveyDirectory entity, String userId) {
        String surveId = entity.getId();
        QueryWrapper<ExamSurveyMarkExam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyMarkExam::getSurveyId), surveId);
        examSurveyMarkExamService.remove(queryWrapper);
        String reader = entity.getReaderList();
        List<String> readerIds = Arrays.asList(reader.split(","));
        examSurveyMarkExamService.createExamSurveyMarkExam(surveId, readerIds, userId);
        List<Question> questionList = entity.getQuestionMation();
        List<Question> existingQuestions = questionService.QueryQuestionByBelongId(surveId);
        List<String> existingIds = existingQuestions.stream().map(Question::getId)
            .collect(Collectors.toList());
        Map<Boolean, List<Question>> partitionedQuestions = questionList.stream()
            .collect(Collectors.partitioningBy(question -> StrUtil.isNotEmpty(question.getId())));
        List<Question> questionsWithId = partitionedQuestions.get(true);
        List<Question> questionsWithoutId = partitionedQuestions.get(false);
        List<String> submittedIds = questionsWithId.stream().map(Question::getId)
            .collect(Collectors.toList());
        Set<String> submittedIdSet = new HashSet<>(submittedIds);
        List<String> idsToDelete = existingIds.stream()
            .filter(id -> !submittedIdSet.contains(id))
            .collect(Collectors.toList());
        questionService.deleteById(idsToDelete);
        if (CollectionUtil.isNotEmpty(questionsWithId)) {
            List<Question> createQuestion = new ArrayList<>();
            //纯题目
            List<Question> collect = questionsWithId.stream()
                .filter(question -> StrUtil.isNotEmpty(question.getId()) &&
                    StrUtil.isEmpty(question.getBelongId())).collect(Collectors.toList());
            createQuestion.addAll(collect);
            for (Question question : createQuestion) {
                question.setBelongId(surveId);
            }
            for (Question question : collect) {
                question.setId(StrUtil.EMPTY);
            }
            questionService.updateEntity(createQuestion, userId);
            questionService.createEntity(collect, userId);
            questionService.updateEntity(questionsWithId, userId);
        }
        questionService.createEntity(questionsWithoutId, userId);

    }

    /**
     * 切换是否删除考试目录的方法
     *
     * @param inputObject  输入对象，包含请求参数
     * @param outputObject 输出对象，用于返回响应数据
     */
    @Override
    public void changeWhetherDeleteById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        UpdateWrapper<ExamSurveyDirectory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getWhetherDelete), CommonNumConstants.NUM_TWO);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSurveyState), CommonNumConstants.NUM_TWO);
        update(updateWrapper);
    }

    @Override
    protected void deletePostpose(ExamSurveyDirectory entity) {
        String id = entity.getId();
        questionService.deleteBySurveyDirectoryId(id);
    }

    /**
     * 更新考试状态结束信息的方法
     *
     * @param inputObject  输入对象，包含请求参数
     * @param outputObject 输出对象，用于返回响应数据
     */
    @Override
    public void updateExamMationEndById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String examSurveyDirectoryId = map.get("id").toString();
        QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, examSurveyDirectoryId);
        ExamSurveyDirectory examSurveyDirectory = getOne(queryWrapper);
        // 判断考试目录对象是否存在
        if (ObjUtil.isNotEmpty(examSurveyDirectory)) {
            // 判断考试目录状态是否为进行中（NUM_ONE）
            if (examSurveyDirectory.getSurveyState().equals(CommonNumConstants.NUM_ONE)) {
                // 获取当前时间作为实际结束时间
                String realEndTime = DateUtil.getTimeAndToString();
                UpdateWrapper<ExamSurveyDirectory> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq(CommonConstants.ID, examSurveyDirectoryId);
                // 设置实际结束时间为当前时间
                updateWrapper.set(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getRealEndTime), realEndTime);
                // 设置考试目录状态为已结束（NUM_TWO）
                updateWrapper.set(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSurveyState), CommonNumConstants.NUM_TWO);
                // 设置结束类型为自动结束（NUM_ONE）
                updateWrapper.set(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getEndType), CommonNumConstants.NUM_ONE);
                // 执行更新操作
                update(updateWrapper);
            }
        } else {
            throw new CustomException("该试卷信息不存在!");
        }
    }

    @Override
    public void queryMyExamList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String userId = inputObject.getLogParams().get("id").toString();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getCreateId), userId);
        extracted(commonPageInfo, queryWrapper);
        outputResult(outputObject, page, queryWrapper);
    }

    @Override
    public void queryFilterExamLists(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
        extracted(commonPageInfo, queryWrapper);
        outputResult(outputObject, page, queryWrapper);
    }

    private static void extracted(CommonPageInfo commonPageInfo, QueryWrapper<ExamSurveyDirectory> queryWrapper) {
        // 学校
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderKey())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSchoolId), commonPageInfo.getHolderKey());
        }
        // 院校
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getFacultyId), commonPageInfo.getHolderId());
        }
        // 专业
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectKey())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getMajorId), commonPageInfo.getObjectKey());
        }
        // 科目
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSubjectId), commonPageInfo.getObjectId());
        }
        // 试卷名称
        if (StrUtil.isNotEmpty(commonPageInfo.getKeyword())) {
            queryWrapper.like(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSurveyName), commonPageInfo.getKeyword());
        }
        // 状态
        if (StrUtil.isNotEmpty(commonPageInfo.getState())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSurveyState), commonPageInfo.getState());
        }
    }

    @Override
    public void queryAllExamList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
        outputResult(outputObject, page, queryWrapper);
    }

    @Override
    public void queryMySurvey(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        extracted(commonPageInfo, queryWrapper);
        outputResult(outputObject, page, queryWrapper);
    }

    @Override
    public void querySurveyListBySubjectLinkId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
        // 班级
        String holderId = commonPageInfo.getHolderId();
        // 科目
        String objectId = commonPageInfo.getObjectId();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSubjectId), objectId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSurveyState), CommonNumConstants.NUM_ONE);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getClassId), holderId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getCreateTime));
        List<ExamSurveyDirectory> examSurveyDirectoryList = list(queryWrapper);
        // 总人数
        Integer stuNum = subjectClassesService.queryStuNumBySubjectId(objectId, holderId);
        if (CollectionUtil.isEmpty(examSurveyDirectoryList)) {
            return;
        }
        List<String> directoryIds = examSurveyDirectoryList.stream().map(ExamSurveyDirectory::getId).collect(Collectors.toList());
        // 获取已回答的人数
        Map<String, Integer> answerNumMap = examSurveyAnswerService.queryAnswerNum(directoryIds);
        // 获取已批阅的人数
        Map<String, Integer> alreadyAnswerNum = examSurveyAnswerService.queryAlreadyAnswerNum(directoryIds);
        List<String> surveyList = examSurveyDirectoryList.stream().map(ExamSurveyDirectory::getId).collect(Collectors.toList());
        Map<String, List<Question>> queryQuestionListBySurveyIdList = questionService.queryQuestionListBySurveyIdList(surveyList);
        for (ExamSurveyDirectory examSurveyDirectory : examSurveyDirectoryList) {
            examSurveyDirectory.setQuestionMation(queryQuestionListBySurveyIdList.getOrDefault(examSurveyDirectory.getId(), Collections.emptyList()));
            // 获取已批阅
            int readNum = alreadyAnswerNum.get(examSurveyDirectory.getId()) == null ? CommonNumConstants.NUM_ZERO : alreadyAnswerNum.get(examSurveyDirectory.getId());
            examSurveyDirectory.setReadNum(readNum);
            int answerNum = answerNumMap.get(examSurveyDirectory.getId()) == null ? CommonNumConstants.NUM_ZERO : answerNumMap.get(examSurveyDirectory.getId());
            // 获取未回答的人数
            int unSubmitNum = stuNum - answerNum;
            unSubmitNum = unSubmitNum == -1 ? CommonNumConstants.NUM_ONE : unSubmitNum;
            examSurveyDirectory.setUnSubmitNum(unSubmitNum);
            // 未批阅
            int unReadNum = answerNum - readNum;
            examSurveyDirectory.setUnreadNum(unReadNum);
        }
        outputObject.settotal(page.getTotal());
        outputObject.setBeans(examSurveyDirectoryList);
    }

    @Override
    public Map<String, List<ExamSurveyDirectory>> querySurveyListByIds(List<String> surveyIds, String createId) {
        QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
        Map<String, List<ExamSurveyDirectory>> listMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(surveyIds)) {
            queryWrapper.in(CommonConstants.ID, surveyIds);
            List<ExamSurveyDirectory> examSurveyDirectoryList = list(queryWrapper);
            //试卷Id和对应的创建者题目
            Map<String, List<Question>> stringListMap = questionService.queryQuestionListBySurveyIds(surveyIds, createId);
            for (ExamSurveyDirectory examSurveyDirectory : examSurveyDirectoryList) {
                examSurveyDirectory.setQuestionMation(stringListMap.get(examSurveyDirectory.getId()));
            }
            listMap = examSurveyDirectoryList.stream().collect(Collectors.groupingBy(ExamSurveyDirectory::getId));
        }
        return listMap;
    }

    @Override
    public ExamSurveyDirectory selectBySurAndStuId(String surveyId, String studentId) {
        ExamSurveyDirectory bean = super.selectById(surveyId);
        List<Question> questionList = questionService.QueryQuestionByBelongIdAndStuId(surveyId, studentId);
        bean.setQuestionMation(questionList);
        return bean;
    }

    @Override
    public Map<String, ExamSurveyDirectory> selectMapBysurveyIds(List<String> surveyIds) {
        if (CollectionUtil.isEmpty(surveyIds)) {
            return new HashMap<>();
        }
        QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, surveyIds);
        List<ExamSurveyDirectory> examSurveyDirectoryList = list(queryWrapper);
        if (CollectionUtil.isEmpty(examSurveyDirectoryList)) {
            return new HashMap<>();
        }
        List<String> schoolIds = examSurveyDirectoryList.stream().map(ExamSurveyDirectory::getSchoolId).collect(Collectors.toList());
        List<String> facultyIds = examSurveyDirectoryList.stream().map(ExamSurveyDirectory::getFacultyId).collect(Collectors.toList());
        List<String> majorIds = examSurveyDirectoryList.stream().map(ExamSurveyDirectory::getMajorId).collect(Collectors.toList());

        Map<String, List<School>> schoolMapList = schoolIds.isEmpty() ? new HashMap<>() : schoolService.selectByIdList(schoolIds);
        Map<String, List<Faculty>> facultyMapList = facultyIds.isEmpty() ? new HashMap<>() : facultyService.selectByIdList(facultyIds);
        Map<String, List<Major>> majorMapList = majorIds.isEmpty() ? new HashMap<>() : majorService.selectByIdList(majorIds);
        for (ExamSurveyDirectory examSurveyDirectory : examSurveyDirectoryList) {
            List<School> schools = schoolMapList.getOrDefault(examSurveyDirectory.getSchoolId(), Collections.emptyList());
            examSurveyDirectory.setSchoolMation(schools.isEmpty() ? null : schools.get(CommonNumConstants.NUM_ZERO));

            List<Faculty> faculties = facultyMapList.getOrDefault(examSurveyDirectory.getFacultyId(), Collections.emptyList());
            examSurveyDirectory.setFacultyMation(faculties.isEmpty() ? null : faculties.get(CommonNumConstants.NUM_ZERO));

            List<Major> majors = majorMapList.getOrDefault(examSurveyDirectory.getMajorId(), Collections.emptyList());
            examSurveyDirectory.setMajorMation(majors.isEmpty() ? null : majors.get(CommonNumConstants.NUM_ZERO));
        }
        return examSurveyDirectoryList.stream().collect(Collectors.toMap(ExamSurveyDirectory::getId, examSurveyDirectory -> examSurveyDirectory));
    }

    @Override
    public Long queryClassExamSurveyDirectoryNum(String classId) {
        QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getClassId), classId);
        return count(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> queryListBySubjectId(String subjectId) {
        if (StrUtil.isEmpty(subjectId)){
            return Collections.emptyList();
        }
        QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSubjectId), subjectId);
        return listMaps(queryWrapper);
    }

    @Override
    public List<String> queryDirectoryIdsByClassId(String classId) {
        QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getClassId), classId);
        return list(queryWrapper).stream().map(ExamSurveyDirectory::getId).collect(Collectors.toList());
    }

    @Override
    public void queryMyDoSurvey(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        // 学生老师的userId
        String userId = inputObject.getLogParams().get("id").toString();
        //班级Id
        String holderId = commonPageInfo.getHolderId();
        //科目Id
        String objectId = commonPageInfo.getObjectId();
        UserOrStudent userOrStudent = schoolCommonService.queryUserOrStudent(userId);
        if (userOrStudent.getUserOrStudent().equals(true)) {
            // 学生
            Map<String, Object> dataMation = userOrStudent.getDataMation();
            String studentNumber = dataMation.get("studentNumber").toString();
            if (StrUtil.isEmpty(studentNumber)) {
                return;
            }
            List<SubjectClassesStu> subjectClassesStuList = subjectClassesStuService.querySubClassLinkIdByStuNumberNo(studentNumber);
            if (CollectionUtil.isEmpty(subjectClassesStuList)) {
                return;
            }
            List<String> subClassLinkIds = subjectClassesStuList.stream().map(SubjectClassesStu::getSubClassLinkId).collect(Collectors.toList());
            List<SubjectClasses> subjectClassesList = subjectClassesService.queryClassBySubClassLinkId(subClassLinkIds);
            //学生所在班级对应的科目id
            List<String> objectIds = subjectClassesList.stream().map(SubjectClasses::getObjectId).collect(Collectors.toList());
            String classesId = subjectClassesList.get(CommonNumConstants.NUM_ZERO).getClassesId();
            //科目对应的所有试卷
            Map<String, List<ExamSurveyDirectory>> objectIdMapList = queryDirectoryIdsByClassIds(objectIds, classesId);
            if (CollectionUtils.isEmpty(objectIdMapList)) {
                return;
            }
            // 学生回答并上交的试卷
            List<ExamSurveyAnswer> examSurveyAnswerList = examSurveyAnswerService.selectSurveyIdByUserId(userId);
            if (CollectionUtil.isEmpty(examSurveyAnswerList)) {
                return;
            }
            List<String> yesDoSurveyList = examSurveyAnswerList.stream().map(ExamSurveyAnswer::getSurveyId).collect(Collectors.toList());
            // 过滤掉学生做过的试卷
            objectIdMapList.replaceAll((subjectId, directories) ->
                directories.stream()
                    .filter(dir -> !yesDoSurveyList.contains(dir.getId()))
                    .collect(Collectors.toList())
            );
            // 获取目标科目下的试卷列表
            List<ExamSurveyDirectory> filteredList = Optional.ofNullable(objectIdMapList.get(objectId))
                .orElseGet(Collections::emptyList);
            int pageSize = Math.max(1, commonPageInfo.getLimit());
            int pageNum = Math.max(1, commonPageInfo.getPage());
            // 计算分页范围
            int total = filteredList.size();
            int fromIndex = (pageNum - 1) * pageSize;
            // 分页结果处理
            List<ExamSurveyDirectory> pagedList;
            if (fromIndex >= total) {
                pagedList = Collections.emptyList();
            } else {
                int toIndex = Math.min(fromIndex + pageSize, total);
                pagedList = filteredList.subList(fromIndex, toIndex);
            }

            outputObject.settotal(filteredList.size());
            outputObject.setBeans(pagedList);
        } else {
            // 老师
            List<SubjectClasses> subjectClassesList = subjectClassesService.selectByCreateId(userId);
            if (CollectionUtil.isEmpty(subjectClassesList)) {
                return;
            }
            //老师创建的所有班级
            List<String> classIds = subjectClassesList.stream().map(SubjectClasses::getClassesId).collect(Collectors.toList());
            //科目对应的老师创建的所有班级对应的试卷
            Map<String, Map<String, List<ExamSurveyDirectory>>> subClassMapList = Optional
                .ofNullable(selectSurveyListByClassIds(classIds))
                .orElseGet(Collections::emptyMap);
            //老师回答过的答卷
            List<ExamSurveyAnswer> examSurveyAnswerList = examSurveyAnswerService.selectSurveyIdByteacherId(userId);
            if (CollectionUtil.isEmpty(examSurveyAnswerList)) {
                return;
            }
            Set<String> yesDoSurveyIds = examSurveyAnswerList.stream().map(ExamSurveyAnswer::getSurveyId)
                .collect(Collectors.toSet());
            // 遍历三层结构：科目 -> 班级 -> 试卷列表
            subClassMapList.forEach((subjectId, classMap) -> {
                classMap.forEach((classId, surveyList) -> {
                    surveyList.forEach(survey -> {
                        // 如果当前试卷ID在老师回答过的集合中存在
                        if (yesDoSurveyIds.contains(survey.getId())) {
                            survey.setIsAnswered(true); // 设置已答标记
                        }
                    });
                });
            });
            List<ExamSurveyDirectory> allResults = Optional.ofNullable(subClassMapList.get(objectId))
                .map(classMap -> classMap.get(holderId))
                .orElseGet(Collections::emptyList);
            int pageSize = Math.max(1, commonPageInfo.getLimit());
            int pageNum = Math.max(1, commonPageInfo.getPage());
            long offset = (long) (pageNum - 1) * pageSize;
            // 分页处理
            List<ExamSurveyDirectory> pagedList = allResults.stream()
                .skip(offset)
                .limit(pageSize)
                .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(pagedList)) {
                outputObject.settotal(CommonNumConstants.NUM_ZERO);
                outputObject.setBeans(Collections.emptyList());
            } else {
                outputObject.settotal(allResults.size());
                outputObject.setBeans(pagedList);
            }
        }
    }

    @Override
    public List<ExamSurveyDirectory> queryCreatedSurveyListByUserId(String userId) {
        QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getCreateId), userId);
        return list(queryWrapper);
    }

    private Map<String, Map<String, List<ExamSurveyDirectory>>> selectSurveyListByClassIds(List<String> classIds) {
        QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getClassId), classIds);
        Map<String, Map<String, List<ExamSurveyDirectory>>> result = list(queryWrapper).stream()
            .collect(Collectors.groupingBy(
                ExamSurveyDirectory::getSubjectId, // 外层 Map 的键是科目ID
                Collectors.groupingBy(
                    ExamSurveyDirectory::getClassId // 内层 Map 的键是班级ID
                )
            ));
        return result;
    }

    private List<ExamSurveyDirectory> selectAllSurveyList(List<String> surveyIds) {
        QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, surveyIds);
        return list(queryWrapper);
    }

    private Map<String, List<ExamSurveyDirectory>> queryDirectoryIdsByClassIds(List<String> objectIds, String classesId) {
        if (CollectionUtil.isEmpty(objectIds)) {
            return new HashMap<>();
        }
        QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSubjectId), objectIds);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getClassId), classesId);
        List<ExamSurveyDirectory> examSurveyDirectoryList = list(queryWrapper);
        return examSurveyDirectoryList.stream().collect(Collectors.groupingBy(ExamSurveyDirectory::getSubjectId));
    }

    @Override
    public void validatorEntity(List<ExamSurveyDirectory> entity) {
        super.validatorEntity(entity);
        ExamSurveyDirectory examSurveyDirectory = entity.get(CommonNumConstants.NUM_ZERO);
        String realStartTime = examSurveyDirectory.getRealStartTime();
        String realEndTime = examSurveyDirectory.getRealEndTime();
        if (ObjUtil.isNotEmpty(realStartTime) && ObjUtil.isNotEmpty(realEndTime)) {
            boolean compareTime = DateUtil.compareTime(realStartTime, realEndTime);
            if (compareTime) {
                throw new CustomException("实际开始时间不能晚于实际结束时间");
            }
        }
    }

    @Override
    protected void deletePostpose(String id) {
        examSurveyClassService.deleteSurveyClassBySurveyId(id);
        examSurveyMarkExamService.deleteSurveyMarkExamBySurveyId(id);
    }

    /**
     * 批量新增
     *
     * @param inputObject  输入对象，包含请求参数
     * @param outputObject 输出对象，用于返回响应数据
     */
    @Override
    public void createExamDirectory(InputObject inputObject, OutputObject outputObject) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        ExamSurveyDirectory examSurveyDirectory = inputObject.getParams(ExamSurveyDirectory.class);
        String classIds = examSurveyDirectory.getClassId();
        List<String> classsIds = Arrays.asList(classIds.split(","));
        List<ExamSurveyDirectory> examSurveyDirectoryList = new ArrayList<>();
        for (String classId : classsIds) {
            examSurveyDirectory.setClassId(classId);
            examSurveyDirectoryList.add(examSurveyDirectory);
        }
        createEntity(examSurveyDirectoryList, userId);
    }

    @Override
    public void createPostpose(List<ExamSurveyDirectory> examSurveyDirectory, String userId) {


        for (ExamSurveyDirectory entity : examSurveyDirectory) {
            String id = entity.getId();
            String reader = entity.getReaderList();
            List<String> readerIds = Arrays.asList(reader.split(","));
            // 创建考试班级
            examSurveyMarkExamService.createExamSurveyMarkExam(id, readerIds, userId);
            String classId = entity.getClassId();
            List<String> classIds = Arrays.asList(classId.split(","));
            // 创建考试试卷
            examSurveyClassService.createExamSurveyClass(id, classIds, userId);
        }
    }

    private void outputResult(OutputObject outputObject, Page page, QueryWrapper<ExamSurveyDirectory> queryWrapper) {
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getWhetherDelete), CommonNumConstants.NUM_ONE);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getCreateTime));
        List<ExamSurveyDirectory> beans = list(queryWrapper).stream().map(item -> {
            //设置科目信息
            item.setSubjectMation(subjectService.selectById(item.getSubjectId()));
            //设置学校信息
            item.setSchoolMation(schoolService.selectById(item.getSchoolId()));
            //设置学院信息
            item.setFacultyMation(facultyService.selectById(item.getFacultyId()));
            //设置专业信息
            item.setMajorMation(majorService.selectById(item.getMajorId()));
            //设置学期信息
            item.setSemesterMation(semesterService.selectById(item.getSemesterId()));
            String classId = item.getClassId();
            if (StrUtil.isEmpty(classId)) {
                return item;
            }
            List<Classes> classesList = new ArrayList<>();
            if (StrUtil.isNotEmpty(classId)) {
                classesList = classesService.selectByIds(classId);
            }
            item.setClassesMation(classesList);
            return item;
        }).collect(Collectors.toList());
        iAuthUserService.setName(beans, "createId", "createName");
        iAuthUserService.setName(beans, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(beans);
        outputObject.settotal(page.getTotal());
    }


    @Override
    public ExamSurveyDirectory selectById(String id) {
        ExamSurveyDirectory bean = getExamSurveyDirectory(id);
        Integer fractionNumber = getFractionNumber(id);
        List<Question> questionList = questionService.QueryQuestionByBelongId(bean.getId());
        if (CollectionUtil.isEmpty(questionList)) {
            return bean;
        }
        bean.setQuestionMation(questionList);
        bean.setFraction(fractionNumber);
        return bean;
    }

    @Override
    public void selectById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        ExamSurveyDirectory bean = getExamSurveyDirectory(id);
        Integer fractionNumber = getFractionNumber(id);
        bean.setFraction(fractionNumber);
        List<Question> questionList = questionService.QueryQuestionByBelongId(bean.getId());
        if (CollectionUtil.isEmpty(questionList)) {
            outputObject.setBean(bean);
        }
        outputObject.setBean(bean);
        outputObject.setBeans(questionList);
    }

    @NotNull
    private ExamSurveyDirectory getExamSurveyDirectory(String id) {
        ExamSurveyDirectory bean = super.selectById(id);
        bean.setSubjectMation(subjectService.selectById(bean.getSubjectId()));
        bean.setSchoolMation(schoolService.selectById(bean.getSchoolId()));
        String classId = bean.getClassId();
        if (StrUtil.isNotEmpty(classId)) {
            String[] split = classId.split(",");
            List<String> stringList = Arrays.asList(split);
            List<Classes> classesList = classesService.selectClssByIds(stringList);
            bean.setClassesMation(classesList);
        }
        bean.setFacultyMation(facultyService.selectById(bean.getFacultyId()));
        bean.setMajorMation(majorService.selectById(bean.getMajorId()));
        bean.setSemesterMation(semesterService.selectById(bean.getSemesterId()));
        List<ExamSurveyMarkExam> examSurveyMarkExamList = examSurveyMarkExamService.selectBySurveyId(bean.getId());
        if (CollectionUtil.isNotEmpty(examSurveyMarkExamList)) {
            List<String> markIds = examSurveyMarkExamList.stream().map(ExamSurveyMarkExam::getUserId).collect(Collectors.toList());
            String markIdsString = String.join(",", markIds);
            List<Map<String, Object>> userMationList = iAuthUserService.queryDataMationByIds(markIdsString);
            bean.setReaderMationList(userMationList);
        }
        return bean;
    }
}
