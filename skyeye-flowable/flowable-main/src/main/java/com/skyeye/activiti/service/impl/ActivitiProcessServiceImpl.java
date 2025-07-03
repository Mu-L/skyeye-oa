/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.base.Joiner;
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
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
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

import java.io.InputStream;
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

    /**
     * 启动流程时获取流程下一个用户节点的审批人
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
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
        List<Map<String, Object>> user = this.nextPrcessApproverByProcessDefinitionKey(modelKey);
        outputObject.setBeans(user);
    }

    public List<Map<String, Object>> nextPrcessApproverByProcessDefinitionKey(String processDefinitionKey) {
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        List<Map<String, Object>> user = new ArrayList<>();
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey);
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
                user = getUserTaskList(processDefinition.get(0));
            }
        }
        return user;
    }

    public List<Map<String, Object>> getUserTaskList(ProcessDefinition processDefinition) {
        String deploymentId = processDefinition.getDeploymentId();
        // 实现读写bpmn文件信息
        InputStream bpmnIs = repositoryService.getResourceAsStream(deploymentId, processDefinition.getResourceName());
        SAXReader saxReader = new SAXReader();
        // 获取流程图文件中的userTask节点的所有属性
        Document document = null;
        try {
            document = saxReader.read(bpmnIs);
        } catch (DocumentException ee) {
            throw new CustomException(ee);
        }
        Element rootElement = document.getRootElement();
        Element process = rootElement.element("process");
        List<Element> userTaskList = process.elements("userTask");

        List<Map<String, Object>> list = new ArrayList<>();
        // 获取第一个用户任务节点
        if (CollectionUtil.isNotEmpty(userTaskList)) {
            Element element = userTaskList.get(0);
            String assignee = element.attributeValue(ActivitiConstants.ASSIGNEE_USER);
            String candidateUsers = element.attributeValue("candidateUsers");
            String candidateGroups = element.attributeValue("candidateGroups");

            if (!ToolUtil.isBlank(candidateGroups)) {
                List<ActGroupUser> actGroupUserList = actGroupUserService.queryActGroupUser(Arrays.asList(candidateGroups.split(CommonCharConstants.COMMA_MARK)));
                actGroupUserList.forEach(bean -> {
                    list.add(bean.getUserMation());
                });
            }
            // 2.候选人员获取
            if (!ToolUtil.isBlank(candidateUsers)) {
                Arrays.asList(candidateUsers.split(CommonCharConstants.COMMA_MARK)).forEach(userId -> {
                    Map<String, Object> userMation = iAuthUserService.queryDataMationById(userId);
                    list.add(userMation);
                });
            }
            // 3.代理人获取
            if (!ToolUtil.isBlank(assignee)) {
                Map<String, Object> userMation = iAuthUserService.queryDataMationById(assignee);
                list.add(userMation);
            }
        }
        // 使用 Stream API 进行过滤、排序并最终收集到 ArrayList 中
        List<Map<String, Object>> resultList = list.stream()
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
        return resultList;
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
