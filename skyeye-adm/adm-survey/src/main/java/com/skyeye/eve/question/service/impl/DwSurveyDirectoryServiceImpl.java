package com.skyeye.eve.question.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
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
import com.skyeye.eve.answer.entity.DwAnAnswer;
import com.skyeye.eve.answer.service.DwAnAnswerService;
import com.skyeye.eve.checkbox.entity.DwQuCheckbox;
import com.skyeye.eve.checkbox.service.DwAnCheckboxService;
import com.skyeye.eve.checkbox.service.DwQuCheckboxService;
import com.skyeye.eve.chen.entity.*;
import com.skyeye.eve.chen.service.*;
import com.skyeye.eve.enumqu.entity.DwAnEnumqu;
import com.skyeye.eve.enumqu.service.DwAnEnumquService;
import com.skyeye.eve.multifllblank.entity.DwAnDfillblank;
import com.skyeye.eve.multifllblank.entity.DwAnFillblank;
import com.skyeye.eve.multifllblank.entity.DwQuMultiFillblank;
import com.skyeye.eve.multifllblank.service.DwAnDfillblankService;
import com.skyeye.eve.multifllblank.service.DwAnFillblankService;
import com.skyeye.eve.multifllblank.service.DwQuMultiFillblankService;
import com.skyeye.eve.order.entity.DwAnOrder;
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
import com.skyeye.eve.radio.entity.DwAnRadio;
import com.skyeye.eve.radio.entity.DwQuRadio;
import com.skyeye.eve.radio.service.DwAnRadioService;
import com.skyeye.eve.radio.service.DwQuRadioService;
import com.skyeye.eve.score.entity.DwAnScore;
import com.skyeye.eve.score.entity.DwQuScore;
import com.skyeye.eve.score.service.DwAnScoreService;
import com.skyeye.eve.score.service.DwQuScoreService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "问卷管理", groupName = "问卷管理")
public class DwSurveyDirectoryServiceImpl extends SkyeyeBusinessServiceImpl<DwSurveyDirectoryDao, DwSurveyDirectory> implements DwSurveyDirectoryService {

    @Autowired
    private DwQuestionService dwQuestionService;
    @Autowired
    private DwQuestionLogicService dwQuestionLogicService;
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
    private DwAnDfillblankService dwAnDfillblankService;
    @Autowired
    private DwAnChenFbkService dwAnChenFbkService;
    @Autowired
    private DwAnChenScoreService dwAnChenScoreService;
    @Autowired
    private DwSurveyAnswerService dwSurveyAnswerService;
    @Autowired
    private DwAnFillblankService dwAnFillblankService;
    @Autowired
    private DwAnAnswerService dwAnAnswerService;
    @Autowired
    private DwAnEnumquService dwAnEnumquService;

    /**
     * 设置问卷目录的方法
     *
     * @param inputObject  输入对象，包含请求参数
     * @param outputObject 输出对象，用于返回响应数据
     */
    @Override
    public void setUpDwDirectory(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        DwSurveyDirectory dwSurveyDirectory = selectById(id);
        if (StrUtil.isEmpty(dwSurveyDirectory.getId())) {
            throw new CustomException("该问卷不存在");
        }
        if (ObjUtil.isNotEmpty(dwSurveyDirectory)) {
            // 判断试卷是否未发布
            if (dwSurveyDirectory.getSurveyState().equals(CommonNumConstants.NUM_ZERO)) {
                String dwSurveyDirectoryId = dwSurveyDirectory.getId();
                getFractionNumber(dwSurveyDirectoryId);
                UpdateWrapper<DwSurveyDirectory> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq(CommonConstants.ID, id);
                updateWrapper.set(MybatisPlusUtil.toColumns(DwSurveyDirectory::getSurveyState), CommonNumConstants.NUM_ONE);
                update(updateWrapper);
            } else {
                throw new CustomException("该问卷已发布，请刷新数据。");
            }
        } else {
            throw new CustomException("该问卷信息不存在。");
        }
    }

    private Integer getFractionNumber(String dwSurveyDirectoryId) {
        if (StrUtil.isEmpty(dwSurveyDirectoryId)) {
            return null;
        }
        // 查询题目
        List<DwQuestion> questions = dwQuestionService.QueryQuestionByBelongId(dwSurveyDirectoryId);
        if (CollectionUtil.isEmpty(questions)) {
            questions = new ArrayList<>();
        }
        // 判断是否有题目
        int fraction = 0; // 总分数
        int questionNum = 0; // 题目总数
        if (CollectionUtil.isNotEmpty(questions)) {
            for (DwQuestion dwQuestion : questions) {
                if (ObjUtil.isEmpty(dwQuestion)) {
                    continue;
                }
                Integer questionType = dwQuestion.getQuType();
                Integer questionFraction = dwQuestion.getFraction();
                if (questionType != null && questionFraction != null) {
                    if (questionType != QuType.PAGETAG.getIndex() && questionType != QuType.PARAGRAPH.getIndex()) {
                        fraction += questionFraction;
                        questionNum++;
                    }
                }
            }
        }

        // 更新数据库
        UpdateWrapper<DwSurveyDirectory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, dwSurveyDirectoryId);
        updateWrapper.set(MybatisPlusUtil.toColumns(DwSurveyDirectory::getFraction), fraction);
        updateWrapper.set(MybatisPlusUtil.toColumns(DwSurveyDirectory::getSurveyQuNum), questionNum);

        if (updateWrapper != null) {
            update(updateWrapper);
        } else {
            throw new CustomException("该问卷信息不存在。");
        }
        return fraction;
    }

    /**
     * 参加问卷的方法
     *
     * @param inputObject  输入对象，包含请求参数
     * @param outputObject 输出对象，用于返回响应数据
     * @return 允许参加问卷时返回问卷目录信息
     */
    @Override
    public void takeExam(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        // 是否可以参加问卷，true：可以；false：不可以
        boolean yesOrNo = false;
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String id = map.get("id").toString();
        DwSurveyDirectory dwSurveyDirectory = selectById(id);
        if (StrUtil.isEmpty(dwSurveyDirectory.getId())) {
            throw new CustomException("该问卷不存在");
        }
        if (ObjUtil.isEmpty(dwSurveyDirectory)) {
            throw new CustomException("该试卷不存在");
        }
        if (dwSurveyDirectory.getSurveyState().equals(CommonNumConstants.NUM_ONE)) {
            DwSurveyAnswer examSurveyAnswer = dwSurveyAnswerService.queryWhetherExamIngByStuId(userId, id); // 查询用户是否已经参加过该问卷
            if (ObjUtil.isNotEmpty(examSurveyAnswer)) {
                throw new CustomException("您已参加过该问卷");
            } else {
                yesOrNo = true;
            }
        } else {
            throw new CustomException("该问卷未发布");
        }
        if (yesOrNo) {
            outputObject.setBean(dwSurveyDirectory);
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
        Map<String, Object> map = inputObject.getParams();
        String dwDirectoryId = map.get("id").toString();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String surveyName = map.get("surveyName").toString();
        DwSurveyDirectory examSurveyDirectory = selectById(dwDirectoryId);
        examSurveyDirectory.setSurveyModel(CommonNumConstants.NUM_ONE);
        examSurveyDirectory.setSurveyState(CommonNumConstants.NUM_ZERO);
        examSurveyDirectory.setCreateId(userId);
        examSurveyDirectory.setCreateTime(DateUtil.getTimeAndToString());
        examSurveyDirectory.setId(ToolUtil.randomStr(6, 12));
        examSurveyDirectory.setDwQuestionMation(new ArrayList<>());
        if (StrUtil.isNotEmpty(surveyName)) {
            examSurveyDirectory.setSurveyName(surveyName);
        } else {
            examSurveyDirectory.setSurveyName(examSurveyDirectory.getSurveyName() + "_副本");
        }
        createEntity(examSurveyDirectory, userId);
        List<DwQuestion> questionList = dwQuestionService.QueryQuestionByBelongId(dwDirectoryId);
        if (CollectionUtil.isNotEmpty(questionList)) {
            List<String> questionIdList = questionList.stream().map(DwQuestion::getId).collect(Collectors.toList());
            Map<String, List<DwQuestionLogic>> stringListMap = dwQuestionLogicService.selectByQuestionIds(questionIdList);
            Map<String, List<DwQuRadio>> stringListMap1 = dwQuRadioService.selectByBelongId(questionIdList);
            Map<String, List<DwQuScore>> stringListMap2 = dwQuScoreService.selectByBelongId(questionIdList);
            Map<String, List<DwQuCheckbox>> stringListMap3 = dwQuCheckboxService.selectByBelongId(questionIdList);
            Map<String, List<DwQuMultiFillblank>> stringListMap4 = dwQuMultiFillblankService.selectByBelongId(questionIdList);
            Map<String, List<DwQuOrderby>> stringListMap5 = dwQuOrderbyService.selectByBelongId(questionIdList);
            Map<String, List<DwQuChenColumn>> stringListMap6 = dwQuChenColumnService.selectByBelongId(questionIdList);
            Map<String, List<DwQuChenRow>> stringListMap7 = dwQuChenRowService.selectByBelongId(questionIdList);
            for (DwQuestion question : questionList) {
                String id = question.getId();
                question.setCopyFromId(id);
                stringListMap.get(id);
                List<DwQuestionLogic> dwQuestionLogics = stringListMap.get(id);
                question.setQuestionLogic(dwQuestionLogics);
                List<DwQuRadio> dwQuRadioList = stringListMap1.get(id);
                question.setRadioTd(dwQuRadioList);
                List<DwQuScore> dwQuScoreList = stringListMap2.get(id);
                question.setScoreTd(dwQuScoreList);
                List<DwQuCheckbox> dwQuCheckboxList = stringListMap3.get(id);
                question.setCheckboxTd(dwQuCheckboxList);
                List<DwQuMultiFillblank> dwQuMultiFillblankList = stringListMap4.get(id);
                question.setMultifillblankTd(dwQuMultiFillblankList);
                List<DwQuOrderby> dwQuOrderbyList = stringListMap5.get(id);
                question.setOrderByTd(dwQuOrderbyList);
                List<DwQuChenColumn> dwQuChenColumnList = stringListMap6.get(id);
                question.setColumnTd(dwQuChenColumnList);
                List<DwQuChenRow> dwQuChenRowList = stringListMap7.get(id);
                question.setRowTd(dwQuChenRowList);
                question.setBelongId(examSurveyDirectory.getId());
            }
            dwQuestionService.createEntity(questionList, userId);
            outputObject.setBean(examSurveyDirectory);
            outputObject.settotal(1);
        }
    }

    /**
     * 创建/更新题目前的操作
     *
     * @param dwSurveyDirectory 问卷目录对象
     */
    @Override
    public void validatorEntity(DwSurveyDirectory dwSurveyDirectory) {
        super.validatorEntity(dwSurveyDirectory);
        String realStartTime = dwSurveyDirectory.getRealStartTime();
        String realEndTime = dwSurveyDirectory.getRealEndTime();
        if (StrUtil.isNotEmpty(realStartTime) && StrUtil.isNotEmpty(realEndTime)) {
            boolean compareTime = DateUtil.compareTime(realStartTime, realEndTime);
            if (compareTime) {
                throw new CustomException("实际开始时间不能晚于实际结束时间");
            }
        }
    }


    @Override
    protected void createPostpose(DwSurveyDirectory entity, String userId) {
        List<DwQuestion> dwQuestionMation = entity.getDwQuestionMation();
        if (CollectionUtil.isNotEmpty(dwQuestionMation)) {
            for (DwQuestion dwQuestion : dwQuestionMation) {
                dwQuestion.setBelongId(entity.getId());
            }
            dwQuestionService.createEntity(dwQuestionMation, userId);
        }
    }


    @Override
    public void updatePostpose(DwSurveyDirectory entity, String userId) {
        List<DwQuestion> questionList = entity.getDwQuestionMation();
        List<DwQuestion> existingQuestions = dwQuestionService.QueryQuestionByBelongId(entity.getId());
        List<String> existingIds = existingQuestions.stream()
            .map(DwQuestion::getId)
            .collect(Collectors.toList());
        Map<Boolean, List<DwQuestion>> partitionedQuestions = questionList.stream()
            .collect(Collectors.partitioningBy(question -> StrUtil.isNotEmpty(question.getId())));
        List<DwQuestion> questionsWithId = partitionedQuestions.get(true);
        List<DwQuestion> questionsWithoutId = partitionedQuestions.get(false);
        List<String> submittedIds = questionsWithId.stream()
            .map(DwQuestion::getId)
            .collect(Collectors.toList());
        Set<String> submittedIdSet = new HashSet<>(submittedIds);
        List<String> idsToDelete = existingIds.stream()
            .filter(id -> !submittedIdSet.contains(id))
            .collect(Collectors.toList());
        dwQuestionService.deleteById(idsToDelete);
        if (CollectionUtil.isNotEmpty(questionsWithId)) {
            List<DwQuestion> createQuestion = new ArrayList<>();
            //纯题目
            List<DwQuestion> collect = questionsWithId.stream()
                .filter(question -> StrUtil.isNotEmpty(question.getId()) &&
                    StrUtil.isEmpty(question.getBelongId())).collect(Collectors.toList());
            createQuestion.addAll(collect);
            for (DwQuestion dwQuestion : createQuestion) {
                dwQuestion.setBelongId(entity.getId());
            }
            for (DwQuestion dwQuestion : collect) {
                dwQuestion.setId(StrUtil.EMPTY);
            }
            dwQuestionService.updateEntity(createQuestion, userId);
            dwQuestionService.createEntity(collect, userId);
            dwQuestionService.updateEntity(questionsWithId, userId);
        }
        dwQuestionService.createEntity(questionsWithoutId, userId);

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
        String dwSurveyDirectoryId = map.get("id").toString();
        QueryWrapper<DwSurveyDirectory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, dwSurveyDirectoryId);
        DwSurveyDirectory examSurveyDirectory = getOne(queryWrapper);
        // 判断问卷目录对象是否存在
        if (ObjUtil.isNotEmpty(examSurveyDirectory)) {
            // 判断问卷目录状态是否为进行中（NUM_ONE）
            if (examSurveyDirectory.getSurveyState().equals(CommonNumConstants.NUM_ONE)) {
                // 获取当前时间作为实际结束时间
                String realEndTime = DateUtil.getTimeAndToString();
                UpdateWrapper<DwSurveyDirectory> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq(CommonConstants.ID, dwSurveyDirectoryId);
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

    @Override
    public void queryDwurveyMationById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        DwSurveyDirectory dwSurveyDirectory = selectById(id);
        if (ObjUtil.isNotEmpty(dwSurveyDirectory)) {
            List<DwQuestion> questionList = dwQuestionService.QueryQuestionByBelongId(dwSurveyDirectory.getId());
            List<Map<String, Object>> list = JSONUtil.toList(JSONUtil.toJsonStr(questionList), null);
            for (Map<String, Object> question : list) {
                question.put("quTypeName ", QuType.getCName(Integer.parseInt(question.get("quType").toString())));
                getQuestionOptionReportListMation(question);
            }
            outputObject.setBean(dwSurveyDirectory);
            outputObject.setBeans(list);
            outputObject.settotal(CommonNumConstants.NUM_ONE);
        } else {
            outputObject.setreturnMessage("该试卷信息不存在。");
        }
    }

    @Override
    public void queryDwSurveyDirectoryMationByIdToHTML(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        DwSurveyDirectory dwSurveyDirectory = selectById(id);
        if (StrUtil.isEmpty(dwSurveyDirectory.getId())) {
            throw new CustomException("该试卷信息不存在!");
        }
        if (ObjectUtil.isNotEmpty(dwSurveyDirectory)) {
            List<DwQuestion> dwQuestions = dwQuestionService.QueryQuestionByBelongId(id);
            int pageNo = 1;
            for (DwQuestion dwQuestion : dwQuestions) {
                if (dwQuestion.getQuType().equals(QuType.PAGETAG.getIndex())) {
                    pageNo++;
                }
            }
            List<Map<String, Object>> list = JSONUtil.toList(JSONUtil.toJsonStr(dwQuestions), null);
            for (Map<String, Object> map : list) {
                map.put("pageNo", pageNo);
                getQuestionOptionReportListMation(map);
            }
            dwSurveyDirectory.setPageNo(pageNo);
            outputObject.setBean(dwSurveyDirectory);
            outputObject.setBeans(list);
            outputObject.settotal(1);
        } else {
            outputObject.setreturnMessage("该试卷信息不存在。");
        }
    }

    public Map<String, Object> getQuestionOptionReportListMation(Map<String, Object> question) {
        Integer quType = Integer.parseInt(question.get("quType").toString());
        String id = question.get("id").toString();
        if (quType.equals(QuType.RADIO.getIndex())) {
            List<DwAnRadio> dwAnRadioList = dwAnRadioService.selectRadioByQuId(id);
            List<Map<String, Object>> radios = (List<Map<String, Object>>) question.get("radioAn");
            Map<String, Long> countMap = dwAnRadioList.stream()
                .collect(Collectors.groupingBy(DwAnRadio::getQuItemId, Collectors.counting()));
            int count = 0;
            for (Map<String, Object> radio : radios) {
                radio.put("anCount", 0);
                String radioId = radio.get("id").toString();
                for (DwAnRadio dwAnRadio : dwAnRadioList) {
                    if (dwAnRadio.getQuItemId().equals(radio.get("id").toString())) {
                        Long count1 = countMap.get(radioId);
                        radio.put("anCount", count1 != null ? count1 : 0);
                    }
                }
                count += Integer.parseInt(radio.get("anCount").toString());
                for (Map<String, Object> map : radios) {
                    map.put("anAllCount", count);
                }
            }
        }
        if (quType.equals(QuType.MULTIFILLBLANK.getIndex())) {
            List<DwAnDfillblank> dwAnDfillblankList = dwAnDfillblankService.selectAnDfillblankQuId(id);
            List<Map<String, Object>> checkBoxs = (List<Map<String, Object>>) question.get("dfillblankAn");
            Map<String, Long> countMap = dwAnDfillblankList.stream().collect(Collectors.groupingBy(DwAnDfillblank::getQuItemId, Collectors.counting()));
            int count = 0;
            for (Map<String, Object> checkBox : checkBoxs) {
                checkBox.put("anCount", 0);
                String checkBoxId = checkBox.get("id").toString();
                for (DwAnDfillblank dwAnDfillblank : dwAnDfillblankList) {
                    if (dwAnDfillblank.getQuItemId().equals(checkBox.get("id").toString())) {
                        Long count1 = countMap.get(checkBoxId);
                        checkBox.put("anCount", count1 != null ? count1 : 0);
                    }
                }
                count += Integer.parseInt(checkBox.get("anCount").toString());
                for (Map<String, Object> map : checkBoxs) {
                    map.put("anAllCount", count);
                }
            }
        }
        if (quType.equals(QuType.FILLBLANK.getIndex())) {
            List<DwAnFillblank> dwAnFillblankList = dwAnFillblankService.selectAnFillblankQuId(id);
            long emptyCount = 0;
            long blankCount = 0;
            for (DwAnFillblank dwAnFillblank : dwAnFillblankList) {
                if (dwAnFillblank.getAnswer() == null || dwAnFillblank.getAnswer().trim().isEmpty()) {
                    emptyCount++;
                } else {
                    blankCount++;
                }
            }
            question.put("rowContent", emptyCount);
            question.put("optionContent", blankCount);
            question.put("anCount", blankCount);
        }
        if (quType.equals(QuType.ANSWER.getIndex())) {
            List<DwAnAnswer> dwAnAnswerList = dwAnAnswerService.selectAnAnswerByQuId(id);
            long emptyCount = 0;
            long blankCount = 0;
            for (DwAnAnswer dwAnAnswer : dwAnAnswerList) {
                if (dwAnAnswer.getAnswer() == null || dwAnAnswer.getAnswer().trim().isEmpty()) {
                    emptyCount++;
                } else {
                    blankCount++;
                }
            }
            question.put("rowContent", emptyCount);
            question.put("optionContent", blankCount);
            question.put("anCount", blankCount);
        }
        if (quType.equals(QuType.ENUMQU.getIndex())) {
            List<DwAnEnumqu> dwAnEnumquList = dwAnEnumquService.selectAnEnumByQuId(id);
            Map<String, Long> answerCountMap = dwAnEnumquList.stream()
                .collect(Collectors.groupingBy(DwAnEnumqu::getAnswer, Collectors.counting()));
            if (answerCountMap.isEmpty()) {
                question.put("anCount", 0);
            } else {
                question.put("anCount", answerCountMap.size());
            }
        }
        if (quType.equals(QuType.CHENRADIO.getIndex())) {
            List<DwAnChenRadio> beans = dwAnChenRadioService.selectByQuId(id);
            List<Map<String, Object>> rows = Optional.ofNullable(question.get("chenRadioAn"))
                .map(list -> (List<Map<String, Object>>) list)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull) // 过滤 null 元素
                .collect(Collectors.toList());
            int count = 0;
            Map<String, Map<String, Integer>> statMap = new HashMap<>();
            for (DwAnChenRadio bean : beans) {
                if (bean.getVisibility() != null && bean.getVisibility() == 1) {
                    String quRowId = bean.getQuRowId();
                    String quColId = bean.getQuColId();
                    statMap.computeIfAbsent(quRowId, k -> new HashMap<>())
                        .merge(quColId, 1, Integer::sum);
                }
            }
            List<Map<String, Object>> statBeans = new ArrayList<>();
            statMap.forEach((quRowId, colCounts) ->
                colCounts.forEach((quColId, cnt) -> {
                    Map<String, Object> statBean = new HashMap<>();
                    statBean.put("quRowId", quRowId);
                    statBean.put("quColId", quColId);
                    statBean.put("count", cnt);
                    statBeans.add(statBean);
                })
            );
            for (Map<String, Object> row : rows) {
                row.put("anCount", 0);
                String rowId = Optional.ofNullable(row.get("id"))
                    .map(Object::toString)
                    .orElse("");
                for (Map<String, Object> statBean : statBeans) {
                    Object quRowIdObject = statBean.get("quRowId");
                    if (quRowIdObject == null) {
                        continue;
                    }
                    String quRowId = quRowIdObject.toString();

                    if (rowId.equals(quRowId)) {
                        Integer currentCount = (Integer) row.get("anCount");
                        Object countObject = statBean.get("count");
                        if (countObject instanceof Integer) {
                            currentCount += (Integer) countObject;
                        } else {
                            currentCount += 0; // 如果 count 不是 Integer 类型，跳过
                        }
                        row.put("anCount", currentCount);
                    }
                }
                Integer anCount = (Integer) row.get("anCount");
                if (anCount != null) {
                    count += anCount;
                }
            }
            for (Map<String, Object> statBean : statBeans) {
                statBean.put("anAllCount", count); // 全局总答案数
                Object quRowIdObject = statBean.get("quRowId");
                if (quRowIdObject == null) {
                    continue; // 如果 quRowId 为 null，跳过当前 statBean
                }
                String quRowId = quRowIdObject.toString();

                // 匹配行并设置当前行的总答案数
                for (Map<String, Object> row : rows) {
                    Object rowIdObject = row.get("id");
                    if (rowIdObject == null) {
                        continue; // 如果 id 为 null，跳过当前行
                    }
                    String rowId = rowIdObject.toString();

                    if (quRowId.equals(rowId)) {
                        Object anCountObject = row.get("anCount");
                        if (anCountObject instanceof Integer) {
                            statBean.put("anCount", anCountObject.toString());
                        } else {
                            statBean.put("anCount", "0");
                        }
                        break;
                    }
                }
            }
            question.put("anChenRadios", statBeans);
        }
        if (quType.equals(QuType.CHENFBK.getIndex())) {
            List<DwAnChenFbk> beans = dwAnChenFbkService.selectByQuId(id);
            List<Map<String, Object>> rows = (List<Map<String, Object>>) question.get("chenFbkAn");
            int count = 0;
            Map<String, Map<String, Integer>> statMap = new HashMap<>();
            for (DwAnChenFbk bean : beans) {
                if (bean.getVisibility() != null && bean.getVisibility() == 1) {
                    String quRowId = bean.getQuRowId();
                    String quColId = bean.getQuColId();
                    statMap.computeIfAbsent(quRowId, k -> new HashMap<>())
                        .merge(quColId, 1, Integer::sum);
                }
            }
            List<Map<String, Object>> statBeans = new ArrayList<>();
            statMap.forEach((quRowId, colCounts) ->
                colCounts.forEach((quColId, cnt) -> {
                    Map<String, Object> statBean = new HashMap<>();
                    statBean.put("quRowId", quRowId);
                    statBean.put("quColId", quColId);
                    statBean.put("count", cnt);
                    statBeans.add(statBean);
                })
            );
            for (Map<String, Object> row : rows) {
                row.put("anCount", 0);
                String rowId = row.get("id").toString();
                for (Map<String, Object> statBean : statBeans) {
                    if (rowId.equals(statBean.get("quRowId").toString())) {
                        int currentCount = (int) row.get("anCount");
                        currentCount += (int) statBean.get("count");
                        row.put("anCount", currentCount);
                    }
                }
                count += (int) row.get("anCount");
            }
            for (Map<String, Object> statBean : statBeans) {
                statBean.put("anAllCount", count); // 全局总答案数
                String quRowId = statBean.get("quRowId").toString();
                // 匹配行并设置当前行的总答案数
                for (Map<String, Object> row : rows) {
                    if (quRowId.equals(row.get("id").toString())) {
                        statBean.put("anCount", row.get("anCount").toString());
                        break;
                    }
                }
            }
            question.put("anChenFbks", statBeans);
        }
        if (quType.equals(QuType.CHENCHECKBOX.getIndex())) {
            List<DwAnChenCheckbox> beans = dwAnChenCheckboxService.selectByQuId(id);
            List<Map<String, Object>> rows = (List<Map<String, Object>>) question.get("chenCheckboxAn");
            int count = 0;
            Map<String, Map<String, Integer>> statMap = new HashMap<>();
            for (DwAnChenCheckbox bean : beans) {
                if (bean.getVisibility() != null && bean.getVisibility() == 1) {
                    String quRowId = bean.getQuRowId();
                    String quColId = bean.getQuColId();
                    statMap.computeIfAbsent(quRowId, k -> new HashMap<>())
                        .merge(quColId, 1, Integer::sum);
                }
            }
            List<Map<String, Object>> statBeans = new ArrayList<>();
            statMap.forEach((quRowId, colCounts) ->
                colCounts.forEach((quColId, cnt) -> {
                    Map<String, Object> statBean = new HashMap<>();
                    statBean.put("quRowId", quRowId);
                    statBean.put("quColId", quColId);
                    statBean.put("count", cnt);
                    statBeans.add(statBean);
                })
            );
            for (Map<String, Object> row : rows) {
                row.put("anCount", 0);
                String rowId = row.get("id").toString();
                for (Map<String, Object> statBean : statBeans) {
                    if (rowId.equals(statBean.get("quRowId").toString())) {
                        int currentCount = (int) row.get("anCount");
                        currentCount += (int) statBean.get("count");
                        row.put("anCount", currentCount);
                    }
                }
                count += (int) row.get("anCount");
            }
            for (Map<String, Object> statBean : statBeans) {
                statBean.put("anAllCount", count); // 全局总答案数
                String quRowId = statBean.get("quRowId").toString();
                // 匹配行并设置当前行的总答案数
                for (Map<String, Object> row : rows) {
                    if (quRowId.equals(row.get("id").toString())) {
                        statBean.put("anCount", row.get("anCount").toString());
                        break;
                    }
                }
            }
            question.put("anChenFbks", statBeans);
        }
        if (quType.equals(QuType.CHENSCORE.getIndex())) {
            List<DwAnChenScore> beans = dwAnChenScoreService.slectByQuId(id);
            question.put("anChenScore", beans);
        }
        if (quType.equals(QuType.SCORE.getIndex())) {
            List<DwAnScore> dwAnScoreList = dwAnScoreService.selectAnScoreByQuId(id);
            List<Map<String, Object>> scores = (List<Map<String, Object>>) question.get("scoreAn");
            Map<String, Long> collectMap = dwAnScoreList.stream().collect(Collectors.groupingBy(DwAnScore::getQuRowId, Collectors.counting()));
            int count = 0;
            for (Map<String, Object> score : scores) {
                score.put("anCount", CommonNumConstants.NUM_ZERO);
                String quRowId = score.get("id").toString();
                for (DwAnScore dwAnScore : dwAnScoreList) {
                    if (dwAnScore.getQuRowId().equals(score.get("id").toString())) {
                        Long count1 = collectMap.get(quRowId);
                        score.put("anCount", count1 != null ? count1 : CommonNumConstants.NUM_ZERO);
                    }
                }
                count += Integer.valueOf(score.get("anCount").toString());
                for (Map<String, Object> map : scores) {
                    map.put("anAllCount", count);
                }
            }
        }
        if (quType.equals(QuType.ORDERQU.getIndex())) {
            List<DwAnOrder> dwAnOrderList = dwAnOrderService.selectAnOrderByQuId(id);
            List<Map<String, Object>> orders = (List<Map<String, Object>>) question.get("orderByTd");
            Map<String, Long> orderCountMap = dwAnOrderList.stream()
                .filter(order ->
                    order.getVisibility() == 1 &&
                        order.getQuId().equals(id)
                )
                .collect(Collectors.groupingBy(
                    DwAnOrder::getQuRowId,
                    Collectors.counting()
                ));
            for (Map<String, Object> order : orders) {
                String rowId = order.get("id").toString();
                order.put("anOrderSum", orderCountMap.getOrDefault(rowId, 0L));
            }
            orders.sort(Comparator.comparingLong(
                o -> (long) o.getOrDefault("anOrderSum", 0L)
            ));
        }
        return question;
    }

    private void outputDwList(OutputObject outputObject, CommonPageInfo
        commonPageInfo, QueryWrapper<DwSurveyDirectory> queryWrapper, Page page) {
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
    public DwSurveyDirectory selectById(String id) {
        DwSurveyDirectory bean = super.selectById(id);
        List<DwQuestion> questionList = dwQuestionService.QueryQuestionByBelongId(id);
        if (CollectionUtil.isNotEmpty(questionList)) {
            bean.setDwQuestionMation(questionList);
        }
        return bean;
    }

    @Override
    public void selectById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        DwSurveyDirectory bean = super.selectById(id);
        List<DwQuestion> questionList = dwQuestionService.QueryQuestionByBelongId(bean.getId());
        if (CollectionUtil.isNotEmpty(questionList)) {
            outputObject.setBeans(questionList);
        }
        outputObject.setBean(bean);
        outputObject.settotal(questionList.size());
    }
}