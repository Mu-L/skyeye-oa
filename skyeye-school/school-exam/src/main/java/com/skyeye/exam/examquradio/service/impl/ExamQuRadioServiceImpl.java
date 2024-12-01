package com.skyeye.exam.examQuRadio.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.question.service.QuestionService;
import com.skyeye.exam.examQuRadio.dao.ExamQuRadioDao;
import com.skyeye.exam.examQuRadio.entity.ExamQuRadio;
import com.skyeye.exam.examQuRadio.service.ExamQuRadioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: ExamQuRadioServiceImpl
 * @Description: 单选题选项表管理服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "单选题选项表管理", groupName = "单选题选项表管理")
public class ExamQuRadioServiceImpl extends SkyeyeBusinessServiceImpl<ExamQuRadioDao, ExamQuRadio> implements ExamQuRadioService {

    @Autowired
    private QuestionService questionService;

//    @Override
//    public void createPrepose(ExamQuRadio entity) {
//        String userId = InputObject.getLogParamsStatic().get("id").toString();
//        entity.setQuType(QuType.RADIO.getIndex());
//        Question question = JSONUtil.toBean(JSONUtil.toJsonPrettyStr(entity), Question.class);
//        String quId = questionService.saveQuestion(question, StrUtil.EMPTY, userId);
//        entity.setQuId(quId);
//        List<ExamQuRadio> list = entity.getRadioTd();
//        if (list.isEmpty()){
//            saveList(list, quId, userId);
//        }
//    }

//    @Override
//    public void updatePrepose(ExamQuRadio entity) {
//        String userId = InputObject.getLogParamsStatic().get("id").toString();
//        Question question = JSONUtil.toBean(JSONUtil.toJsonPrettyStr(entity), Question.class);
//        String quId = questionService.saveQuestion(question, entity.getQuId(), userId);
//        List<ExamQuRadio> list = entity.getRadioTd();
//        if (list.isEmpty()) {
//            saveList(list, quId, userId);
//        }
//    }

    @Override
    public void saveList(List<ExamQuRadio> list, String quId, String userId) {
        List<ExamQuRadio> quRadio = new ArrayList<>();
        List<ExamQuRadio> editquRadio = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ExamQuRadio object = list.get(i);
            ExamQuRadio bean = new ExamQuRadio();
            bean.setOrderById(object.getOrderById());
            bean.setOptionName(object.getOptionName());
            bean.setIsNote(object.getIsNote());
            bean.setOptionTitle(object.getOptionTitle());
            bean.setIsDefaultAnswer(object.getIsDefaultAnswer());
            if (!ToolUtil.isBlank(object.getCheckType().toString())) {
                bean.setCheckType(object.getCheckType());
            } else {
                bean.setCheckType(object.getCheckType());
            }
            bean.setIsRequiredFill(object.getIsRequiredFill());
            if (ToolUtil.isBlank(object.getOptionId())) {
                bean.setQuId(object.getQuId());
                bean.setVisibility(1);
                bean.setId(ToolUtil.getSurFaceId());
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                quRadio.add(bean);
            } else {
                bean.setId(object.getOptionId());
                editquRadio.add(bean);
            }
        }
        if (!quRadio.isEmpty()) {
            createEntity(quRadio, userId);
        }
        if (!editquRadio.isEmpty()) {
            updateEntity(editquRadio, userId);
        }
    }


}
