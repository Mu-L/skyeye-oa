/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.courseware.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.chapter.entity.Chapter;
import com.skyeye.school.courseware.entity.Courseware;

import java.util.List;
import java.util.Map;

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

    Map<String, Double> queryCoursewareByChapterId(Long classNum, String... ids);

    Map<String, Map<String, Object>> queryInterAnalysisByChapters(Integer classNum, List<Chapter> chapterList, String type);

    Map<String, Long> queryCoursewareBySubjectIdAndChapterIds(String subjectId, List<String> chapterIds);

    Map<String, Long> queryStuCourBySubIdAndChapIdsAndStuIds(String subjectId, List<String> chapterIds, List<String> stuIds);

    Long queryClassCoursewareNum(String subjectId);

    List<String> queryClassCourIdsBySubjectClassId(String id);
}
