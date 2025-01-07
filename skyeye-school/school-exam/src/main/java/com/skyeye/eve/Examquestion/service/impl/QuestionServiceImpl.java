/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.Examquestion.service.impl;

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
import com.skyeye.eve.Examquestion.dao.QuestionDao;
import com.skyeye.eve.Examquestion.entity.Question;
import com.skyeye.eve.Examquestion.service.QuestionService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private ExamQuScoreService quScoreService;

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
        // 处理题目逻辑
        List<ExamQuestionLogic> questionLogic = entity.getQuestionLogic();
        if (!questionLogic.isEmpty()) {
            examQuestionLogicService.setLogics(quId, questionLogic, userId);
        } else {
            throw new CustomException("请设置问题逻辑");
        }
        // 根据不同的题目类型，保存对应的题目数据
        // 处理单选题
        List<ExamQuRadio> radioTd = entity.getRadioTd();
        if (!radioTd.isEmpty()) {
            examQuRadioService.saveList(radioTd, quId, userId);
        }
        // 处理得分题
        List<ExamQuScore> ScoreTd = entity.getScoreTd();
        if (!ScoreTd.isEmpty()) {
            quScoreService.saveList(ScoreTd, quId, userId);
        }
        // 处理多选题
        List<ExamQuCheckbox> checkboxTd = entity.getCheckboxTd();
        if (!checkboxTd.isEmpty()) {
            examQuCheckboxService.saveList(checkboxTd, quId, userId);
        }
        // 处理多空填空题
        List<ExamQuMultiFillblank> multiFillblankTd = entity.getMultifillblankTd();
        if (!multiFillblankTd.isEmpty()) {
            examQuMultiFillblankService.saveList(multiFillblankTd, quId, userId);
        }
        // 处理排序题
        List<ExamQuOrderby> orderbyTd = entity.getOrderbyTd();
        if (!orderbyTd.isEmpty()) {
            examQuOrderbyService.saveList(orderbyTd, quId, userId);
        }
        // 处理矩阵题
        List<ExamQuChenColumn> columnTd = entity.getColumnTd();
        List<ExamQuChenRow> rowTd = entity.getRowTd();
        if (!columnTd.isEmpty() && !rowTd.isEmpty()) {
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
        if (!radioTd.isEmpty()) {
            examQuRadioService.saveList(radioTd, quId, userId);
        }
        // 更新得分题
        List<ExamQuScore> scoreTd = entity.getScoreTd();
        if (!scoreTd.isEmpty()) {
            quScoreService.saveList(scoreTd, quId, userId);
        }
        // 更新多选题
        List<ExamQuCheckbox> checkboxTd = entity.getCheckboxTd();
        if (!checkboxTd.isEmpty()) {
            examQuCheckboxService.saveList(checkboxTd, quId, userId);
        }
        // 更新多空填空题
        List<ExamQuMultiFillblank> multiFillblankTd = entity.getMultifillblankTd();
        if (!multiFillblankTd.isEmpty()) {
            examQuMultiFillblankService.saveList(multiFillblankTd, quId, userId);
        }
        // 更新排序题
        List<ExamQuOrderby> orderbyTd = entity.getOrderbyTd();
        if (!orderbyTd.isEmpty()) {
            examQuOrderbyService.saveList(orderbyTd, quId, userId);
        }
        // 更新陈列题
        List<ExamQuChenColumn> columnTd = entity.getColumnTd();
        List<ExamQuChenRow> rowTd = entity.getRowTd();
        if (!columnTd.isEmpty() && !rowTd.isEmpty()) {
            examQuChenColumnService.saveList(columnTd, rowTd, quId, userId);
        }
    }

    /**
     * 构建查询题目的包装器
     *
     * @param commonPageInfo 分页信息
     * @return 题目查询包装器
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Question> queryWrapper = super.getQueryWrapper(commonPageInfo);
        // 只查询当前用户创建的题目
        queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        return queryWrapper;
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
            for (ExamQuRadio examQuRadio : examQuRadioList) {
                examQuRadio.setId(ToolUtil.getSurFaceId()); // 设置新的唯一ID
                examQuRadio.setCreateTime(DateUtil.getTimeAndToString()); // 设置创建时间
                examQuRadio.setQuId(question.getId()); // 设置所属题目ID
            }
            if (!examQuRadioList.isEmpty()) {
                examQuRadioService.createEntity(examQuRadioList, StrUtil.EMPTY); // 创建实体
            }
        }
        // 复制多选题或复合多选题
        else if (quType.equals(QuType.CHECKBOX.getActionName()) || quType.equals(QuType.COMPCHECKBOX.getActionName())) {
            List<ExamQuCheckbox> examQuCheckboxList = examQuCheckboxService.selectQuChenbox(question.getCopyFromId());
            for (ExamQuCheckbox examQuCheckbox : examQuCheckboxList) {
                examQuCheckbox.setId(ToolUtil.getSurFaceId());
                examQuCheckbox.setCreateTime(DateUtil.getTimeAndToString());
                examQuCheckbox.setQuId(question.getId());
            }
            if (!examQuCheckboxList.isEmpty()) {
                examQuCheckboxService.createEntity(examQuCheckboxList, StrUtil.EMPTY);
            }
        }
        // 复制多空填空题
        else if (quType.equals(QuType.MULTIFILLBLANK.getActionName())) {
            List<ExamQuMultiFillblank> multiFillblanksList = examQuMultiFillblankService.selectQuMultiFillblank(question.getCopyFromId());
            for (ExamQuMultiFillblank examQuMultiFillblank : multiFillblanksList) {
                examQuMultiFillblank.setId(ToolUtil.getSurFaceId());
                examQuMultiFillblank.setCreateTime(DateUtil.getTimeAndToString());
                examQuMultiFillblank.setQuId(question.getId());
            }
            if (!multiFillblanksList.isEmpty()) {
                examQuMultiFillblankService.createEntity(multiFillblanksList, StrUtil.EMPTY);
            }
        }
        // 复制陈列题相关数据
        else if (quType.equals(QuType.CHENRADIO.getActionName()) || quType.equals(QuType.CHENCHECKBOX.getActionName()) ||
                quType.equals(QuType.CHENSCORE.getActionName()) || quType.equals(QuType.CHENFBK.getActionName()) ||
                quType.equals(QuType.COMPCHENRADIO.getActionName())) {
            List<ExamQuChenRow> examQuChenRowList = examQuChenRowService.selectQuChenRow(question.getCopyFromId());
            List<ExamQuChenColumn> examQuChenColumnList = examQuChenColumnService.selectQuChenColumn(question.getCopyFromId());
            for (ExamQuChenRow examQuChenRow : examQuChenRowList) {
                examQuChenRow.setId(ToolUtil.getSurFaceId());
                examQuChenRow.setCreateTime(DateUtil.getTimeAndToString());
                examQuChenRow.setQuId(question.getId());
            }
            if (!examQuChenRowList.isEmpty()) {
                examQuChenRowService.createEntity(examQuChenRowList, StrUtil.EMPTY);
            }
            for (ExamQuChenColumn examQuChenColumn : examQuChenColumnList) {
                examQuChenColumn.setId(ToolUtil.getSurFaceId());
                examQuChenColumn.setCreateTime(DateUtil.getTimeAndToString());
                examQuChenColumn.setQuId(question.getId());
            }
            if (!examQuChenColumnList.isEmpty()) {
                examQuChenColumnService.createEntity(examQuChenColumnList, StrUtil.EMPTY);
            }
        }
        // 复制得分题
        else if (quType.equals(QuType.SCORE.getActionName())) {
            List<ExamQuScore> examQuScoreList = examQuScoreService.selectQuScore(question.getCopyFromId());
            for (ExamQuScore examQuScore : examQuScoreList) {
                examQuScore.setId(ToolUtil.getSurFaceId());
                examQuScore.setCreateTime(DateUtil.getTimeAndToString());
                examQuScore.setQuId(question.getId());
            }
            if (!examQuScoreList.isEmpty()) {
                examQuScoreService.createEntity(examQuScoreList, StrUtil.EMPTY);
            }
        }
        // 复制排序题
        else if (quType.equals(QuType.ORDERQU.getActionName())) {
            List<ExamQuOrderby> examQuOrderbyList = examQuOrderbyService.selectQuOrderby(question.getCopyFromId());
            for (ExamQuOrderby examQuOrderby : examQuOrderbyList) {
                examQuOrderby.setId(ToolUtil.getSurFaceId());
                examQuOrderby.setCreateTime(DateUtil.getTimeAndToString());
                examQuOrderby.setQuId(question.getId());
            }
            if (!examQuOrderbyList.isEmpty()) {
                examQuOrderbyService.createEntity(examQuOrderbyList, StrUtil.EMPTY);
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
        queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getTag), CommonNumConstants.NUM_TWO); // 等于标签2
        queryWrapper.ne(MybatisPlusUtil.toColumns(Question::getQuTag), CommonNumConstants.NUM_TWO); // 不等于题目标签2
        queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getVisibility), CommonNumConstants.NUM_ONE); // 等于可见性1
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

}
