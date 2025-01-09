package com.skyeye.exam.examananswer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exam.examananswer.dao.ExamAnAnswerDao;
import com.skyeye.exam.examananswer.entity.ExamAnAnswer;
import com.skyeye.exam.examananswer.service.ExamAnAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ExamAnAnswerServiceImpl
 * @Description: 答卷/问答题保存表管理服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Service
@SkyeyeService(name = "答卷/问答题保存表管理", groupName = "答卷/问答题保存表管理")
public class ExamAnAnswerServiceImpl extends SkyeyeBusinessServiceImpl<ExamAnAnswerDao, ExamAnAnswer> implements ExamAnAnswerService {

    @Autowired
    private ExamAnAnswerService examAnAnswerService;

    @Override
    public void queryExamAnAnswerListById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<ExamAnAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<ExamAnAnswer> examAnAnswerList = list(queryWrapper);
        outputObject.setBean(examAnAnswerList);
        outputObject.settotal(examAnAnswerList.size());
    }

    @Override
    public List<ExamAnAnswer> selectBySurveyId(String surveyId) {
        QueryWrapper<ExamAnAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnAnswer::getBelongId), surveyId);
        return list(queryWrapper);
    }
}
