package com.skyeye.eve.servey.service.impl;

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
import com.skyeye.eve.question.entity.DwQuestion;
import com.skyeye.eve.question.entity.DwQuestionLogic;
import com.skyeye.eve.question.service.DwQuestionLogicService;
import com.skyeye.eve.question.service.DwQuestionService;
import com.skyeye.eve.radio.entity.DwAnRadio;
import com.skyeye.eve.radio.entity.DwQuRadio;
import com.skyeye.eve.radio.service.DwAnRadioService;
import com.skyeye.eve.radio.service.DwQuRadioService;
import com.skyeye.eve.score.entity.DwAnScore;
import com.skyeye.eve.score.entity.DwQuScore;
import com.skyeye.eve.score.service.DwAnScoreService;
import com.skyeye.eve.score.service.DwQuScoreService;
import com.skyeye.eve.servey.dao.DwSurveyDirectoryDao;
import com.skyeye.eve.servey.entity.DwSurveyAnswer;
import com.skyeye.eve.servey.entity.DwSurveyDirectory;
import com.skyeye.eve.servey.service.DwSurveyAnswerService;
import com.skyeye.eve.servey.service.DwSurveyDirectoryService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
     * 是否可以参加问卷
     *
     * @param inputObject  输入对象，包含请求参数
     * @param outputObject 输出对象，用于返回响应数据
     * @return 允许参加问卷时返回问卷目录信息
     */
    @Override
    public void takeExam(InputObject inputObject, OutputObject outputObject) {
        try {
            Map<String, Object> map = inputObject.getParams();
            // 是否可以参加问卷，true：可以；false：不可以
            boolean yesOrNo = false;
            String id = map.get("id").toString();
            DwSurveyDirectory dwSurveyDirectory = selectById(id);
            if (ObjUtil.isEmpty(dwSurveyDirectory) && StrUtil.isEmpty(dwSurveyDirectory.getId())) {
                throw new CustomException("该问卷不存在");
            }
            if (dwSurveyDirectory.getSurveyState().equals(CommonNumConstants.NUM_ONE)) {
                if (dwSurveyDirectory.getIsEffective().equals(CommonNumConstants.NUM_ONE)) {
                    if (dwSurveyDirectory.getEffective().equals(CommonNumConstants.NUM_ONE)) {
                        // 获取前端传进来的机器码
                        String machineCode = map.get("machineCode").toString();
                        DwSurveyAnswer dwSurveyAnswer = dwSurveyAnswerService.querySurveyAnswerByRuleCode(machineCode, id);
                        if (ObjUtil.isNotEmpty(dwSurveyAnswer)) {
                            throw new CustomException("此问卷只能答一次，您已参加过该问卷");
                        }
                        // 密码访问是否正确
                        yesOrNo = isYesOrNoRuleCode(dwSurveyDirectory, map, yesOrNo);
                    }
                    if (dwSurveyDirectory.getEffectiveIp().equals(CommonNumConstants.NUM_ONE)) {
                        // 获取IP地址
                        String Ip = InetAddress.getLocalHost().getHostAddress();
                        DwSurveyAnswer dwSurveyAnswer = dwSurveyAnswerService.querySurveyAnswerByIp(Ip, id);
                        if (ObjUtil.isNotEmpty(dwSurveyAnswer)) {
                            throw new CustomException("此IP只能答一次，您已参加过该问卷");
                        } else {
                            yesOrNo = true;
                        }
                        // 密码访问是否正确
                        yesOrNo = isYesOrNoRuleCode(dwSurveyDirectory, map, yesOrNo);
                    }
                    yesOrNo = isYesOrNoRuleCode(dwSurveyDirectory, map, yesOrNo);
                }
                if (dwSurveyDirectory.getYnEndTime().equals(CommonNumConstants.NUM_ONE)) {
                    // 获取截止时间
                    String endTime = dwSurveyDirectory.getEndTime();
                    // 获取当前时间
                    String nowTime = DateUtil.formatDate2Str(new Date(), DateUtil.YYYY_MM_DD_HH_MM_SS);
                    // 当前时间是否在截止时间之前
                    boolean compare = DateUtil.compare(nowTime, endTime);
                    if (!compare) {
                        throw new CustomException("该问卷已截止");
                    } else {
                        yesOrNo = true;
                    }
                    yesOrNo = IsYesOrNoEndNum(dwSurveyDirectory, id, yesOrNo);
                }
                yesOrNo = IsYesOrNoEndNum(dwSurveyDirectory, id, yesOrNo);
                String userId = inputObject.getLogParams().get("id").toString();
                List<DwSurveyAnswer> dwSurveyAnswers = dwSurveyAnswerService.queryWhetherExamIngByStuId(userId, id);
                if (CollectionUtil.isNotEmpty(dwSurveyAnswers)) {
                    dwSurveyDirectory.setIsAnswered(CommonNumConstants.NUM_ONE);
                } else {
                    dwSurveyDirectory.setIsAnswered(CommonNumConstants.NUM_ZERO);
                }
            } else {
                throw new CustomException("该问卷未发布");
            }
            if (yesOrNo) {
                outputObject.setBean(dwSurveyDirectory);
            } else {
                throw new CustomException("您不具备该问卷权限");
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean IsYesOrNoEndNum(DwSurveyDirectory dwSurveyDirectory, String id, boolean yesOrNo) {
        if (dwSurveyDirectory.getYnEndNum().equals(CommonNumConstants.NUM_ONE)) {
            // 查询是否达到最大人数
            List<DwSurveyAnswer> dwSurveyAnswers = dwSurveyAnswerService.querySurveyAnswerNumById(id);
            if (dwSurveyAnswers.size() == dwSurveyDirectory.getEndNum()) {
                throw new CustomException("该问卷回答人数已达到最大人数");
            } else {
                yesOrNo = true;
            }
        }
        return yesOrNo;
    }

    private static boolean isYesOrNoRuleCode(DwSurveyDirectory dwSurveyDirectory, Map<String, Object> map, boolean yesOrNo) {
        if (dwSurveyDirectory.getRule().equals(CommonNumConstants.NUM_THREE)) {
            // 获取访问密码
            String ruleCode = map.get("ruleCode").toString();
            if (dwSurveyDirectory.getRuleCode().equals(ruleCode)) {
                yesOrNo = true;
            } else {
                throw new CustomException("访问密码错误");
            }
        }
        return yesOrNo;
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
        //问卷Id
        String id = inputObject.getParams().get("id").toString();
        // 获取试卷信息
        DwSurveyDirectory dwSurveyDirectory = selectById(id);
        if (ObjUtil.isNotEmpty(dwSurveyDirectory)) {
            // 获取试卷下的题目
            List<DwQuestion> questionList = dwQuestionService.QueryQuestionByBelongId(id);
            // 转换题目成List<Map<String, Onject>>类型
            List<Map<String, Object>> list = JSONUtil.toList(JSONUtil.toJsonStr(questionList), null);
            for (int i = 0; i < list.size(); i++) {
                // 获取每一道题目
                Map<String, Object> question = list.get(i);
//                question.put("quTypeName ", QuType.getCName(Integer.parseInt(question.get("quType").toString())));
                // 接收返回值并更新列表中的对象
                question = getQuestionOptionReportListMation(question);
                list.set(i, question);
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
        // 获取题目的类型枚举
        Integer quType = Integer.parseInt(question.get("quType").toString());
        // 获取题目的id
        String id = question.get("id").toString();
        // 如果是单选题
        if (quType.equals(QuType.RADIO.getIndex())) {
            // 单选题的答案
            List<DwAnRadio> dwAnRadioList = dwAnRadioService.selectRadioByQuId(id);
            // 单选题的选项
            List<Map<String, Object>> radios = Optional.ofNullable(question.get("radioTd"))
                .filter(list -> list instanceof List)
                .map(list -> (List<Map<String, Object>>) list)
                .orElseGet(Collections::emptyList);
            // 进行答案过滤
            List<DwAnRadio> safeDwAnRadioList = Optional.ofNullable(dwAnRadioList).orElseGet(Collections::emptyList);
            // 统计所有答案中的所有选项id出现的次数
            Map<String, Long> countMap = safeDwAnRadioList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                    bean -> Objects.toString(bean.getQuItemId(), ""),
                    Collectors.counting()
                ));
            int count = 0;
            // 先给每个radio设置anCount，并累加总数
            for (Map<String, Object> radio : radios) {
                String radioId = radio.get("id").toString();
                long count1 = 0;
                if (safeDwAnRadioList != null) {
                    for (DwAnRadio dwAnRadio : safeDwAnRadioList) {
                        if (dwAnRadio != null && radioId.equals(dwAnRadio.getQuItemId())) {
                            count1 = countMap.getOrDefault(radioId, 0L);
                            break;
                        }
                    }
                }

                radio.put("anCount", count1);
                count += (int) count1;
            }
            // 最后统一设置总次数
            for (Map<String, Object> radio : radios) {
                radio.put("anAllCount", count);
            }
        }
        // 如果是多项填空题
        if (quType.equals(QuType.MULTIFILLBLANK.getIndex())) {
            List<DwAnDfillblank> dwAnDfillblankList = dwAnDfillblankService.selectAnDfillblankQuId(id);
            List<Map<String, Object>> checkBoxs = Optional.ofNullable(question.get("multifillblankTd"))
                .filter(list -> list instanceof List)
                .map(list -> (List<Map<String, Object>>) list)
                .orElseGet(Collections::emptyList);
            Map<String, Long> countMap = dwAnDfillblankList.stream().collect(Collectors.groupingBy(DwAnDfillblank::getQuItemId, Collectors.counting()));
            int totalCount = 0;
            for (Map<String, Object> checkBox : checkBoxs) {
                String checkBoxId = checkBox.get("id").toString();
                long count1 = 0;

                if (dwAnDfillblankList != null) {
                    for (DwAnDfillblank dwAnDfillblank : dwAnDfillblankList) {
                        if (dwAnDfillblank != null && checkBoxId.equals(dwAnDfillblank.getQuItemId())) {
                            count1 = countMap.getOrDefault(checkBoxId, 0L);
                            break;
                        }
                    }
                }

                checkBox.put("anCount", count1);
                totalCount += (int) count1;
            }
            for (Map<String, Object> checkBox : checkBoxs) {
                checkBox.put("anAllCount", totalCount);
            }
        }
        // 如果是填空题
        if (quType.equals(QuType.FILLBLANK.getIndex())) {
            // 答案
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
            // 矩阵单选题答案
            List<DwAnChenRadio> beans = dwAnChenRadioService.selectByQuId(id);
            // 获取行选项
            List<Map<String, Object>> rows = Optional.ofNullable(question.get("rowTd"))
                .filter(list -> list instanceof List)
                .map(list -> (List<Map<String, Object>>) list)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .peek(map -> map.putIfAbsent("id", ""))
                .collect(Collectors.toList());
            // 防御空集合
            if (beans == null) beans = Collections.emptyList();
            if (rows == null) rows = Collections.emptyList();

            Map<String, Integer> rowTotal = new HashMap<>();
            Map<String, Map<String, Integer>> detail = new LinkedHashMap<>();
            beans.stream()
                .filter(b -> b != null
                    && CommonNumConstants.NUM_ONE.equals(b.getVisibility()))
                .forEach(b -> {
                    String rowId = b.getQuRowId();
                    String colId = b.getQuColId();
                    detail.computeIfAbsent(rowId, k -> new LinkedHashMap<>())
                        .merge(colId, 1, Integer::sum);
                    rowTotal.merge(rowId, 1, Integer::sum);
                });

            int allCount = 0;
            for (Map<String, Object> row : rows) {
                String rowId = String.valueOf(row.getOrDefault("id", ""));
                int cnt = rowTotal.getOrDefault(rowId, 0);
                row.put("anCount", cnt);
                allCount += cnt;
            }
            final int total = allCount;   // lambda 里要用 final
            List<Map<String, Object>> statBeans = new ArrayList<>();

            detail.forEach((rowId, colMap) ->
                colMap.forEach((colId, cnt) -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("quRowId", rowId);
                    m.put("quColId", colId);
                    m.put("count", cnt);
                    m.put("anCount", rowTotal.getOrDefault(rowId, 0)); // 行总次数
                    m.put("anAllCount", total);                         // 整题总次数
                    statBeans.add(m);
                })
            );

            question.put("anChenRadios", statBeans);
        }
        if (quType.equals(QuType.CHENFBK.getIndex())) {
            // 矩阵填空题答案
            List<DwAnChenFbk> beans = dwAnChenFbkService.selectByQuId(id);
            List<Map<String, Object>> rows = Optional.ofNullable(question.get("rowTd"))
                .filter(list -> list instanceof List)
                .map(list -> (List<Map<String, Object>>) list)
                .orElseGet(Collections::emptyList);
            // ---------- 0. 防御式空集合 ----------
            if (beans == null) beans = Collections.emptyList();
            if (rows == null) rows = Collections.emptyList();
            Map<String, Map<String, Integer>> detail = new LinkedHashMap<>();
            Map<String, Integer> rowTotal = new LinkedHashMap<>();

            for (DwAnChenFbk b : beans) {
                if (b == null || !Objects.equals(b.getVisibility(), 1)) {
                    continue;
                }
                String rowId = b.getQuRowId();
                String colId = b.getQuColId();

                detail.computeIfAbsent(rowId, k -> new LinkedHashMap<>())
                    .merge(colId, 1, Integer::sum);
                rowTotal.merge(rowId, 1, Integer::sum);
            }

            rows.forEach(r -> r.put("anCount",
                rowTotal.getOrDefault(String.valueOf(r.get("id")), 0)));
            int allCount = rowTotal.values().stream().mapToInt(Integer::intValue).sum();
            final int total = allCount;  // lambda 需要 final
            List<Map<String, Object>> statBeans = new ArrayList<>();

            detail.forEach((rowId, colMap) ->
                colMap.forEach((colId, cnt) -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("quRowId", rowId);
                    m.put("quColId", colId);
                    m.put("count", cnt);
                    m.put("anCount", rowTotal.get(rowId)); // 行总次数
                    m.put("anAllCount", total);              // 题目总次数
                    statBeans.add(m);
                })
            );

            question.put("anChenFbks", statBeans);
        }
        if (quType.equals(QuType.CHENCHECKBOX.getIndex())) {
            List<DwAnChenCheckbox> beans = dwAnChenCheckboxService.selectByQuId(id);
            List<Map<String, Object>> rows = Optional.ofNullable(question.get("rowTd"))
                .filter(list -> list instanceof List)
                .map(list -> (List<Map<String, Object>>) list)
                .orElseGet(Collections::emptyList);
            if (beans == null) beans = Collections.emptyList();
            if (rows == null) rows = Collections.emptyList();

            Map<String, Map<String, Integer>> detail = new LinkedHashMap<>();
            Map<String, Integer> rowTotal = new LinkedHashMap<>();

            beans.stream()
                .filter(b -> b != null && Objects.equals(b.getVisibility(), 1))
                .forEach(b -> {
                    String rowId = b.getQuRowId();
                    String colId = b.getQuColId();
                    detail.computeIfAbsent(rowId, k -> new LinkedHashMap<>())
                        .merge(colId, 1, Integer::sum);
                    rowTotal.merge(rowId, 1, Integer::sum);
                });

            rows.forEach(r -> r.put("anCount",
                rowTotal.getOrDefault(String.valueOf(r.get("id")), 0)));

            int allCount = rowTotal.values().stream().mapToInt(Integer::intValue).sum();
            final int total = allCount;
            List<Map<String, Object>> statBeans = new ArrayList<>();

            detail.forEach((rowId, colMap) ->
                colMap.forEach((colId, cnt) -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("quRowId", rowId);
                    m.put("quColId", colId);
                    m.put("count", cnt);
                    m.put("anCount", rowTotal.get(rowId)); // 行总次数
                    m.put("anAllCount", total);              // 题目总次数
                    statBeans.add(m);
                })
            );
            question.put("anChenFbks", statBeans);
        }
        if (quType.equals(QuType.CHENSCORE.getIndex())) {
            List<DwAnChenScore> beans = dwAnChenScoreService.slectByQuId(id);
            question.put("anChenScore", beans);
        }
        if (quType.equals(QuType.SCORE.getIndex())) {
            List<DwAnScore> dwAnScoreList = dwAnScoreService.selectAnScoreByQuId(id);
            List<Map<String, Object>> scores = Optional.ofNullable(question.get("scoreAn"))
                .filter(list -> list instanceof List)
                .map(list -> (List<Map<String, Object>>) list)
                .orElseGet(Collections::emptyList);
            Map<String, Long> collectMap = dwAnScoreList.stream().collect(Collectors.groupingBy(DwAnScore::getQuRowId, Collectors.counting()));
            // 0. 防御空集合
            if (dwAnScoreList == null) dwAnScoreList = Collections.emptyList();

            int totalCount = 0;
            for (Map<String, Object> score : scores) {
                String quRowId = String.valueOf(score.get("id"));
                // 从 collectMap 里取次数，没有就 0
                long cnt = collectMap.getOrDefault(quRowId, 0L);
                score.put("anCount", cnt);
                totalCount += cnt;
            }

            for (Map<String, Object> score : scores) {
                score.put("anAllCount", totalCount);
            }
        }
        if (quType.equals(QuType.ORDERQU.getIndex())) {
            List<DwAnOrder> dwAnOrderList = dwAnOrderService.selectAnOrderByQuId(id);
            List<Map<String, Object>> orders = Optional.ofNullable(question.get("orderByTd"))
                .filter(list -> list instanceof List)
                .map(list -> (List<Map<String, Object>>) list)
                .orElseGet(Collections::emptyList);
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
        List<String> dwQuestionIdList = questionList.stream().map(DwQuestion::getId).collect(Collectors.toList());
        Map<String, List<DwQuestionLogic>> map = dwQuestionLogicService.selectByDwQuestionIdList(dwQuestionIdList)
            .stream().collect(Collectors.groupingBy(DwQuestionLogic::getCkQuId));
        questionList.forEach(
            question ->
            {
                List<DwQuestionLogic> dwQuestionLogicList = map.get(question.getId());
                if (CollectionUtil.isNotEmpty(dwQuestionLogicList)) {
                    question.setQuestionLogic(dwQuestionLogicList);
                }
            }
        );
        if (CollectionUtil.isNotEmpty(questionList)) {
            bean.setDwQuestionMation(questionList);
        }
        return bean;
    }

    @Override
    public DwSurveyDirectory selectDirectoryAndAnswerById(String surveyId, String userId, String id) {
        DwSurveyDirectory dwSurveyDirectory = super.selectById(surveyId);
        List<DwQuestion> dwQuestions = dwQuestionService.QueryQuestionByBelongIdAndStuId(surveyId, userId, id);
        dwSurveyDirectory.setDwQuestionMation(dwQuestions);
        return dwSurveyDirectory;
    }

    @Override
    public Map<String, DwSurveyDirectory> selectMapBydwSurveyIds(List<String> dwSurveyIds) {
        if (CollectionUtil.isEmpty(dwSurveyIds)) {
            return new HashMap<>();
        }
        QueryWrapper<DwSurveyDirectory> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, dwSurveyIds);
        List<DwSurveyDirectory> dwSurveyDirectoryList = list(queryWrapper);
        if (CollectionUtil.isEmpty(dwSurveyDirectoryList)) {
            return new HashMap<>();
        }
//        List<String> schoolIds = dwSurveyDirectoryList.stream().map(DwSurveyDirectory::getSchoolId).collect(Collectors.toList());
//        List<String> facultyIds = dwSurveyDirectoryList.stream().map(DwSurveyDirectory::getFacultyId).collect(Collectors.toList());
//        List<String> majorIds = dwSurveyDirectoryList.stream().map(DwSurveyDirectory::getMajorId).collect(Collectors.toList());
//
//        Map<String, List<School>> schoolMapList = schoolIds.isEmpty() ? new HashMap<>() : schoolService.selectByIdList(schoolIds);
//        Map<String, List<Faculty>> facultyMapList = facultyIds.isEmpty() ? new HashMap<>() : facultyService.selectByIdList(facultyIds);
//        Map<String, List<Major>> majorMapList = majorIds.isEmpty() ? new HashMap<>() : majorService.selectByIdList(majorIds);
//        for (DwSurveyDirectory dwSurveyDirectory : dwSurveyDirectoryList) {
//            List<School> schools = schoolMapList.getOrDefault(dwSurveyDirectory.getSchoolId(), Collections.emptyList());
//            dwSurveyDirectory.setSchoolMation(schools.isEmpty() ? null : schools.get(CommonNumConstants.NUM_ZERO));
//
//            List<Faculty> faculties = facultyMapList.getOrDefault(dwSurveyDirectory.getFacultyId(), Collections.emptyList());
//            dwSurveyDirectory.setFacultyMation(faculties.isEmpty() ? null : faculties.get(CommonNumConstants.NUM_ZERO));
//
//            List<Major> majors = majorMapList.getOrDefault(dwSurveyDirectory.getMajorId(), Collections.emptyList());
//            dwSurveyDirectory.setMajorMation(majors.isEmpty() ? null : majors.get(CommonNumConstants.NUM_ZERO));
//        }
        return dwSurveyDirectoryList.stream().collect(Collectors.toMap(DwSurveyDirectory::getId, dwSurveyDirectory -> dwSurveyDirectory));
    }

    @Override
    public DwSurveyDirectory selectBySurAndStuIds(String surveyId, String createId, String id) {
        DwSurveyDirectory bean = super.selectById(surveyId);
        List<DwQuestion> questionList = dwQuestionService.QueryQuestionByBelongIdAndStuId(surveyId, createId, id);
        bean.setDwQuestionMation(questionList);
        return bean;
    }

}