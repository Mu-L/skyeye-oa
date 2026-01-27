/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.patrol.entity.PatrolTask;

/**
 * @ClassName: PatrolTaskService
 * @Description: 巡检任务服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface PatrolTaskService extends SkyeyeBusinessService<PatrolTask> {

    /**
     * 开始执行任务（待执行 -> 执行中）
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    void startTask(InputObject inputObject, OutputObject outputObject);

    /**
     * 完成任务（执行中 -> 已完成）
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    void completeTask(InputObject inputObject, OutputObject outputObject);

    /**
     * 取消任务（待执行/执行中 -> 已取消）
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    void cancelTask(InputObject inputObject, OutputObject outputObject);

    /**
     * 重新分配超时任务（已超时 -> 待执行）
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    void reassignTimeoutTask(InputObject inputObject, OutputObject outputObject);

}

