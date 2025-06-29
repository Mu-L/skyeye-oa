package com.skyeye.school.lesson.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.school.lesson.dao.LessonReviewModelDao;
import com.skyeye.school.lesson.entity.LessonReviewModel;
import com.skyeye.school.lesson.service.LessonReviewModelService;
import org.springframework.stereotype.Service;

@Service
@SkyeyeService(name = "听评课模型管理", groupName = "听评课模型管理")
public class LessonReviewModelServiceImpl extends SkyeyeBusinessServiceImpl<LessonReviewModelDao, LessonReviewModel> implements LessonReviewModelService {

    @Override
    protected void createPrepose(LessonReviewModel entity) {
        noName(entity);
    }

    @Override
    protected void updatePrepose(LessonReviewModel entity) {
        noName(entity);
    }

    private void noName(LessonReviewModel entity) {
        String name = entity.getName();
        QueryWrapper<LessonReviewModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LessonReviewModel::getName), name);
        LessonReviewModel one = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(one) && !one.getId().equals(entity.getId())) {
            throw new RuntimeException("模型名称已存在");
        }
    }
}
