/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machinprocedure.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.machinprocedure.entity.MachinProcedureAccept;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: MachinProcedureAcceptService
 * @Description: 工序验收服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/24 20:13
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface MachinProcedureAcceptService extends SkyeyeBusinessService<MachinProcedureAccept> {

    Integer calcNumByMachinProcedureFarmId(String machinProcedureFarmId);

    Integer calcAllNumByMachinProcedureFarmId(String machinProcedureFarmId);

    Map<String, List<MachinProcedureAccept>> queryMachinProcedureAcceptByMachinProcedureFarmId(String... machinProcedureFarmId);

    List<MachinProcedureAccept> queryListByMachinProcedureId(String machinProcedureId);

    List<MachinProcedureAccept> queryListByMachinProcedureIdList(List<String> machinProcedureIdList);

    List<MachinProcedureAccept> queryProcedureAcceptByIds(List<String> procedureAcceptIdList);
}
