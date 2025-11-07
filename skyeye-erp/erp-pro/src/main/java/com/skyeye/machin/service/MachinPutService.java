/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machin.service;

import com.skyeye.business.service.SkyeyeErpOrderService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.machin.entity.MachinPut;

import java.util.List;

/**
 * @ClassName: MachinPutService
 * @Description: 加工入库单服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/6 22:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface MachinPutService extends SkyeyeErpOrderService<MachinPut> {

    void queryMachinPutTransById(InputObject inputObject, OutputObject outputObject);

    void insertMachinPutToTurnDepot(InputObject inputObject, OutputObject outputObject);

    /**
     * 根据车间任务ID查询加工入库单
     *
     * @param machinProcedureFarmId 车间任务ID
     * @return 加工入库单列表
     */
    List<MachinPut> queryMachinPutByMachinProcedureFarmId(String machinProcedureFarmId);
}
