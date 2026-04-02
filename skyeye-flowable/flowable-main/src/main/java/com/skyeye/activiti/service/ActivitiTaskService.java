/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.flowable.bpmn.model.UserTask;
import org.flowable.task.api.Task;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ActivitiTaskService
 * @Description: 工作流用户任务相关
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/2 20:55
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ActivitiTaskService {

    void queryUserAgencyTasksListByUserId(InputObject inputObject, OutputObject outputObject);

    void queryStartProcessNotSubByUserId(InputObject inputObject, OutputObject outputObject);

    void queryMyHistoryTaskByUserId(InputObject inputObject, OutputObject outputObject);

    void queryApprovalTasksHistoryByProcessInstanceId(InputObject inputObject, OutputObject outputObject);

    void queryAllComplateProcessList(InputObject inputObject, OutputObject outputObject);

    void queryAllConductProcessList(InputObject inputObject, OutputObject outputObject);

    void queryMyRunningCountersignList(InputObject inputObject, OutputObject outputObject);

    void queryMyHostCountersignList(InputObject inputObject, OutputObject outputObject);

    void querySubFormMationByTaskId(InputObject inputObject, OutputObject outputObject);

    List<String> getTaskAssignee(String processInstanceId);

    void editActivitiModelToRun(InputObject inputObject, OutputObject outputObject);

    /**
     * 获取该指定节点下所有的子节点还有多少个
     *
     * @param parentTaskId 节点id
     * @return 剩余的子节点数量
     */
    long getSubTaskCount(String parentTaskId);

    void setNextUserTaskApproval(String processInstanceId, String approverId);

    /**
     * 根据taskId获取UserTask对象
     *
     * @param taskId 任务id
     * @return UserTask对象
     */
    UserTask getCurrentUserTaskByTaskId(String taskId);

    /**
     * 根据任务节点id判断该节点是否为会签节点
     *
     * @param taskId 任务节点id
     * @return true：是会签节点；false：不是会签节点
     */
    boolean isMultiInstance(String taskId, Map<String, Object> map);

    void delegateTask(InputObject inputObject, OutputObject outputObject);

    void transferTask(InputObject inputObject, OutputObject outputObject);

    void beforeAddSignTask(InputObject inputObject, OutputObject outputObject);

    void afterAddSignTask(InputObject inputObject, OutputObject outputObject);

    void jointlySignTaskDetail(InputObject inputObject, OutputObject outputObject);

    void jointlySignAddSignTask(InputObject inputObject, OutputObject outputObject);

    void jointlySignCancelTask(InputObject inputObject, OutputObject outputObject);
}
