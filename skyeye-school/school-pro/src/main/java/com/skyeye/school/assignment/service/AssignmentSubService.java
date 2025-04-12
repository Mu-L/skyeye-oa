/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.assignment.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.assignment.entity.AssignmentSub;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AssignmentSubService
 * @Description: 作业提交服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/2 11:10
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AssignmentSubService extends SkyeyeBusinessService<AssignmentSub> {

    void queryAssignmentSubListByAssignmentId(InputObject inputObject, OutputObject outputObject);

    void readOverAssignmentSubById(InputObject inputObject, OutputObject outputObject);

    /**
     * 根据作业id获取【已经提交作业】的数量
     *
     * @param assignmentId 作业id
     * @return
     */
    Map<String, Long> querySubResult(String... assignmentId);

    /**
     * 根据作业id获取【已经批改的作业】的数量
     *
     * @param assignmentId 作业id
     * @return
     */
    Map<String, Long> querySubCorrectResult(String... assignmentId);

    /**
     * 根据作业id获取当前登录人的作业提交状态
     *
     * @param userId       用户id
     * @param assignmentId 作业id
     * @return
     */
    Map<String, String> querySubResultByUserId(String userId, String... assignmentId);

    void queryAssignmentStuSubListByAssignmentId(InputObject inputObject, OutputObject outputObject);

    void queryAssignmentNotSubListByAssignmentId(InputObject inputObject, OutputObject outputObject);

    Long queryClassAssignmentJoinNum(String id);

    List<AssignmentSub> queryAssSubByAssignmentIds(List<String> assIds);

    Map<String, Long> queryStuAssignNumBySubClassesId(String subjectClassId, List<String> stuIds);

    Double queryClassAssignmentAvg(String subjectClassId);

    Long queryStuAssignNumByStuId(String id, String stuId);
}
