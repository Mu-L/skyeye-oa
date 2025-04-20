/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.score.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.score.entity.ScoreTypeChild;

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

    ScoreTypeChild select(String subjectId, String subjectClassesId, String nameLinkId);

    void delete(String subjectId, String subjectClassesId, String nameLinkId);

    List<ScoreTypeChild> queryBySubjectIdAndSubjectClassId(String subjectId, String subjectClassesId);

    void initScoreTypeChild(String subjectId, String subjectClassId);

    void deleteBySubjectIdAndSubjectClassId(String subjectId, String subjectClassesId);

    void editName(String subjectId, String subjectClassesId, String nameLinkId, String name);

    void editNames(String subjectId, List<String> subjectClassesId, String nameLinkId, String name);

    void queryScoreTypeChildFirstList(InputObject inputObject, OutputObject outputObject);

    void queryScoreTypeChildSecondList(InputObject inputObject, OutputObject outputObject);

    List<ScoreTypeChild> selectIds(String subjectId, List<String> collect, String testKey);
}
