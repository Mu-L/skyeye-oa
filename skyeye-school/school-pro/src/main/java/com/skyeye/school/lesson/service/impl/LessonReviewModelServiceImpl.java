package com.skyeye.school.lesson.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.school.lesson.dao.LessonReviewModelDao;
import com.skyeye.school.lesson.entity.LessonReviewModel;
import com.skyeye.school.lesson.entity.LessonReviewType;
import com.skyeye.school.lesson.service.LessonReviewModelService;
import com.skyeye.school.lesson.service.LessonReviewTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SkyeyeService(name = "听评课模型管理", groupName = "听评课模型管理")
public class LessonReviewModelServiceImpl extends SkyeyeBusinessServiceImpl<LessonReviewModelDao, LessonReviewModel> implements LessonReviewModelService {

    @Autowired
    private LessonReviewTypeService lessonReviewTypeService;

    @Override
    protected void createPrepose(LessonReviewModel entity) {
        noName(entity);
    }

    @Override
    protected void updatePrepose(LessonReviewModel entity) {
        noName(entity);
    }

    @Override
    protected void updatePostpose(LessonReviewModel entity, String userId) {
        List<LessonReviewType> lessonReviewTypeList = entity.getLessonReviewTypeList();
        if (ObjectUtil.isNotEmpty(lessonReviewTypeList)) {
            lessonReviewTypeService.updateEntity(lessonReviewTypeList, userId);
        }
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

    @Override
    public LessonReviewModel queryByModelId(String modelId) {
        QueryWrapper<LessonReviewModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, modelId);
        return getOne(queryWrapper);
    }

    @Override
    public LessonReviewModel selectById(String id) {
        List<LessonReviewType> lessonReviewTypeList = lessonReviewTypeService.queryTypeByModelId(id);
        LessonReviewModel lessonReviewModel = super.selectById(id);
        lessonReviewModel.setLessonReviewTypeList(lessonReviewTypeList);
        return lessonReviewModel;
    }

    @Override
    protected void deletePreExecution(String id) {
        lessonReviewTypeService.deleteTypeByModelId(id);
    }
}
