package com.skyeye.exam.examanquestion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examanfillblank.entity.ExamAnFillblank;
import com.skyeye.exam.examanquestion.dao.ExamAnQuestionDao;
import com.skyeye.exam.examanquestion.entity.ExamAnQuestion;
import com.skyeye.exam.examanquestion.service.ExamAnQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ExamAnQuestionServiceImpl
 * @Description: 答卷 试题答案相关信息表服务接口层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Service
@SkyeyeService(name = "答卷 试题答案相关信息表", groupName = "答卷 试题答案相关信息表")
public class ExamAnQuestionServiceImpl extends SkyeyeBusinessServiceImpl<ExamAnQuestionDao, ExamAnQuestion> implements ExamAnQuestionService{

    @Override
    public void queryExamAnQuestionById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<ExamAnQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<ExamAnQuestion> examAnQuestionList = list(queryWrapper);
        outputObject.setBean(examAnQuestionList);
        outputObject.settotal(examAnQuestionList.size());
    }
}
