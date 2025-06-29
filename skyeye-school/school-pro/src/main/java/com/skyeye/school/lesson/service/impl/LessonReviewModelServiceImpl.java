package com.skyeye.school.lesson.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.school.lesson.dao.LessonReviewModelDao;
import com.skyeye.school.lesson.entity.LessonReviewModel;
import com.skyeye.school.lesson.service.LessonReviewModelService;
import org.springframework.stereotype.Service;

@Service
@SkyeyeService(name = "听评课模型管理", groupName = "听评课模型管理")
public class LessonReviewModelServiceImpl extends SkyeyeBusinessServiceImpl<LessonReviewModelDao, LessonReviewModel> implements LessonReviewModelService {
}
