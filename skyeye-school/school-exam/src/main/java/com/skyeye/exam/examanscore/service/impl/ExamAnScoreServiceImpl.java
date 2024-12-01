package com.skyeye.exam.examanscore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examanscore.dao.ExamAnScoreDao;
import com.skyeye.exam.examanscore.entity.ExamAnScore;
import com.skyeye.exam.examanscore.service.ExamAnScoreService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "评分题保存表管理", groupName = "评分题保存表管理")
public class ExamAnScoreServiceImpl extends SkyeyeBusinessServiceImpl<ExamAnScoreDao, ExamAnScore> implements ExamAnScoreService {

    @Override
    public void queryExamAnScoreListById(InputObject inputObject, OutputObject outputObject) {
        String examAnScoreId = inputObject.getParams().get("id").toString();
        QueryWrapper<ExamAnScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID,examAnScoreId);
        List<ExamAnScore> examAnScoreList = list(queryWrapper);
        outputObject.setBean(examAnScoreList);
        outputObject.settotal(examAnScoreList.size());
    }
}
