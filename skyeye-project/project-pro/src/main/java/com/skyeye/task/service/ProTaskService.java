/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.task.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.task.entity.Task;

/**
 * @ClassName: ProTaskService
 * @Description: 项目任务管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 20:11
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ProTaskService extends SkyeyeBusinessService<Task> {

    void executionTask(InputObject inputObject, OutputObject outputObject);

    void complateTask(InputObject inputObject, OutputObject outputObject);

    void closeTask(InputObject inputObject, OutputObject outputObject);

    void queryProTaskListForGantt(InputObject inputObject, OutputObject outputObject);
}
