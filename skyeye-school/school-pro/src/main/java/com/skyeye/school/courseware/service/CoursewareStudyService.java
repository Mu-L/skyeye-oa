/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.courseware.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.courseware.entity.CoursewareStudy;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: CoursewareStudyService
 * @Description: 互动课件学习信息服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/2 10:00
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface CoursewareStudyService extends SkyeyeBusinessService<CoursewareStudy> {

    void studyCoursewareByCoursewareId(InputObject inputObject, OutputObject outputObject);

    Map<String, String> queryStudyState(List<String> coursewareIdList, String userId);

    double queryCoursewareFinshRate(List<String> ids, Long classNum);

    List<CoursewareStudy> queryCoursewareSubByCoursewareIds(List<String> coursewareIds);

    Map<String, Long> queryStuCourBySubjectIdsAndStuIds(String subjectId, List<String> stuIds);

    Long queryStuStudyCoursewareNum(String id, String stuId);

    List<CoursewareStudy> queryByCoursewareIdList(List<String> coursewareIdList);
}
