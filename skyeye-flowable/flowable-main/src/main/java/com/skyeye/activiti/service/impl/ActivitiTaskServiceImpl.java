/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.skyeye.activiti.classenum.ProcessInstanceWeatherEnd;
import com.skyeye.activiti.cmd.multiinstanceexecution.*;
import com.skyeye.activiti.entity.NextTaskInfo;
import com.skyeye.activiti.mapper.FlowableTaskDao;
import com.skyeye.activiti.service.ActivitiModelService;
import com.skyeye.activiti.service.ActivitiProcessService;
import com.skyeye.activiti.service.ActivitiTaskService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.exception.CustomException;
import com.skyeye.userprocess.dao.ActUserProcessDao;
import com.skyeye.userprocess.entity.ActUserProcess;
import com.skyeye.userprocess.service.ActUserProcessService;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.flowable.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ActivitiTaskServiceImpl
 * @Description: 工作流用户任务相关
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/2 20:55
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ActivitiTaskServiceImpl implements ActivitiTaskService {

    private static Logger LOGGER = LoggerFactory.getLogger(ActivitiTaskServiceImpl.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private ActUserProcessDao actUserProcessInstanceIdDao;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ActivitiModelService activitiModelService;

    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Autowired
    protected ManagementService managementService;

    @Autowired
    private FlowableTaskDao flowableTaskDao;

    @Autowired
    private ActivitiProcessService activitiProcessService;

    @Autowired
    private ActUserProcessService actUserProcessService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @Override
    @IgnoreTenant
    public void queryUserAgencyTasksListByUserId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        pageInfo.setCreateId(inputObject.getLogParams().get("id").toString());
        queryAgencyTask(outputObject, pageInfo);
    }

    private void queryAgencyTask(OutputObject outputObject, CommonPageInfo pageInfo) {
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        if (tenantEnable) {
            pageInfo.setTenantId(TenantContext.getTenantId());
        }
        List<Map<String, Object>> beans = flowableTaskDao.getApplyingTasks(pageInfo);
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        List<String> processInstanceIds = beans.stream()
            .map(bean -> bean.get("processInstanceId").toString()).distinct().collect(Collectors.toList());
        // 获取流程创建信息
        Map<String, ActUserProcess> actUserProcessMap = actUserProcessService.selectByProcessInstanceId(processInstanceIds);
        // 获取运行中的流程信息
        Set<String> processInstanceIdsSet = new HashSet<>(processInstanceIds);
        List<ProcessInstance> processInstanceList = runtimeService.createProcessInstanceQuery().processInstanceIds(processInstanceIdsSet).list();
        Map<String, ProcessInstance> processInstanceMap = processInstanceList.stream()
            .collect(Collectors.toMap(ProcessInstance::getProcessInstanceId, bean -> bean));
        List<String> runProcessInstanceIds = processInstanceList.stream().map(ProcessInstance::getProcessInstanceId).collect(Collectors.toList());
        // 获取运行中的任务
        Map<String, List<Task>> taskMap = null;
        if (CollectionUtil.isNotEmpty(runProcessInstanceIds)) {
            List<Task> tasks = taskService.createTaskQuery().processInstanceIdIn(runProcessInstanceIds).list();
            taskMap = tasks.stream().filter(bean -> StrUtil.isNotEmpty(bean.getAssignee()))
                .collect(Collectors.groupingBy(Task::getProcessInstanceId));
        }

        for (Map<String, Object> task : beans) {
            String processInstanceId = task.get("processInstanceId").toString();

            ActUserProcess userProcess = actUserProcessMap.get(processInstanceId);
            task.put("processMation", userProcess);

            if (processInstanceMap.containsKey(processInstanceId)) {
                task.put("suspended", processInstanceMap.get(processInstanceId).isSuspended());
            }

            if (CollectionUtil.isNotEmpty(taskMap) && CollectionUtil.isNotEmpty(taskMap.get(processInstanceId))) {
                List<String> assigneeList = taskMap.get(processInstanceId).stream().map(Task::getAssignee).collect(Collectors.toList());
                List<Map<String, Object>> assigneeUser = iAuthUserService.queryDataMationByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(assigneeList));
                task.put("assigneeList", assigneeUser);
            }
        }
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 获取我启动的流程
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryStartProcessNotSubByUserId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        pageInfo.setCreateId(inputObject.getLogParams().get("id").toString());
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        List<Map<String, Object>> beans = actUserProcessInstanceIdDao.queryStartProcessNotSubByUserId(pageInfo);
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        List<String> processInstanceIds = beans.stream()
            .map(bean -> bean.get("processInstanceId").toString()).collect(Collectors.toList());
        // 获取流程创建信息
        Map<String, ActUserProcess> actUserProcessMap = actUserProcessService.selectByProcessInstanceId(processInstanceIds);
        // 获取运行中的流程信息
        Set<String> processInstanceIdsSet = new HashSet<>(processInstanceIds);
        List<ProcessInstance> processInstanceList = runtimeService.createProcessInstanceQuery().processInstanceIds(processInstanceIdsSet).list();
        List<String> runProcessInstanceIds = processInstanceList.stream().map(ProcessInstance::getProcessInstanceId).collect(Collectors.toList());
        Map<String, ProcessInstance> processInstanceMap = processInstanceList.stream()
            .collect(Collectors.toMap(ProcessInstance::getProcessInstanceId, bean -> bean));
        // 获取运行中的任务
        Map<String, List<Task>> taskMap = null;
        if (CollectionUtil.isNotEmpty(runProcessInstanceIds)) {
            List<Task> tasks = taskService.createTaskQuery().processInstanceIdIn(runProcessInstanceIds).list();
            taskMap = tasks.stream().filter(bean -> StrUtil.isNotEmpty(bean.getAssignee()))
                .collect(Collectors.groupingBy(Task::getProcessInstanceId));
        }

        for (Map<String, Object> bean : beans) {
            String processInstanceId = bean.get("processInstanceId").toString();
            ActUserProcess userProcess = actUserProcessMap.get(processInstanceId);
            bean.put("processMation", userProcess);
            bean.put("weatherEnd", ProcessInstanceWeatherEnd.ENDED.getKey());

            if (runProcessInstanceIds.contains(processInstanceId)) {
                bean.put("weatherEnd", ProcessInstanceWeatherEnd.NOT_FINISHED.getKey());
            }

            if (processInstanceMap.containsKey(processInstanceId)) {
                bean.put("suspended", processInstanceMap.get(processInstanceId).isSuspended());
            }

            if (CollectionUtil.isNotEmpty(taskMap) && CollectionUtil.isNotEmpty(taskMap.get(processInstanceId))) {
                List<String> assigneeList = taskMap.get(processInstanceId).stream().map(Task::getAssignee).collect(Collectors.toList());
                List<Map<String, Object>> assigneeUser = iAuthUserService.queryDataMationByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(assigneeList));
                bean.put("assigneeList", assigneeUser);
            }
        }
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 获取我的历史任务--历史表中存在并非是单一类型的数据，就拿历史任务表来说，里边既有已经结束的任务，也有还没有结束的任务。
     * 如果要单独查询结束了的任务，就可以调用finished()方法，查询的就是已经结束的任务
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryMyHistoryTaskByUserId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        Map<String, Object> user = inputObject.getLogParams();
        String userId = user.get("id").toString();
        // 构造查询条件
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery().taskAssignee(userId);
        if (StrUtil.isNotEmpty(pageInfo.getKeyword())) {
            query = query.processInstanceId(pageInfo.getKeyword());
        }
        // 获取我的已办历史
        List<HistoricTaskInstance> hisTaskList = query.orderByTaskCreateTime().desc()
            .finished().listPage((pageInfo.getPage() - 1) * pageInfo.getLimit(), pageInfo.getLimit());
        if (CollectionUtil.isEmpty(hisTaskList)) {
            return;
        }
        List<String> processInstanceIds = hisTaskList.stream().map(HistoricTaskInstance::getProcessInstanceId).collect(Collectors.toList());

        // 获取流程创建信息
        Map<String, ActUserProcess> actUserProcessMap = actUserProcessService.selectByProcessInstanceId(processInstanceIds);
        // 获取运行中的流程信息
        Set<String> processInstanceIdsSet = new HashSet<>(processInstanceIds);
        List<ProcessInstance> processInstanceList = runtimeService.createProcessInstanceQuery().processInstanceIds(processInstanceIdsSet).list();
        List<String> runProcessInstanceIds = processInstanceList.stream().map(ProcessInstance::getProcessInstanceId).collect(Collectors.toList());
        // 获取运行中的任务
        Map<String, List<Task>> taskMap = null;
        if (CollectionUtil.isNotEmpty(runProcessInstanceIds)) {
            List<Task> tasks = taskService.createTaskQuery().processInstanceIdIn(runProcessInstanceIds).list();
            taskMap = tasks.stream().filter(bean -> StrUtil.isNotEmpty(bean.getAssignee()))
                .collect(Collectors.groupingBy(Task::getProcessInstanceId));
        }

        List<Map<String, Object>> beans = new ArrayList<>();
        for (HistoricTaskInstance hisTask : hisTaskList) {
            Map<String, Object> hisModel = new HashMap<>();
            hisModel.put("hisTask", hisTask);
            hisModel.put("weatherEnd", ProcessInstanceWeatherEnd.ENDED.getKey());

            ActUserProcess userProcess = actUserProcessMap.get(hisTask.getProcessInstanceId());
            hisModel.put("processMation", userProcess);

            if (runProcessInstanceIds.contains(hisTask.getProcessInstanceId())) {
                hisModel.put("weatherEnd", ProcessInstanceWeatherEnd.NOT_FINISHED.getKey());
            }
            if (CollectionUtil.isNotEmpty(taskMap) && CollectionUtil.isNotEmpty(taskMap.get(hisTask.getProcessInstanceId()))) {
                List<String> assigneeList = taskMap.get(hisTask.getProcessInstanceId()).stream().map(Task::getAssignee).collect(Collectors.toList());
                List<Map<String, Object>> assigneeUser = iAuthUserService.queryDataMationByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(assigneeList));
                hisModel.put("assigneeList", assigneeUser);
            }
            beans.add(hisModel);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(query.count());
    }

    /**
     * 获取历史审批列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryApprovalTasksHistoryByProcessInstanceId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String processInstanceId = map.get("processInstanceId").toString();
        ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        // 保证运行ing
        List<Map<String, Object>> leaveList = null;
        if (instance != null) {
            Object o = runtimeService.getVariable(processInstanceId, ActivitiConstants.PROCESSINSTANCEID_TASK_LEAVE_OPINION_LIST_VARABLES);
            if (o != null) {
                /* 获取历史审核信息 */
                leaveList = (List<Map<String, Object>>) o;
            }
        } else {
            leaveList = new ArrayList<>();
            List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).list();
            for (HistoricVariableInstance historicDetail : list) {
                if (ActivitiConstants.PROCESSINSTANCEID_TASK_LEAVE_OPINION_LIST_VARABLES.equals(historicDetail.getVariableName())) {
                    leaveList.clear();
                    leaveList.addAll((List<Map<String, Object>>) historicDetail.getValue());
                }
            }
        }
        if (leaveList == null) {
            leaveList = new ArrayList<>();
        }
        //根据时间排序
        Collections.sort(leaveList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> p1, Map<String, Object> p2) {
                String a = p1.get("createTime").toString();
                String b = p2.get("createTime").toString();
                if (DateUtil.compare(a, b)) {
                    return 1;
                }
                return -1;
            }
        });
        for (Map<String, Object> leave : leaveList) {
            leave.put("flagName", (boolean) leave.get("flag") ? "通过" : "拒绝");
            leave.put("opinion", ToolUtil.isBlank(leave.get("opinion").toString()) ? "暂无审批意见" : leave.get("opinion").toString());
        }
        outputObject.setBeans(leaveList);
        outputObject.settotal(leaveList.size());
    }

    /**
     * 获取所有已完成的流程信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryAllComplateProcessList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        // 构造查询条件
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();
        if (StrUtil.isNotEmpty(pageInfo.getKeyword())) {
            query = query.processInstanceId(pageInfo.getKeyword());
        }
        // 获取我的已办历史
        List<HistoricProcessInstance> processInstances = query.orderByProcessInstanceEndTime().desc()
            .finished().listPage((pageInfo.getPage() - 1) * pageInfo.getLimit(), pageInfo.getLimit());
        if (CollectionUtil.isEmpty(processInstances)) {
            return;
        }
        List<String> processInstanceIds = processInstances.stream().map(HistoricProcessInstance::getId).collect(Collectors.toList());
        // 获取流程创建信息
        Map<String, ActUserProcess> actUserProcessMap = actUserProcessService.selectByProcessInstanceId(processInstanceIds);

        List<Map<String, Object>> beans = new ArrayList<>();
        for (HistoricProcessInstance bean : processInstances) {
            Map<String, Object> hisModel = new HashMap<>();
            hisModel.put("historicProcessInstance", bean);

            ActUserProcess userProcess = actUserProcessMap.get(bean.getId());
            hisModel.put("processMation", userProcess);
            beans.add(hisModel);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(query.count());
    }

    /**
     * 获取所有待办的流程信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @IgnoreTenant
    public void queryAllConductProcessList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        queryAgencyTask(outputObject, pageInfo);
    }

    @Override
    public void queryMyRunningCountersignList(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        String userId = inputObject.getLogParams().get("id").toString();
        String keyword = tableSelectInfo == null ? StrUtil.EMPTY : StrUtil.nullToEmpty(tableSelectInfo.getKeyword());

        // 通过 ACT_RU_EXECUTION + ACT_RU_VARIABLE(assignee) 获取当前参与的进行中流程
        List<String> runningProcessIds = queryRunningProcessIdsByExecutionAssignee(userId, keyword);
        if (CollectionUtil.isEmpty(runningProcessIds)) {
            return;
        }

        Set<String> runningProcessIdSet = new LinkedHashSet<>(runningProcessIds);
        List<ProcessInstance> runningProcesses = runtimeService.createProcessInstanceQuery()
            .processInstanceIds(runningProcessIdSet).list();
        if (CollectionUtil.isEmpty(runningProcesses)) {
            return;
        }

        Map<String, ProcessInstance> processInstanceMap = runningProcesses.stream()
            .collect(Collectors.toMap(ProcessInstance::getProcessInstanceId, bean -> bean));
        List<String> availableProcessIds = runningProcessIds.stream()
            .filter(processInstanceMap::containsKey).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(availableProcessIds)) {
            return;
        }

        List<Task> activeTasks = taskService.createTaskQuery().processInstanceIdIn(availableProcessIds).list();
        if (CollectionUtil.isEmpty(activeTasks)) {
            return;
        }

        Map<String, List<Task>> countersignTaskMap = activeTasks.stream()
            .filter(task -> StrUtil.isNotEmpty(task.getAssignee()))
            .collect(Collectors.groupingBy(Task::getProcessInstanceId));
        if (CollectionUtil.isEmpty(countersignTaskMap)) {
            return;
        }

        Map<String, ActUserProcess> processMationMap = actUserProcessService.selectByProcessInstanceId(availableProcessIds);
        List<Map<String, Object>> beans = new ArrayList<>();
        for (String processInstanceId : availableProcessIds) {
            List<Task> processTasks = countersignTaskMap.get(processInstanceId);
            if (CollectionUtil.isEmpty(processTasks)) {
                continue;
            }
            // 并行会签等多任务场景下，必须使用当前用户自己的任务 ID 才能打开审批
            boolean canApprove = processTasks.stream().anyMatch(t -> userId.equals(t.getAssignee()));
            Task currentTask = processTasks.stream()
                .filter(t -> userId.equals(t.getAssignee()))
                .findFirst()
                .orElse(processTasks.get(0));
            Map<String, Object> bean = new HashMap<>();
            bean.put("processInstanceId", processInstanceId);
            bean.put("taskId", currentTask.getId());
            bean.put("taskName", currentTask.getName());
            bean.put("taskType", "会签");
            bean.put("task", currentTask);
            bean.put("processMation", processMationMap.get(processInstanceId));
            bean.put("weatherEnd", ProcessInstanceWeatherEnd.NOT_FINISHED.getKey());

            // 会签统计信息
            int participantCount = 0;
            int approvedCount = 0;
            int mandatoryCount = 0;
            boolean isSequential = false;
            String hostId = "";
            try {
                UserTask currentUserTask = this.getCurrentUserTaskByTaskId(currentTask.getId());
                isSequential = currentUserTask != null
                    && currentUserTask.getLoopCharacteristics() != null
                    && currentUserTask.getLoopCharacteristics().isSequential();
                List<Map<String, Object>> detailAssigneeList = managementService.executeCommand(
                    new FindMultiInstanceExecutionUserCmd(currentTask.getId(), isSequential));
                if (CollectionUtil.isNotEmpty(detailAssigneeList)) {
                    hostId = detailAssigneeList.stream()
                        .filter(item -> "1".equals(Objects.toString(item.get("type"), "0")))
                        .map(item -> Objects.toString(item.get("id"), ""))
                        .filter(StrUtil::isNotEmpty)
                        .findFirst().orElse("");
                    List<Map<String, Object>> participantList = detailAssigneeList.stream()
                        .filter(item -> !"1".equals(Objects.toString(item.get("type"), "0")))
                        .collect(Collectors.toList());
                    participantCount = participantList.size();
                    approvedCount = (int) participantList.stream()
                        .filter(item -> item.containsKey("isActive") && !(Boolean) item.get("isActive"))
                        .count();
                    mandatoryCount = (int) participantList.stream()
                        .filter(item -> "1".equals(Objects.toString(item.get("isMandatory"), "0")))
                        .count();
                }
            } catch (Exception e) {
                LOGGER.warn("build countersign statistics failed, taskId: {}", currentTask.getId(), e);
            }
            bean.put("jointlySignType", isSequential ? "串行会签" : "并行会签");
            bean.put("participantCount", participantCount);
            bean.put("approvedCount", approvedCount);
            bean.put("pendingCount", Math.max(participantCount - approvedCount, 0));
            bean.put("countersignCondition", mandatoryCount > 0 ? String.format(Locale.ROOT, "含必选评审人(%d人)", mandatoryCount) : "普通会签");

            ProcessInstance processInstance = processInstanceMap.get(processInstanceId);
            bean.put("suspended", processInstance != null && processInstance.isSuspended());
            bean.put("canApprove", canApprove);
            // 可取消状态：仅主持人且无人已审可取消
            boolean canCancelCountersign = userId.equals(hostId)
                && participantCount > 0
                && approvedCount == 0
                && (processInstance == null || !processInstance.isSuspended());
            String cannotCancelReason = "";
            if (!canCancelCountersign) {
                if (!userId.equals(hostId)) {
                    cannotCancelReason = "仅主持人可取消";
                } else if (participantCount <= 0) {
                    cannotCancelReason = "无可取消参与人";
                } else if (approvedCount > 0) {
                    cannotCancelReason = "已有参与人审核";
                } else if (processInstance != null && processInstance.isSuspended()) {
                    cannotCancelReason = "流程已挂起";
                } else {
                    cannotCancelReason = "当前不可取消";
                }
            }
            bean.put("canCancelCountersign", canCancelCountersign);
            bean.put("cannotCancelReason", cannotCancelReason);

            putCountersignListPersonTimeFields(bean, processMationMap.get(processInstanceId),
                processInstanceMap.get(processInstanceId), currentTask, hostId, processInstanceId);

            List<String> assigneeList = processTasks.stream().map(Task::getAssignee).distinct().collect(Collectors.toList());
            List<Map<String, Object>> assigneeUser = iAuthUserService.queryDataMationByIds(
                Joiner.on(CommonCharConstants.COMMA_MARK).join(assigneeList));
            bean.put("assigneeList", assigneeUser);
            beans.add(bean);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    @Override
    public void queryMyHostCountersignList(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        String userId = inputObject.getLogParams().get("id").toString();
        String keyword = tableSelectInfo == null ? StrUtil.EMPTY : StrUtil.nullToEmpty(tableSelectInfo.getKeyword());

        // 主持人口径：使用会签主持人变量
        List<String> runningProcessIds = queryRunningProcessIdsByHostAssignee(userId, keyword);
        if (CollectionUtil.isEmpty(runningProcessIds)) {
            return;
        }
        List<Task> myActiveTasks = taskService.createTaskQuery().active().processInstanceIdIn(runningProcessIds).list();
        if (CollectionUtil.isEmpty(myActiveTasks)) {
            return;
        }

        LinkedHashSet<String> processIdsSet = myActiveTasks.stream()
            .map(Task::getProcessInstanceId).collect(Collectors.toCollection(LinkedHashSet::new));
        List<String> processIds = new ArrayList<>(processIdsSet);
        Map<String, Task> hostTaskMap = myActiveTasks.stream()
            .collect(Collectors.toMap(Task::getProcessInstanceId, bean -> bean, (a, b) -> a));

        Map<String, ActUserProcess> processMationMap = actUserProcessService.selectByProcessInstanceId(processIds);
        List<ProcessInstance> runningProcesses = runtimeService.createProcessInstanceQuery().processInstanceIds(processIdsSet).list();
        Map<String, ProcessInstance> processInstanceMap = runningProcesses.stream()
            .collect(Collectors.toMap(ProcessInstance::getProcessInstanceId, bean -> bean));

        List<Task> activeTasksInProcess = taskService.createTaskQuery().processInstanceIdIn(processIds).list();
        Map<String, List<Task>> countersignTaskMap = activeTasksInProcess.stream()
            .filter(task -> StrUtil.isNotEmpty(task.getAssignee()))
            .collect(Collectors.groupingBy(Task::getProcessInstanceId));

        List<Map<String, Object>> beans = new ArrayList<>();
        for (String processInstanceId : processIds) {
            Task hostTask = hostTaskMap.get(processInstanceId);
            if (hostTask == null) {
                continue;
            }
            Map<String, Object> bean = new HashMap<>();
            bean.put("processInstanceId", processInstanceId);
            bean.put("taskId", hostTask.getId());
            bean.put("taskName", hostTask.getName());
            bean.put("taskType", "会签");
            bean.put("task", hostTask);
            bean.put("processMation", processMationMap.get(processInstanceId));
            bean.put("weatherEnd", ProcessInstanceWeatherEnd.NOT_FINISHED.getKey());

            // 会签统计信息
            int participantCount = 0; // 参与人数
            int approvedCount = 0; // 已审人数
            int mandatoryCount = 0; // 必选评审人人数
            boolean isSequential = false; // 是否是串行会签
            try {
                UserTask currentUserTask = this.getCurrentUserTaskByTaskId(hostTask.getId());
                isSequential = currentUserTask != null
                    && currentUserTask.getLoopCharacteristics() != null
                    && currentUserTask.getLoopCharacteristics().isSequential();
                List<Map<String, Object>> detailAssigneeList = managementService.executeCommand(
                    new FindMultiInstanceExecutionUserCmd(hostTask.getId(), isSequential));
                if (CollectionUtil.isNotEmpty(detailAssigneeList)) {
                    List<Map<String, Object>> participantList = detailAssigneeList.stream()
                        .filter(item -> !"1".equals(Objects.toString(item.get("type"), "0")))
                        .collect(Collectors.toList());
                    participantCount = participantList.size();
                    approvedCount = (int) participantList.stream()
                        .filter(item -> item.containsKey("isActive") && !(Boolean) item.get("isActive"))
                        .count();
                    mandatoryCount = (int) participantList.stream()
                        .filter(item -> "1".equals(Objects.toString(item.get("isMandatory"), "0")))
                        .count();
                }
            } catch (Exception e) {
                LOGGER.warn("build countersign statistics failed, taskId: {}", hostTask.getId(), e);
            }
            bean.put("jointlySignType", isSequential ? "串行会签" : "并行会签");
            bean.put("participantCount", participantCount);
            bean.put("approvedCount", approvedCount);
            bean.put("pendingCount", Math.max(participantCount - approvedCount, 0));
            bean.put("countersignCondition", mandatoryCount > 0 ? String.format(Locale.ROOT, "含必选评审人(%d人)", mandatoryCount) : "普通会签");

            ProcessInstance processInstance = processInstanceMap.get(processInstanceId);
            bean.put("suspended", processInstance != null && processInstance.isSuspended());
            // 可取消状态：仅在无人已审且有参与人时可取消；挂起流程不可取消
            boolean canCancelCountersign = participantCount > 0 && approvedCount == 0 && (processInstance == null || !processInstance.isSuspended());
            String cannotCancelReason = "";
            if (!canCancelCountersign) {
                if (participantCount <= 0) {
                    cannotCancelReason = "无可取消参与人";
                } else if (approvedCount > 0) {
                    cannotCancelReason = "已有参与人审核";
                } else if (processInstance != null && processInstance.isSuspended()) {
                    cannotCancelReason = "流程已挂起";
                } else {
                    cannotCancelReason = "当前不可取消";
                }
            }
            bean.put("canCancelCountersign", canCancelCountersign);
            bean.put("cannotCancelReason", cannotCancelReason);

            putCountersignListPersonTimeFields(bean, processMationMap.get(processInstanceId),
                processInstanceMap.get(processInstanceId), hostTask, userId, processInstanceId);

            List<Task> processTasks = countersignTaskMap.get(processInstanceId);
            if (CollectionUtil.isNotEmpty(processTasks)) {
                List<String> assigneeList = processTasks.stream().map(Task::getAssignee).distinct().collect(Collectors.toList());
                List<Map<String, Object>> assigneeUser = iAuthUserService.queryDataMationByIds(
                    Joiner.on(CommonCharConstants.COMMA_MARK).join(assigneeList));
                bean.put("assigneeList", assigneeUser);
            } else {
                bean.put("assigneeList", CollectionUtil.newArrayList());
            }
            beans.add(bean);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 会签列表补充：流程发起人、流程启动时间、会签主持人、当前会签任务生成时间
     */
    private void putCountersignListPersonTimeFields(Map<String, Object> bean, ActUserProcess processMation,
                                                    ProcessInstance processInstance, Task task, String hostUserId,
                                                    String processInstanceId) {
        if (processMation != null) {
            bean.put("processStarterId", StrUtil.nullToEmpty(processMation.getCreateId()));
            bean.put("processStarterName", StrUtil.nullToEmpty(processMation.getCreateName()));
            bean.put("processRecordCreateTime", StrUtil.nullToEmpty(processMation.getCreateTime()));
        } else {
            bean.put("processStarterId", StrUtil.EMPTY);
            bean.put("processStarterName", StrUtil.EMPTY);
            bean.put("processRecordCreateTime", StrUtil.EMPTY);
        }
        if (processInstance != null && processInstance.getStartTime() != null) {
            bean.put("flowStartTime", DateUtil.formatDate2Str(processInstance.getStartTime(), DateUtil.YYYY_MM_DD_HH_MM_SS));
        } else {
            bean.put("flowStartTime", StrUtil.EMPTY);
        }
        if (task != null && task.getCreateTime() != null) {
            bean.put("countersignTaskCreateTime",
                DateUtil.formatDate2Str(task.getCreateTime(), DateUtil.YYYY_MM_DD_HH_MM_SS));
        } else {
            bean.put("countersignTaskCreateTime", StrUtil.EMPTY);
        }
        String effectiveHostId = StrUtil.nullToEmpty(hostUserId);
        if (StrUtil.isEmpty(effectiveHostId) && StrUtil.isNotEmpty(processInstanceId)) {
            String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
            effectiveHostId = StrUtil.nullToEmpty(
                flowableTaskDao.queryHostAssigneeByProcessInstanceId(processInstanceId, tenantId));
        }
        bean.put("countersignHostId", effectiveHostId);
        String hostName = StrUtil.EMPTY;
        if (StrUtil.isNotEmpty(effectiveHostId)) {
            Map<String, Object> hm = iAuthUserService.queryDataMationById(effectiveHostId);
            if (hm != null && hm.get("name") != null) {
                hostName = Objects.toString(hm.get("name"), StrUtil.EMPTY);
            }
        }
        bean.put("countersignHostName", hostName);
    }

    private List<String> queryRunningProcessIdsByExecutionAssignee(String userId, String keyword) {
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        List<String> processIds = flowableTaskDao.queryRunningProcessIdsByExecutionAssignee(userId, keyword, tenantId);
        if (CollectionUtil.isEmpty(processIds)) {
            return CollectionUtil.newArrayList();
        }
        return processIds.stream().filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
    }

    private List<String> queryRunningProcessIdsByHostAssignee(String userId, String keyword) {
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        List<String> processIds = flowableTaskDao.queryRunningProcessIdsByHostAssignee(userId, keyword, tenantId);
        if (CollectionUtil.isEmpty(processIds)) {
            return CollectionUtil.newArrayList();
        }
        return processIds.stream().filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
    }

    /**
     * 清除流程实例下各执行上的会签主持人标记，避免取消会签后仍出现在「我主持的会签」查询中。
     */
    private void removeMultiInstanceHostAssigneeFromProcess(String processInstanceId) {
        if (StrUtil.isEmpty(processInstanceId)) {
            return;
        }
        List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).list();
        for (Execution ex : executions) {
            if (runtimeService.getVariableLocal(ex.getId(), ActivitiConstants.MULTI_INSTANCE_HOST_ASSIGNEE) != null) {
                runtimeService.removeVariableLocal(ex.getId(), ActivitiConstants.MULTI_INSTANCE_HOST_ASSIGNEE);
            }
        }
    }

    /**
     * 根据taskId获取表单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void querySubFormMationByTaskId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String taskId = map.get("taskId").toString();
        String processInstanceId = map.get("processInstanceId").toString();
        String flag = map.getOrDefault("flag", CommonNumConstants.NUM_ONE).toString();
        // 获取任务自定义id和名称
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        map.put("taskKey", task.getTaskDefinitionKey());
        map.put("taskKeyName", task.getName());
        // 是否委派，如果是委派，则不需要选择下一个节点的审批人
        boolean delegation = task.getDelegationState() != null && DelegationState.PENDING == task.getDelegationState();
        map.put("delegation", delegation);
        // 是否是多实例
        Boolean isMultiInstance = isMultiInstance(task.getId(), map);
        map.put("isMultiInstance", isMultiInstance);
        // 获取提交时候的信息（委派任务不需要判断下一个节点，因为委派完成后会回到原委派人）
        if (!isMultiInstance && !delegation) {
            // 因为获取下一个节点可能会遇到网关节点，所以默认设置审批结果为true
            Map<String, Object> variable = new HashMap<>();
            variable.put("flag", flag);
            NextTaskInfo nextTaskInfo = activitiProcessService.getNextTaskInfo(taskId, variable);
            if (nextTaskInfo != null && nextTaskInfo.getUserTask() != null) {
                map.put("nextTask", true);
            }
        }
        // 获取流程关联页面类型
        ActUserProcess userProcess = actUserProcessService.selectByProcessInstanceId(processInstanceId);
        map.put("pageTypes", userProcess.getPageTypes());
        map.put("title", userProcess.getTitle());

        outputObject.setBean(map);
    }

    @Override
    public List<String> getTaskAssignee(String processInstanceId) {
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if (CollectionUtil.isEmpty(tasks)) {
            return CollectionUtil.newArrayList();
        }
        return tasks.stream().filter(bean -> StrUtil.isNotEmpty(bean.getAssignee()))
            .map(Task::getAssignee).distinct().collect(Collectors.toList());
    }

    /**
     * 提交审批结果
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void editActivitiModelToRun(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String processInstanceId = map.get("processInstanceId").toString();
        ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        // 是否挂起
        if (instance.isSuspended()) {
            outputObject.setreturnMessage("该流程已被挂起，无法操作。");
            return;
        }
        Map<String, Object> user = inputObject.getLogParams();
        String taskId = map.get("taskId").toString();//当前任务节点
        TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
        if (taskEntity != null) {
            // 是否委派
            boolean delegation = taskEntity.getDelegationState() != null && DelegationState.PENDING == taskEntity.getDelegationState();
            // 获取审批结果
            boolean flag = getApprovedResult(map.get("flag").toString());
            // 处理加签父任务
            String parentTaskId = taskEntity.getParentTaskId();
            Task task;
            if (StringUtils.isNotBlank(parentTaskId)) {
                task = taskService.createTaskQuery().taskId(parentTaskId).singleResult();
            } else {
                task = taskService.createTaskQuery().taskId(taskId).singleResult();
            }
            if (delegation) {
                // 完成任务委托
                Map<String, Object> bean = new HashMap<>();
                String opinion = String.format(Locale.ROOT, "【委派审批建议】：%s", map.get("opinion").toString());
                List<Map<String, Object>> leaveList = activitiModelService.getUpLeaveList(user.get("id").toString(),
                    user.get("userName").toString(), opinion, flag, task, ActivitiConstants.LeaveType.TASK_DELEGATE_RESULT.getType());
                runtimeService.setVariable(processInstanceId, ActivitiConstants.PROCESSINSTANCEID_TASK_LEAVE_OPINION_LIST_VARABLES, leaveList);
                taskService.resolveTask(taskId, bean);
            } else {
                // 多实例会签任务处理
                boolean pendingType = handleMultiInstance(taskId, user, flag, map.get("opinion").toString());
                if (pendingType) {
                    // 审批结果设定
                    approvalResult(map, processInstanceId, user, taskId, task, flag);
                }
                // 处理加签父任务
                handleSignTask(taskEntity, map.get("approverId").toString(), processInstanceId);
            }
        } else {
            outputObject.setreturnMessage("没有此任务，请确认!");
        }
    }

    private boolean handleMultiInstance(String taskId, Map<String, Object> user, boolean flag, String opinion) {
        return managementService.executeCommand(new HandlerMultiInstanceExecutionCmd(taskId, user, flag, opinion));
    }

    private void handleSignTask(TaskEntity taskEntity, String approverId, String processInstanceId) {
        String parentTaskId = taskEntity.getParentTaskId();
        if (StringUtils.isNotBlank(parentTaskId)) {
            long subTaskCount = getSubTaskCount(parentTaskId);
            if (subTaskCount == 0) {
                Task task = taskService.createTaskQuery().taskId(parentTaskId).singleResult();
                // 处理前后加签的任务
                taskService.resolveTask(parentTaskId);
                if (ActivitiConstants.AFTER_ADDSIGN.equals(task.getScopeType())) {
                    // 如果是后加签，完成父任务
                    taskService.complete(parentTaskId);
                    // 绘制图像
                    activitiModelService.queryProHighLighted(processInstanceId);
                    // 设置下个节点的审批人
                    setNextUserTaskApproval(processInstanceId, approverId);
                }
            }
        }
    }

    @Override
    public long getSubTaskCount(String parentTaskId) {
        String tableName = managementService.getTableName(TaskEntity.class);
        String sql = "select count(1) from " + tableName + " where PARENT_TASK_ID_ = #{parentTaskId}";
        long subTaskCount =
            taskService.createNativeTaskQuery().sql(sql).parameter("parentTaskId", parentTaskId).count();
        return subTaskCount;
    }

    private void approvalResult(Map<String, Object> map, String processInstanceId, Map<String, Object> user,
                                String taskId, Task task, boolean flag) {
        // 判断节点是否已经拒绝过一次了
        Map<String, Object> bean = setWhetherNeedEnd(taskId, flag);
        // 获取指定任务节点的审批信息
        List<Map<String, Object>> leaveList = activitiModelService.getUpLeaveList(user.get("id").toString(),
            user.get("userName").toString(), map.get("opinion").toString(), flag, task, ActivitiConstants.LeaveType.APPROVAL_COMMENTS.getType());
        runtimeService.setVariable(processInstanceId, ActivitiConstants.PROCESSINSTANCEID_TASK_LEAVE_OPINION_LIST_VARABLES, leaveList);
        // 校验参数
        bean.put("flag", map.get("flag"));
        taskService.complete(taskId, bean);
        LOGGER.info("complete success, processInstanceId is {}.", processInstanceId);
        // 绘制图像
        activitiModelService.queryProHighLighted(processInstanceId);

        executeParentTask(processInstanceId, task, user.get("id").toString());

        // 设置下个节点的审批人
        setNextUserTaskApproval(processInstanceId, map.get("approverId").toString());
    }

    private void executeParentTask(String processInstanceId, Task task, String userId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        UserTask userTask = (UserTask) process.getFlowElement(task.getTaskDefinitionKey());
        Object behavior = userTask.getBehavior();
        if (behavior instanceof ParallelMultiInstanceBehavior) {
            // 并行会签
        } else if (behavior instanceof SequentialMultiInstanceBehavior) {
            // 串行会签，判断完成会签任务后，是否还有从属任务，如果有，则一起完成
            Task currentTask = taskService.createTaskQuery().processInstanceId(processInstanceId).active().singleResult();
            if (ObjectUtil.isNotEmpty(currentTask)) {
                taskService.setAssignee(currentTask.getId(), userId);
                taskService.complete(currentTask.getId());
                // 绘制图像
                activitiModelService.queryProHighLighted(processInstanceId);
            }
        }
    }

    @Override
    public void setNextUserTaskApproval(String processInstanceId, String approverId) {
        if (!ToolUtil.isBlank(approverId)) {
            Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).active().singleResult();
            LOGGER.info("set processInstanceId: " + processInstanceId + ", taskId: " + task.getId() + ", approverId is: " + approverId);
            taskService.setAssignee(task.getId(), approverId);
        }
    }

    /**
     * 获取审批人的审批结果，并转成boolean类型
     *
     * @param flag 审批结果
     * @return
     */
    private boolean getApprovedResult(String flag) {
        // 是否通过
        if ("1".equals(flag)) {
            // 通过
            return true;
        } else if ("2".equals(flag)) {
            // 不通过
            return false;
        } else {
            throw new CustomException("approve result 'flag' value is wrong");
        }
    }

    /**
     * 判断节点是否已经拒绝过一次了，如果是，则结束流程
     *
     * @param taskId
     * @param flag
     */
    private Map<String, Object> setWhetherNeedEnd(String taskId, boolean flag) {
        Map<String, Object> bean = new HashMap<>();
        Map<String, Object> variables = taskService.getVariables(taskId);
        // 判断节点是否已经拒绝过一次了
        Object needend = variables.get("needend");
        if (needend != null && (boolean) needend && (!flag)) {
            // 结束
            bean.put("needfinish", -1);
        } else {
            if (flag) {
                // 审批通过，则下一个节点
                bean.put("needfinish", 1);
            } else {
                // 审批不通过
                bean.put("needfinish", 0);
            }
        }
        return bean;
    }

    @Override
    public UserTask getCurrentUserTaskByTaskId(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());

        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        // 当前节点
        UserTask currentUserTask = (UserTask) process.getFlowElement(task.getTaskDefinitionKey());
        return currentUserTask;
    }

    @Override
    public boolean isMultiInstance(String taskId, Map<String, Object> map) {
        UserTask currentTaskNode = this.getCurrentUserTaskByTaskId(taskId);
        // 1.判断工作流模型中的这个节点是否是会签节点
        if (currentTaskNode.getLoopCharacteristics() != null) {
            Map<String, Object> result = managementService.executeCommand(new GetMultiInstanceExecutionMation(taskId));
            map.putAll(result);
            return true;
        }
        return false;
    }

    /**
     * 委派
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void delegateTask(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        // 任务id
        String taskId = map.get("taskId").toString();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task != null) {
            // 被委托人id
            String principalUserId = map.get("principalUserId").toString();
            Map<String, Object> principalUser = iAuthUserService.queryDataMationById(principalUserId);
            // 1.添加审批意见
            String opinion = String.format(Locale.ROOT, "【委派】任务委派给【%s_%s】", principalUser.get("jobNumber").toString(),
                principalUser.get("userName").toString());
            List<Map<String, Object>> leaveList = activitiModelService.getUpLeaveList(user.get("id").toString(),
                user.get("userName").toString(), opinion, true, task, ActivitiConstants.LeaveType.TASK_DELEGATE.getType());
            runtimeService.setVariable(task.getProcessInstanceId(), ActivitiConstants.PROCESSINSTANCEID_TASK_LEAVE_OPINION_LIST_VARABLES, leaveList);
            // 2.设置委派人
            taskService.delegateTask(taskId, principalUserId);
        } else {
            outputObject.setreturnMessage("没有运行时的任务实例,请确认!");
        }
    }

    /**
     * 转办
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void transferTask(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        // 任务id
        String taskId = map.get("taskId").toString();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task != null) {
            // 被转办人id
            String transferredPersonId = map.get("transferredPersonId").toString();
            Map<String, Object> transferredPersonUser = iAuthUserService.queryDataMationById(transferredPersonId);
            // 1.添加审批意见
            String opinion = String.format(Locale.ROOT, "【转办】任务转派给【%s_%s】", transferredPersonUser.get("jobNumber").toString(),
                transferredPersonUser.get("userName").toString());
            List<Map<String, Object>> leaveList = activitiModelService.getUpLeaveList(user.get("id").toString(),
                user.get("userName").toString(), opinion, true, task, ActivitiConstants.LeaveType.TASK_TRANSFER.getType());
            runtimeService.setVariable(task.getProcessInstanceId(), ActivitiConstants.PROCESSINSTANCEID_TASK_LEAVE_OPINION_LIST_VARABLES, leaveList);
            // 2.设置转派人
            taskService.setAssignee(taskId, transferredPersonId);
        } else {
            outputObject.setreturnMessage("没有运行时的任务实例,请确认!");
        }
    }

    /**
     * 前加签
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void beforeAddSignTask(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        // 任务id
        String taskId = map.get("taskId").toString();
        this.addSignTask(taskId, user, false, map.get("chooseUserMation").toString(), outputObject);
    }

    /**
     * 后加签
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void afterAddSignTask(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        // 任务id
        String taskId = map.get("taskId").toString();
        this.addSignTask(taskId, user, true, map.get("chooseUserMation").toString(), outputObject);
    }

    /**
     * 获取会签节点的数据信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void jointlySignTaskDetail(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String taskId = map.get("taskId").toString();
        UserTask currentTaskNode = this.getCurrentUserTaskByTaskId(taskId);
        Map<String, Object> result = new HashMap<>();
        // 多实例节点
        if (currentTaskNode.getLoopCharacteristics() != null) {
            // true：串行多实例节点，false：并行多实例节点
            Boolean isSequential = currentTaskNode.getLoopCharacteristics().isSequential();
            result.put("isSequential", isSequential);
            List<Map<String, Object>> assigneeList = managementService.executeCommand(new FindMultiInstanceExecutionUserCmd(taskId, isSequential));
            result.put("assigneeList", assigneeList);
        }
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private static Map<String, Boolean> buildAssigneeMandatoryMap(List<Map<String, Object>> list) {
        Map<String, Boolean> m = new HashMap<>();
        if (CollectionUtil.isEmpty(list)) {
            return m;
        }
        for (Map<String, Object> u : list) {
            if (u != null && u.get("id") != null) {
                m.put(u.get("id").toString(), isAssigneeMandatoryFlag(u));
            }
        }
        return m;
    }

    private static boolean isAssigneeMandatoryFlag(Map<String, Object> user) {
        if (user == null) {
            return false;
        }
        Object im = user.get("isMandatory");
        return im != null && "1".equals(im.toString());
    }

    /**
     * 必选评审人变更写入意见（与引擎同步逻辑一致：isMandatory 为 1 表示必选）
     */
    private static void appendMandatoryReviewerOpinion(StringBuilder opinion,
                                                       Map<String, Boolean> mandatoryBefore,
                                                       List<Map<String, Object>> afterList) {
        if (CollectionUtil.isEmpty(afterList)) {
            return;
        }
        for (Map<String, Object> u : afterList) {
            if (u == null || u.get("id") == null) {
                continue;
            }
            String id = u.get("id").toString();
            boolean afterM = isAssigneeMandatoryFlag(u);
            boolean beforeM = mandatoryBefore.getOrDefault(id, false);
            if (beforeM == afterM) {
                continue;
            }
            String name = StrUtil.blankToDefault(Objects.toString(u.get("name"), ""), id);
            if (afterM) {
                opinion.append(String.format(Locale.ROOT, "将【%s】设为必选评审人。<br>", name));
            } else {
                opinion.append(String.format(Locale.ROOT, "取消【%s】的必选评审人。<br>", name));
            }
        }
    }

    /**
     * 会签加减签
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void jointlySignAddSignTask(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        // 任务id
        String taskId = map.get("taskId").toString();
        List<Map<String, Object>> addSignUser = JSONArray.fromObject(map.get("chooseUserMation").toString());
        if (CollectionUtil.isEmpty(addSignUser)) {
            outputObject.setreturnMessage("会签人不能为空");
            return;
        }
        // 去重（按id），保序
        LinkedHashMap<String, Map<String, Object>> uniqueUserMap = new LinkedHashMap<>();
        for (Map<String, Object> user : addSignUser) {
            if (user != null && user.get("id") != null) {
                uniqueUserMap.put(user.get("id").toString(), user);
            }
        }
        addSignUser = new ArrayList<>(uniqueUserMap.values());

        Task currentTask = taskService.createTaskQuery().taskId(taskId).singleResult();
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        String hostAssigneeId = currentTask == null ? StrUtil.EMPTY :
            StrUtil.nullToEmpty(flowableTaskDao.queryHostAssigneeByProcessInstanceId(currentTask.getProcessInstanceId(), tenantId));

        // 获取当前会签人，兜底确保主持人不会从提交参数中丢失
        UserTask currentTaskNode = this.getCurrentUserTaskByTaskId(taskId);
        List<Map<String, Object>> currentAssigneeList = managementService.executeCommand(
            new FindMultiInstanceExecutionUserCmd(taskId, currentTaskNode.getLoopCharacteristics().isSequential()));
        Map<String, Object> hostUser = null;
        if (StrUtil.isNotEmpty(hostAssigneeId)) {
            hostUser = currentAssigneeList.stream()
                .filter(bean -> hostAssigneeId.equals(Objects.toString(bean.get("id"), StrUtil.EMPTY)))
                .findFirst().orElse(null);
        } else {
            hostUser = currentAssigneeList.stream()
                .filter(bean -> "1".equals(Objects.toString(bean.get("type"), "0")))
                .findFirst().orElse(null);
        }
        if (hostUser == null) {
            // 数据兜底：先用运行时变量里的主持人ID补齐
            if (StrUtil.isNotEmpty(hostAssigneeId)) {
                Map<String, Object> currentHostUser = iAuthUserService.queryDataMationById(hostAssigneeId);
                if (currentHostUser != null && !currentHostUser.isEmpty()) {
                    currentHostUser.put("id", hostAssigneeId);
                    currentHostUser.put("type", 1);
                    currentHostUser.put("noDelete", true);
                    currentHostUser.put("isActive", true);
                    hostUser = currentHostUser;
                    // 同步补到当前会签人列表，保证后续“删除差异计算”一致
                    currentAssigneeList.add(currentHostUser);
                }
            }
        }
        if (hostUser != null && hostUser.get("id") != null) {
            String hostId = hostUser.get("id").toString();
            boolean hostExists = addSignUser.stream().anyMatch(item -> hostId.equals(Objects.toString(item.get("id"), "")));
            if (!hostExists) {
                addSignUser.add(hostUser);
            }
        }

        // 1.获取回显的没有修改过的数据（同时过滤掉不能删除的数据）
        List<Map<String, Object>> echoList = addSignUser.stream()
            .filter(bean -> (bean.containsKey("noDelete") && !(Boolean) bean.get("noDelete")) || !bean.containsKey("noDelete"))
            .filter(bean -> bean.containsKey("echo") && Boolean.TRUE.equals(bean.get("echo"))).collect(Collectors.toList());
        List<String> echoUserIds = echoList.stream().map(p -> p.get("id").toString()).collect(Collectors.toList());
        // 2.获取新增的数据
        List<Map<String, Object>> newAddList = addSignUser.stream()
            .filter(bean -> !bean.containsKey("echo")).collect(Collectors.toList());
        // 3.获取现有的会签人
        List<Map<String, Object>> assigneeList = currentAssigneeList;
        // 3.1过滤掉不能删除的对象
        assigneeList = assigneeList.stream().filter(
                bean -> (bean.containsKey("noDelete") && !(Boolean) bean.get("noDelete")) || !bean.containsKey("noDelete"))
            .collect(Collectors.toList());
        // 保存前必选状态：必须用未过滤 noDelete 的完整列表（当前审批人/主持人等也会带 noDelete，过滤后会丢必选状态导致无法生成「取消必选」意见）
        Map<String, Boolean> mandatoryBefore = buildAssigneeMandatoryMap(currentAssigneeList);
        // 4.获取被删掉的会签人（现有的会签人-回显的没有修改过的数据）
        List<Map<String, Object>> newDeleteList = assigneeList.stream()
            .filter(item -> !echoUserIds.contains(item.get("id").toString())).collect(Collectors.toList());

        StringBuilder opinion = new StringBuilder("【会签】：<br>");
        // 删除会签人
        List<String> newDeleteUserIds = newDeleteList.stream().map(p -> p.get("id").toString()).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(newDeleteUserIds)) {
            managementService.executeCommand(new DeleteMultiInstanceExecutionCmd(taskId, newDeleteUserIds));
            opinion.append("删除了");
            for (Map<String, Object> bean : newDeleteList) {
                opinion.append(String.format(Locale.ROOT, "【%s】", bean.get("name").toString()));
            }
            opinion.append("的会签。<br>");
        }

        // 新增会签人
        List<String> newAddUserIds = newAddList.stream().map(p -> p.get("id").toString()).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(newAddUserIds)) {
            managementService.executeCommand(new AddMultiInstanceExecutionCmd(taskId, newAddList, addSignUser));
            managementService.executeCommand(new NewMultiInstanceExecutionSetAssignee(taskId, newAddUserIds));
            opinion.append("新增了");
            for (Map<String, Object> bean : newAddList) {
                opinion.append(String.format(Locale.ROOT, "【%s】", bean.get("name").toString()));
            }
            opinion.append("的会签。<br>");
        }

        appendMandatoryReviewerOpinion(opinion, mandatoryBefore, addSignUser);

        // 同步必选评审人（含仅修改 echo 行开关、未删未增的场景）
        managementService.executeCommand(new SyncMultiInstanceMandatoryFlagsCmd(taskId, addSignUser));

        // 添加加签操作历史
        Map<String, Object> user = inputObject.getLogParams();
        Task taskInfo = taskService.createTaskQuery().taskId(taskId).singleResult();
        List<Map<String, Object>> leaveList = activitiModelService.getUpLeaveList(user.get("id").toString(),
            user.get("userName").toString(), opinion.toString(), true, taskInfo, ActivitiConstants.LeaveType.JOINTLY_SIGN_TASK.getType());
        runtimeService.setVariable(taskInfo.getProcessInstanceId(), ActivitiConstants.PROCESSINSTANCEID_TASK_LEAVE_OPINION_LIST_VARABLES, leaveList);
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void jointlySignCancelTask(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String taskId = map.get("taskId").toString();
        String userId = inputObject.getLogParams().get("id").toString();

        UserTask currentTaskNode = this.getCurrentUserTaskByTaskId(taskId);
        if (currentTaskNode == null || currentTaskNode.getLoopCharacteristics() == null) {
            outputObject.setreturnMessage("当前节点不是会签节点，无法取消会签。");
            return;
        }
        boolean isSequential = currentTaskNode.getLoopCharacteristics().isSequential();
        List<Map<String, Object>> assigneeList = managementService.executeCommand(
            new FindMultiInstanceExecutionUserCmd(taskId, isSequential));
        if (CollectionUtil.isEmpty(assigneeList)) {
            outputObject.setreturnMessage("未获取到会签人员，无法取消会签。");
            return;
        }

        Map<String, Object> hostUser = assigneeList.stream()
            .filter(bean -> "1".equals(Objects.toString(bean.get("type"), "0")))
            .findFirst().orElse(null);
        if (hostUser == null || !userId.equals(Objects.toString(hostUser.get("id"), ""))) {
            outputObject.setreturnMessage("仅会签主持人可执行取消会签。");
            return;
        }

        List<Map<String, Object>> participantList = assigneeList.stream()
            .filter(bean -> !"1".equals(Objects.toString(bean.get("type"), "0")))
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(participantList)) {
            outputObject.setreturnMessage("当前已无可取消的会签参与人。");
            return;
        }

        long approvedCount = participantList.stream()
            .filter(bean -> bean.containsKey("isActive") && !Boolean.TRUE.equals(bean.get("isActive")))
            .count();
        if (approvedCount > 0) {
            outputObject.setreturnMessage("已有会签参与人完成评审，不能取消会签。");
            return;
        }

        List<String> deleteUserIds = participantList.stream()
            .map(bean -> Objects.toString(bean.get("id"), ""))
            .filter(StrUtil::isNotEmpty)
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(deleteUserIds)) {
            outputObject.setreturnMessage("当前已无可取消的会签参与人。");
            return;
        }

        Task taskBefore = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (taskBefore == null) {
            outputObject.setreturnMessage("没有此任务，请确认!");
            return;
        }
        String processInstanceId = taskBefore.getProcessInstanceId();

        managementService.executeCommand(new DeleteMultiInstanceExecutionCmd(taskId, deleteUserIds));

        removeMultiInstanceHostAssigneeFromProcess(processInstanceId);

        Task taskInfo = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (taskInfo != null) {
            taskService.setAssignee(taskId, userId);
            String opinion = "【会签】：取消会签，移除了全部参与人，仅保留主持人。";
            Map<String, Object> user = inputObject.getLogParams();
            List<Map<String, Object>> leaveList = activitiModelService.getUpLeaveList(user.get("id").toString(),
                user.get("userName").toString(), opinion, true, taskInfo, ActivitiConstants.LeaveType.JOINTLY_SIGN_TASK.getType());
            runtimeService.setVariable(taskInfo.getProcessInstanceId(), ActivitiConstants.PROCESSINSTANCEID_TASK_LEAVE_OPINION_LIST_VARABLES, leaveList);
        }
    }

    /**
     * 任务加签
     *
     * @param taskId             任务id
     * @param userMation         操作人用户信息
     * @param flag               true向后加签  false向前加签
     * @param addSignUserJsonStr 加签用户的json串
     * @param outputObject       出参以及提示信息的返回值对象
     */
    public String addSignTask(String taskId, Map<String, Object> userMation, Boolean flag, String addSignUserJsonStr, OutputObject outputObject) {
        TaskEntityImpl taskEntity = (TaskEntityImpl) taskService.createTaskQuery().taskId(taskId).singleResult();
        // 1.把当前的节点设置为空
        if (taskEntity != null) {
            String userId = userMation.get("id").toString();
            taskEntity.setOwner(userId);
            taskEntity.setAssignee(null);
            taskEntity.setCountEnabled(true);
            if (flag) {
                taskEntity.setScopeType(ActivitiConstants.AFTER_ADDSIGN);
            } else {
                taskEntity.setScopeType(ActivitiConstants.BEFORE_ADDSIGN);
            }
            // 1.2 设置任务为空执行者
            taskService.saveTask(taskEntity);
            // 2.添加加签数据
            this.createSignSubTasks(addSignUserJsonStr, taskEntity, userMation);
            return taskEntity.getProcessInstanceId();
        } else {
            outputObject.setreturnMessage("不存在任务实例，请确认!");
        }
        return StringUtils.EMPTY;
    }

    /**
     * 创建加签子任务
     *
     * @param addSignUserJsonStr 加签参数
     * @param taskEntity         父任务
     * @param userMation         操作人用户信息
     */
    private void createSignSubTasks(String addSignUserJsonStr, TaskEntity taskEntity, Map<String, Object> userMation) {
        List<Map<String, Object>> addSignUser = JSONArray.fromObject(addSignUserJsonStr);
        if (CollectionUtil.isNotEmpty(addSignUser)) {
            String userId = userMation.get("id").toString();
            String parentTaskId = taskEntity.getParentTaskId();
            if (StringUtils.isBlank(parentTaskId)) {
                parentTaskId = taskEntity.getId();
            }
            String finalParentTaskId = parentTaskId;
            String userNames = "";
            // 1.创建被加签人的任务列表
            for (Map<String, Object> bean : addSignUser) {
                String signUserId = bean.get("id").toString();
                if (StringUtils.isNotBlank(signUserId)) {
                    this.createSubTask(taskEntity, finalParentTaskId, signUserId);
                    userNames += bean.get("name").toString() + "; ";
                }
            }
            String taskId = taskEntity.getId();
            if (StringUtils.isBlank(taskEntity.getParentTaskId())) {
                // 2.创建加签人的任务并执行完毕
                Task task = this.createSubTask(taskEntity, finalParentTaskId, userId);
                taskId = task.getId();
            }
            Task taskInfo = taskService.createTaskQuery().taskId(taskId).singleResult();
            // 添加加签操作历史
            String opinion = getAddSignTaskOpinion(taskEntity, userNames);
            List<Map<String, Object>> leaveList = activitiModelService.getUpLeaveList(userId,
                userMation.get("userName").toString(), opinion, true, taskInfo, getAddSignTaskLeaveType(taskEntity));
            runtimeService.setVariable(taskInfo.getProcessInstanceId(), ActivitiConstants.PROCESSINSTANCEID_TASK_LEAVE_OPINION_LIST_VARABLES, leaveList);
            if (null != taskInfo) {
                taskService.complete(taskId);
            }
        }
    }

    private String getAddSignTaskOpinion(TaskEntity taskEntity, String userNames) {
        String opinion = "";
        if (ActivitiConstants.AFTER_ADDSIGN.equals(taskEntity.getScopeType())) {
            // 后加签
            opinion = String.format(Locale.ROOT, "【加签】为【%s】进行后加签.", userNames);
        } else if (ActivitiConstants.BEFORE_ADDSIGN.equals(taskEntity.getScopeType())) {
            // 前加签
            opinion = String.format(Locale.ROOT, "【加签】为【%s】进行前加签.", userNames);
        }
        return opinion;
    }

    private Integer getAddSignTaskLeaveType(TaskEntity taskEntity) {
        Integer type = null;
        if (ActivitiConstants.AFTER_ADDSIGN.equals(taskEntity.getScopeType())) {
            // 后加签
            type = ActivitiConstants.LeaveType.AFTER_ADD_SIGN_TASK.getType();
        } else if (ActivitiConstants.BEFORE_ADDSIGN.equals(taskEntity.getScopeType())) {
            // 前加签
            type = ActivitiConstants.LeaveType.BEFORE_ADD_SIGN_TASK.getType();
        }
        return type;
    }

    /**
     * 创建子任务
     *
     * @param ptask    创建子任务
     * @param assignee 子任务的执行人
     * @return
     */
    protected TaskEntity createSubTask(TaskEntity ptask, String ptaskId, String assignee) {
        TaskEntity task = null;
        if (ptask != null) {
            // 1.生成子任务
            task = (TaskEntity) taskService.newTask(ToolUtil.getSurFaceId());
            task.setCategory(ptask.getCategory());
            task.setDescription(ptask.getDescription());
            task.setTenantId(ptask.getTenantId());
            task.setAssignee(assignee);
            task.setName(ptask.getName());
            task.setParentTaskId(ptaskId);
            task.setProcessDefinitionId(ptask.getProcessDefinitionId());
            task.setProcessInstanceId(ptask.getProcessInstanceId());
            task.setTaskDefinitionKey(ptask.getTaskDefinitionKey());
            task.setTaskDefinitionId(ptask.getTaskDefinitionId());
            task.setPriority(ptask.getPriority());
            task.setCreateTime(new Date());
            taskService.saveTask(task);
        }
        return task;
    }

}
