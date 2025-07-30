package com.skyeye.school.lesson.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.school.lesson.dao.LessonReviewTypeChildDao;
import com.skyeye.school.lesson.entity.LessonReviewTypeChild;
import com.skyeye.school.lesson.service.LessonReviewTypeChildService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SkyeyeService(name = "听评课模型子类管理", groupName = "听评课模型管理")
public class LessonReviewTypeChildServiceImpl extends SkyeyeBusinessServiceImpl<LessonReviewTypeChildDao, LessonReviewTypeChild> implements LessonReviewTypeChildService {

    @Override
    public void deleteReviewTypeChildByParentIdList(List<String> idList) {
        if (CollectionUtil.isEmpty(idList)) {
            return;
        }
        QueryWrapper<LessonReviewTypeChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(LessonReviewTypeChild::getParentId), idList);
        remove(queryWrapper);
    }
}

