/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.courseware.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.courseware.entity.Courseware;

import java.util.List;

/**
 * @ClassName: CoursewareService
 * @Description: 互动课件服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/2 9:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface CoursewareService extends SkyeyeBusinessService<Courseware> {

    void queryCoursewareListBySubjectId(InputObject inputObject, OutputObject outputObject);

    Long queryClassCoursewareNum(String subjectId);

    List<String> queryClassCourIdsBySubjectClassId(String id);

    List<Courseware> queryBySubjectId(String objectId);
}
