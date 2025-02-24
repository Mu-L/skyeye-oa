package com.skyeye.eve.question.service.impl;

import cn.hutool.core.collection.CollectionUtil;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
                String belongId = dwSurveyDirectory.getId(); // 获取问卷ID
                List<DwQuestion> questions = dwQuestionService.QueryQuestionByBelongId(belongId); // 根据问卷ID查询题目
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
        } else {
            throw new CustomException("该问卷不存在");
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
        }
    }

    /**
     * 创建/更新题目前的操作
     *
     * @param dwSurveyDirectory 问卷目录对象
     */
    @Override
    public void validatorEntity(DwSurveyDirectory dwSurveyDirectory) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String realStartTime = dwSurveyDirectory.getRealStartTime(); // 获取实际开始时间
        String realEndTime = dwSurveyDirectory.getRealEndTime(); // 获取实际结束时间
        if (ObjUtil.isNotEmpty(realStartTime) && ObjUtil.isNotEmpty(realEndTime)) { // 判断开始和结束时间是否都不为空
            LocalDateTime start = LocalDateTime.parse(realStartTime, formatter); // 将字符串转换为 LocalDateTime
            LocalDateTime end = LocalDateTime.parse(realEndTime, formatter); // 将字符串转换为 LocalDateTime
            if (start.isAfter(end)) { // 判断开始时间是否在结束时间之后
                throw new CustomException("实际开始时间不能晚于实际结束时间"); // 开始时间晚于结束时间抛出异常
            }
        }
    }

//    /**
//     * 创建问卷目录后的后置操作
//     *
//     * @param entity 问卷目录对象
//     * @param userId 创建者ID
//     */
//    @Transactional
//    @Override
//    public void createPostpose(DwSurveyDirectory entity, String userId) {
//        String id = entity.getId(); // 获取问卷目录ID
//        String reader = entity.getReaderList(); // 阅卷人
//        String[] readerList = reader.split(","); // 将阅卷人转换为列表
//        String classId = entity.getClassId(); // 获取班级ID
//        String[] classIdList = classId.split(","); // 将班级ID转换为列表
//        for (String classIdItem : classIdList) {
//            examSurveyClassService.createExamSurveyClass(id, classIdItem, userId); // 创建问卷班级
//        }
//        for (String readerItem : readerList) {
//            examSurveyMarkExamService.createExamSurveyMarkExam(id, readerItem, userId); // 创建阅卷人关系
//        }
//    }

//    @Transactional
//    @Override
//    public void updatePostpose(ExamSurveyDirectory entity, String userId) {
//        String id = entity.getId(); // 获取问卷id
//        QueryWrapper<ExamSurveyMarkExam> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyMarkExam::getSurveyId), id);
//        examSurveyMarkExamService.remove(queryWrapper); // 删除阅卷人与卷子关系
//        String reader = entity.getReaderList(); // 阅卷人
//        String[] readerList = reader.split(","); // 将阅卷人转换为列表
//        for (String readerItem : readerList) {
//            examSurveyMarkExamService.createExamSurveyMarkExam(id, readerItem, userId); // 创建阅卷人关系
//        }
//    }

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
    public void queryMyDwList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String userId = inputObject.getLogParams().get("id").toString();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<DwSurveyDirectory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyDirectory::getCreateId), userId);
        outputResult(outputObject, page, queryWrapper);
    }

    @Override
    public void queryFilterDwLists(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<DwSurveyDirectory> queryWrapper = new QueryWrapper<>();
        outputResult(outputObject, page, queryWrapper);
    }

    private void outputResult(OutputObject outputObject, Page page, QueryWrapper<DwSurveyDirectory> queryWrapper) {
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyDirectory::getWhetherDelete), CommonNumConstants.NUM_ONE);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(DwSurveyDirectory::getCreateTime));
        List<DwSurveyDirectory> beans = list(queryWrapper);
        iAuthUserService.setName(beans, "createId", "createName");
        iAuthUserService.setName(beans, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(beans);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void queryAllDwList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<DwSurveyDirectory> queryWrapper = new QueryWrapper<>();
        outputResult(outputObject, page, queryWrapper);
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
        List<String> questionIds = questionList.stream().map(DwQuestion::getId).collect(Collectors.toList());
        Map<String, List<Map<String, Object>>> examQuestionLogicMapList = dwQuestionLogicService.selectByQuestionIds(questionIds);
        Map<String, List<Map<String, Object>>> examQuRadioMapList = dwQuRadioService.selectByBelongId(id);
        Map<String, List<Map<String, Object>>> examQuScoreMapList = dwQuScoreService.selectByBelongId(id);
        Map<String, List<Map<String, Object>>> examQuCheckboxMapList = dwQuCheckboxService.selectByBelongId(id);
        Map<String, List<Map<String, Object>>> examQuChenColumnsMapList = dwQuChenColumnService.selectByBelongId(id);
        Map<String, List<Map<String, Object>>> examQuchenRowMapList = dwQuChenRowService.selectByBelongId(id);
        Map<String, List<Map<String, Object>>> examQuMultiFillblankMapList = dwQuMultiFillblankService.selectByBelongId(id);
        Map<String, List<Map<String, Object>>> examQuOrderbyMapList = dwQuOrderbyService.selectByBelongId(id);
        List<Map<String, List<Map<String, Object>>>> flagList = Arrays.asList(examQuestionLogicMapList, examQuRadioMapList, examQuScoreMapList,
                examQuCheckboxMapList, examQuChenColumnsMapList, examQuchenRowMapList, examQuMultiFillblankMapList, examQuOrderbyMapList);
        Map<String, List<Map<String, Object>>> collect = flagList.stream().flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> newValue));
        questionList.forEach(item -> {
            String quId = item.getId();
            if (collect.containsKey(quId) && item.getQuType() == QuType.RADIO.getIndex()) {// 单选题
                item.setRadioTd(JSONUtil.toList(JSONUtil.parseArray(JSONUtil.toJsonStr(collect.get(quId))), DwQuRadio.class));
            }
            if (collect.containsKey(quId) && item.getQuType() == QuType.SCORE.getIndex()) {// 评分题
                item.setScoreTd(JSONUtil.toList(JSONUtil.parseArray(JSONUtil.toJsonStr(collect.get(quId))), DwQuScore.class));
            }
            if (collect.containsKey(quId) && item.getQuType() == QuType.CHECKBOX.getIndex()) {// 多选题
                item.setCheckboxTd(JSONUtil.toList(JSONUtil.parseArray(JSONUtil.toJsonStr(collect.get(quId))), DwQuCheckbox.class));
            }
            List<Integer> quChenIndexList = Arrays.asList(QuType.CHENRADIO.getIndex(), QuType.CHENFBK.getIndex(), QuType.CHENCHECKBOX.getIndex(), QuType.COMPCHENRADIO.getIndex());
            if (collect.containsKey(quId) && quChenIndexList.contains(item.getQuType())) {// 矩阵题
                try {
                    item.setColumnTd(JSONUtil.toList(JSONUtil.parseArray(JSONUtil.toJsonStr(collect.get(quId))), DwQuChenColumn.class));// 尝试转换为列选择项
                } catch (RuntimeException e) {
                    item.setRowTd(JSONUtil.toList(JSONUtil.parseArray(JSONUtil.toJsonStr(collect.get(quId))), DwQuChenRow.class));// 转换为列选择项失败时，则说明其为行选项
                }
            }
            if (collect.containsKey(quId) && item.getQuType() == QuType.ANSWER.getIndex()) {//多行填空题
                item.setMultifillblankTd(JSONUtil.toList(JSONUtil.parseArray(JSONUtil.toJsonStr(collect.get(quId))), DwQuMultiFillblank.class));
            }
            if (collect.containsKey(quId) && item.getQuType() == QuType.ORDERQU.getIndex()) {// 排序题
                item.setOrderbyTd(JSONUtil.toList(JSONUtil.parseArray(JSONUtil.toJsonStr(collect.get(quId))), DwQuOrderby.class));
            }
            if (collect.containsKey(quId)) {// 问题逻辑设置信息
                item.setQuestionLogic(JSONUtil.toList(JSONUtil.parseArray(JSONUtil.toJsonStr(collect.get(quId))), DwQuestionLogic.class));
            }
        });
        outputObject.setBean(bean);
        outputObject.setBeans(questionList);
    }

}