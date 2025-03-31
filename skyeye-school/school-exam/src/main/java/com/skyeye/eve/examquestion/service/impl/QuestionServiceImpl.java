/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.examquestion.service.impl;

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
import com.skyeye.eve.examquestion.dao.QuestionDao;
import com.skyeye.eve.examquestion.entity.Question;
import com.skyeye.eve.examquestion.service.QuestionService;
import com.skyeye.eve.service.SchoolService;
import com.skyeye.exam.examancheckbox.entitiy.ExamAnCheckbox;
import com.skyeye.exam.examancheckbox.service.ExamAnCheckboxService;
import com.skyeye.exam.examanchencheckbox.entity.ExamAnChenCheckbox;
import com.skyeye.exam.examanchencheckbox.service.ExamAnChenCheckboxService;
import com.skyeye.exam.examanchenfbk.service.ExamAnChenFbkService;
import com.skyeye.exam.examanchenradio.entity.ExamAnChenRadio;
import com.skyeye.exam.examanchenradio.service.ExamAnChenRadioService;
import com.skyeye.exam.examanchenscore.service.ExamAnChenScoreService;
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
import com.skyeye.school.faculty.service.FacultyService;
import com.skyeye.school.knowledge.entity.KnowledgePoints;
import com.skyeye.school.knowledge.service.KnowledgePointsService;
import com.skyeye.school.major.service.MajorService;
import com.skyeye.school.subject.service.SubjectService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
    protected void createPrepose(Question entity) {
        // 设置题目的标签和可见性
        entity.setQuTag(1);
        // 设置文件类型，默认为0
        Integer fileType = entity.getFileType() != null ? entity.getFileType() : 0;
        entity.setFileType(fileType);
        // 设置是否上传，默认为2
        Integer whetherUpload = entity.getWhetherUpload() != null ? entity.getWhetherUpload() : 2;
        entity.setWhetherUpload(whetherUpload);
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
            for (String id : collect2) {
                examQuRadioService.deleteById(id);
            }
            examQuRadioService.saveList(radioTd, quId, userId);
        }
        // 更新得分题
        List<ExamQuScore> scoreTd = entity.getScoreTd();
        if (CollectionUtils.isNotEmpty(scoreTd)) {
            List<String> collect = scoreTd.stream().map(ExamQuScore::getOptionId).collect(Collectors.toList());
            List<String> collect1 = examquScoreService.selectQuScore(entityId).stream().map(ExamQuScore::getId).collect(Collectors.toList());
            List<String> collect2 = collect1.stream().filter(optionId -> !collect.contains(optionId)).collect(Collectors.toList());
            for (String id : collect2) {
                examquScoreService.deleteById(id);
            }
            examquScoreService.saveList(scoreTd, quId, userId);
        }
        // 更新多选题
        List<ExamQuCheckbox> checkboxTd = entity.getCheckboxTd();
        if (CollectionUtils.isNotEmpty(checkboxTd)) {
            List<String> collect = checkboxTd.stream().map(ExamQuCheckbox::getOptionId).collect(Collectors.toList());
            List<String> collect1 = examQuCheckboxService.selectQuChenbox(entityId).stream().map(ExamQuCheckbox::getId).collect(Collectors.toList());
            List<String> collect2 = collect1.stream().filter(optionId -> !collect.contains(optionId)).collect(Collectors.toList());
            for (String id : collect2) {
                examQuCheckboxService.deleteById(id);
            }
            examQuCheckboxService.saveList(checkboxTd, quId, userId);
        }
        // 更新多空填空题
        List<ExamQuMultiFillblank> multiFillblankTd = entity.getMultifillblankTd();
        if (CollectionUtils.isNotEmpty(multiFillblankTd)) {
            List<String> collect = multiFillblankTd.stream().map(ExamQuMultiFillblank::getOptionId).collect(Collectors.toList());
            List<String> collect1 = examQuMultiFillblankService.selectQuMultiFillblank(entityId).stream().map(ExamQuMultiFillblank::getId).collect(Collectors.toList());
            List<String> collect2 = collect1.stream().filter(optionId -> !collect.contains(optionId)).collect(Collectors.toList());
            for (String id : collect2) {
                examQuMultiFillblankService.deleteById(id);
            }
            examQuMultiFillblankService.saveList(multiFillblankTd, quId, userId);
        }
        // 更新排序题
        List<ExamQuOrderby> orderByTd = entity.getOrderByTd();
        if (CollectionUtils.isNotEmpty(orderByTd)) {
            List<String> collect = orderByTd.stream().map(ExamQuOrderby::getOptionId).collect(Collectors.toList());
            List<String> collect1 = examQuOrderbyService.selectQuOrderby(entityId).stream().map(ExamQuOrderby::getId).collect(Collectors.toList());
            List<String> collect2 = collect1.stream().filter(optionId -> !collect.contains(optionId)).collect(Collectors.toList());
            for (String id : collect2) {
                examQuOrderbyService.deleteById(id);
            }
            examQuOrderbyService.saveList(orderByTd, quId, userId);
        }
        // 更新陈列题
        List<ExamQuChenColumn> columnTd = entity.getColumnTd();
        List<ExamQuChenRow> rowTd = entity.getRowTd();
        if (CollectionUtils.isNotEmpty(columnTd) && CollectionUtils.isNotEmpty(rowTd)) {
            List<String> collect = columnTd.stream().map(ExamQuChenColumn::getOptionId).collect(Collectors.toList());
            List<String> collect1 = examQuChenColumnService.selectQuChenColumn(entityId).stream().map(ExamQuChenColumn::getId).collect(Collectors.toList());
            List<String> collect2 = collect1.stream().filter(optionId -> !collect.contains(optionId)).collect(Collectors.toList());
            for (String id : collect2) {
                examQuChenColumnService.deleteById(id);
            }
            List<String> collect3 = rowTd.stream().map(ExamQuChenRow::getOptionId).collect(Collectors.toList());
            List<String> collect4 = examQuChenRowService.selectQuChenRow(entityId).stream().map(ExamQuChenRow::getId).collect(Collectors.toList());
            List<String> collect5 = collect4.stream().filter(optionId -> !collect3.contains(optionId)).collect(Collectors.toList());
            for (String id : collect5) {
                examQuChenRowService.deleteById(id);
            }
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
    public List<Question> selectByIds(String... ids) {
        List<Question> questionList = new ArrayList<>();
        for (String id : ids) {
            Question question = super.selectById(id);
            questionList.add(question);
            String knowledgeIds = question.getKnowledgeIds();
            if (StrUtil.isNotEmpty(knowledgeIds)) {
                String[] split = knowledgeIds.split(",");
                for (String knowledgeId : split) {
                    List<KnowledgePoints> knowledgePointsList = knowledgePointsService.queryKnowledge(knowledgeId);
                    question.setKnowledgePointsMation(knowledgePointsList);
                }
            }
        }
        iAuthUserService.setName(questionList, "createId", "createName");
        iAuthUserService.setName(questionList, "lastUpdateId", "lastUpdateName");
        questionList = questionList.stream().map(item -> {
            item.setSchoolMation(schoolService.selectById(item.getSchoolId()));
            item.setFacultyMation(facultyService.selectById(item.getFacultyId()));
            item.setMajorMation(majorService.selectById(item.getMajorId()));
            item.setSubjectMation(subjectService.selectById(item.getSubjectId()));
            return item;
        }).collect(Collectors.toList());
        for (Question question : questionList) {
            String knowledgeIds = question.getKnowledgeIds();
            String[] split = knowledgeIds.split(",");
            List<KnowledgePoints> allKnowledgePointsList = new ArrayList<>();
            for (String knowledgeId : split) {
                List<KnowledgePoints> knowledgePointsList = knowledgePointsService.queryKnowledge(knowledgeId);
                allKnowledgePointsList.addAll(knowledgePointsList);
            }
            question.setKnowledgePointsMation(allKnowledgePointsList);
            // 1 单选题
            if (question.getQuType() == QuType.RADIO.getIndex()) {
                List<ExamQuRadio> radioList = examQuRadioService.selectQuRadio(question.getId());
                List<ExamAnRadio> examAnRadioList = examAnRadioService.selectByQuid(question.getId());
                question.setRadioTd(radioList);
                question.setRadioAn(examAnRadioList);
                continue;
            }
            // 2 多选题
            if (question.getQuType() == QuType.CHECKBOX.getIndex()) {
                List<ExamQuCheckbox> examQuCheckboxeList = examQuCheckboxService.selectQuChenbox(question.getId());
                List<ExamAnCheckbox> examAnCheckboxes = examAnCheckboxService.selectAnCheckBoxByQuId(question.getId());
                question.setCheckboxTd(examQuCheckboxeList);
                question.setCheckboxAn(examAnCheckboxes);
                continue;
            }
            // 8 评分题
            if (question.getQuType() == QuType.SCORE.getIndex()) {
                List<ExamQuScore> scoreList = examQuScoreService.selectQuScore(question.getId());
                List<ExamAnScore> examAnScoreList = examAnScoreService.selectAnScoreByQuId(question.getId());
                question.setScoreTd(scoreList);
                question.setScoreAn(examAnScoreList);
                continue;
            }
            // 9 排序题
            if (question.getQuType() == QuType.ORDERQU.getIndex()) {
                List<ExamQuOrderby> orderbyList = examQuOrderbyService.selectQuOrderby(question.getId());
                List<ExamAnOrder> examAnOrderbyList = examAnOrderService.selectAnOrderByQuId(question.getId());
                question.setOrderByTd(orderbyList);
                question.setOrderByAn(examAnOrderbyList);
                continue;
            }
            // 4 多行填空题
            if (question.getQuType() == QuType.MULTIFILLBLANK.getIndex()) {
                List<ExamQuMultiFillblank> multiFillblanks = examQuMultiFillblankService.selectQuMultiFillblank(question.getId());
                List<ExamAnDfillblank> examAnDfillblankList = examAnDfilllankService.selectAnMultiFillblankQuId(question.getId());
                question.setMultifillblankTd(multiFillblanks);
                question.setDFillblankAn(examAnDfillblankList);
                continue;
            }
            // 11 矩阵单选题CHENRADIO 12 矩阵填空题CHENFBK 13 矩阵多选题CHENCHECKBOX 18 矩阵评分题CHENSCORE
            if (question.getQuType() == QuType.CHENRADIO.getIndex() ||
                question.getQuType() == QuType.CHENFBK.getIndex() ||
                question.getQuType() == QuType.CHENCHECKBOX.getIndex() ||
                question.getQuType() == QuType.CHENSCORE.getIndex()) {
                List<ExamQuChenColumn> examQuChenColumnList = examQuChenColumnService.selectQuChenColumn(question.getId());
                List<ExamQuChenRow> examQuChenRowList = examQuChenRowService.selectQuChenRow(question.getId());
                List<ExamAnChenRadio> examAnChenRadioList = examAnChenRadioService.selectAnChenRadioByQuId(question.getId());
                List<ExamAnChenCheckbox> examAnCheckboxList = examAnChenCheckboxService.selectAnChenCheckboxByQuId(question.getId());
                question.setColumnTd(examQuChenColumnList);
                question.setChenRadioAn(examAnChenRadioList);
                question.setRowTd(examQuChenRowList);
                question.setChenCheckboxAn(examAnCheckboxList);
            }
        }
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
        List<Question> radioList = questionList.stream().filter(question -> question.getQuType().equals(QuType.RADIO.getIndex()))
            .collect(Collectors.toList());
        for (Question question : radioList) {
            question.setRadioTd(examQuRadioService.selectQuRadio(question.getId()));
            question.setRadioAn(examAnRadioService.selectByQuid(question.getId()));
        }
        List<Question> cheankboxList = questionList.stream().filter(question -> question.getQuType().equals(QuType.CHECKBOX.getIndex())).collect(Collectors.toList());
        for (Question question : cheankboxList) {
            question.setCheckboxTd(examQuCheckboxService.selectQuChenbox(question.getId()));
            question.setCheckboxAn(examAnCheckboxService.selectAnCheckBoxByQuId(question.getId()));
        }
        List<Question> scoreList = questionList.stream().filter(question -> question.getQuType().equals(QuType.SCORE.getIndex())).collect(Collectors.toList());
        for (Question question : scoreList) {
            question.setScoreTd(examQuScoreService.selectQuScore(question.getId()));
            question.setScoreAn(examAnScoreService.selectAnScoreByQuId(question.getId()));
        }
        List<Question> orderQuList = questionList.stream().filter(question -> question.getQuType().equals(QuType.ORDERQU.getIndex())).collect(Collectors.toList());
        for (Question question : orderQuList) {
            question.setOrderByTd(examQuOrderbyService.selectQuOrderby(question.getId()));
            question.setOrderByAn(examAnOrderService.selectAnOrderByQuId(question.getId()));
        }
        List<Question> multifillblankList = questionList.stream().filter(question -> question.getQuType().equals(QuType.MULTIFILLBLANK.getIndex())).collect(Collectors.toList());
        for (Question question : multifillblankList) {
            question.setMultifillblankTd(examQuMultiFillblankService.selectQuMultiFillblank(question.getId()));
            question.setDFillblankAn(examAnDfilllankService.selectAnMultiFillblankQuId(question.getId()));
        }
        List<Question> chenList = questionList.stream().filter(question ->
            question.getQuType().equals(QuType.CHENRADIO.getIndex()) ||
                question.getQuType().equals(QuType.CHENFBK.getIndex()) ||
                question.getQuType().equals(QuType.CHENCHECKBOX.getIndex()) ||
                question.getQuType().equals(QuType.CHENSCORE.getIndex())
        ).collect(Collectors.toList());
        for (Question question : chenList) {
            String questionId = question.getId();
            question.setColumnTd(examQuChenColumnService.selectQuChenColumn(questionId));
            question.setChenRadioAn(examAnChenRadioService.selectAnChenRadioByQuId(questionId));
            question.setChenFbkAn(examAnChenFbkService.selectByQuId(questionId));
            question.setChenScoreAn(examAnChenScoreService.selectByQuId(questionId));
            question.setCompChenRadioAn(examAnCompChenRadioService.selectByQuId(questionId));
            question.setRowTd(examQuChenRowService.selectQuChenRow(questionId));
            question.setChenCheckboxAn(examAnChenCheckboxService.selectAnChenCheckboxByQuId(questionId));
        }
        return questionList;
    }

    /**
     * 根据调查复制ID查询题目信息
     *
     * @param surveyCopyId 调查复制ID
     * @return 题目信息列表
     */
    @Override
    public List<Question> queryQuestionMationCopyById(String surveyCopyId) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getBelongId), surveyCopyId); // 等于归属ID
        return list(queryWrapper); // 返回查询结果列表
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
        List<Question> questionList = list(queryWrapper)
            .stream().map(item -> {
                //设置学校信息
                item.setSchoolMation(schoolService.selectById(item.getSchoolId()));
                //设置学院信息
                item.setFacultyMation(facultyService.selectById(item.getFacultyId()));
                //设置专业信息
                item.setMajorMation(majorService.selectById(item.getMajorId()));
                //设置科目信息
                item.setSubjectMation(subjectService.selectById(item.getSubjectId()));
                return item;
            }).collect(Collectors.toList());
        iAuthUserService.setName(questionList, "createId", "createName");
        iAuthUserService.setName(questionList, "lastUpdateId", "lastUpdateName");
        return questionList;
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
