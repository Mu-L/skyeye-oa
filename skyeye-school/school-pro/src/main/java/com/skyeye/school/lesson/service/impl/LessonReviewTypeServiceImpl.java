package com.skyeye.school.lesson.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.school.lesson.dao.LessonReviewTypeDao;
import com.skyeye.school.lesson.entity.LessonReviewType;
import com.skyeye.school.lesson.service.LessonReviewTypeService;
import org.springframework.stereotype.Service;

@Service
@SkyeyeService(name = "听评课角色管理", groupName = "听评课角色管理")
public class LessonReviewTypeServiceImpl extends SkyeyeBusinessServiceImpl<LessonReviewTypeDao, LessonReviewType> implements LessonReviewTypeService {
}
