package com.skyeye.exam.examsurveydirectory.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.common.util.question.QuType;
import com.skyeye.eve.examquestion.entity.Question;
import com.skyeye.eve.examquestion.service.QuestionService;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.exam.examquchckbox.entity.ExamQuCheckbox;
import com.skyeye.exam.examquchckbox.service.ExamQuCheckboxService;
import com.skyeye.exam.examquchencolumn.entity.ExamQuChenColumn;
import com.skyeye.exam.examquchencolumn.service.ExamQuChenColumnService;
import com.skyeye.exam.examquchenoption.service.ExamQuChenOptionService;
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
import com.skyeye.school.grade.service.ClassesService;
import com.skyeye.school.subject.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
public class ExamSurveyDirectoryServiceImpl extends SkyeyeBusinessServiceImpl<ExamSurveyDirectoryDao, ExamSurveyDirectory> implements ExamSurveyDirectoryService {

    @Autowired
    private ExamSurveyClassService examSurveyClassService;

    @Autowired
    private ExamSurveyDirectoryService examSurveyDirectoryService;

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
    private ExamQuScoreService examQuScoreService;

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
    private ExamQuChenOptionService examQuChenOptionService;

    /**
     * 设置考试目录的方法
     *
     * @param inputObject  输入对象，包含请求参数
     * @param outputObject 输出对象，用于返回响应数据
     */
    @Override
    public void setUpExamDirectory(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams(); // 获取请求参数Map
        String id = map.get("id").toString(); // 获取试卷ID
        ExamSurveyDirectory examSurveyDirectory = selectById(id); // 根据ID查询试卷信息
        if (ObjUtil.isNotEmpty(examSurveyDirectory)) { // 判断试卷信息是否存在
            if (examSurveyDirectory.getSurveyState().equals(CommonNumConstants.NUM_ZERO)) { // 判断试卷是否未发布
                String belongId = examSurveyDirectory.getId(); // 获取试卷ID
                List<Question> questions = questionService.QueryQuestionByBelongId(belongId); // 根据试卷ID查询题目
                if (!questions.isEmpty()) { // 判断是否有题目
                    // 总分数
                    int fraction = 0;
                    // 题目总数
                    int questionNum = 0;
                    for (Question question : questions) {
                        int questionType = question.getQuType();
                        if (questionType != 16 && questionType != 17) {
                            fraction += question.getFraction();
                            questionNum++;
                        }
                    }
                    UpdateWrapper<ExamSurveyDirectory> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.eq(CommonConstants.ID, id);
                    updateWrapper.set(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getFraction), fraction);
                    updateWrapper.set(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSurveyQuNum), questionNum);
                    update(updateWrapper);
                } else {
                    throw new CustomException("该试卷没有调查项，无法发布试卷。");
                }
            } else {
                throw new CustomException("该试卷已发布，请刷新数据。");
            }
        } else {
            throw new CustomException("该试卷信息不存在。");
        }
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
        Map<String, Object> map = inputObject.getParams(); // 获取请求参数Map
        // 是否可以参加考试，true：可以；false：不可以
        boolean yesOrNo = false;
        String userId = InputObject.getLogParamsStatic().get("id").toString(); // 获取当前登录用户ID
        String id = map.get("id").toString(); // 获取试卷ID
        ExamSurveyDirectory examSurveyDirectory = examSurveyDirectoryService.selectById(id); // 根据ID查询试卷信息
        if (ObjUtil.isNotEmpty(examSurveyDirectory)) { // 判断试卷是否存在
            if (examSurveyDirectory.getSurveyState().equals(CommonNumConstants.NUM_ONE)) { // 判断试卷是否发布
                if (!ToolUtil.isBlank(userId)) { // 判断用户是否登录
                    ExamSurveyAnswer examSurveyAnswer = examSurveyAnswerService.queryWhetherExamIngByStuId(userId, id); // 查询用户是否已经参加过该考试
                    if (ObjUtil.isNotEmpty(examSurveyAnswer)) { // 用户已经参加过考试
                        throw new CustomException("您已参加过该考试");
                    } else {
                        yesOrNo = true;
                    }
                } else {
                    throw new CustomException("您不具备该考试权限");
                }
            } else {
                throw new CustomException("该试卷未发布");
            }
        } else {
            throw new CustomException("该试卷不存在");
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
        ExamSurveyDirectory examSurveyDirectories = new ExamSurveyDirectory(); // 创建新的考试目录对象
        Map<String, Object> map = inputObject.getParams(); // 获取请求参数Map
        String examDirectoryId = map.get("id").toString(); // 获取试卷ID
        ExamSurveyDirectory examSurveyDirectory = selectById(examDirectoryId);// 根据ID查询试卷信息
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        examSurveyDirectories.setSid(ToolUtil.randomStr(6, 12)); // 设置调查ID
        examSurveyDirectories.setSurveyModel(1); // 设置调查模型
        examSurveyDirectories.setCreateId(userId); // 设置创建者ID
        examSurveyDirectories.setCreateTime(DateUtil.getTimeAndToString()); // 设置创建时间
        examSurveyDirectories.setSurveyName(examSurveyDirectory.getSurveyName() + "_副本"); // 设置调查名称
        examSurveyDirectories.setSurveyNote(examSurveyDirectory.getSurveyNote()); // 设置调查说明
        examSurveyDirectories.setSurveyQuNum(examSurveyDirectory.getSurveyQuNum()); // 设置题目数量
        examSurveyDirectories.setRealStartTime(examSurveyDirectory.getRealStartTime());
        examSurveyDirectories.setRealEndTime(examSurveyDirectory.getRealEndTime());
        examSurveyDirectories.setSurveyModel(examSurveyDirectory.getSurveyModel()); // 设置调查模型
        examSurveyDirectories.setEndType(examSurveyDirectory.getEndType()); // 设置结束方式
        examSurveyDirectories.setViewAnswer(examSurveyDirectory.getViewAnswer()); // 设置是否公开结果
        examSurveyDirectories.setSchoolId(examSurveyDirectory.getSchoolId()); // 设置所属学校
        examSurveyDirectories.setGradeId(examSurveyDirectory.getGradeId()); // 设置所属年级
        examSurveyDirectories.setSemesterId(examSurveyDirectory.getSemesterId()); // 设置所属学期
        examSurveyDirectories.setSubjectId(examSurveyDirectory.getSubjectId()); // 设置所属科目
        examSurveyDirectories.setFacultyId(examSurveyDirectory.getFacultyId()); // 设置所属院系
        examSurveyDirectories.setMajorId(examSurveyDirectory.getMajorId()); // 设置所属专业
        examSurveyDirectories.setFraction(examSurveyDirectory.getFraction());
        examSurveyDirectories.setSurveyState(examSurveyDirectory.getSurveyState()); // 设置调查状态
        examSurveyDirectories.setWhetherDelete(0); // 设置是否删除
        examSurveyDirectories.setClassId(examSurveyDirectory.getClassId()); // 设置班级ID
        createEntity(examSurveyDirectories, userId); // 创建新的试卷
        List<Question> questionList = questionService.queryQuestionMationCopyById(examDirectoryId); // 根据试卷ID查询题目
        if (ObjUtil.isEmpty(questionList)) {
            throw new CustomException("没有找到题目");
        }
        for (Question question : questionList) { // 遍历题目
            question.setCopyFromId(question.getId()); // 设置复制来源ID
            List<ExamQuestionLogic> examQuestionLogics = examQuestionLogicService.selectByQuestionId(question.getId());
            question.setQuestionLogic(examQuestionLogics);
            List<ExamQuRadio> examQuRadioList = examQuRadioService.selectQuRadio(question.getId());
            question.setRadioTd(examQuRadioList);
            List<ExamQuScore> examQuScoreList = examquScoreService.selectQuScore(question.getId());
            question.setScoreTd(examQuScoreList);
            List<ExamQuCheckbox> examQuCheckboxList = examQuCheckboxService.selectQuChenbox(question.getId());
            question.setCheckboxTd(examQuCheckboxList);
            List<ExamQuMultiFillblank> multiFillblanks = examQuMultiFillblankService.selectQuMultiFillblank(question.getId());
            question.setMultifillblankTd(multiFillblanks);
            List<ExamQuOrderby> examQuOrderbyList = examQuOrderbyService.selectQuOrderby(question.getId());
            question.setOrderbyTd(examQuOrderbyList);
            List<ExamQuChenColumn> examQuChenColumnList = examQuChenColumnService.selectQuChenColumn(question.getId());
            question.setColumnTd(examQuChenColumnList);
            List<ExamQuChenRow> examQuChenRows = examQuChenRowService.selectQuChenRow(question.getId());
            question.setRowTd(examQuChenRows);
            List<ExamQuScore> examQuScoreList1 = examQuScoreService.selectQuScore(question.getId());
            question.setScoreTd(examQuScoreList1);
            question.setBelongId(examSurveyDirectories.getId()); // 设置所属试卷ID
            questionService.createEntity(question, userId); // 创建新的题目
            questionService.copyQuestionListMation(question); // 复制题目选项信息
        }
    }

    /**
     * 创建/更新题目前的操作
     *
     * @param examSurveyDirectory 考试目录对象
     */
    @Override
    public void validatorEntity(ExamSurveyDirectory examSurveyDirectory) {
        LocalDateTime realStartTime = examSurveyDirectory.getRealStartTime(); // 获取实际开始时间
        LocalDateTime realEndTime = examSurveyDirectory.getRealEndTime(); // 获取实际结束时间
        if (ObjUtil.isNotEmpty(realStartTime) && ObjUtil.isNotEmpty(realEndTime)) { // 判断开始和结束时间是否都不为空
            if (realStartTime.isAfter(realEndTime)) { // 判断开始时间是否在结束时间之后
                throw new CustomException("实际开始时间不能晚于实际结束时间"); // 开始时间晚于结束时间抛出异常
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
    protected void createPostpose(ExamSurveyDirectory entity, String userId) {
        String id = entity.getId(); // 获取考试目录ID
        String reader = entity.getReaderList(); // 阅卷人
        String[] readerList = reader.split(","); // 将阅卷人转换为列表
        String classId = entity.getClassId(); // 获取班级ID
        String[] classIdList = classId.split(","); // 将班级ID转换为列表
        for (String classIdItem : classIdList) {
            examSurveyClassService.createExamSurveyClass(id, classIdItem, userId); // 创建考试班级
        }
        for (String readerItem : readerList) {
            examSurveyMarkExamService.createExamSurveyMarkExam(id, readerItem, userId); // 创建阅卷考试
        }
    }

    @Transactional
    @Override
    public void updatePostpose(ExamSurveyDirectory entity, String userId) {
        String id = entity.getId(); // 获取考试id
        QueryWrapper<ExamSurveyMarkExam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyMarkExam::getSurveyId), id);
        examSurveyMarkExamService.remove(queryWrapper); // 删除阅卷人与卷子关系
        String reader = entity.getReaderList(); // 阅卷人
        String[] readerList = reader.split(","); // 将阅卷人转换为列表
        for (String readerItem : readerList) {
            examSurveyMarkExamService.createExamSurveyMarkExam(id, readerItem, userId); // 创建阅卷考试
        }
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
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getCreateId),InputObject.getLogParamsStatic().get("Id").toString());
        extracted(commonPageInfo, queryWrapper);
        outputResult(outputObject, page, queryWrapper);
    }

    private void outputResult(OutputObject outputObject, Page page, QueryWrapper<ExamSurveyDirectory> queryWrapper) {
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getCreateTime));
        List<ExamSurveyDirectory> beans = list(queryWrapper).stream().map(item -> {
            item.setSubjectMation(subjectService.selectById(item.getSubjectId()));
            item.setClassesMation(classesService.selectById(item.getClassId()));
            return item;
        }).collect(Collectors.toList());
        iAuthUserService.setName(beans, "createId", "createName");
        iAuthUserService.setName(beans, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(beans);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public ExamSurveyDirectory selectById(String id) {
        ExamSurveyDirectory bean = super.selectById(id);
        List<Question> questionList = questionService.QueryQuestionByBelongId(bean.getId());
        List<String> questionIds = questionList.stream().map(Question::getId).collect(Collectors.toList());
        Map<String, List<Map<String, Object>>> examQuestionLogicMapList = examQuestionLogicService.selectByQuestionIds(questionIds);
        Map<String, List<Map<String, Object>>> examQuRadioMapList = examQuRadioService.selectByBelongId(id);
        Map<String, List<Map<String, Object>>> examQuScoreMapList = examQuScoreService.selectByBelongId(id);
        Map<String, List<Map<String, Object>>> examQuCheckboxMapList = examQuCheckboxService.selectByBelongId(id);
        Map<String, List<Map<String, Object>>> examQuChenColumnsMapList = examQuChenColumnService.selectByBelongId(id);
        Map<String, List<Map<String, Object>>> examQuchenRowMapList = examQuChenRowService.selectByBelongId(id);
        Map<String, List<Map<String, Object>>> examQuMultiFillblankMapList = examQuMultiFillblankService.selectByBelongId(id);
        Map<String, List<Map<String, Object>>> examQuOrderbyMapList = examQuOrderbyService.selectByBelongId(id);
        List<Map<String, List<Map<String, Object>>>> flagList = Arrays.asList(examQuestionLogicMapList, examQuRadioMapList, examQuScoreMapList,
            examQuCheckboxMapList, examQuChenColumnsMapList, examQuchenRowMapList, examQuMultiFillblankMapList, examQuOrderbyMapList);
        Map<String, List<Map<String, Object>>> collect = flagList.stream().flatMap(map -> map.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> newValue));
        questionList.forEach(item -> {
            String quId = item.getId();
            if (collect.containsKey(quId) && item.getQuType() == QuType.RADIO.getIndex()) {// 单选题
                item.setRadioTd(JSONUtil.toList(JSONUtil.parseArray(JSONUtil.toJsonStr(collect.get(quId))), ExamQuRadio.class));
            }
            if (collect.containsKey(quId) && item.getQuType() == QuType.SCORE.getIndex()) {// 评分题
                item.setScoreTd(JSONUtil.toList(JSONUtil.parseArray(JSONUtil.toJsonStr(collect.get(quId))), ExamQuScore.class));
            }
            if (collect.containsKey(quId) && item.getQuType() == QuType.CHECKBOX.getIndex()) {// 多选题
                item.setCheckboxTd(JSONUtil.toList(JSONUtil.parseArray(JSONUtil.toJsonStr(collect.get(quId))), ExamQuCheckbox.class));
            }
            List<Integer> quChenIndexList = Arrays.asList(QuType.CHENRADIO.getIndex(), QuType.CHENFBK.getIndex(), QuType.CHENCHECKBOX.getIndex(), QuType.COMPCHENRADIO.getIndex());
            if (collect.containsKey(quId) && quChenIndexList.contains(item.getQuType())) {// 矩阵题
                try {
                    item.setColumnTd(JSONUtil.toList(JSONUtil.parseArray(JSONUtil.toJsonStr(collect.get(quId))), ExamQuChenColumn.class));// 尝试转换为列选择项
                } catch (RuntimeException e) {
                    item.setRowTd(JSONUtil.toList(JSONUtil.parseArray(JSONUtil.toJsonStr(collect.get(quId))), ExamQuChenRow.class));// 转换为列选择项失败时，则说明其为行选项
                }
            }
            if (collect.containsKey(quId) && item.getQuType() == QuType.ANSWER.getIndex()) {//多行填空题
                item.setMultifillblankTd(JSONUtil.toList(JSONUtil.parseArray(JSONUtil.toJsonStr(collect.get(quId))), ExamQuMultiFillblank.class));
            }
            if (collect.containsKey(quId) && item.getQuType() == QuType.ORDERQU.getIndex()) {// 排序题
                item.setOrderbyTd(JSONUtil.toList(JSONUtil.parseArray(JSONUtil.toJsonStr(collect.get(quId))), ExamQuOrderby.class));
            }
            if (collect.containsKey(quId)) {// 问题逻辑设置信息
                item.setQuestionLogic(JSONUtil.toList(JSONUtil.parseArray(JSONUtil.toJsonStr(collect.get(quId))), ExamQuestionLogic.class));
            }
        });
        bean.setClassesMation(classesService.selectById(bean.getClassId()));
        bean.setSubjectMation(subjectService.selectById(bean.getSubjectId()));
        bean.setQuestionList(questionList);
        return bean;
    }
}
