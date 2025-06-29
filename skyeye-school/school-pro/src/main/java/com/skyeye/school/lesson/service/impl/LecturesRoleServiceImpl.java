package com.skyeye.school.lesson.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.school.lesson.dao.LecturesRoleDao;
import com.skyeye.school.lesson.entity.LecturesRole;
import com.skyeye.school.lesson.service.LecturesRoleService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: LecturesRoleServiceImpl
 * @Description: 听评课角色管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 15:28
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "听评课角色管理", groupName = "听评课角色管理")
public class LecturesRoleServiceImpl extends SkyeyeBusinessServiceImpl<LecturesRoleDao, LecturesRole> implements LecturesRoleService {
}
