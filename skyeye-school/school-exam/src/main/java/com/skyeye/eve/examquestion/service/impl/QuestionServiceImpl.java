/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.examquestion.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.common.util.question.QuType;
import com.skyeye.eve.entity.School;
import com.skyeye.eve.examquestion.dao.QuestionDao;
import com.skyeye.eve.examquestion.entity.Question;
import com.skyeye.eve.examquestion.service.QuestionService;
import com.skyeye.eve.service.SchoolService;
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
import com.skyeye.exam.examanorder.entity.ExamAnOrder;
import com.skyeye.exam.examanorder.service.ExamAnOrderService;
import com.skyeye.exam.examanradio.entity.ExamAnRadio;
import com.skyeye.exam.examanradio.service.ExamAnRadioService;
import com.skyeye.exam.examanscore.entity.ExamAnScore;
import com.skyeye.exam.examanscore.service.ExamAnScoreService;
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
import com.skyeye.exception.CustomException;
import com.skyeye.school.faculty.entity.Faculty;
import com.skyeye.school.faculty.service.FacultyService;
import com.skyeye.school.knowledge.entity.KnowledgePoints;
import com.skyeye.school.knowledge.service.KnowledgePointsService;
import com.skyeye.school.major.entity.Major;
import com.skyeye.school.major.service.MajorService;
import com.skyeye.school.subject.entity.Subject;
import com.skyeye.school.subject.service.SubjectService;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName: QuestionServiceImpl
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/15 15:18
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "题目", groupName = "题库管理")
public class QuestionServiceImpl extends SkyeyeBusinessServiceImpl<QuestionDao, Question> implements QuestionService {

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
    private ExamQuScoreService examQuScoreService;

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private MajorService majorService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ExamAnRadioService examAnRadioService;

    @Autowired
    private ExamAnCheckboxService examAnCheckboxService;

    @Autowired
    private ExamAnScoreService examAnScoreService;

    @Autowired
    private ExamAnOrderService examAnOrderService;

    @Autowired
    private ExamAnChenRadioService examAnChenRadioService;

    @Autowired
    private ExamAnChenCheckboxService examAnChenCheckboxService;

    @Autowired
    private ExamAnDfilllankService examAnDfilllankService;

    @Autowired
    private ExamAnChenFbkService examAnChenFbkService;

    @Autowired
    private ExamAnChenScoreService examAnChenScoreService;

    @Autowired
    private ExamAnCompChenRadioService examAnCompChenRadioService;

    @Autowired
    private KnowledgePointsService knowledgePointsService;

    @Override
    public void createPrepose(List<Question> entity) {
        for (Question question : entity) {
            // 设置题目的标签和可见性
            question.setQuTag(1);
            // 设置文件类型，默认为0
            Integer fileType = question.getFileType() != null ? question.getFileType() : 0;
            question.setFileType(fileType);
            // 设置是否上传，默认为2
            Integer whetherUpload = question.getWhetherUpload() != null ? question.getWhetherUpload() : 2;
            question.setWhetherUpload(whetherUpload);
        }
    }

    @Override
    protected void createPrepose(Question question) {
        // 设置题目的标签和可见性
        question.setQuTag(1);
        // 设置文件类型，默认为0
        Integer fileType = question.getFileType() != null ? question.getFileType() : 0;
        question.setFileType(fileType);
        // 设置是否上传，默认为2
        Integer whetherUpload = question.getWhetherUpload() != null ? question.getWhetherUpload() : 2;
        question.setWhetherUpload(whetherUpload);
    }

    @Override
    public void createPostpose(List<Question> questionList, String userId) {
        for (Question entity : questionList) {
            Integer tag = entity.getTag();
            if (tag == null) {
                throw new CustomException("请设置题目标记");
            } else if (!tag.equals(CommonNumConstants.NUM_ONE) && !tag.equals(CommonNumConstants.NUM_TWO)) {
                throw new CustomException("题目标记值不正确");
            }
        }
        examQuestionLogicService.createLogics(questionList, userId);
        examQuRadioService.createRadios(questionList, userId);
        examquScoreService.createScores(questionList, userId);
        examQuCheckboxService.createCheckboxs(questionList, userId);
        examQuMultiFillblankService.createMultiFillblanks(questionList, userId);
        examQuOrderbyService.createOrderbys(questionList, userId);
        examQuChenColumnService.createChenColumns(questionList, userId);
    }


    /**
     * 创建题目后的后置处理
     *
     * @param entity 题目实体对象
     * @param userId 创建题目的用户ID
     */
    @Override
    public void createPostpose(Question entity, String userId) {
        // 获取题目ID
        String quId = entity.getId();
        Integer tag = entity.getTag();
        if (tag == null) {
            throw new CustomException("请设置题目标记");
        } else {
            if (tag.equals(CommonNumConstants.NUM_ONE)) {
            } else if (tag.equals(CommonNumConstants.NUM_TWO)) {
                List<ExamQuestionLogic> questionLogic = entity.getQuestionLogic();
                if (CollectionUtils.isEmpty(questionLogic)) {
                } else {
                    examQuestionLogicService.setLogics(quId, questionLogic, userId);
                }
            } else {
                throw new CustomException("题目标记值不正确");
            }
        }
        // 根据不同的题目类型，保存对应的题目数据
        // 处理单选题
        List<ExamQuRadio> radioTd = entity.getRadioTd();
        if (CollectionUtils.isNotEmpty(radioTd)) {
            examQuRadioService.saveList(radioTd, quId, userId);
        }
        // 处理得分题
        List<ExamQuScore> ScoreTd = entity.getScoreTd();
        if (CollectionUtils.isNotEmpty(ScoreTd)) {
            examquScoreService.saveList(ScoreTd, quId, userId);
        }
        // 处理多选题
        List<ExamQuCheckbox> checkboxTd = entity.getCheckboxTd();
        if (CollectionUtils.isNotEmpty(checkboxTd)) {
            examQuCheckboxService.saveList(checkboxTd, quId, userId);
        }
        // 处理多空填空题
        List<ExamQuMultiFillblank> multiFillblankTd = entity.getMultifillblankTd();
        if (CollectionUtils.isNotEmpty(multiFillblankTd)) {
            examQuMultiFillblankService.saveList(multiFillblankTd, quId, userId);
        }
        // 处理排序题
        List<ExamQuOrderby> orderByTd = entity.getOrderByTd();
        if (CollectionUtils.isNotEmpty(orderByTd)) {
            examQuOrderbyService.saveList(orderByTd, quId, userId);
        }
        // 处理矩阵题
        List<ExamQuChenColumn> columnTd = entity.getColumnTd();
        List<ExamQuChenRow> rowTd = entity.getRowTd();
        if (CollectionUtils.isNotEmpty(columnTd) && CollectionUtils.isNotEmpty(rowTd)) {
            examQuChenColumnService.saveList(columnTd, rowTd, quId, userId);
        }
    }

    @Override
    protected void updatePostpose(List<Question> questionList, String userId) {
        deleteNoBelongQuestions(questionList);

        // 批量更新各题型数据
        examQuRadioService.updateRadios(questionList, userId);
        examquScoreService.updateScores(questionList, userId);
        examQuCheckboxService.updateCheckboxs(questionList, userId);
        examQuMultiFillblankService.updateMultiFillblanks(questionList, userId);
        examQuOrderbyService.updateOrderbys(questionList, userId);
        examQuChenColumnService.updateChenColumn(questionList, userId);
    }

    private void deleteNoBelongQuestions(List<Question> questions) {
        List<String> questionIds = questions.stream()
            .filter(q -> StrUtil.isEmpty(q.getBelongId()))
            .map(Question::getId)
            .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(questionIds)) {
            examQuRadioService.removeByQuIds(questionIds);
            examquScoreService.removeByQuIds(questionIds);
            examQuCheckboxService.removeByQuIds(questionIds);
            examQuMultiFillblankService.removeByQuIds(questionIds);
            examQuOrderbyService.removeByQuIds(questionIds);
            examQuChenColumnService.removeByQuIds(questionIds);
        }
    }

    @Override
    public void updatePostpose(Question entity, String userId) {
        String entityId = entity.getId();
        // 更新单选题
        String belongId = entity.getBelongId();
        if (StrUtil.isEmpty(belongId)) {
            examQuRadioService.removeByQuId(entityId);
            examquScoreService.removeByquId(entityId);
            examQuCheckboxService.removeByQuId(entityId);
            examQuMultiFillblankService.removeByQuId(entityId);
            examQuOrderbyService.removeByQuId(entityId);
            examQuChenColumnService.removeByQuId(entityId);
        }
        List<ExamQuRadio> radioTd = entity.getRadioTd();
        String quId = entity.getId();
        if (CollectionUtils.isNotEmpty(radioTd)) {
            List<String> collect = radioTd.stream().map(ExamQuRadio::getOptionId).collect(Collectors.toList());
            List<ExamQuRadio> examQuRadioList = examQuRadioService.selectQuRadio(entityId);
            List<String> collect1 = examQuRadioList.stream().map(ExamQuRadio::getId).collect(Collectors.toList());
            List<String> collect2 = collect1.stream().filter(
                optionId -> !collect.contains(optionId)
            ).collect(Collectors.toList());
            examQuRadioService.deleteById(collect2);
            examQuRadioService.saveList(radioTd, quId, userId);
        }
        // 更新得分题
        List<ExamQuScore> scoreTd = entity.getScoreTd();
        if (CollectionUtils.isNotEmpty(scoreTd)) {
            List<String> collect = scoreTd.stream().map(ExamQuScore::getOptionId).collect(Collectors.toList());
            List<String> collect1 = examquScoreService.selectQuScore(entityId).stream().map(ExamQuScore::getId).collect(Collectors.toList());
            List<String> collect2 = collect1.stream().filter(optionId -> !collect.contains(optionId)).collect(Collectors.toList());
            examquScoreService.deleteById(collect2);
            examquScoreService.saveList(scoreTd, quId, userId);
        }
        // 更新多选题
        List<ExamQuCheckbox> checkboxTd = entity.getCheckboxTd();
        if (CollectionUtils.isNotEmpty(checkboxTd)) {
            List<String> collect = checkboxTd.stream().map(ExamQuCheckbox::getOptionId).collect(Collectors.toList());
            List<String> collect1 = examQuCheckboxService.selectQuChenbox(entityId).stream().map(ExamQuCheckbox::getId).collect(Collectors.toList());
            List<String> collect2 = collect1.stream().filter(optionId -> !collect.contains(optionId)).collect(Collectors.toList());
            examQuCheckboxService.deleteById(collect2);
            examQuCheckboxService.saveList(checkboxTd, quId, userId);
        }
        // 更新多空填空题
        List<ExamQuMultiFillblank> multiFillblankTd = entity.getMultifillblankTd();
        if (CollectionUtils.isNotEmpty(multiFillblankTd)) {
            List<String> collect = multiFillblankTd.stream().map(ExamQuMultiFillblank::getOptionId).collect(Collectors.toList());
            List<String> collect1 = examQuMultiFillblankService.selectQuMultiFillblank(entityId).stream().map(ExamQuMultiFillblank::getId).collect(Collectors.toList());
            List<String> collect2 = collect1.stream().filter(optionId -> !collect.contains(optionId)).collect(Collectors.toList());
            examQuMultiFillblankService.deleteById(collect2);
            examQuMultiFillblankService.saveList(multiFillblankTd, quId, userId);
        }
        // 更新排序题
        List<ExamQuOrderby> orderByTd = entity.getOrderByTd();
        if (CollectionUtils.isNotEmpty(orderByTd)) {
            List<String> collect = orderByTd.stream().map(ExamQuOrderby::getOptionId).collect(Collectors.toList());
            List<String> collect1 = examQuOrderbyService.selectQuOrderby(entityId).stream().map(ExamQuOrderby::getId).collect(Collectors.toList());
            List<String> collect2 = collect1.stream().filter(optionId -> !collect.contains(optionId)).collect(Collectors.toList());
            examQuOrderbyService.deleteById(collect2);
            examQuOrderbyService.saveList(orderByTd, quId, userId);
        }
        // 更新陈列题
        List<ExamQuChenColumn> columnTd = entity.getColumnTd();
        List<ExamQuChenRow> rowTd = entity.getRowTd();
        if (CollectionUtils.isNotEmpty(columnTd) && CollectionUtils.isNotEmpty(rowTd)) {
            List<String> collect = columnTd.stream().map(ExamQuChenColumn::getOptionId).collect(Collectors.toList());
            List<String> collect1 = examQuChenColumnService.selectQuChenColumn(entityId).stream().map(ExamQuChenColumn::getId).collect(Collectors.toList());
            List<String> collect2 = collect1.stream().filter(optionId -> !collect.contains(optionId)).collect(Collectors.toList());
            examQuChenColumnService.deleteById(collect2);
            List<String> collect3 = rowTd.stream().map(ExamQuChenRow::getOptionId).collect(Collectors.toList());
            List<String> collect4 = examQuChenRowService.selectQuChenRow(entityId).stream().map(ExamQuChenRow::getId).collect(Collectors.toList());
            List<String> collect5 = collect4.stream().filter(optionId -> !collect3.contains(optionId)).collect(Collectors.toList());
            examQuChenRowService.deleteById(collect5);
            examQuChenColumnService.saveList(columnTd, rowTd, quId, userId);
        }
    }

    @Override
    public void queryMyQuestionList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        queryWrapper
            .and(wrapper -> wrapper
                .isNull(MybatisPlusUtil.toColumns(Question::getBelongId))
                .or()
                .eq(MybatisPlusUtil.toColumns(Question::getBelongId), "")
            );
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Question::getCreateTime));
        List<Question> questionList = getBaseInfo(queryWrapper);
        outputObject.setBeans(questionList);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void deleteBySurveyDirectoryId(String id) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getBelongId), id);
        List<Question> list = list(queryWrapper);
        deleteById(list.stream().map(Question::getId).collect(Collectors.toList()));
        remove(queryWrapper);
    }

    @Override
    public Question selectById(String id) {
        Question question = super.selectById(id);
        List<ExamQuRadio> examQuRadioList = examQuRadioService.selectQuRadio(id);
        question.setRadioTd(examQuRadioList);
        List<ExamQuScore> examQuScoreList = examquScoreService.selectQuScore(id);
        question.setScoreTd(examQuScoreList);
        List<ExamQuCheckbox> examQuCheckboxList = examQuCheckboxService.selectQuChenbox(id);
        question.setCheckboxTd(examQuCheckboxList);
        List<ExamQuMultiFillblank> examQuMultiFillblankList = examQuMultiFillblankService.selectQuMultiFillblank(id);
        question.setMultifillblankTd(examQuMultiFillblankList);
        List<ExamQuOrderby> examQuOrderbyList = examQuOrderbyService.selectQuOrderby(id);
        question.setOrderByTd(examQuOrderbyList);
        List<ExamQuChenColumn> examQuChenColumnList = examQuChenColumnService.selectQuChenColumn(id);
        question.setColumnTd(examQuChenColumnList);
        List<ExamQuChenRow> examQuChenRowList = examQuChenRowService.selectQuChenRow(id);
        question.setRowTd(examQuChenRowList);
        return question;
    }

    @Override
    public List<Question> selectByIds(String... quIds) {
        List<Question> questionList = super.selectByIds(quIds);
        // 提取所有知识点ID（带去重处理）
        Set<String> allKnowledgeIds = questionList.stream()
            .map(Question::getKnowledgeIds)
            .filter(StrUtil::isNotEmpty)
            .flatMap(ids -> Arrays.stream(ids.split(",")))
            .collect(Collectors.toSet());
        // 批量获取知识点数据（按ID映射）
        Map<String, KnowledgePoints> knowledgeMap = allKnowledgeIds.isEmpty()
            ? Collections.emptyMap()
            : knowledgePointsService.queryKnowledge(new ArrayList<>(allKnowledgeIds))
            .stream()
            .collect(Collectors.toMap(KnowledgePoints::getId, Function.identity()));
        // 构建题目与知识点的映射关系
        questionList.forEach(question -> {
            if (StrUtil.isNotEmpty(question.getKnowledgeIds())) {
                List<KnowledgePoints> points = Arrays.stream(question.getKnowledgeIds().split(","))
                    .map(knowledgeMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
                question.setKnowledgePointsMation(points);
            }
        });
        iAuthUserService.setName(questionList, "createId", "createName");
        iAuthUserService.setName(questionList, "lastUpdateId", "lastUpdateName");
        schoolService.setDataMation(questionList, Question::getSchoolId);
        facultyService.setDataMation(questionList, Question::getFacultyId);
        majorService.setDataMation(questionList, Question::getMajorId);
        subjectService.setDataMation(questionList, Question::getSubjectId);
        getQuestionOption(questionList);
        return questionList;
    }


    /**
     * 删除题目前的执行操作
     *
     * @param entity 题目实体对象
     */
    @Override
    public void deletePreExecution(Question entity) {
        // 获取题目ID和类型
        String quId = entity.getId();
        Integer quType = entity.getQuType();
        // 根据题目类型删除对应的题目数据
        if (quType.equals(QuType.RADIO.getIndex())) {
            examQuRadioService.removeByQuId(quId);
        } else if (quType.equals(QuType.MULTIFILLBLANK.getIndex())) {
            examQuMultiFillblankService.removeByQuId(quId);
        } else if (quType.equals(QuType.CHECKBOX.getIndex())) {
            examQuCheckboxService.removeByQuId(quId);
        } else if (quType.equals(QuType.SCORE.getIndex())) {
            examQuScoreService.removeByquId(quId);
        } else if (quType.equals(QuType.ORDERQU.getIndex())) {
            examQuOrderbyService.removeByQuId(quId);
        } else if (quType.equals(QuType.CHENRADIO.getIndex()) ||
            quType.equals(QuType.CHENFBK.getIndex()) ||
            quType.equals(QuType.CHENCHECKBOX.getIndex()) ||
            quType.equals(QuType.COMPCHENRADIO.getIndex()) ||
            quType.equals(QuType.CHENSCORE.getIndex())
        ) {
            examQuChenColumnService.removeByQuId(quId);
        }
    }

    @Override
    protected void deletePostpose(List<String> ids) {
        examQuRadioService.removeByQuIds(ids);
        examQuMultiFillblankService.removeByQuIds(ids);
        examQuCheckboxService.removeByQuIds(ids);
        examQuScoreService.removeByQuIds(ids);
        examQuOrderbyService.removeByQuIds(ids);
        examQuChenColumnService.removeByQuIds(ids);
    }

    /**
     * 根据归属ID查询题目列表
     *
     * @param belongId 归属ID
     * @return 题目列表
     */
    @Override
    public List<Question> QueryQuestionByBelongId(String belongId) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getBelongId), belongId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(Question::getOrderById));
        List<Question> questionList = list(queryWrapper);
        getQuestionOption(questionList);
        return questionList;
    }

    private void getQuestionOption(List<Question> questionList) {
        List<Question> radioList = questionList.stream().filter(question -> question.getQuType().equals(QuType.RADIO.getIndex()))
            .collect(Collectors.toList());
        List<String> radioIds = radioList.stream().map(Question::getId).collect(Collectors.toList());
        Map<String, List<ExamQuRadio>> radioMapList = examQuRadioService.selectByQuestionIds(radioIds);

        List<Question> cheankboxList = questionList.stream().filter(question -> question.getQuType().equals(QuType.CHECKBOX.getIndex())).collect(Collectors.toList());
        List<String> cheankboxIds = cheankboxList.stream().map(Question::getId).collect(Collectors.toList());
        Map<String, List<ExamQuCheckbox>> chaeckBoxMapList = examQuCheckboxService.selectByQuestionIds(cheankboxIds);

        List<Question> scoreList = questionList.stream().filter(question -> question.getQuType().equals(QuType.SCORE.getIndex())).collect(Collectors.toList());
        List<String> scoreIds = scoreList.stream().map(Question::getId).collect(Collectors.toList());
        Map<String, List<ExamQuScore>> scoreMapList = examQuScoreService.selectByQuestionIds(scoreIds);

        List<Question> orderQuList = questionList.stream().filter(question -> question.getQuType().equals(QuType.ORDERQU.getIndex())).collect(Collectors.toList());
        List<String> orderQuIds = orderQuList.stream().map(Question::getId).collect(Collectors.toList());
        Map<String, List<ExamQuOrderby>> orderQuMapList = examQuOrderbyService.selectByQuestionIds(orderQuIds);

        List<Question> multifillblankList = questionList.stream().filter(question -> question.getQuType().equals(QuType.MULTIFILLBLANK.getIndex())).collect(Collectors.toList());
        List<String> multifillblankIds = multifillblankList.stream().map(Question::getId).collect(Collectors.toList());
        Map<String, List<ExamQuMultiFillblank>> multifillblankMapList = examQuMultiFillblankService.selectByQuestionIds(multifillblankIds);

        List<Question> chenList = questionList.stream().filter(question ->
            question.getQuType().equals(QuType.CHENRADIO.getIndex()) ||
                question.getQuType().equals(QuType.CHENFBK.getIndex()) ||
                question.getQuType().equals(QuType.CHENCHECKBOX.getIndex()) ||
                question.getQuType().equals(QuType.CHENSCORE.getIndex())
        ).collect(Collectors.toList());
        List<String> chenIds = chenList.stream().map(Question::getId).collect(Collectors.toList());
        Map<String, List<ExamQuChenColumn>> chenColMapList = examQuChenColumnService.selectByQuestionIds(chenIds);
        Map<String, List<ExamQuChenRow>> chenRowMapList = examQuChenRowService.selectByQuestionIds(chenIds);
        questionList.forEach(question -> {
            String qid = question.getId();
            int quType = question.getQuType();

            switch (quType) {
                case 1: // 单选题
                    question.setRadioTd(radioMapList.getOrDefault(qid, Collections.emptyList()));
                    break;
                case 2: // 多选题
                    question.setCheckboxTd(chaeckBoxMapList.getOrDefault(qid, Collections.emptyList()));
                    break;
                case 8: // 评分题
                    question.setScoreTd(scoreMapList.getOrDefault(qid, Collections.emptyList()));
                    break;
                case 9: // 排序题
                    question.setOrderByTd(orderQuMapList.getOrDefault(qid, Collections.emptyList()));
                    break;
                case 4: // 多行填空题
                    question.setMultifillblankTd(multifillblankMapList.getOrDefault(qid, Collections.emptyList()));
                    break;
                case 11:
                case 12:
                case 13:
                case 18:
                    question.setColumnTd(chenColMapList.getOrDefault(qid, Collections.emptyList()));
                    question.setRowTd(chenRowMapList.getOrDefault(qid, Collections.emptyList()));
                    break;
                default:
            }
        });
    }

    @Override
    public Map<String, List<Question>> queryQuestionListBySurveyIds(List<String> surveyIds, String createId) {
        return surveyIds.stream()
            .collect(Collectors.toMap(
                surveyId -> surveyId,
                surveyId -> QueryQuestionByBelongIdAndStuId(surveyId, createId)
            ));
    }

    @Override
    public List<Question> QueryQuestionByBelongIdAndStuId(String surveyId, String studentId) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getBelongId), surveyId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(Question::getOrderById));
        List<Question> questionList = list(queryWrapper);
        return getQuestionOptionAndAnswer(studentId, questionList);
    }

    @Override
    public Map<String, List<Question>> queryQuestionListBySurveyIdList(List<String> surveyList) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(Question::getBelongId), surveyList);
        List<Question> questionList = list(queryWrapper);
        getQuestionOption(questionList);
        Map<String, List<Question>> questionListBySurveyId = questionList.stream().collect(Collectors.groupingBy(Question::getBelongId));
        return questionListBySurveyId;
    }

    @NotNull
    private List<Question> getQuestionOptionAndAnswer(String studentId, List<Question> questionList) {
        List<Question> radioList = questionList.stream().filter(question -> question.getQuType().equals(QuType.RADIO.getIndex())).collect(Collectors.toList());
        List<String> radioIds = radioList.stream().map(Question::getId).collect(Collectors.toList());
        Map<String, List<ExamQuRadio>> radioQuMapList = examQuRadioService.selectByQuestionIds(radioIds);
        Map<String, List<ExamAnRadio>> radioAnMapList = examAnRadioService.selectByQuIdAndStuId(radioIds, studentId);

        List<Question> cheankboxList = questionList.stream().filter(question -> question.getQuType().equals(QuType.CHECKBOX.getIndex())).collect(Collectors.toList());
        List<String> cheankboxIds = cheankboxList.stream().map(Question::getId).collect(Collectors.toList());
        Map<String, List<ExamQuCheckbox>> chaeckBoxQuMapList = examQuCheckboxService.selectByQuestionIds(cheankboxIds);
        Map<String, List<ExamAnCheckbox>> chaeckBoxAnMapList = examAnCheckboxService.selectByQuIdAndStuId(cheankboxIds, studentId);

        List<Question> scoreList = questionList.stream().filter(question -> question.getQuType().equals(QuType.SCORE.getIndex())).collect(Collectors.toList());
        List<String> scoreIds = scoreList.stream().map(Question::getId).collect(Collectors.toList());
        Map<String, List<ExamQuScore>> scoreMapList = examQuScoreService.selectByQuestionIds(scoreIds);
        Map<String, List<ExamAnScore>> scoreAnMapList = examAnScoreService.selectByQuIdAndStuId(scoreIds, studentId);

        List<Question> orderQuList = questionList.stream().filter(question -> question.getQuType().equals(QuType.ORDERQU.getIndex())).collect(Collectors.toList());
        List<String> orderQuIds = orderQuList.stream().map(Question::getId).collect(Collectors.toList());
        Map<String, List<ExamQuOrderby>> orderQuMapList = examQuOrderbyService.selectByQuestionIds(orderQuIds);
        Map<String, List<ExamAnOrder>> orderAnMapList = examAnOrderService.selectByQuIdAndStuId(orderQuIds, studentId);

        List<Question> multifillblankList = questionList.stream().filter(question -> question.getQuType().equals(QuType.MULTIFILLBLANK.getIndex())).collect(Collectors.toList());
        List<String> multifillblankIds = multifillblankList.stream().map(Question::getId).collect(Collectors.toList());
        Map<String, List<ExamQuMultiFillblank>> multifillblankMapList = examQuMultiFillblankService.selectByQuestionIds(multifillblankIds);
        Map<String, List<ExamAnDfillblank>> multifillblankAnMapList = examAnDfilllankService.selectByQuIdAndStuId(multifillblankIds, studentId);

        List<Question> chenList = questionList.stream().filter(question ->
            question.getQuType().equals(QuType.CHENRADIO.getIndex()) ||
                question.getQuType().equals(QuType.CHENFBK.getIndex()) ||
                question.getQuType().equals(QuType.CHENCHECKBOX.getIndex()) ||
                question.getQuType().equals(QuType.CHENSCORE.getIndex())
        ).collect(Collectors.toList());
        List<String> chenIds = chenList.stream().map(Question::getId).collect(Collectors.toList());
        Map<String, List<ExamQuChenColumn>> chenColumnMapList = examQuChenColumnService.selectByQuestionIds(chenIds);
        Map<String, List<ExamQuChenRow>> chenRowMapList = examQuChenRowService.selectByQuestionIds(chenIds);
        Map<String, List<ExamAnChenRadio>> chenAnRadio = examAnChenRadioService.selectByQuIdAndStuId(chenIds, studentId);
        Map<String, List<ExamAnChenFbk>> chenAnFbk = examAnChenFbkService.selectByQuIdAndStuId(chenIds, studentId);
        Map<String, List<ExamAnChenScore>> chenAnScore = examAnChenScoreService.selectByQuIdAndStuId(chenIds, studentId);
        Map<String, List<ExamAnCompChenRadio>> compChenAnRadio = examAnCompChenRadioService.selectByQuIdAndStuId(chenIds, studentId);
        Map<String, List<ExamAnChenCheckbox>> chenAnCheckbox = examAnChenCheckboxService.selectByQuIdAndStuId(chenIds, studentId);
        questionList.forEach(question -> {
            String qid = question.getId();
            int quType = question.getQuType();

            switch (quType) {
                case 1: // 单选题
                    question.setRadioTd(radioQuMapList.getOrDefault(qid, Collections.emptyList()));
                    question.setRadioAn(radioAnMapList.getOrDefault(qid, Collections.emptyList()));
                    break;
                case 2: // 多选题
                    question.setCheckboxTd(chaeckBoxQuMapList.getOrDefault(qid, Collections.emptyList()));
                    question.setCheckboxAn(chaeckBoxAnMapList.getOrDefault(qid, Collections.emptyList()));
                    break;
                case 8: // 评分题
                    question.setScoreTd(scoreMapList.getOrDefault(qid, Collections.emptyList()));
                    question.setScoreAn(scoreAnMapList.getOrDefault(qid, Collections.emptyList()));
                    break;
                case 9: // 排序题
                    question.setOrderByTd(orderQuMapList.getOrDefault(qid, Collections.emptyList()));
                    question.setOrderByAn(orderAnMapList.getOrDefault(qid, Collections.emptyList()));
                    break;
                case 4: // 多行填空题
                    question.setMultifillblankTd(multifillblankMapList.getOrDefault(qid, Collections.emptyList()));
                    question.setDFillblankAn(multifillblankAnMapList.getOrDefault(qid, Collections.emptyList()));
                    break;
                case 11:
                case 12:
                case 13:
                case 18:
                    question.setColumnTd(chenColumnMapList.getOrDefault(qid, Collections.emptyList()));
                    question.setRowTd(chenRowMapList.getOrDefault(qid, Collections.emptyList()));
                    question.setChenRadioAn(chenAnRadio.getOrDefault(qid, Collections.emptyList()));
                    question.setChenFbkAn(chenAnFbk.getOrDefault(qid, Collections.emptyList()));
                    question.setChenScoreAn(chenAnScore.getOrDefault(qid, Collections.emptyList()));
                    question.setCompChenRadioAn(compChenAnRadio.getOrDefault(qid, Collections.emptyList()));
                    question.setChenCheckboxAn(chenAnCheckbox.getOrDefault(qid, Collections.emptyList()));
                    break;
                default:
            }
        });
        return questionList;
    }

    @Override
    public void queryPageQuestionList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = null;
        setCommonPageInfoOtherInfo(commonPageInfo);
        if (commonPageInfo.getIsPaging() == null || commonPageInfo.getIsPaging()) {
            pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        }
        QueryWrapper<Question> queryWrapper = getQueryWrapper(commonPageInfo);
        queryWrapper
            .and(wrapper -> wrapper
                .isNull(MybatisPlusUtil.toColumns(Question::getBelongId))
                .or()
                .eq(MybatisPlusUtil.toColumns(Question::getBelongId), "")
            );
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Question::getCreateTime));// 按创建时间降序
        List<Question> questionList = list(queryWrapper);
        outputObject.setBeans(questionList);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    public void selectQuestionBySubjectId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String subjectId = commonPageInfo.getHolderId();
        if (StrUtil.isEmpty(subjectId)) {
            throw new IllegalArgumentException("subjectId不能为空");
        }
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getSubjectId), subjectId);
        List<Question> questionList = getBaseInfo(queryWrapper);
        for (Question question : questionList) {
            String quId = question.getId();
            int quType = question.getQuType();
            // 根据quType的值，去不同的表查询信息
            switch (quType) {
                case 1:
                    // 去ExamQuRadio表查询
                    List<ExamQuRadio> radioList = examQuRadioService.selectQuRadio(quId);
                    question.setRadioTd(radioList);
                    break;
                case 8:
                    // 去ExamQuScore表查询
                    List<ExamQuScore> scoreList = examQuScoreService.selectQuScore(quId);
                    question.setScoreTd(scoreList);
                    break;
                case 9:
                    // 去ExamQuOrderby表查询
                    List<ExamQuOrderby> orderbyList = examQuOrderbyService.selectQuOrderby(quId);
                    question.setOrderByTd(orderbyList);
                    break;
                case 2:
                    // 去ExamQuCheckBox表查询
                    List<ExamQuCheckbox> examQuCheckboxeList = examQuCheckboxService.selectQuChenbox(quId);
                    question.setCheckboxTd(examQuCheckboxeList);
                    break;
                case 11:
                case 12:
                case 13:
                case 18:
                    // 去ExamQuChenColumn和ExamQuChenRow表查询
                    List<ExamQuChenColumn> examQuChenColumnList = examQuChenColumnService.selectQuChenColumn(quId);
                    List<ExamQuChenRow> examQuChenRowList = examQuChenRowService.selectQuChenRow(quId);
                    question.setColumnTd(examQuChenColumnList);
                    question.setRowTd(examQuChenRowList);
                    break;
                default:
                    break;
            }
        }
        outputObject.setBeans(questionList);
        outputObject.settotal(pages.getTotal());
    }

    private List<Question> getBaseInfo(QueryWrapper<Question> queryWrapper) {
        List<Question> questionList1 = list(queryWrapper);
        List<String> schoolIds = questionList1.stream().map(Question::getSchoolId).collect(Collectors.toList());
        List<String> facultyIds = questionList1.stream().map(Question::getFacultyId).collect(Collectors.toList());
        List<String> majorIds = questionList1.stream().map(Question::getMajorId).collect(Collectors.toList());
        List<String> subjectIds = questionList1.stream().map(Question::getSubjectId).collect(Collectors.toList());
        Map<String, List<School>> schoolMapList = schoolService.selectByIdList(schoolIds);
        Map<String, List<Faculty>> facultyMapList = facultyService.selectByIdList(facultyIds);
        Map<String, List<Major>> majorMapList = majorService.selectByIdList(majorIds);
        Map<String, List<Subject>> subjectMapList = subjectService.selectByIdList(subjectIds);
        List<Question> questionList = questionList1.parallelStream()
            .map(item -> {
                item.setSchoolMation(getFirst(schoolMapList.get(item.getSchoolId())));
                item.setFacultyMation(getFirst(facultyMapList.get(item.getFacultyId())));
                item.setMajorMation(getFirst(majorMapList.get(item.getMajorId())));
                item.setSubjectMation(getFirst(subjectMapList.get(item.getSubjectId())));
                return item;
            })
            .collect(Collectors.toList());
        iAuthUserService.setName(questionList, "createId", "createName");
        iAuthUserService.setName(questionList, "lastUpdateId", "lastUpdateName");
        return questionList;
    }

    private static <T> T getFirst(List<T> list) {
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }

    @Override
    public void queryQuestionLists(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getIsDelete), CommonNumConstants.NUM_ONE);
        queryWrapper
            .and(wrapper -> wrapper
                .isNull(MybatisPlusUtil.toColumns(Question::getBelongId))
                .or()
                .eq(MybatisPlusUtil.toColumns(Question::getBelongId), "")
            );
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Question::getCreateTime));
        List<Question> questionList = getBaseInfo(queryWrapper);
        outputObject.setBeans(questionList);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void queryFilterQuestionList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getIsDelete), CommonNumConstants.NUM_ONE);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Question::getCreateTime));
        queryWrapper
            .and(wrapper -> wrapper
                .isNull(MybatisPlusUtil.toColumns(Question::getBelongId))
                .or()
                .eq(MybatisPlusUtil.toColumns(Question::getBelongId), "")
            );
        // 学校
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderKey())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getSchoolId), commonPageInfo.getHolderKey());
        }
        // 院系
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getFacultyId), commonPageInfo.getHolderId());
        }
        // 专业
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectKey())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getMajorId), commonPageInfo.getObjectKey());
        }
        // 科目
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getSubjectId), commonPageInfo.getObjectId());
        }
        // 类型
        if (StrUtil.isNotEmpty(commonPageInfo.getType())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getQuType), Integer.parseInt(commonPageInfo.getType()));
        }
        // 题目名称
        if (StrUtil.isNotEmpty(commonPageInfo.getKeyword())) {
            queryWrapper.like(MybatisPlusUtil.toColumns(Question::getQuTitle), commonPageInfo.getKeyword());
        }
        // 是否公开
        if (commonPageInfo.getEnabled() != null) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getIsPublic), commonPageInfo.getEnabled());
        }
        List<Question> beans = getBaseInfo(queryWrapper);
        outputObject.setBeans(beans);
        outputObject.settotal(page.getTotal());
    }

}
