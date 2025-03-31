/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.score.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.score.entity.ScoreTypeChild;
import com.skyeye.school.subject.entity.SubjectClasses;

import java.util.List;

/**
 * @ClassName: ScoreTypeChildService
 * @Description: 成绩类型子表管理服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/29 10:53
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ScoreTypeChildService extends SkyeyeBusinessService<ScoreTypeChild> {
    List<ScoreTypeChild> queryListByParentIdList(List<String> list);

    ScoreTypeChild queryByTypeId(String typeId);

    void createDeFaultInfo(SubjectClasses subjectClasses);

    void boundDataOrNot(InputObject inputObject, OutputObject outputObject);

    void changeProportion(InputObject inputObject, OutputObject outputObject);

    List<ScoreTypeChild> queryListBySubjectIdAndClassId(String subjectId, String classId);

    String deleteByTypeId(String typeId);

    ScoreTypeChild queryById(String id);

    ScoreTypeChild selectBySubjectIdClassIdAndNumberCode(String subjectId, String classesId, Integer numberCode);

    List<ScoreTypeChild> queryByObjectIdAndClassId(String subjectId, String classesId);

    void deleteByIdList(List<String> idList);
}
