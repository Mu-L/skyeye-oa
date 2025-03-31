/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.score.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.score.entity.ScoreType;
import com.skyeye.school.subject.entity.SubjectClasses;

import java.util.List;

/**
 * @ClassName: ScoreTypeService
 * @Description: 成绩类型管理服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/29 10:53
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ScoreTypeService extends SkyeyeBusinessService<ScoreType> {

    void querySameTableDateList(InputObject inputObject, OutputObject outputObject);

    void queryDifferentTableDateList(InputObject inputObject, OutputObject outputObject);

    void createDeFaultInfo(SubjectClasses subjectClasses, String userId);

    List<ScoreType> queryList(String subjectId,String ClassId);

    ScoreType queryDefaultInfo(String subjectId, String classId);

    List<ScoreType> queryNotDefaultInfo(String subjectId, String classId);

}
