package com.skyeye.school.lesson.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.school.lesson.entity.LessonReviewModel;

public interface LessonReviewModelService extends SkyeyeBusinessService<LessonReviewModel> {
    LessonReviewModel queryByModelId(String modelId);
}
