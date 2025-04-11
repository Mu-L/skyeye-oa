/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.score.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.score.entity.Score;
import com.skyeye.school.score.entity.ScoreTypeChild;

import java.util.List;

/**
 * @ClassName: ScoreService
 * @Description: 成绩管理服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/29 10:53
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ScoreService extends SkyeyeBusinessService<Score> {

    void deleteByObjectId(String... objectId);

    void initScorePartForScoreType(List<String> scoreTypeIds, String subClassLinkId);

    void initScorePartForScoreType(String scoreTypeId, String subClassLinkId);

    void initScorePartForStudent(List<String> scoreTypeIds, String studentNumber);

    void deleteScoreByStudentNumber(String studentNumber);

    List<Score> queryScoreList(List<String> scoreTypeIds, String studentNumber);

    /**
     * 修改学生的成绩
     *
     * @param subjectId      科目id
     * @param subClassLinkId 科目与班级关联id
     * @param studentNumber  学生学号
     * @param nameLinkId     业务数据的id(作业id，试卷id等)
     * @param nameLinkKey    业务数据的key(作业key，试卷key等)
     * @param nameLinkName   业务数据名称(作业名称，试卷名称等)
     * @param score          成绩
     */
    void updateStudentScore(String subjectId, String subClassLinkId, String studentNumber, String nameLinkId,
                            String nameLinkKey, String nameLinkName, String score);

    /**
     * 计算成绩
     *
     * @param scoreTypeChildrenList 成绩类型子项列表
     * @param scoreList             成绩列表
     */
    void calculateScore(List<ScoreTypeChild> scoreTypeChildrenList, List<Score> scoreList);

    void calculateScore(String subjectId, String subClassLinkId);

    void updateScore(InputObject inputObject, OutputObject outputObject);
}
