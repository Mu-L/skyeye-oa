/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.Examquestion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.DateUtil;
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
import com.skyeye.exam.examqumultfillblank.entity.ExamQuMultiFillblank;
import com.skyeye.exam.examqumultfillblank.service.ExamQuMultiFillblankService;
import com.skyeye.exam.examquorderby.entity.ExamQuOrderby;
import com.skyeye.exam.examquorderby.service.ExamQuOrderbyService;
import com.skyeye.exam.examquradio.entity.ExamQuRadio;
import com.skyeye.exam.examquradio.service.ExamQuRadioService;
import com.skyeye.exam.examquscore.entity.ExamQuScore;
import com.skyeye.exam.examquscore.service.ExamQuScoreService;
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

    @Override
    public void createPostpose(Question entity, String userId) {
        entity.setQuTag(1);
        entity.setVisibility(1);
        Integer fileType = entity.getFileType() != null ? entity.getFileType() : 0;
        entity.setFileType(fileType);
        Integer whetherUpload = entity.getWhetherUpload() != null ? entity.getWhetherUpload() : 2;
        entity.setWhetherUpload(whetherUpload);
        entity.setCreateTime(DateUtil.getTimeAndToString());
        String quId = entity.getId();
        entity.setCreateTime(DateUtil.getTimeAndToString());
        List<ExamQuRadio> radioTd = entity.getRadioTd();
        if (!radioTd.isEmpty()) {
            entity.setQuType(QuType.RADIO.getIndex());
            examQuRadioService.saveList(radioTd, quId, userId);
        }
        List<ExamQuScore> ScoreTd = entity.getScoreTd();
        if (!ScoreTd.isEmpty()) {
            entity.setQuType(QuType.SCORE.getIndex());
            quScoreService.saveList(ScoreTd, quId, userId);
        }
        List<ExamQuCheckbox> checkboxTd = entity.getCheckboxTd();
        if (!checkboxTd.isEmpty()) {
            entity.setQuType(QuType.CHECKBOX.getIndex());
            examQuCheckboxService.saveList(checkboxTd, quId, userId);
        }
        List<ExamQuMultiFillblank> multiFillblankTd = entity.getMultifillblankTd();
        if (!multiFillblankTd.isEmpty()) {
            entity.setQuType(QuType.MULTIFILLBLANK.getIndex());
            examQuMultiFillblankService.saveList(multiFillblankTd, quId, userId);
        }
        List<ExamQuOrderby> orderbyTd = entity.getOrderbyTd();
        if (!orderbyTd.isEmpty()) {
            entity.setQuType(QuType.ORDERQU.getIndex());
            examQuOrderbyService.saveList(orderbyTd, quId, userId);
        }
        List<ExamQuChenColumn> columnTd = entity.getColumnTd();
        List<ExamQuChenRow> rowTd = entity.getRowTd();
        if (!columnTd.isEmpty() && !rowTd.isEmpty()) {
            examQuChenColumnService.saveList(columnTd, rowTd, quId, userId);
        }
    }

    @Override
    public void updatePrepose(Question entity) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        List<ExamQuRadio> radioTd = entity.getRadioTd();
        String quId = entity.getId();
        if (!radioTd.isEmpty()) {
            examQuRadioService.saveList(radioTd, quId, userId);
        }
        List<ExamQuScore> ScoreTd = entity.getScoreTd();
        if (!ScoreTd.isEmpty()) {
            quScoreService.saveList(ScoreTd, quId, userId);
        }
        List<ExamQuCheckbox> checkboxTd = entity.getCheckboxTd();
        if (!checkboxTd.isEmpty()) {
            examQuCheckboxService.saveList(checkboxTd, quId, userId);
        }
        List<ExamQuMultiFillblank> multiFillblankTd = entity.getMultifillblankTd();
        if (!multiFillblankTd.isEmpty()) {
            examQuMultiFillblankService.saveList(multiFillblankTd, quId, userId);
        }
        List<ExamQuOrderby> orderbyTd = entity.getOrderbyTd();
        if (!orderbyTd.isEmpty()) {
            examQuOrderbyService.saveList(orderbyTd, quId, userId);
        }
        List<ExamQuChenColumn> columnTd = entity.getColumnTd();
        List<ExamQuChenRow> rowTd = entity.getRowTd();
        if (!columnTd.isEmpty() && !rowTd.isEmpty()) {
            examQuChenColumnService.saveList(columnTd, rowTd, quId, userId);
        }
    }

    @Override
    public QueryWrapper<Question> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Question> queryWrapper = super.getQueryWrapper(commonPageInfo);
        // 我创建的
        queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        return queryWrapper;
    }

    @Override
    public void deletePreExecution(Question entity) {
        String quId = entity.getId();
        Integer quType = entity.getQuType();
        if (quType == QuType.RADIO.getIndex()) {
            examQuRadioService.removeByQuId(quId);
        } else if (quType == QuType.MULTIFILLBLANK.getIndex()) {
            examQuMultiFillblankService.removeByQuId(quId);
        } else if (quType == QuType.CHECKBOX.getIndex()) {
            examQuCheckboxService.removeByQuId(quId);
        } else if (quType == QuType.ORDERQU.getIndex()) {
            examQuOrderbyService.removeByQuId(quId);
        } else if (quType == QuType.CHENRADIO.getIndex() ||
            quType == QuType.CHENFBK.getIndex() ||
            quType == QuType.CHENCHECKBOX.getIndex() ||
            quType == QuType.COMPCHENRADIO.getIndex()) {
            examQuChenColumnService.removeByQuId(quId);
        }
    }

    @Override
    public List<Question> QueryQuestionByBelongId(String belongId) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getBelongId), belongId);
        List<Question> questionList = list(queryWrapper);
        return questionList;

    }
}
