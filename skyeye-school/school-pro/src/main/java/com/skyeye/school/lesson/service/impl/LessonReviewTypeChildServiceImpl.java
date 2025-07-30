package com.skyeye.school.lesson.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.school.lesson.dao.LessonReviewTypeChildDao;
import com.skyeye.school.lesson.entity.LessonReviewTypeChild;
import com.skyeye.school.lesson.service.LessonReviewTypeChildService;
import org.springframework.stereotype.Service;

@Service
@SkyeyeService(name = "听评课模型子类管理", groupName = "听评课模型管理")
public class LessonReviewTypeChildServiceImpl extends SkyeyeBusinessServiceImpl<LessonReviewTypeChildDao, LessonReviewTypeChild> implements LessonReviewTypeChildService {
}
