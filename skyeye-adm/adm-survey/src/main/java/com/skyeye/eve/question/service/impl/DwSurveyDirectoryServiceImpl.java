package com.skyeye.eve.question.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
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
import com.skyeye.eve.checkbox.entity.DwQuCheckbox;
import com.skyeye.eve.checkbox.service.DwAnCheckboxService;
import com.skyeye.eve.checkbox.service.DwQuCheckboxService;
import com.skyeye.eve.chen.entity.DwQuChenColumn;
import com.skyeye.eve.chen.entity.DwQuChenRow;
import com.skyeye.eve.chen.service.DwAnChenCheckboxService;
import com.skyeye.eve.chen.service.DwAnChenRadioService;
import com.skyeye.eve.chen.service.DwQuChenColumnService;
import com.skyeye.eve.chen.service.DwQuChenRowService;
import com.skyeye.eve.multifllblank.entity.DwQuMultiFillblank;
import com.skyeye.eve.multifllblank.service.DwQuMultiFillblankService;
import com.skyeye.eve.order.service.DwAnOrderService;
import com.skyeye.eve.orderby.entity.DwQuOrderby;
import com.skyeye.eve.orderby.service.DwQuOrderbyService;
import com.skyeye.eve.question.dao.DwSurveyDirectoryDao;
import com.skyeye.eve.question.entity.DwQuestion;
import com.skyeye.eve.question.entity.DwQuestionLogic;
import com.skyeye.eve.question.entity.DwSurveyAnswer;
import com.skyeye.eve.question.entity.DwSurveyDirectory;
import com.skyeye.eve.question.service.DwQuestionLogicService;
import com.skyeye.eve.question.service.DwQuestionService;
import com.skyeye.eve.question.service.DwSurveyAnswerService;
import com.skyeye.eve.question.service.DwSurveyDirectoryService;
import com.skyeye.eve.radio.entity.DwQuRadio;
import com.skyeye.eve.radio.service.DwAnRadioService;
import com.skyeye.eve.radio.service.DwQuRadioService;
import com.skyeye.eve.score.entity.DwQuScore;
import com.skyeye.eve.score.service.DwAnScoreService;
import com.skyeye.eve.score.service.DwQuScoreService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "问卷管理", groupName = "问卷管理")
public class DwSurveyDirectoryServiceImpl extends SkyeyeBusinessServiceImpl<DwSurveyDirectoryDao, DwSurveyDirectory> implements DwSurveyDirectoryService {

    @Autowired
    private DwSurveyDirectoryService dwSurveyDirectoryService;
    @Autowired
    private DwQuestionService dwQuestionService;
    @Autowired
    private DwQuRadioService dwQuRadioService;
    @Autowired
    private DwQuScoreService dwQuScoreService;
    @Autowired
    private DwQuCheckboxService dwQuCheckboxService;
    @Autowired
    private DwQuMultiFillblankService dwQuMultiFillblankService;
    @Autowired
    private DwQuOrderbyService dwQuOrderbyService;
    @Autowired
    private DwQuChenColumnService dwQuChenColumnService;
    @Autowired
    private DwQuChenRowService dwQuChenRowService;
    @Autowired
    private DwAnRadioService dwAnRadioService;
    @Autowired
    private DwAnCheckboxService dwAnCheckboxService;
    @Autowired
    private DwAnScoreService dwAnScoreService;
    @Autowired
    private DwAnOrderService dwAnOrderService;
    @Autowired
    private DwAnChenRadioService dwAnChenRadioService;
    @Autowired
    private DwAnChenCheckboxService dwAnChenCheckboxService;
    @Autowired
    private DwSurveyAnswerService dwSurveyAnswerService;
    @Autowired
    private DwQuestionLogicService dwQuestionLogicService;

    /**
     * 设置问卷目录的方法
     *
     * @param inputObject  输入对象，包含请求参数
     * @param outputObject 输出对象，用于返回响应数据
     */
    @Override
    public void setUpDwDirectory(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams(); // 获取请求参数Map
        String id = map.get("id").toString(); // 获取问卷ID
        DwSurveyDirectory dwSurveyDirectory = selectById(id); // 根据ID查询问卷信息
        if (ObjUtil.isNotEmpty(dwSurveyDirectory)) { // 判断问卷信息是否存在
            if (dwSurveyDirectory.getSurveyState().equals(CommonNumConstants.NUM_ZERO)) { // 判断问卷是否未发布
                List<DwQuestion> questions = dwQuestionService.QueryQuestionByBelongId(id); // 根据问卷ID查询题目
                if (CollectionUtil.isNotEmpty(questions)) { // 判断是否有题目
                    // 总分数
                    int fraction = 0;
                    // 题目总数
                    int questionNum = 0;
                    for (DwQuestion question : questions) {
                        int questionType = question.getQuType();
                        if (questionType != 16 && questionType != 17) {
                            fraction += question.getFraction();
                            questionNum++;
                        }
                    }
                    UpdateWrapper<DwSurveyDirectory> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.eq(CommonConstants.ID, id);
                    updateWrapper.set(MybatisPlusUtil.toColumns(DwSurveyDirectory::getFraction), fraction);
                    updateWrapper.set(MybatisPlusUtil.toColumns(DwSurveyDirectory::getSurveyQuNum), questionNum);
                    updateWrapper.set(MybatisPlusUtil.toColumns(DwSurveyDirectory::getSurveyState), CommonNumConstants.NUM_ONE);
                    update(updateWrapper);
                } else {
                    throw new CustomException("该问卷没有调查项，无法发布问卷。");
                }
            } else {
                throw new CustomException("该问卷已发布，请刷新数据。");
            }
        } else {
            throw new CustomException("该问卷信息不存在。");
        }
    }

    /**
     * 参加问卷的方法
     *
     * @param inputObject  输入对象，包含请求参数
     * @param outputObject 输出对象，用于返回响应数据
     * @return 允许参加问卷时返回问卷目录信息
     */
    @Override
    public DwSurveyDirectory takeExam(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams(); // 获取请求参数Map
        // 是否可以参加问卷，true：可以；false：不可以
        boolean yesOrNo = false;
        String userId = InputObject.getLogParamsStatic().get("id").toString(); // 获取当前登录用户ID
        String id = map.get("id").toString(); // 获取问卷ID
        DwSurveyDirectory dwSurveyDirectory = dwSurveyDirectoryService.selectById(id); // 根据ID查询问卷信息
        if (dwSurveyDirectory == null || dwSurveyDirectory.getId() == null) {
            throw new CustomException("该问卷不存在");
        }
        if (ObjUtil.isNotEmpty(dwSurveyDirectory)) { // 判断问卷是否存在
            if (dwSurveyDirectory.getSurveyState().equals(CommonNumConstants.NUM_ONE)) { // 判断问卷是否发布
                if (!ToolUtil.isBlank(userId)) { // 判断用户是否登录
                    DwSurveyAnswer examSurveyAnswer = dwSurveyAnswerService.queryWhetherExamIngByStuId(userId, id); // 查询用户是否已经参加过该问卷
                    if (ObjUtil.isNotEmpty(examSurveyAnswer)) { // 用户已经参加过问卷
                        throw new CustomException("您已参加过该问卷");
                    } else {
                        yesOrNo = true;
                    }
                } else {
                    throw new CustomException("您不具备该问卷权限");
                }
            } else {
                throw new CustomException("该问卷未发布");
            }
        }
        if (yesOrNo) {
            return dwSurveyDirectory;
        } else {
            throw new CustomException("您不具备该问卷权限");
        }
    }

    /**
     * 复制问卷目录的方法
     *
     * @param inputObject  输入对象，包含请求参数
     * @param outputObject 输出对象，用于返回响应数据
     */
    @Override
    public void copyDwDirectory(InputObject inputObject, OutputObject outputObject) {
        DwSurveyDirectory examSurveyDirectories = new DwSurveyDirectory(); // 创建新的问卷目录对象
        Map<String, Object> map = inputObject.getParams(); // 获取请求参数Map
        String dwDirectoryId = map.get("id").toString(); // 获取问卷ID
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String surveyName = map.get("surveyName").toString(); // 获取问卷名称
        DwSurveyDirectory examSurveyDirectory = selectById(dwDirectoryId);// 根据ID查询问卷信息
        examSurveyDirectories.setSid(ToolUtil.randomStr(6, 12)); // 设置调查ID
        examSurveyDirectories.setSurveyModel(1); // 设置调查模型
        examSurveyDirectories.setCreateId(userId); // 设置创建者ID
        examSurveyDirectories.setCreateTime(DateUtil.getTimeAndToString()); // 设置创建时间
        if (StrUtil.isNotEmpty(surveyName)) {
            examSurveyDirectories.setSurveyName(surveyName); // 设置问卷名称
        } else {
            examSurveyDirectories.setSurveyName(examSurveyDirectory.getSurveyName() + "_副本"); // 设置调查名称
        }
        examSurveyDirectories.setSurveyNote(examSurveyDirectory.getSurveyNote()); // 设置调查说明
        examSurveyDirectories.setSurveyQuNum(examSurveyDirectory.getSurveyQuNum()); // 设置题目数量
        examSurveyDirectories.setRealStartTime(examSurveyDirectory.getRealStartTime());
        examSurveyDirectories.setRealEndTime(examSurveyDirectory.getRealEndTime());
        examSurveyDirectories.setSurveyModel(examSurveyDirectory.getSurveyModel()); // 设置调查模型
        examSurveyDirectories.setEndType(examSurveyDirectory.getEndType()); // 设置结束方式
        examSurveyDirectories.setViewAnswer(examSurveyDirectory.getViewAnswer()); // 设置是否公开结果
        examSurveyDirectories.setFraction(examSurveyDirectory.getFraction());
        examSurveyDirectories.setSurveyState(examSurveyDirectory.getSurveyState()); // 设置调查状态
        examSurveyDirectories.setWhetherDelete(examSurveyDirectory.getWhetherDelete()); // 设置是否删除
        createEntity(examSurveyDirectories, userId); // 创建新的问卷
        List<DwQuestion> questionList = dwQuestionService.QueryQuestionByBelongId(dwDirectoryId); // 根据问卷ID查询题目
        if (CollectionUtil.isEmpty(questionList)) {
            throw new CustomException("没有找到题目");
        }
        for (DwQuestion question : questionList) { // 遍历题目
            question.setCopyFromId(question.getId()); // 设置复制来源ID
            List<DwQuestionLogic> examQuestionLogics = dwQuestionLogicService.selectByQuestionId(question.getId());
            question.setQuestionLogic(examQuestionLogics);
            List<DwQuRadio> examQuRadioList = dwQuRadioService.selectQuRadio(question.getId());
            question.setRadioTd(examQuRadioList);
            List<DwQuScore> examQuScoreList = dwQuScoreService.selectQuScore(question.getId());
            question.setScoreTd(examQuScoreList);
            List<DwQuCheckbox> examQuCheckboxList = dwQuCheckboxService.selectQuChenbox(question.getId());
            question.setCheckboxTd(examQuCheckboxList);
            List<DwQuMultiFillblank> multiFillblanks = dwQuMultiFillblankService.selectQuMultiFillblank(question.getId());
            question.setMultifillblankTd(multiFillblanks);
            List<DwQuOrderby> examQuOrderbyList = dwQuOrderbyService.selectQuOrderby(question.getId());
            question.setOrderbyTd(examQuOrderbyList);
            List<DwQuChenColumn> examQuChenColumnList = dwQuChenColumnService.selectQuChenColumn(question.getId());
            question.setColumnTd(examQuChenColumnList);
            List<DwQuChenRow> examQuChenRows = dwQuChenRowService.selectQuChenRow(question.getId());
            question.setRowTd(examQuChenRows);
            question.setBelongId(examSurveyDirectories.getId()); // 设置所属问卷ID
            dwQuestionService.createEntity(question, userId); // 创建新的题目
            dwQuestionService.copyQuestionListMation(question); // 复制题目选项信息
            outputObject.setBean(examSurveyDirectories);
            outputObject.settotal(1);
        }
    }


    @Override
    protected void createPrepose(DwSurveyDirectory entity) {
        String endTime = entity.getEndTime();
        String realStartTime = entity.getRealStartTime(); // 获取实际开始时间
        String realEndTime = entity.getRealEndTime(); // 获取实际结束时间
        realStartTime = (realStartTime == null || realStartTime.trim().isEmpty()) ? null : realStartTime;
        realEndTime = (realEndTime == null || realEndTime.trim().isEmpty()) ? null : realEndTime;
        endTime = (endTime == null || endTime.trim().isEmpty()) ? null : endTime;
        entity.setEndTime(endTime);
        entity.setRealStartTime(realStartTime);
        entity.setRealEndTime(realEndTime);
    }

    /**
     * 创建/更新题目前的操作
     *
     * @param dwSurveyDirectory 问卷目录对象
     */
    @Override
    public void validatorEntity(DwSurveyDirectory dwSurveyDirectory) {
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String realStartTime = dwSurveyDirectory.getRealStartTime(); // 获取实际开始时间
        String realEndTime = dwSurveyDirectory.getRealEndTime(); // 获取实际结束时间
        String endTime = dwSurveyDirectory.getEndTime();
        realStartTime = (realStartTime == null || realStartTime.trim().isEmpty()) ? null : realStartTime;
        realEndTime = (realEndTime == null || realEndTime.trim().isEmpty()) ? null : realEndTime;
        if (StrUtil.isNotEmpty(realStartTime) && StrUtil.isNotEmpty(realEndTime)) {
            LocalDateTime start = parseDateTime(realStartTime, formatter1, formatter2, formatter3);
            LocalDateTime end = parseDateTime(realEndTime, formatter1, formatter2, formatter3);
            if (start.isAfter(end)) {
                throw new CustomException("实际开始时间不能晚于实际结束时间");
            }
        }
    }

    @Override
    protected void createPostpose(DwSurveyDirectory entity, String userId) {
        List<DwQuestion> dwQuestionMation = entity.getDwQuestionMation();
        if (CollectionUtil.isNotEmpty(dwQuestionMation)) {
            for (DwQuestion dwQuestion : dwQuestionMation) {
                dwQuestion.setBelongId(entity.getId()); // 设置所属试卷ID
                dwQuestionService.createEntity(dwQuestion, userId); // 创建新的题目
            }
        }
    }

    @Override
    protected void updatePrepose(DwSurveyDirectory entity) {
        String realEndTime = entity.getRealEndTime();
        String realStartTime = entity.getRealStartTime();
        String endTime = entity.getEndTime();
        if (StrUtil.isEmpty(endTime)) {
            endTime = null;
        }
        if (StrUtil.isEmpty(realStartTime)) {
            realStartTime = null;
        }
        if (StrUtil.isEmpty(realEndTime)) {
            realEndTime = null;
        }
        entity.setRealStartTime(realStartTime);
        entity.setRealEndTime(realEndTime);
        entity.setEndTime(endTime);
    }

    @Override
    protected void updatePostpose(DwSurveyDirectory entity, String userId) {
        List<DwQuestion> dwQuestionMation = entity.getDwQuestionMation();
        List<DwQuestion> dwQuestions = dwQuestionService.QueryQuestionByBelongId(entity.getId());
        List<String> collect = dwQuestions.stream().map(DwQuestion::getId).collect(Collectors.toList());
        Map<Boolean, List<DwQuestion>> partitionedQuestions = dwQuestionMation.stream()
                .collect(Collectors.partitioningBy(question -> StrUtil.isNotEmpty(question.getId())));
        List<DwQuestion> questionsWithId = partitionedQuestions.get(true);
        List<DwQuestion> questionsWithoutId = partitionedQuestions.get(false);
        List<String> submittedIds = questionsWithId.stream()
                .map(DwQuestion::getId)
                .collect(Collectors.toList());
        Set<String> submittedIdSet = new HashSet<>(submittedIds);
        List<String> idsToDelete = collect.stream()
                .filter(id -> !submittedIdSet.contains(id))
                .collect(Collectors.toList());
        for (String idToDelete : idsToDelete) {
            dwQuestionService.deleteById(idToDelete);
        }
        if (CollectionUtil.isNotEmpty(questionsWithId)) {
            for (DwQuestion question : questionsWithId) {
                String questionId = question.getId();
                String belongId = question.getBelongId();
                if (StrUtil.isNotEmpty(questionId) && StrUtil.isEmpty(belongId)) {
                    question.setBelongId(entity.getId());
                } else {
                    dwQuestionService.updateEntity(question, userId); // 更新题目
                }
            }
        }
        for (DwQuestion question : questionsWithoutId) {
            dwQuestionService.createEntity(question, userId);
        }
    }

    private LocalDateTime parseDateTime(String dateTimeStr, DateTimeFormatter... formatters) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                if (dateTimeStr.length() == 10) {
                    LocalDate date = LocalDate.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    return date.atStartOfDay(); // 将日期转换为当天的起始时间
                }
                return LocalDateTime.parse(dateTimeStr, formatter);
            } catch (DateTimeParseException e) {
            }
        }
        throw new DateTimeParseException("无法解析时间字符串: " + dateTimeStr, dateTimeStr, 0);
    }

    /**
     * 切换是否删除问卷目录的方法
     *
     * @param inputObject  输入对象，包含请求参数
     * @param outputObject 输出对象，用于返回响应数据
     */
    @Override
    public void changeWhetherDeleteById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        UpdateWrapper<DwSurveyDirectory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(DwSurveyDirectory::getWhetherDelete), CommonNumConstants.NUM_TWO);
        updateWrapper.set(MybatisPlusUtil.toColumns(DwSurveyDirectory::getSurveyState), CommonNumConstants.NUM_TWO);
        update(updateWrapper);
    }

    /**
     * 更新问卷状态结束信息的方法
     *
     * @param inputObject  输入对象，包含请求参数
     * @param outputObject 输出对象，用于返回响应数据
     */
    @Override
    public void updateDwMationEndById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String examSurveyDirectoryId = map.get("id").toString();
        QueryWrapper<DwSurveyDirectory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, examSurveyDirectoryId);
        DwSurveyDirectory dwSurveyDirectory = getOne(queryWrapper);
        // 判断问卷目录对象是否存在
        if (ObjUtil.isNotEmpty(dwSurveyDirectory)) {
            // 判断问卷目录状态是否为进行中（NUM_ONE）
            if (dwSurveyDirectory.getSurveyState().equals(CommonNumConstants.NUM_ONE)) {
                // 获取当前时间作为实际结束时间
                String realEndTime = DateUtil.getTimeAndToString();
                UpdateWrapper<DwSurveyDirectory> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq(CommonConstants.ID, examSurveyDirectoryId);
                // 设置实际结束时间为当前时间
                updateWrapper.set(MybatisPlusUtil.toColumns(DwSurveyDirectory::getRealEndTime), realEndTime);
                // 设置问卷目录状态为已结束（NUM_TWO）
                updateWrapper.set(MybatisPlusUtil.toColumns(DwSurveyDirectory::getSurveyState), CommonNumConstants.NUM_TWO);
                // 设置结束类型为自动结束（NUM_ONE）
                updateWrapper.set(MybatisPlusUtil.toColumns(DwSurveyDirectory::getEndType), CommonNumConstants.NUM_ONE);
                // 执行更新操作
                update(updateWrapper);
            }
        } else {
            throw new CustomException("该问卷信息不存在!");
        }
    }

    @Override
    public void queryFilterDwLists(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<DwSurveyDirectory> queryWrapper = new QueryWrapper<>();
        outputDwList(outputObject, commonPageInfo, queryWrapper, page);
    }

    private void outputNoDelete(OutputObject outputObject, QueryWrapper<DwSurveyDirectory> queryWrapper, Page page) {
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyDirectory::getWhetherDelete), CommonNumConstants.NUM_ONE);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(DwSurveyDirectory::getCreateTime));
        List<DwSurveyDirectory> beans = list(queryWrapper);
        iAuthUserService.setName(beans, "createId", "createName");
        iAuthUserService.setName(beans, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(beans);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void queryMyDwurvey(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<DwSurveyDirectory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyDirectory::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        outputDwList(outputObject, commonPageInfo, queryWrapper, page);
    }

    private void outputDwList(OutputObject outputObject, CommonPageInfo commonPageInfo, QueryWrapper<DwSurveyDirectory> queryWrapper, Page page) {
        // 试卷名称
        if (StrUtil.isNotEmpty(commonPageInfo.getKeyword())) {
            queryWrapper.like(MybatisPlusUtil.toColumns(DwSurveyDirectory::getSurveyName), commonPageInfo.getKeyword());
        }
        // 状态
        if (StrUtil.isNotEmpty(commonPageInfo.getState())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyDirectory::getSurveyState), commonPageInfo.getState());
        }
        outputNoDelete(outputObject, queryWrapper, page);
    }

    @Override
    public void validatorEntity(List<DwSurveyDirectory> entity) {
        DwSurveyDirectory examSurveyDirectory = entity.get(CommonNumConstants.NUM_ZERO);
        String realStartTime = examSurveyDirectory.getRealStartTime(); // 获取实际开始时间
        String realEndTime = examSurveyDirectory.getRealEndTime(); // 获取实际结束时间
        // 假设时间格式为 yyyy-MM-dd HH:mm:ss，根据实际情况调整格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (ObjUtil.isNotEmpty(realStartTime) && ObjUtil.isNotEmpty(realEndTime)) { // 判断开始和结束时间是否都不为空
            try {
                LocalDateTime startTime = LocalDateTime.parse(realStartTime, formatter); // 将字符串转换为 LocalDateTime
                LocalDateTime endTime = LocalDateTime.parse(realEndTime, formatter); // 将字符串转换为 LocalDateTime
                if (startTime.isAfter(endTime)) { // 判断开始时间是否在结束时间之后
                    throw new CustomException("实际开始时间不能晚于实际结束时间"); // 开始时间晚于结束时间抛出异常
                }
            } catch (Exception e) {
                throw new CustomException("时间格式错误，请检查时间格式是否正确：" + e.getMessage());
            }
        }
    }

    @Override
    public void selectById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        DwSurveyDirectory bean = super.selectById(id);
        List<DwQuestion> questionList = dwQuestionService.QueryQuestionByBelongId(bean.getId());
        if (CollectionUtil.isEmpty(questionList)) {
            outputObject.setBean(bean);
        }
        outputObject.setBean(bean);
        outputObject.setBeans(questionList);
    }

}