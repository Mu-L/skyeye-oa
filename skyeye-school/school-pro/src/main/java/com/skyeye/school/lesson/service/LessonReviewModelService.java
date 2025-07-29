package com.skyeye.school.lesson.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.lesson.entity.LessonReviewModel;

public interface LessonReviewModelService extends SkyeyeBusinessService<LessonReviewModel> {
    LessonReviewModel queryByModelId(String modelId);

    void queryLessonReviewModel(InputObject inputObject, OutputObject outputObject);
}
