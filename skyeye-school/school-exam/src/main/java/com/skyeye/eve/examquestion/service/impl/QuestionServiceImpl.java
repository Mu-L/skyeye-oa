/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.examquestion.service.impl;

import cn.hutool.core.util.ObjUtil;
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
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
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
import com.skyeye.exam.examanchenradio.entity.ExamAnChenRadio;
import com.skyeye.exam.examanchenradio.service.ExamAnChenRadioService;
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
        if (ObjUtil.isNotEmpty(radioTd)) {
            examQuRadioService.saveList(radioTd, quId, userId);
        }
        // 处理得分题
        List<ExamQuScore> ScoreTd = entity.getScoreTd();
        if (ObjUtil.isNotEmpty(ScoreTd)) {
            examquScoreService.saveList(ScoreTd, quId, userId);
        }
        // 处理多选题
        List<ExamQuCheckbox> checkboxTd = entity.getCheckboxTd();
        if (ObjUtil.isNotEmpty(checkboxTd)) {
            examQuCheckboxService.saveList(checkboxTd, quId, userId);
        }
        // 处理多空填空题
        List<ExamQuMultiFillblank> multiFillblankTd = entity.getMultifillblankTd();
        if (ObjUtil.isNotEmpty(multiFillblankTd)) {
            examQuMultiFillblankService.saveList(multiFillblankTd, quId, userId);
        }
        // 处理排序题
        List<ExamQuOrderby> orderbyTd = entity.getOrderbyTd();
        if (ObjUtil.isNotEmpty(orderbyTd)) {
            examQuOrderbyService.saveList(orderbyTd, quId, userId);
        }
        // 处理矩阵题
        List<ExamQuChenColumn> columnTd = entity.getColumnTd();
        List<ExamQuChenRow> rowTd = entity.getRowTd();
        if (ObjUtil.isNotEmpty(columnTd) && ObjUtil.isNotEmpty(rowTd)) {
            examQuChenColumnService.saveList(columnTd, rowTd, quId, userId);
        }
    }

    /**
     * 更新题目前的前置处理
     *
     * @param entity 题目实体对象
     */
    @Override
    public void updatePrepose(Question entity) {
        // 获取当前登录用户ID
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        // 更新不同题目类型的数据
        // 更新单选题
        List<ExamQuRadio> radioTd = entity.getRadioTd();
        String quId = entity.getId();
        if (ObjUtil.isNotEmpty(radioTd)) {
            examQuRadioService.saveList(radioTd, quId, userId);
        }
        // 更新得分题
        List<ExamQuScore> scoreTd = entity.getScoreTd();
        if (ObjUtil.isNotEmpty(scoreTd)) {
            examquScoreService.saveList(scoreTd, quId, userId);
        }
        // 更新多选题
        List<ExamQuCheckbox> checkboxTd = entity.getCheckboxTd();
        if (ObjUtil.isNotEmpty(checkboxTd)) {
            examQuCheckboxService.saveList(checkboxTd, quId, userId);
        }
        // 更新多空填空题
        List<ExamQuMultiFillblank> multiFillblankTd = entity.getMultifillblankTd();
        if (ObjUtil.isNotEmpty(multiFillblankTd)) {
            examQuMultiFillblankService.saveList(multiFillblankTd, quId, userId);
        }
        // 更新排序题
        List<ExamQuOrderby> orderbyTd = entity.getOrderbyTd();
        if (ObjUtil.isNotEmpty(orderbyTd)) {
            examQuOrderbyService.saveList(orderbyTd, quId, userId);
        }
        // 更新陈列题
        List<ExamQuChenColumn> columnTd = entity.getColumnTd();
        List<ExamQuChenRow> rowTd = entity.getRowTd();
        if (ObjUtil.isNotEmpty(columnTd) && ObjUtil.isNotEmpty(rowTd)) {
            examQuChenColumnService.saveList(columnTd, rowTd, quId, userId);
        }
    }

    @Override
    public void queryMyQuestionList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Question::getCreateTime));
        List<Question> questionList = getBaseInfo(queryWrapper);
        outputObject.setBeans(questionList);
        outputObject.settotal(page.getTotal());

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
        question.setOrderbyTd(examQuOrderbyList);
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
            // 1 单选题
            if (question.getQuType() == QuType.RADIO.getIndex()) {
                List<ExamQuRadio> radioList = examQuRadioService.selectQuRadio(question.getId());
                ExamAnRadio examAnRadio = examAnRadioService.selectById(question.getId());
                question.setRadioTd(radioList);
                question.setRadioAn(examAnRadio);
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
                question.setOrderbyTd(orderbyList);
                question.setOrderbyAn(examAnOrderbyList);
                continue;
            }
            // 11 矩阵单选题CHENRADIO 12 矩阵填空题CHENFBK 13 矩阵多选题CHENCHECKBOX 18 矩阵评分题CHENSCORE
            if (question.getQuType() == QuType.CHENRADIO.getIndex() ||
                    question.getQuType() == QuType.CHENFBK.getIndex() ||
                    question.getQuType() == QuType.CHENCHECKBOX.getIndex() ||
                    question.getQuType() == QuType.CHENSCORE.getIndex()) {
                List<ExamQuChenColumn> examQuChenColumnList = examQuChenColumnService.selectQuChenColumn(question.getId());
                List<ExamQuChenRow> examQuChenRowList = examQuChenRowService.selectQuChenRow(question.getId());
                ExamAnChenRadio examAnChenRadio = examAnChenRadioService.selectById(question.getId());
                List<ExamAnChenCheckbox> examAnCheckboxList = examAnChenCheckboxService.selectAnChenCheckboxByQuId(question.getId());
                question.setColumnTd(examQuChenColumnList);
                question.setChenAn(examAnChenRadio);
                question.setRowTd(examQuChenRowList);
                question.setChenRowAn(examAnCheckboxList);

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
        return list(queryWrapper);
    }

    /**
     * 复制题目信息的方法
     *
     * @param question 题目实体对象，包含要复制的题目信息以及复制后题目的ID
     */
    @Override
    public void copyQuestionListMation(Question question) {
        // 根据题目类型进行不同的复制操作
        String quType = QuType.getActionName(Integer.parseInt(question.getQuType().toString()));
        // 复制单选题或复合单选题
        if (quType.equals(QuType.RADIO.getActionName()) || quType.equals(QuType.COMPRADIO.getActionName())) {
            List<ExamQuRadio> examQuRadioList = examQuRadioService.selectQuRadio(question.getCopyFromId());
            if (CollectionUtils.isEmpty(examQuRadioList)) {
                throw new CustomException("没有找到题目选项信息");
            }
            for (ExamQuRadio examQuRadio : examQuRadioList) {
                examQuRadio.setId(ToolUtil.getSurFaceId()); // 设置新的唯一ID
                examQuRadio.setCreateTime(DateUtil.getTimeAndToString()); // 设置创建时间
                examQuRadio.setQuId(question.getId()); // 设置所属题目ID
                examQuRadioService.createEntity(examQuRadio, StrUtil.EMPTY);
            }
        }
        // 复制多选题或复合多选题
        else if (quType.equals(QuType.CHECKBOX.getActionName()) || quType.equals(QuType.COMPCHECKBOX.getActionName())) {
            List<ExamQuCheckbox> examQuCheckboxList = examQuCheckboxService.selectQuChenbox(question.getCopyFromId());
            if (CollectionUtils.isEmpty(examQuCheckboxList)) {
                throw new CustomException("没有找到题目选项信息");
            }
            for (ExamQuCheckbox examQuCheckbox : examQuCheckboxList) {
                examQuCheckbox.setId(ToolUtil.getSurFaceId());
                examQuCheckbox.setCreateTime(DateUtil.getTimeAndToString());
                examQuCheckbox.setQuId(question.getId());
                examQuCheckboxService.createEntity(examQuCheckbox, StrUtil.EMPTY);
            }
        }
        // 复制多空填空题
        else if (quType.equals(QuType.MULTIFILLBLANK.getActionName())) {
            List<ExamQuMultiFillblank> multiFillblanksList = examQuMultiFillblankService.selectQuMultiFillblank(question.getCopyFromId());
            if (CollectionUtils.isEmpty(multiFillblanksList)) {
                throw new CustomException("没有找到题目选项信息");
            }
            for (ExamQuMultiFillblank examQuMultiFillblank : multiFillblanksList) {
                examQuMultiFillblank.setId(ToolUtil.getSurFaceId());
                examQuMultiFillblank.setCreateTime(DateUtil.getTimeAndToString());
                examQuMultiFillblank.setQuId(question.getId());
                examQuMultiFillblankService.createEntity(examQuMultiFillblank, StrUtil.EMPTY);
            }
        }
        // 复制陈列题相关数据
        else if (quType.equals(QuType.CHENRADIO.getActionName()) || quType.equals(QuType.CHENCHECKBOX.getActionName()) ||
                quType.equals(QuType.CHENSCORE.getActionName()) || quType.equals(QuType.CHENFBK.getActionName()) ||
                quType.equals(QuType.COMPCHENRADIO.getActionName())) {
            List<ExamQuChenRow> examQuChenRowList = examQuChenRowService.selectQuChenRow(question.getCopyFromId());
            List<ExamQuChenColumn> examQuChenColumnList = examQuChenColumnService.selectQuChenColumn(question.getCopyFromId());
            if (CollectionUtils.isEmpty(examQuChenRowList) || CollectionUtils.isEmpty(examQuChenColumnList)) {
                throw new CustomException("没有找到题目选项信息");
            }
            for (ExamQuChenRow examQuChenRow : examQuChenRowList) {
                examQuChenRow.setId(ToolUtil.getSurFaceId());
                examQuChenRow.setCreateTime(DateUtil.getTimeAndToString());
                examQuChenRow.setQuId(question.getId());
                examQuChenRowService.createEntity(examQuChenRow, StrUtil.EMPTY);
            }
            for (ExamQuChenColumn examQuChenColumn : examQuChenColumnList) {
                examQuChenColumn.setId(ToolUtil.getSurFaceId());
                examQuChenColumn.setCreateTime(DateUtil.getTimeAndToString());
                examQuChenColumn.setQuId(question.getId());
                examQuChenColumnService.createEntity(examQuChenColumn, StrUtil.EMPTY);
            }
        }
        // 复制得分题
        else if (quType.equals(QuType.SCORE.getActionName())) {
            List<ExamQuScore> examQuScoreList = examQuScoreService.selectQuScore(question.getCopyFromId());
            for (ExamQuScore examQuScore : examQuScoreList) {
                examQuScore.setId(ToolUtil.getSurFaceId());
                examQuScore.setCreateTime(DateUtil.getTimeAndToString());
                examQuScore.setQuId(question.getId());
                examQuScoreService.createEntity(examQuScore, StrUtil.EMPTY);
            }
        }
        // 复制排序题
        else if (quType.equals(QuType.ORDERQU.getActionName())) {
            List<ExamQuOrderby> examQuOrderbyList = examQuOrderbyService.selectQuOrderby(question.getCopyFromId());
            for (ExamQuOrderby examQuOrderby : examQuOrderbyList) {
                examQuOrderby.setId(ToolUtil.getSurFaceId());
                examQuOrderby.setCreateTime(DateUtil.getTimeAndToString());
                examQuOrderby.setQuId(question.getId());
                examQuOrderbyService.createEntity(examQuOrderby, StrUtil.EMPTY);
            }
        }
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
//        queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getTag), CommonNumConstants.NUM_TWO); // 等于标签2
//        queryWrapper.ne(MybatisPlusUtil.toColumns(Question::getQuTag), CommonNumConstants.NUM_TWO); // 不等于题目标签2
//        queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getVisibility), CommonNumConstants.NUM_ONE); // 等于可见性1
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
                    question.setOrderbyTd(orderbyList);
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
