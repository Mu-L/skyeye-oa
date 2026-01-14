/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.service;

import com.skyeye.afterseal.entity.ProjectInstallerCommission;
import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: ProjectInstallerCommissionService
 * @Description: 安装员提成服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/24 12:00
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ProjectInstallerCommissionService extends SkyeyeBusinessService<ProjectInstallerCommission> {

    /**
     * 计算提成（报工审核通过后自动调用）
     *
     * @param dispatchId 工单id
     */
    void calculateCommission(String dispatchId);

    /**
     * 获取提成统计数据（根据项目ID）
     *
     * @param inputObject  输入对象
     * @param outputObject 输出对象
     */
    void queryCommissionStatistics(InputObject inputObject, OutputObject outputObject);

    /**
     * 根据工单ID查询提成统计
     *
     * @param inputObject  输入对象
     * @param outputObject 输出对象
     */
    void queryCommissionStatisticsByDispatchId(InputObject inputObject, OutputObject outputObject);

}

