package com.skyeye.eve.answer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.answer.dao.DwAnAnswerDao;
import com.skyeye.eve.answer.entity.DwAnAnswer;
import com.skyeye.eve.answer.service.DwAnAnswerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SkyeyeService(name = "答卷/问答题保存表管理", groupName = "答卷/问答题保存表管理")
public class DwAnAnswerServiceImpl extends SkyeyeBusinessServiceImpl<DwAnAnswerDao, DwAnAnswer> implements DwAnAnswerService {
    @Override
    public List<DwAnAnswer> selectBySurveyId(String surveyId) {
        QueryWrapper<DwAnAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnAnswer::getBelongId), surveyId);
        return list(queryWrapper);
    }
}
