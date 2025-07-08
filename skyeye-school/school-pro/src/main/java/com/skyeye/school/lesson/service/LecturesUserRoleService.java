package com.skyeye.school.lesson.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.school.lesson.entity.LecturesUserRole;

/**
 * @ClassName: LecturesUserRoleService
 * @Description: 质评用户角色关联接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:55
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的F
 */
public interface LecturesUserRoleService extends SkyeyeBusinessService<LecturesUserRole> {
    void deleteByRoleId(String id);
}
