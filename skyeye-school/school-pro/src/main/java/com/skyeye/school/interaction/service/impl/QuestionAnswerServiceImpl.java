package com.skyeye.school.interaction.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.school.interaction.dao.QuestionAnswerDao;
import com.skyeye.school.interaction.entity.QuestionAnswer;
import com.skyeye.school.interaction.entity.Questions;
import com.skyeye.school.interaction.service.QuestionAnswerService;
import com.skyeye.school.interaction.service.QuestionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: questionAnswerServiceImpl
 * @Description: 互动答题题目答案管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/17 10:46
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "互动答题题目答案管理", groupName = "互动答题题目答案管理")
public class QuestionAnswerServiceImpl extends SkyeyeBusinessServiceImpl<QuestionAnswerDao, QuestionAnswer> implements QuestionAnswerService {

    @Autowired
    private QuestionsService questionsService;

    @Autowired
    private QuestionAnswerService questionAnswerService;

    @Override
    public void validatorEntity(QuestionAnswer questionAnswer){
        super.validatorEntity(questionAnswer);
        String id = questionAnswer.getId();
        String questionId = questionAnswer.getQuestionId();
        Questions questions = questionsService.selectById(questionId);
        QuestionAnswer questionAnswer1 = questionAnswerService.selectById(id);
        QueryWrapper<QuestionAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(QuestionAnswer::getQuestionId), questionId);
        List<QuestionAnswer> list = questionAnswerService.list(queryWrapper);
        if(StrUtil.isEmpty(id)){//新增
            if (ObjectUtil.isNull(questions.getId())){
                throw new CustomException("该题目id不存在");
            }
        }else {//编辑
            if(ObjectUtil.isNull(questionAnswer1.getId())){
                throw new CustomException("该答案id不存在");
            }
            if(ObjectUtil.isNull(questions.getId())){
                throw new CustomException("题目id不存在");
            }
            if(CollectionUtil.isNotEmpty(list) && !list.get(0).getId().equals(id) ){
                throw new CustomException("题目答案已存在");
            }
        }
    }

    @Override
    public  void createPrepose(QuestionAnswer questionAnswer){
        String questionId = questionAnswer.getQuestionId();
        Questions questions = questionsService.selectById(questionId);
        questionAnswer.setQuestionText(questions.getQuestionText());
    }

    @Override
    public  void updatePrepose(QuestionAnswer questionAnswer){
        String id = questionAnswer.getId();
        String questionId = questionAnswer.getQuestionId();
        Questions questions = questionsService.selectById(questionId);
        String questionText = questions.getQuestionText();
        questionAnswer.setQuestionText(questionText);
        UpdateWrapper<QuestionAnswer> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set(MybatisPlusUtil.toColumns(QuestionAnswer::getQuestionId), questionId);
        questionAnswerService.update(updateWrapper);
    }

    @Override
    public void queryAnswerByQuestionId(InputObject inputObject, OutputObject outputObject) {
        String questionId = inputObject.getParams().get("questionId").toString();
        QueryWrapper<QuestionAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(QuestionAnswer::getQuestionId), questionId);
        List<QuestionAnswer> questionAnswers = list(queryWrapper);
        if (CollectionUtil.isEmpty(questionAnswers)) {
            throw new CustomException("该答案不存在");
        }
        iAuthUserService.setDataMation(questionAnswers,QuestionAnswer::getCreateId);
        outputObject.setBeans(questionAnswers);
        outputObject.settotal(questionAnswers.size());
    }
}
