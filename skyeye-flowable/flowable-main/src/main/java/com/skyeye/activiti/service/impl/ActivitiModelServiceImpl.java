/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.skyeye.activiti.flowimg.FlowImgService;
import com.skyeye.activiti.mapper.ActivityMapper;
import com.skyeye.activiti.mapper.FlowReModelDao;
import com.skyeye.activiti.mapper.FlowReProcdefDao;
import com.skyeye.activiti.service.ActivitiModelService;
import com.skyeye.activiti.service.ActivitiTaskService;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.tenant.TenantTypeEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.entity.ActFlowMation;
import com.skyeye.eve.flowable.entity.FlowableSubData;
import com.skyeye.exception.CustomException;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.userprocess.service.ActUserProcessService;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.editor.constants.ModelDataJsonConstants;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.persistence.entity.ModelEntityImpl;
import org.flowable.engine.impl.persistence.entity.ProcessDefinitionEntityImpl;
import org.flowable.engine.repository.*;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ActivitiModelServiceImpl
 * @Description: 工作流模型操作
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/17 20:53
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ActivitiModelServiceImpl implements ActivitiModelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivitiModelServiceImpl.class);

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    public JedisClientService jedisClient;

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivitiTaskService activitiTaskService;

    @Autowired
    private FlowImgService flowImgService;

    @Autowired
    private FlowReModelDao flowReModelDao;

    @Autowired
    private FlowReProcdefDao flowReProcdefDao;

    @Value("${skyeye.author}")
    private String author;

    @Autowired
    private ActUserProcessService actUserProcessService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    /**
     * 新建一个空模型
     *
     * @param modelName 模型名称
     * @param modelKey  流程id
     * @return
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public String insertNewActivitiModel(String modelName, String modelKey) {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 初始化一个空模型
        Model model = repositoryService.newModel();
        // 设置一些默认信息
        ObjectNode modelNode = getMateInfo(modelName);

        model.setName(modelName);
        model.setKey(ToolUtil.getSurFaceId());
        model.setMetaInfo(modelNode.toString());
        if (tenantEnable) {
            String tenantId = TenantContext.getTenantId();
            model.setTenantId(tenantId);
        } else {
            model.setTenantId(TenantTypeEnum.PLATFORM.getCode());
        }

        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");

        ObjectNode processProperties = getProcessProperties(modelName, modelKey, null);
        editorNode.put("properties", processProperties);

        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.put("stencilset", stencilSetNode);

        repositoryService.saveModel(model);
        try {
            repositoryService.addModelEditorSource(model.getId(), editorNode.toString().getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new CustomException(e);
        }
        return model.getId();
    }

    private ObjectNode getProcessProperties(String modelName, String modelKey, ObjectNode processProperties) {
        if (processProperties == null) {
            processProperties = objectMapper.createObjectNode();
        }
        processProperties.put("process_author", author);
        processProperties.put("process_id", modelKey);
        processProperties.put("name", modelName);
        return processProperties;
    }

    private ObjectNode getMateInfo(String modelName) {
        ObjectNode modelNode = objectMapper.createObjectNode();
        modelNode.put(ModelDataJsonConstants.MODEL_NAME, modelName);
        modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, StringUtils.EMPTY);
        modelNode.put(ModelDataJsonConstants.MODEL_REVISION, CommonNumConstants.NUM_ONE);
        return modelNode;
    }

    /**
     * 设置流程模型信息
     *
     * @param actFlowList
     */
    @Override
    public void setActivitiModelList(List<Map<String, Object>> actFlowList) {
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        // 查询模型
        List<String> modelIds = actFlowList.stream().map(bean -> bean.get("modelId").toString()).collect(Collectors.toList());
        List<ModelEntityImpl> modelList = flowReModelDao.getModelByIds(modelIds, tenantId);
        Map<String, Model> modelMap = modelList.stream().collect(Collectors.toMap(bean -> bean.getId(), bean -> bean));
        // 查询发布流程
        List<String> deploymentIds = modelList.stream().filter(bean -> !ToolUtil.isBlank(bean.getDeploymentId())).map(bean -> bean.getDeploymentId()).collect(Collectors.toList());
        List<ProcessDefinitionEntityImpl> procdefList = flowReProcdefDao.getProcdefByDeploymentIds(deploymentIds);
        Map<String, ProcessDefinition> procdefMap = procdefList.stream().collect(Collectors.toMap(bean -> bean.getDeploymentId(), bean -> bean));

        actFlowList.forEach(bean -> {
            Model model = modelMap.get(bean.get("modelId").toString());
            bean.put("model", BeanUtil.beanToMap(model));
            ProcessDefinition processDefinition = procdefMap.get(model.getDeploymentId());
            bean.put("procdef", processDefinition != null ? BeanUtil.beanToMap(processDefinition) : new HashMap<>());
        });
    }

    @Override
    public void setActivitiModelListForTenant(List<Map<String, Object>> actFlowList) {
        // 查询模型
        List<ModelEntityImpl> modelList = flowReModelDao.getModelByIdsForTenant(actFlowList);
        Map<String, Model> modelMap = modelList.stream().collect(Collectors.toMap(bean -> bean.getId(), bean -> bean));
        // 查询发布流程
        List<String> deploymentIds = modelList.stream().filter(bean -> !ToolUtil.isBlank(bean.getDeploymentId())).map(bean -> bean.getDeploymentId()).collect(Collectors.toList());
        List<ProcessDefinitionEntityImpl> procdefList = flowReProcdefDao.getProcdefByDeploymentIds(deploymentIds);
        Map<String, ProcessDefinition> procdefMap = procdefList.stream().collect(Collectors.toMap(bean -> bean.getDeploymentId(), bean -> bean));

        actFlowList.forEach(bean -> {
            Model model = modelMap.get(bean.get("modelId").toString());
            bean.put("model", BeanUtil.beanToMap(model));
            ProcessDefinition processDefinition = procdefMap.get(model.getDeploymentId());
            bean.put("procdef", processDefinition != null ? BeanUtil.beanToMap(processDefinition) : new HashMap<>());
        });
    }

    /**
     * 发布模型为流程定义
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void editActivitiModelToDeploy(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String modelId = map.get("modelId").toString();
        // 获取模型
        Model modelData = repositoryService.getModel(modelId);
        byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());
        if (bytes == null) {
            outputObject.setreturnMessage("模型数据为空，请先设计流程并成功保存，再进行发布。");
            return;
        }
        try {
            JsonNode modelNode = new ObjectMapper().readTree(bytes);
            LOGGER.info("modelNode = {}", modelNode.toString());
            BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
            if (model.getProcesses().size() == 0) {
                outputObject.setreturnMessage("数据模型不符要求，请至少设计一条主线流程。");
                return;
            }
            byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);

            // 发布流程
            String processName = modelData.getName() + ".bpmn20.xml";
            Deployment deployment;
            if (tenantEnable) {
                // 添加租户信息
                processName = modelData.getTenantId() + ":" + processName;
                deployment = repositoryService.createDeployment()
                    .name(modelData.getName())
                    .tenantId(modelData.getTenantId())
                    .addString(processName, new String(bpmnBytes, "UTF-8")).deploy();
            } else {
                processName = TenantTypeEnum.PLATFORM.getCode() + ":" + processName;
                deployment = repositoryService.createDeployment()
                    .name(modelData.getName())
                    .tenantId(TenantTypeEnum.PLATFORM.getCode())
                    .addString(processName, new String(bpmnBytes, "UTF-8")).deploy();
            }

            // 发布版本+1
            Integer version = modelData.getVersion();
            modelData.setVersion(StrUtil.isBlank(modelData.getDeploymentId()) ? version : version + 1);
            modelData.setDeploymentId(deployment.getId());
            repositoryService.saveModel(modelData);

            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
            if (!StrUtil.isBlank(processName)) {
                activityMapper.updateProcessDefinitionName(processName, processDefinition.getId(), deployment.getTenantId());
                LOGGER.info("流程模型【{}】没有配置流程名称，默认使用流程模型名称作为流程名称", processName);
            }
            LOGGER.info("流程【{}】成功发布", processName);
        } catch (Exception ee) {
            throw new CustomException(ee);
        }
    }

    @Override
    public String startProcess(FlowableSubData flowableSubData, ActFlowMation actFlowMation) {
        if (!judgeProcessKeyIsLive(actFlowMation)) {
            throw new CustomException("任务发起失败,不存在该流程模型.");
        }
        LOGGER.info("approvalId is : " + flowableSubData.getApprovalId());
        // 业务对象
        Map<String, Object> varables = new HashMap<>();
        // 默认设置审批人为会签人
        varables.put(ActivitiConstants.ASSIGNEE_USER_LIST, Arrays.asList(flowableSubData.getApprovalId()));
        LOGGER.info("startProcessInstanceByKey start.");

        String tenantId = TenantTypeEnum.PLATFORM.getCode();
        if (tenantEnable) {
            // 添加租户信息
            tenantId = TenantContext.getTenantId();
        }
        ProcessInstance process = runtimeService.createProcessInstanceBuilder()
            .processDefinitionKey(actFlowMation.getModelKey())
            .tenantId(tenantId)
            .variables(varables)
            .start();
        // 启动流程---流程图id，业务表id
        String processInstanceId = process.getProcessInstanceId();

        queryProHighLighted(processInstanceId);
        // 存储用户启动的流程
        actUserProcessService.saveActUserProcess(processInstanceId, actFlowMation.getId(), flowableSubData.getObjectId(), flowableSubData.getObjectKey(),
            flowableSubData.getAppId());

        // 设置第一个userTask任务的审批人
        activitiTaskService.setNextUserTaskApproval(processInstanceId, flowableSubData.getApprovalId());
        LOGGER.info("start process success, processInstanceId is: {}", processInstanceId);
        return processInstanceId;
    }

    /**
     * 判断该key的流程是否还存在
     *
     * @param actFlowMation skyeye的工作流模型
     * @return
     */
    private boolean judgeProcessKeyIsLive(ActFlowMation actFlowMation) {
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().processDefinitionKey(actFlowMation.getModelKey());
        if (tenantEnable) {
            processDefinitionQuery.processDefinitionTenantId(tenantId);
        }
        List<ProcessDefinition> processDefinition = processDefinitionQuery.list();
        if (processDefinition != null) {
            List<String> deploymentIds = processDefinition.stream().map(p -> p.getDeploymentId()).collect(Collectors.toList());
            ModelQuery modelQuery = repositoryService.createModelQuery();
            if (tenantEnable) {
                modelQuery.modelTenantId(tenantId);
            }
            List<Model> beans = modelQuery.latestVersion().orderByLastUpdateTime().desc().list();
            beans = beans.stream().filter(bean -> deploymentIds.contains(bean.getDeploymentId())).collect(Collectors.toList());
            if (beans != null && !beans.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取指定任务节点的审批信息
     *
     * @param approvedId   审批人id
     * @param approvedName 审批人名字
     * @param opinion      审批意见
     * @param flag         该节点是否审批通过，true:通过，false:不通过，null: 委派/转办历史
     * @param task         任务
     * @return
     */
    @Override
    public List<Map<String, Object>> getUpLeaveList(String approvedId, String approvedName, String opinion, Boolean flag, Task task, int type) {
        Map<String, Object> leaveOpinion = new HashMap<>();
        // 审批人id
        leaveOpinion.put("opId", approvedId);
        // 审批人姓名
        leaveOpinion.put("opName", approvedName);
        // 操作节点
        leaveOpinion.put("title", task.getName());
        // 审批意见
        leaveOpinion.put("opinion", opinion);
        // 审批时间
        leaveOpinion.put("createTime", DateUtil.getTimeAndToString());
        leaveOpinion.put("flag", flag);
        leaveOpinion.put("type", type);
        // 任务id
        leaveOpinion.put("taskId", task.getId());
        // 获取该任务所有的流程变量
        Object o = runtimeService.getVariable(task.getProcessInstanceId(), ActivitiConstants.PROCESSINSTANCEID_TASK_LEAVE_OPINION_LIST_VARABLES);
        List<Map<String, Object>> leaveList = new ArrayList<>();
        if (o != null) {
            leaveList = (List<Map<String, Object>>) o;
        }
        leaveList.add(leaveOpinion);
        return leaveList;
    }

    /**
     * 流程图高亮显示
     *
     * @param processInstanceId
     */
    @Override
    public void queryProHighLighted(String processInstanceId) {
        byte[] b = flowImgService.generateImageByProcInstId(processInstanceId);
        ByteArrayInputStream imageStream = new ByteArrayInputStream(b);
        FileOutputStream fos = null;
        try {
            BufferedImage bi = ImageIO.read(imageStream);
            FileUtil.createDirs(tPath + "upload/activiti/");

            File file = new File(tPath + "upload/activiti/" + processInstanceId + ".png");
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            ImageIO.write(bi, "png", fos);
        } catch (Exception ee) {
            throw new CustomException(ee);
        } finally {
            FileUtil.close(fos);
            FileUtil.close(imageStream);
        }
    }

    /**
     * 删除模型
     *
     * @param modelId 模型id
     */
    @Override
    public void deleteActivitiModelById(String modelId) {
        try {
            ModelQuery modelQuery = repositoryService.createModelQuery().modelId(modelId);
            Model model = modelQuery.singleResult();
            String deploymentId = model.getDeploymentId();
            // 已发布的模型需要删除流程定义和流程发布表
            if (StrUtil.isNotBlank(deploymentId)) {
                repositoryService.deleteDeployment(deploymentId);
            }
            // 删除流程模型表
            repositoryService.deleteModel(modelId);
        } catch (Exception e) {
            throw new CustomException("删除失败");
        }
    }

    /**
     * 取消发布
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void deleteReleasedActivitiModelById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String deploymentId = map.get("deploymentId").toString();
        try {
            repositoryService.deleteDeployment(deploymentId, true);
        } catch (Exception e) {
            outputObject.setreturnMessage("存在正在进行的流程，无法取消发布。");
        }
    }

    /**
     * 导出model的xml文件
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void editApprovalActivitiTaskListByUserId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String modelId = map.get("modelId").toString();
        Model modelData = repositoryService.getModel(modelId);
        BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
        try {
            JsonNode editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
            BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
            BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
            byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);
            ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
            String filename = bpmnModel.getMainProcess().getId() + ".bpmn20.xml";
            PutObject.getResponse().setHeader("REQUESTMATION", "DOWNLOAD");
            PutObject.getResponse().setHeader("Content-Disposition", "attachment;filename=" + filename);
            // 1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
            PutObject.getResponse().setContentType("multipart/form-data");
            BufferedOutputStream out1 = new BufferedOutputStream(PutObject.getResponse().getOutputStream());
            int len = 0;
            while ((len = in.read()) != -1) {
                out1.write(len);
                out1.flush();
            }
        } catch (IOException ee) {
            throw new CustomException(ee);
        }
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void editModelByModelId(String modelId, String modelName, String modelKey) {
        Model model = repositoryService.getModel(modelId);

        if (model != null) {
            model.setName(modelName);
            model.setVersion(model.getVersion() + 1);
            repositoryService.saveModel(model);

            try {
                // 获取模型配置信息并重新设置模型key
                ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(new String(repositoryService.getModelEditorSource(model.getId()), "utf-8"));
                ObjectNode processProperties = (ObjectNode) editorJsonNode.get("properties");
                processProperties = getProcessProperties(modelName, modelKey, processProperties);
                editorJsonNode.set("properties", processProperties);

                repositoryService.addModelEditorSource(model.getId(), editorJsonNode.toString().getBytes("utf-8"));
            } catch (Exception e) {
                LOGGER.error("Error get model JSON", e);
                throw new FlowableException("Error get model JSON", e);
            }
        }
    }

}
