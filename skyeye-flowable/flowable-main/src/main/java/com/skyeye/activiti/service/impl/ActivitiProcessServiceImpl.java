/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.base.Joiner;
import com.skyeye.activiti.cmd.nextusertask.FindFirstUserTaskByConditionCmd;
import com.skyeye.activiti.cmd.nextusertask.FindNextUserTaskNodeCmd;
import com.skyeye.activiti.cmd.rollback.RollbackCmd;
import com.skyeye.activiti.entity.NextTaskInfo;
import com.skyeye.activiti.service.ActivitiModelService;
import com.skyeye.activiti.service.ActivitiProcessService;
import com.skyeye.activiti.service.ActivitiTaskService;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.FlowableUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.entity.ActFlowMation;
import com.skyeye.eve.entity.ActGroupUser;
import com.skyeye.eve.flowable.classenum.FormSubType;
import com.skyeye.eve.flowable.entity.FlowableSubData;
import com.skyeye.eve.service.ActFlowService;
import com.skyeye.eve.service.ActGroupUserService;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.exception.CustomException;
import com.skyeye.userprocess.entity.ActUserProcess;
import com.skyeye.userprocess.service.ActUserProcessService;
import net.sf.json.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ModelQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.task.api.Task;
import org.nutz.trans.Trans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ActivitiProcessServiceImpl
 * @Description: 工作流流程相关操作
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/2 21:29
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ActivitiProcessServiceImpl implements ActivitiProcessService {

    private static Logger LOGGER = LoggerFactory.getLogger(ActivitiProcessServiceImpl.class);

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ActivitiModelService activitiModelService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Autowired
    private ActivitiTaskService activitiTaskService;

    @Autowired
    private ManagementService managementService;

    @Autowired
    private ActFlowService actFlowService;

    @Autowired
    private ActivitiService activitiService;

    @Autowired
    private ActUserProcessService actUserProcessService;

    @Autowired
    private ActGroupUserService actGroupUserService;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    /**
     * 流程挂起
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void updateProcessToHangUp(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String processInstanceId = map.get("processInstanceId").toString();
        // 根据一个流程实例的id挂起该流程实例
        runtimeService.suspendProcessInstanceById(processInstanceId);
    }

    /**
     * 流程激活
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void updateProcessToActivation(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String processInstanceId = map.get("processInstanceId").toString();
        // 根据一个流程实例的id激活该流程实例
        runtimeService.activateProcessInstanceById(processInstanceId);
    }

    /**
     * 流程撤回(撤回审批过的流程)
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void editProcessInstanceWithDraw(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        String userId = user.get("id").toString();
        String processInstanceId = map.get("processInstanceId").toString();
        String hisTaskId = map.get("hisTaskId").toString();
        // 根据流程id查询代办任务中流程信息
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        if (task == null) {
            outputObject.setreturnMessage("流程未启动或已执行完成，无法撤回");
            return;
        }
        // 撤回
        managementService.executeCommand(new RollbackCmd(hisTaskId, userId));
        // 绘制图像
        activitiModelService.queryProHighLighted(processInstanceId);
    }

    /**
     * 刷新流程图
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void editProcessInstancePicToRefresh(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        // 绘制图像
        activitiModelService.queryProHighLighted(map.get("processInstanceId").toString());
    }

    /**
     * 获取流程下一个节点的审批人
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void nextPrcessApprover(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String taskId = params.get("taskId").toString();
        String processInstanceId = params.get("processInstanceId").toString();
        // 获取表单数据用于排他网关的参数校验
        Map<String, Object> map = getFormVariable(processInstanceId, params);
        NextTaskInfo nextTaskInfo = this.getNextTaskInfo(taskId, map);
        if (nextTaskInfo != null && nextTaskInfo.getUserTask() != null) {
            // 1.获取下个节点的所有可选审批人
            List<Map<String, Object>> user = this.getNextTaskApprove(nextTaskInfo.getUserTask());
            outputObject.setBeans(user);
            // 2.获取节点信息
            Map<String, Object> nodeMation = new HashMap<>();
            nodeMation.put("nodeName", nextTaskInfo.getUserTask().getName());
            nodeMation.put("nodeType", ActivitiConstants.USER_TASK);
            outputObject.setBean(nodeMation);
        }
    }

    /**
     * 获取表单数据用于排他网关的参数校验
     *
     * @param processInstanceId 流程id
     * @param inputParams       入参
     * @return
     */
    private Map<String, Object> getFormVariable(String processInstanceId, Map<String, Object> inputParams) {
        Map<String, Object> variable = new HashMap<>();
        Map<String, Object> params = activitiTaskService.getCurrentTaskParamsByTaskId(processInstanceId);
        for (String key : params.keySet()) {
            if (params.get(key) == null) {
                continue;
            }
            String str = params.get(key).toString();
            if (ToolUtil.isJson(str)) {
                Map<String, Object> formItemMation = JSONObject.fromObject(str);
                variable.put(key, formItemMation.containsKey("value") ? formItemMation.get("value") : StringUtils.EMPTY);
            }
        }
        // 审批结果
        if (!ToolUtil.isBlank(inputParams.get("flag").toString())) {
            variable.put("flag", inputParams.get("flag"));
        }
        return variable;
    }

    private List<Map<String, Object>> getNextTaskApprove(UserTask userTask) {
        List<Map<String, Object>> user = new ArrayList<>();
        // 1.候选组人员获取
        List<String> groupIds = userTask.getCandidateGroups();
        if (CollectionUtils.isNotEmpty(groupIds)) {
            List<ActGroupUser> actGroupUserList = actGroupUserService.queryActGroupUser(groupIds);
            actGroupUserList.forEach(bean -> {
                user.add(bean.getUserMation());
            });
        }
        // 2.候选人员获取
        List<String> userIds = userTask.getCandidateUsers();
        if (CollectionUtils.isNotEmpty(userIds)) {
            userIds.forEach(userId -> {
                Map<String, Object> userMation = iAuthUserService.queryDataMationById(userId);
                user.add(userMation);
            });
        }
        // 3.代理人获取
        String assignee = userTask.getAssignee();
        if (assignee != null) {
            Map<String, Object> userMation = iAuthUserService.queryDataMationById(assignee);
            user.add(userMation);
        }
        user.removeAll(Collections.singleton(null));
        return user;
    }

    /**
     * 获取下一个用户任务信息
     *
     * @param taskId 任务Id信息
     * @param map    表单数据
     * @return 下一个用户任务用户组信息
     */
    @Override
    public NextTaskInfo getNextTaskInfo(String taskId, Map<String, Object> map) {
        try {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (StringUtils.isNotEmpty(task.getParentTaskId())) {
                // 如果是加签节点
                task = getRealTask(task);
            }
            String executionId = task.getExecutionId();
            if (StringUtils.isNotEmpty(executionId)) {
                ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(executionId).singleResult();
                BpmnModel bpmnModel = repositoryService.getBpmnModel(execution.getProcessDefinitionId());
                Trans.begin();
                NextTaskInfo nextTaskInfo = new NextTaskInfo();
                nextTaskInfo.setUserTask(managementService.executeCommand(new FindNextUserTaskNodeCmd(execution, bpmnModel, map)));
                return nextTaskInfo;
            }
        } catch (Exception ee) {
            LOGGER.warn("getNextTaskInfo error, because {}", ee);
        } finally {
            Trans.clear(true);
        }
        return null;
    }

    private Task getRealTask(Task task) {
        String parentTaskId = task.getParentTaskId();
        long subTaskCount = activitiTaskService.getSubTaskCount(parentTaskId);
        if ((subTaskCount - 1) == 0) {
            // 说明是最后一个后加签子任务
            Task parentTask = taskService.createTaskQuery().taskId(parentTaskId).singleResult();
            if (ActivitiConstants.AFTER_ADDSIGN.equals(parentTask.getScopeType())) {
                return parentTask;
            }
        }
        return task;
    }

    @Override
    public void nextPrcessApproverByProcessDefinitionKey(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String modelKey = params.get("modelKey").toString();
        // 获取业务数据
        String businessDataStr = params.get("businessData").toString();
        Map<String, Object> businessData = null;
        if (!ToolUtil.isBlank(businessDataStr)) {
            businessData = JSONObject.fromObject(businessDataStr);
        }

        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        List<Map<String, Object>> user = new ArrayList<>();
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().processDefinitionKey(modelKey);
        if (tenantEnable) {
            processDefinitionQuery.processDefinitionTenantId(tenantId);
        }
        List<ProcessDefinition> processDefinition = processDefinitionQuery.list();
        if (processDefinition != null) {
            ModelQuery modelQuery = repositoryService.createModelQuery();
            if (tenantEnable) {
                modelQuery.modelTenantId(tenantId);
            }
            List<Model> beans = modelQuery.latestVersion().orderByLastUpdateTime().desc().list();
            List<String> deploymentIds = beans.stream().map(p -> p.getDeploymentId()).collect(Collectors.toList());
            processDefinition = processDefinition.stream().filter(bean -> deploymentIds.contains(bean.getDeploymentId())).collect(Collectors.toList());
            if (processDefinition != null && !processDefinition.isEmpty()) {
                user = getFistUserTaskUserList(processDefinition.get(0), businessData);
            }
        }
        outputObject.setBeans(user);
    }

    @Override
    public void nextPrcessDefaultApproverByProcessDefinitionKey(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String modelKey = params.get("modelKey").toString();
        // 获取业务数据
        String businessDataStr = params.get("businessData").toString();
        Map<String, Object> businessData = null;
        if (!ToolUtil.isBlank(businessDataStr)) {
            businessData = JSONObject.fromObject(businessDataStr);
        }
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        Map<String, Object> user = new HashMap<>();
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().processDefinitionKey(modelKey);
        if (tenantEnable) {
            processDefinitionQuery.processDefinitionTenantId(tenantId);
        }
        List<ProcessDefinition> processDefinition = processDefinitionQuery.list();
        if (processDefinition != null) {
            ModelQuery modelQuery = repositoryService.createModelQuery();
            if (tenantEnable) {
                modelQuery.modelTenantId(tenantId);
            }
            List<Model> beans = modelQuery.latestVersion().orderByLastUpdateTime().desc().list();
            List<String> deploymentIds = beans.stream().map(p -> p.getDeploymentId()).collect(Collectors.toList());
            processDefinition = processDefinition.stream().filter(bean -> deploymentIds.contains(bean.getDeploymentId())).collect(Collectors.toList());
            if (processDefinition != null && !processDefinition.isEmpty()) {
                user = getFirstUserTaskDefaultUser(processDefinition.get(0), businessData);
            }
        }
        outputObject.setBean(user);
    }

    /**
     * 根据业务数据获取第一个符合条件的用户任务节点的所有审批人列表
     * 
     * <p><b>功能说明：</b></p>
     * <ul>
     *   <li>从流程的开始节点开始，根据 businessData 中的值判断条件表达式</li>
     *   <li>找到第一个符合条件的用户任务节点</li>
     *   <li>返回该用户任务节点的所有审批人（包括 assignee、candidateUsers、candidateGroups）</li>
     * </ul>
     * 
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>流程启动前，需要根据业务数据确定第一个审批人列表</li>
     *   <li>流程中有多个分支，需要根据条件选择不同的审批路径</li>
     * </ul>
     * 
     * @param processDefinition 流程定义
     * @param businessData 业务数据，用于判断条件表达式。key 为变量名，value 为变量值
     * @return 审批人列表，如果未找到则返回空列表
     */
    public List<Map<String, Object>> getFistUserTaskUserList(ProcessDefinition processDefinition, Map<String, Object> businessData) {
        try {
            // 获取 BPMN 模型
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
            if (bpmnModel == null || bpmnModel.getProcesses().isEmpty()) {
                LOGGER.warn("无法获取 BPMN 模型，流程定义ID: {}", processDefinition.getId());
                return new ArrayList<>();
            }

            // 使用 ManagementService 执行 Command 来查找第一个用户任务节点
            FindFirstUserTaskByConditionCmd cmd = new FindFirstUserTaskByConditionCmd(
                    bpmnModel, 
                    businessData != null ? businessData : new HashMap<>(), 
                    processDefinition.getId()
                );
            
            UserTask firstUserTask = managementService.executeCommand(cmd);
            if (firstUserTask == null) {
                return new ArrayList<>();
            }

            // 第一步：收集所有需要查询的ID（先构造数据，避免多次数据库操作）
            Set<String> allGroupIds = new HashSet<>();  // 所有组ID（去重）
            Set<String> allUserIds = new HashSet<>();  // 所有用户ID（去重）
            
            // 1. 收集候选组ID
            List<String> candidateGroups = firstUserTask.getCandidateGroups();
            if (CollectionUtils.isNotEmpty(candidateGroups)) {
                for (String groupId : candidateGroups) {
                    // 评估表达式（如果 candidateGroups 是表达式）
                    if (FlowableUtil.isExpression(groupId)) {
                        Object evaluated = FlowableUtil.evaluateExpression(groupId, businessData);
                        if (evaluated != null) {
                            groupId = evaluated.toString();
                        }
                    }
                    if (!ToolUtil.isBlank(groupId)) {
                        allGroupIds.add(groupId);
                    }
                }
            }
            
            // 2. 收集候选用户ID
            List<String> candidateUsers = firstUserTask.getCandidateUsers();
            if (CollectionUtils.isNotEmpty(candidateUsers)) {
                for (String userId : candidateUsers) {
                    // 评估表达式（如果 candidateUsers 是表达式）
                    if (FlowableUtil.isExpression(userId)) {
                        userId = FlowableUtil.evaluateExpressionAsString(userId, businessData);
                    }
                    if (!ToolUtil.isBlank(userId)) {
                        allUserIds.add(userId);
                    }
                }
            }
            
            // 3. 收集代理人ID
            String assignee = firstUserTask.getAssignee();
            if (!ToolUtil.isBlank(assignee)) {
                // 如果 assignee 是表达式，需要评估
                if (FlowableUtil.isExpression(assignee)) {
                    assignee = FlowableUtil.evaluateExpressionAsString(assignee, businessData);
                }
                if (!ToolUtil.isBlank(assignee)) {
                    allUserIds.add(assignee);
                }
            }

            // 第二步：批量查询数据（减少数据库操作次数）
            List<Map<String, Object>> userList = new ArrayList<>();
            Set<String> processedUserIds = new HashSet<>();  // 已处理的用户ID（用于去重）
            
            // 批量查询组用户
            if (!allGroupIds.isEmpty()) {
                List<ActGroupUser> actGroupUserList = actGroupUserService.queryActGroupUser(new ArrayList<>(allGroupIds));
                if (CollectionUtils.isNotEmpty(actGroupUserList)) {
                    actGroupUserList.forEach(bean -> {
                        Map<String, Object> userMation = bean.getUserMation();
                        if (CollectionUtil.isNotEmpty(userMation)) {
                            String userId = userMation.get("id").toString();
                            // 避免重复添加（组用户和直接指定的用户可能重复）
                            if (!processedUserIds.contains(userId)) {
                                userList.add(userMation);
                                processedUserIds.add(userId);
                                // 从 allUserIds 中移除已处理的用户ID，避免重复查询
                                allUserIds.remove(userId);
                            }
                        }
                    });
                }
            }
            
            // 批量查询剩余的用户信息（排除已从组用户中获取的用户）
            if (!allUserIds.isEmpty()) {
                String userIdsStr = Joiner.on(CommonCharConstants.COMMA_MARK).join(allUserIds);
                List<Map<String, Object>> users = iAuthUserService.queryDataMationByIds(userIdsStr);
                if (CollectionUtils.isNotEmpty(users)) {
                    users.forEach(user -> {
                        Object userId = user != null ? user.get("id") : null;
                        if (userId != null && !processedUserIds.contains(userId.toString())) {
                            userList.add(user);
                            processedUserIds.add(userId.toString());
                        }
                    });
                }
            }
            
            // 使用 Stream API 进行过滤、排序并最终收集到 ArrayList 中
            List<Map<String, Object>> resultList = userList.stream()
                .filter(Objects::nonNull)  // 确保 item 不是 null
                .map(item -> item.entrySet().stream()
                    .filter(entry -> entry != null && entry.getKey() != null &&
                        entry.getValue() != null && !"".equals(entry.getValue()))
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> existing,  // 解决重复键的问题
                        LinkedHashMap::new))  // 保持插入顺序
                )
                .filter(Objects::nonNull)  // 确保转换后的 map 不是 null
                .sorted(Comparator.comparing(p -> String.valueOf(p.get("id")), Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.collectingAndThen(
                    Collectors.toCollection(ArrayList::new),
                    ArrayList::new
                ));
            
            // 根据 userId 去除重复的审批人
            resultList = resultList.stream()
                .collect(Collectors.collectingAndThen(
                     Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(p -> String.valueOf(p.get("id")), Comparator.nullsLast(Comparator.naturalOrder())))),
                     ArrayList::new
                 ));
            
            return resultList;
            
        } catch (Exception e) {
            LOGGER.error("获取第一个用户任务节点审批人列表异常，流程定义ID: {}", processDefinition.getId(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据业务数据获取第一个符合条件的用户任务节点的默认审批人
     * 
     * <p><b>功能说明：</b></p>
     * <ul>
     *   <li>从流程的开始节点开始，根据 businessData 中的值判断条件表达式</li>
     *   <li>找到第一个符合条件的用户任务节点</li>
     *   <li>返回该用户任务节点的默认审批人</li>
     * </ul>
     * 
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>流程启动前，需要根据业务数据确定第一个审批人</li>
     *   <li>流程中有多个分支，需要根据条件选择不同的审批路径</li>
     * </ul>
     * 
     * <p><b>示例：</b></p>
     * <pre>{@code
     * // businessData 示例
     * Map<String, Object> businessData = new HashMap<>();
     * businessData.put("amount", 5000);  // 金额
     * businessData.put("dept", "finance");  // 部门
     * 
     * // 如果流程中有条件：${amount > 1000}，会根据 businessData 中的 amount 值判断
     * Map<String, Object> user = getFirstUserTaskDefaultUser(processDefinition, businessData);
     * }</pre>
     * 
     * @param processDefinition 流程定义
     * @param businessData 业务数据，用于判断条件表达式。key 为变量名，value 为变量值
     * @return 用户信息，如果未找到则返回null
     */
    public Map<String, Object> getFirstUserTaskDefaultUser(ProcessDefinition processDefinition, Map<String, Object> businessData) {
        try {
            // 获取 BPMN 模型
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
            if (bpmnModel == null || bpmnModel.getProcesses().isEmpty()) {
                LOGGER.warn("无法获取 BPMN 模型，流程定义ID: {}", processDefinition.getId());
                return null;
            }

            // 使用 ManagementService 执行 Command 来查找第一个用户任务节点
            // 这样可以确保 ExecutionEntity 在正确的上下文中创建
            FindFirstUserTaskByConditionCmd cmd = new FindFirstUserTaskByConditionCmd(
                    bpmnModel, 
                    businessData != null ? businessData : new HashMap<>(), 
                    processDefinition.getId()
                );
            
            UserTask firstUserTask = managementService.executeCommand(cmd);
            
            if (firstUserTask != null) {
                // 获取用户任务节点的 assignee 属性
                String assignee = firstUserTask.getAssignee();
                if (!ToolUtil.isBlank(assignee)) {
                    // 如果 assignee 是表达式，需要评估
                    if (FlowableUtil.isExpression(assignee)) {
                        assignee = FlowableUtil.evaluateExpressionAsString(assignee, businessData);
                    }
                    if (!ToolUtil.isBlank(assignee)) {
                        Map<String, Object> userMation = iAuthUserService.queryDataMationById(assignee);
                        return userMation;
                    }
                }
            }

            return null;
        } catch (Exception e) {
            LOGGER.error("获取第一个用户任务节点默认审批人异常，流程定义ID: {}", processDefinition.getId(), e);
            throw new CustomException("获取第一个用户任务节点默认审批人失败: " + e.getMessage());
        }
    }

    /**
     * 启动流程
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void startProcess(InputObject inputObject, OutputObject outputObject) {
        FlowableSubData flowableSubData = inputObject.getParams(FlowableSubData.class);
        Integer formSubType = flowableSubData.getFormSubType();
        if (!formSubType.equals(FormSubType.SUB_FLOWABLE.getKey())) {
            return;
        }
        // 根据业务数据和className获取配置的工作流key,如果actModel没有配置，则无法提交审批
        ActFlowMation actFlowMation = actFlowService.getActFlowByModelKey(flowableSubData.getModelKey());
        if (actFlowMation != null) {
            LOGGER.info("actFlow mation is: " + JSONUtil.toJsonStr(actFlowMation));
            // 提交审批
            String processInstanceId = activitiModelService.startProcess(flowableSubData, actFlowMation);
            Map<String, Object> result = new HashMap<>();
            result.put("processInstanceId", processInstanceId);
            outputObject.setBean(result);
        } else {
            throw new CustomException("该功能暂未配置工作流，请联系管理员进行配置。");
        }
    }

    /**
     * 撤销流程
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void revokeProcess(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String processInstanceId = params.get("processInstanceId").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        activitiService.revokeByProcessInstanceId(processInstanceId, userId);
    }

    /**
     * 根据流程实例id获取流程信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryProcessInstance(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String processInstanceId = params.get("processInstanceId").toString();
        // 获取流程信息
        ActUserProcess userProcess = actUserProcessService.selectByProcessInstanceId(processInstanceId);

        // 任务信息
        Map<String, Object> task = new HashMap<>();
        // 任务-当前审批人
        List<String> assignee = activitiTaskService.getTaskAssignee(processInstanceId);
        task.put("taskCurrentAssignee", assignee);
        if (CollectionUtil.isNotEmpty(assignee)) {
            task.put("taskCurrentAssigneeMation", iAuthUserService.queryDataMationByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(assignee)));
        }
        userProcess.setTask(task);

        outputObject.setBean(userProcess);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

}
