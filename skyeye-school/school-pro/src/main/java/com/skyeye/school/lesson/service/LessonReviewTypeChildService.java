package com.skyeye.school.lesson.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.school.lesson.entity.LessonReviewTypeChild;

import java.util.List;

public interface LessonReviewTypeChildService extends SkyeyeBusinessService<LessonReviewTypeChild> {
    void deleteReviewTypeChildByParentIdList(List<String> idList);
}
