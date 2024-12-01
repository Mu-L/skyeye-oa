package com.skyeye.exam.examQuScore.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.question.CheckType;
import com.skyeye.common.util.question.QuType;
import com.skyeye.eve.question.entity.Question;
import com.skyeye.eve.question.service.QuestionService;
import com.skyeye.exam.examQuRadio.entity.ExamQuRadio;
import com.skyeye.exam.examQuScore.dao.ExamQuScoreDao;
import com.skyeye.exam.examQuScore.entity.ExamQuScore;
import com.skyeye.exam.examQuScore.service.ExamQuScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: ExamQuScoreServiceImpl
 * @Description: 公评分题行选项管理服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "评分题行选项管理", groupName = "评分题行选项管理")
public class ExamQuScoreServiceImpl extends SkyeyeBusinessServiceImpl<ExamQuScoreDao, ExamQuScore> implements ExamQuScoreService {

    @Autowired
    private QuestionService questionService;

//    @Override
//    protected void createPrepose(ExamQuScore entity) {
//        String userId = InputObject.getLogParamsStatic().get("id").toString();
//        entity.setQuType(QuType.SCORE.getIndex());
//        Question question = JSONUtil.toBean(JSONUtil.toJsonPrettyStr(entity), Question.class);
//        String quId = questionService.saveQuestion(question, StrUtil.EMPTY, userId);
//        System.out.println(quId);
//        entity.setQuId(quId);
//        List<ExamQuScore> score = entity.getScoreTd();
//        saveList(score, quId, userId);
//    }
//
//    @Override
//    protected void updatePrepose(ExamQuScore entity) {
//        String userId = InputObject.getLogParamsStatic().get("id").toString();
//        Question question = JSONUtil.toBean(JSONUtil.toJsonPrettyStr(entity), Question.class);
//        String quId = questionService.saveQuestion(question, entity.getQuId(), userId);
//        List<ExamQuScore> score = entity.getScoreTd();
//        saveList(score, quId, userId);
//    }

    @Override
    public void saveList(List<ExamQuScore> score, String quId, String userId) {
        List<ExamQuScore> quScore = new ArrayList<>();
        List<ExamQuScore> editquScore = new ArrayList<>();
        for (int i = 0; i < score.size(); i++) {
            ExamQuScore object = score.get(i);
            ExamQuScore bean = new ExamQuScore();
            bean.setOrderById(object.getOrderById());
            bean.setOptionName(object.getOptionName());
            if (ToolUtil.isBlank(object.getOptionId())) {
                bean.setQuId(quId);
                bean.setVisibility(1);
                bean.setId(ToolUtil.getSurFaceId());
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                quScore.add(bean);
            } else {
                bean.setId(object.getOptionId());
                editquScore.add(bean);
            }
        }
        if (!quScore.isEmpty()) {
            createEntity(quScore, userId);
        }
        if (!editquScore.isEmpty()) {
            updateEntity(editquScore, userId);
        }
        quScore.addAll(editquScore);
    }
}
