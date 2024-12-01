package com.skyeye.exam.examSurveyAnswer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examSurveyAnswer.dao.ExamSurveyAnswerDao;
import com.skyeye.exam.examSurveyAnswer.entity.ExamSurveyAnswer;
import com.skyeye.exam.examSurveyAnswer.service.ExamSurveyAnswerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ExamSurveyAnswerServiceImpl
 * @Description: 试卷回答信息表管理服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "试卷回答信息表管理", groupName = "试卷回答信息表管理")
public class ExamSurveyAnswerServiceImpl extends SkyeyeBusinessServiceImpl<ExamSurveyAnswerDao, ExamSurveyAnswer> implements ExamSurveyAnswerService {
    @Override
    public void queryMySurveyAnswerList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String createId = (String) map.get("createId");
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("create_id", createId);
        List<ExamSurveyAnswer> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }
}
