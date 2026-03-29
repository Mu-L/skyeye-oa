/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.QuartzConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.entity.xxljob.XxlJobInfo;
import com.skyeye.eve.entity.xxljob.XxlJobInfoBatchDelete;
import com.skyeye.eve.rest.quartz.SysQuartzMation;
import com.skyeye.eve.rest.xxljob.XxlJobService;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.eve.service.SysQuartzService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: SysQuartzServiceImpl
 * @Description: 定时任务服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/3 23:29
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SysQuartzServiceImpl implements SysQuartzService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SysQuartzServiceImpl.class);

    @Autowired
    private XxlJobService xxlJobService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    /**
     * 启动定时任务
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void startUpTaskQuartz(InputObject inputObject, OutputObject outputObject) {
        SysQuartzMation sysQuartz = inputObject.getParams(SysQuartzMation.class);
        String userId = inputObject.getLogParams().get("id").toString();
        LOGGER.info("start quartz, title is {}, userId is {}", sysQuartz.getTitle(), userId);
        String groupId = ExecuteFeignClient.get(() -> xxlJobService.getGroupId(sysQuartz.getAppName())).getBean().get("id").toString();
        LOGGER.info("xxl-job groupId is {}", groupId);
        XxlJobInfo xxlJobInfo = this.getXxlJobInfo(sysQuartz.getName(), groupId, sysQuartz.getDelayedTime(), sysQuartz.getTitle(), sysQuartz.getGroupId(), userId, sysQuartz.getScheduleConf());
        ExecuteFeignClient.get(() -> xxlJobService.addAndStart(xxlJobInfo));
    }

    private XxlJobInfo getXxlJobInfo(String objectId, String groupId, String delayedTime, String jobDesc, String taskType, String userId, String scheduleConf) {
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        Map<String, Object> user = iAuthUserService.queryDataMationById(userId);
        String author = "商城用户";
        if (CollectionUtil.isNotEmpty(user)) {
            author = user.get("name").toString();
        }
        xxlJobInfo.setAuthor(author);
        xxlJobInfo.setJobGroup(Integer.parseInt(groupId));
        xxlJobInfo.setJobDesc(QuartzConstants.QuartzMateMationJobType.getRemarkPrefixByTaskType(taskType, jobDesc));
        xxlJobInfo.setScheduleType("CRON");
        String cron = StrUtil.isNotBlank(scheduleConf)
            ? scheduleConf.trim()
            : ToolUtil.getCrons1(delayedTime);
        xxlJobInfo.setScheduleConf(cron);
        xxlJobInfo.setGlueType("BEAN");
        xxlJobInfo.setExecutorRouteStrategy("FIRST");
        xxlJobInfo.setExecutorHandler(QuartzConstants.QuartzMateMationJobType.getServiceNameByTaskType(taskType));
        xxlJobInfo.setObjectId(objectId);
        xxlJobInfo.setExecutorParam(this.getExecutorParam(objectId, userId));
        xxlJobInfo.setMisfireStrategy("DO_NOTHING");
        xxlJobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
        xxlJobInfo.setExecutorTimeout(0);
        xxlJobInfo.setExecutorFailRetryCount(0);
        xxlJobInfo.setGlueRemark("GLUE代码初始化");

        return xxlJobInfo;
    }

    private String getExecutorParam(String objectId, String userId) {
        Map<String, String> executorParam = new HashMap<>();
        executorParam.put("objectId", objectId);
        executorParam.put("userId", userId);
        if (tenantEnable) {
            executorParam.put("tenantId", TenantContext.getTenantId());
        }
        return JSON.toJSONString(executorParam);
    }

    /**
     * 停止并删除定时任务
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void stopAndDeleteTaskQuartz(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String objectId = map.get("objectId").toString();
        LOGGER.info("stop quartz, name is {}", objectId);
        ExecuteFeignClient.get(() -> xxlJobService.removeJob(objectId));
    }

    @Override
    public void batchStopAndDeleteTaskQuartz(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String objectIds = map.get("objectIds").toString();
        if (StrUtil.isEmpty(objectIds)) {
            return;
        }
        List<String> objectIdList = Arrays.asList(objectIds.split(CommonCharConstants.COMMA_MARK))
            .stream().filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(objectIdList)) {
            return;
        }
        LOGGER.info("batch stop quartz, objectIds is {}", objectIds);
        XxlJobInfoBatchDelete xxlJobInfoBatchDelete = new XxlJobInfoBatchDelete();
        xxlJobInfoBatchDelete.setObjectIds(objectIdList);
        ExecuteFeignClient.get(() -> xxlJobService.batchRemoveJob(xxlJobInfoBatchDelete));
    }

}
