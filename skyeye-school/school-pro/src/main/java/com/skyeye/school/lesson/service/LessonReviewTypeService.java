package com.skyeye.school.lesson.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.school.lesson.entity.LessonReviewType;

import java.util.List;

public interface LessonReviewTypeService extends SkyeyeBusinessService<LessonReviewType> {
    void deleteTypeByModelId(String id);

    List<LessonReviewType> queryTypeByModelId(String id);

}
