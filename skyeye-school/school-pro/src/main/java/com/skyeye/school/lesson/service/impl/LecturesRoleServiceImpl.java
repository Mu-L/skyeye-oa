package com.skyeye.school.lesson.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.school.lesson.dao.LecturesRoleDao;
import com.skyeye.school.lesson.entity.LecturesRole;
import com.skyeye.school.lesson.service.LecturesRoleService;
import org.springframework.stereotype.Service;

@Service
@SkyeyeService(name = "听评课角色管理", groupName = "听评课角色管理")
public class LecturesRoleServiceImpl extends SkyeyeBusinessServiceImpl<LecturesRoleDao, LecturesRole> implements LecturesRoleService {
}
