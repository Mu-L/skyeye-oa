/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.faculty.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.faculty.entity.Faculty;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: FacultyService
 * @Description: 院系管理服务接口层
 * @author: xqz
 * @date: 2023/8/8 15:00
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface FacultyService extends SkyeyeBusinessService<Faculty> {

    void queryFacultyListBySchoolId(InputObject inputObject, OutputObject outputObject);

    Map<String, List<Faculty>> selectByIdList(List<String> facultyIds);
}
