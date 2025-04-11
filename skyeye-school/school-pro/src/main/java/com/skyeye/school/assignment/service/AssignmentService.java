/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.assignment.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.assignment.entity.Assignment;
import com.skyeye.school.chapter.entity.Chapter;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AssignmentService
 * @Description: 作业管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/2 10:45
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AssignmentService extends SkyeyeBusinessService<Assignment> {

    void queryAssignmentListBySubjectClassesId(InputObject inputObject, OutputObject outputObject);

    Long queryClassAssignmentNum(String id, String chapterId);

    List<String> queryAssignmentIdsBySubjectCLassId(String id);

    List<Assignment> queryListByObjectIdAndSubjectIdAndClassId(String objectId, String subjectClassesId);

    Map<String, Map<String, Object>> queryAssAnalysisByChapters(Integer classNum, List<Chapter> chapterList, String type);

    Map<String, Long> queryAssignmentBySubjectClassesIdAndChapterIds(String subjectClassesId, List<String> chapterIds);

    Map<String, Long> queryStuAssignNumBySubClassesId(String id, List<String> chapterIds, List<String> stuIds);
}
