/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.task.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeFlowableServiceImpl;
import com.skyeye.common.base.handler.enclosure.bean.Enclosure;
import com.skyeye.common.base.handler.util.PersistableHandlerExecutor;
import com.skyeye.common.base.handler.util.eitity.ExecuteTypeEnum;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.enumeration.ScheduleDayObjectType;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.MapUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.rest.schedule.OtherModuleScheduleMation;
import com.skyeye.eve.service.IScheduleDayService;
import com.skyeye.exception.CustomException;
import com.skyeye.milestone.entity.Milestone;
import com.skyeye.milestone.service.MilestoneService;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.task.classenum.TaskAuthEnum;
import com.skyeye.task.classenum.TaskStateEnum;
import com.skyeye.task.dao.ProTaskDao;
import com.skyeye.task.entity.Task;
import com.skyeye.task.service.ProTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ProTaskServiceImpl
 * @Description: 项目任务管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 13:01
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "任务管理", groupName = "任务管理", flowable = true, teamAuth = true)
public class ProTaskServiceImpl extends SkyeyeFlowableServiceImpl<ProTaskDao, Task> implements ProTaskService {

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private IScheduleDayService iScheduleDayService;

    @Autowired
    private MilestoneService milestoneService;

    @Override
    public Class getAuthEnumClass() {
        return TaskAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(TaskAuthEnum.ADD.getKey(), TaskAuthEnum.EDIT.getKey(), TaskAuthEnum.DELETE.getKey(),
            TaskAuthEnum.REVOKE.getKey(), TaskAuthEnum.INVALID.getKey(), TaskAuthEnum.SUBMIT_TO_APPROVAL.getKey(), TaskAuthEnum.LIST.getKey(),
            TaskAuthEnum.EXECUTING.getKey(), TaskAuthEnum.COMPLETED.getKey(), TaskAuthEnum.CLOSE.getKey(), TaskAuthEnum.MY_EXECUTE.getKey(), TaskAuthEnum.MY_CREATE.getKey());
    }

    @Override
    public QueryWrapper<Task> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Task> queryWrapper = super.getQueryWrapper(commonPageInfo);
        setQueryWrapper(commonPageInfo, queryWrapper);
        return queryWrapper;
    }

    private void setQueryWrapper(TableSelectInfo commonPageInfo, QueryWrapper<Task> queryWrapper) {
        Task task = ReflectUtil.newInstance(clazz);
        task.setObjectId(commonPageInfo.getObjectId());
        task.setObjectKey(commonPageInfo.getObjectKey());
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        if (StrUtil.equals("myExecute", commonPageInfo.getType())) {
            // 我执行的
            checkAuthPermission(task, userId, CommonNumConstants.NUM_TEN);
            queryWrapper.apply("INSTR(CONCAT(',', REPLACE(REPLACE(" + MybatisPlusUtil.toColumns(Task::getPerformId) + ", '[', ''), ']', ''), ','), CONCAT(',\"', {0}, '\",'))", userId);
        } else if (StrUtil.equals("myCreate", commonPageInfo.getType())) {
            // 我创建的
            checkAuthPermission(task, userId, CommonNumConstants.NUM_ELEVEN);
            queryWrapper.eq(MybatisPlusUtil.toColumns(Task::getCreateId), userId);
        } else if (StrUtil.equals("list", commonPageInfo.getType())) {
            // 所有的
            checkAuthPermission(task, userId, CommonNumConstants.NUM_SIX);
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Task::getObjectId), commonPageInfo.getObjectId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Task::getMilestoneId), commonPageInfo.getHolderId());
        }
    }

    @Override
    public List<Map<String, Object>> queryPageData(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageData(inputObject);
        List<String> ids = beans.stream().map(bean -> bean.get("id").toString()).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return beans;
        }
        // 查询子节点信息(包含当前节点)
        List<String> childIds = skyeyeBaseMapper.queryAllChildIdsByParentId(ids);
        beans = selectMapByIds(childIds).values().stream().map(bean -> BeanUtil.beanToMap(bean)).collect(Collectors.toList());
        beans.forEach(bean -> {
            bean.put("lay_is_open", true);
        });

        milestoneService.setMationForMap(beans, "milestoneId", "milestoneMation");
        return beans;
    }

    @Override
    public void createPrepose(Task entity) {
        super.createPrepose(entity);
        if (StrUtil.isNotEmpty(entity.getParentId())) {
            Task task = selectById(entity.getParentId());
            entity.setMilestoneId(task.getMilestoneId());
        } else {
            entity.setParentId(CommonNumConstants.NUM_ZERO.toString());
        }
        checkTime(entity.getMilestoneId(), entity.getStartTime(), entity.getEndTime());
    }

    @Override
    protected void updatePrepose(Task entity) {
        super.updatePrepose(entity);
        Task oldTask = selectById(entity.getId());
        checkTime(oldTask.getMilestoneId(), entity.getStartTime(), entity.getEndTime());
    }

    private void checkTime(String milestoneId, String startTime, String endTime) {
        Milestone milestone = milestoneService.selectById(milestoneId);
        if (DateUtil.getDistanceDay(milestone.getStartTime(), startTime) < 0) {
            // 任务开始时间小于里程碑开始时间
            throw new CustomException("任务开始时间不能早于里程碑开始时间");
        }
        if (DateUtil.getDistanceDay(milestone.getEndTime(), endTime) > 0) {
            // 任务结束时间大于里程碑结束时间
            throw new CustomException("任务结束时间不能晚于里程碑结束时间");
        }
    }

    @Override
    public void deleteById(String id) {
        // 查询子节点信息(包含当前节点)
        List<String> childIds = skyeyeBaseMapper.queryAllChildIdsByParentId(Arrays.asList(id));
        deleteById(childIds);
    }

    @Override
    public Task selectById(String id) {
        Task task = super.selectById(id);
        task.setDepartmentMation(iDepmentService.queryDataMationById(task.getDepartmentId()));
        task.setPerformMation(iAuthUserService.queryDataMationByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(task.getPerformId())));
        milestoneService.setDataMation(task, Task::getMilestoneId);
        return task;
    }

    @Override
    public List<Task> selectByIds(String... ids) {
        List<Task> tasks = super.selectByIds(ids);
        iDepmentService.setDataMation(tasks, Task::getDepartmentId);

        List<String> performIdList = tasks.stream()
            .filter(bean -> CollectionUtil.isNotEmpty(bean.getPerformId()))
            .flatMap(norms -> norms.getPerformId().stream()).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(performIdList)) {
            Map<String, Map<String, Object>> userMap = iAuthUserService.queryDataMationForMapByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(performIdList));
            tasks.forEach(task -> {
                if (CollectionUtil.isEmpty(task.getPerformId())) {
                    return;
                }
                List<Map<String, Object>> userMation = new ArrayList<>();
                task.getPerformId().forEach(performId -> {
                    if (!userMap.containsKey(performId)) {
                        return;
                    }
                    userMation.add(userMap.get(performId));
                });
                task.setPerformMation(userMation);
            });
        }
        return tasks;
    }

    @Override
    public void approvalEndIsSuccess(Task entity) {
        for (String executorId : entity.getPerformId()) {
            OtherModuleScheduleMation scheduleMation = new OtherModuleScheduleMation();
            scheduleMation.setTitle(entity.getName());
            scheduleMation.setContent(entity.getTaskInstructions());
            scheduleMation.setStartTime(String.format(Locale.ROOT, "%s 00:00:00", entity.getStartTime()));
            scheduleMation.setEndTime(String.format(Locale.ROOT, "%s 23:59:59", entity.getEndTime()));
            scheduleMation.setUserId(executorId);
            scheduleMation.setObjectId(entity.getId());
            scheduleMation.setObjectType(ScheduleDayObjectType.PRO_TASK.getKey());
            iScheduleDayService.insertScheduleMationByOtherModule(scheduleMation);
        }
    }

    /**
     * 任务开始执行
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void executionTask(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Task task = selectById(id);
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        checkAuthPermission(task, userId, CommonNumConstants.NUM_SEVEN);

        if (StrUtil.equals(FlowableStateEnum.PASS.getKey(), task.getState())) {
            // 审核通过状态下可以开始执行
            UpdateWrapper<Task> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(Task::getState), TaskStateEnum.EXECUTING.getKey());
            update(updateWrapper);
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 任务执行完成
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void complateTask(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Task task = selectById(id);
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        checkAuthPermission(task, userId, CommonNumConstants.NUM_EIGHT);

        if (StrUtil.equals(TaskStateEnum.EXECUTING.getKey(), task.getState())) {
            // 执行中状态下可以执行完成
            UpdateWrapper<Task> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(Task::getState), TaskStateEnum.COMPLETED.getKey());
            updateWrapper.set(MybatisPlusUtil.toColumns(Task::getActualWorkload), map.get("actualWorkload").toString());
            updateWrapper.set(MybatisPlusUtil.toColumns(Task::getExecutionResult), map.get("executionResult").toString());
            update(updateWrapper);
            // 处理附件
            Enclosure executionEnclosureInfo = JSONUtil.toBean(map.get("executionEnclosureInfo").toString(), Enclosure.class);
            task.setExecutionEnclosureInfo(executionEnclosureInfo);
            PersistableHandlerExecutor.executorHandler(task, ExecuteTypeEnum.POST_EXECUTION.getType());
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void closeTask(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Task task = selectById(id);
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        checkAuthPermission(task, userId, CommonNumConstants.NUM_NINE);

        if (StrUtil.equals(TaskStateEnum.COMPLETED.getKey(), task.getState())) {
            // 执行完成状态下可以关闭
            UpdateWrapper<Task> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(Task::getState), TaskStateEnum.CLOSE.getKey());
            update(updateWrapper);
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    public void queryProTaskListForGantt(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        // 查询数据
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        setQueryWrapper(tableSelectInfo, queryWrapper);
        List<Task> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        List<Map<String, Object>> beans = JSONUtil.toList(JSONUtil.toJsonStr(list), null);
        // 构造数据
        List<Map<String, Object>> node = new ArrayList<>();
        List<Map<String, Object>> link = new ArrayList<>();
        beans.forEach(bean -> {
            node.add(getNode(bean));
            String parentId = MapUtil.checkKeyIsNull(bean, "parentId") ? "" : bean.get("parentId").toString();
            if (StrUtil.isNotEmpty(parentId) && !StrUtil.equals(parentId, CommonNumConstants.NUM_ZERO.toString())) {
                link.add(getLink(bean));
            }
        });
        Map<String, Object> result = new HashMap<>();
        result.put("node", node);
        result.put("link", link);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private Map<String, Object> getNode(Map<String, Object> bean) {
        Map<String, Object> retult = new HashMap<>();
        retult.put("id", bean.get("id"));
        retult.put("text", bean.get("name"));
        retult.put("parent", bean.get("parentId"));
        retult.put("start_date", bean.get("startTime"));
        retult.put("end_date", bean.get("endTime"));
        retult.put("open", true);
        return retult;
    }

    private Map<String, Object> getLink(Map<String, Object> bean) {
        Map<String, Object> retult = new HashMap<>();
        retult.put("id", bean.get("id") + "CC");
        retult.put("source", bean.get("parentId"));
        retult.put("target", bean.get("id"));
        retult.put("type", CommonNumConstants.NUM_ZERO);
        return retult;
    }

}
