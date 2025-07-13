/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machinprocedure.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.machinprocedure.entity.MachinProcedureAcceptChild;

import java.util.List;

/**
 * @ClassName: MachinProcedureAcceptChildService
 * @Description: 工序验收子单据服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/25 17:23
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface MachinProcedureAcceptChildService extends SkyeyeBusinessService<MachinProcedureAcceptChild> {

    void deleteByParentId(String parentId);

    List<MachinProcedureAcceptChild> selectByParentId(String parentId);

    void saveList(String parentId, List<MachinProcedureAcceptChild> machinProcedureAcceptChildList);

    List<MachinProcedureAcceptChild> queryListByParentId(List<String> acceptIdList);
}
