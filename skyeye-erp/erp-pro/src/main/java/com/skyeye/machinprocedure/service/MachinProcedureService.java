/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machinprocedure.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.machinprocedure.entity.MachinProcedure;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: MachinProcedureService
 * @Description: 加工单子单据工序信息服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/24 15:00
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface MachinProcedureService extends SkyeyeBusinessService<MachinProcedure> {

    void deleteByParentId(String parentId);

    void saveList(String parentId, List<MachinProcedure> machinProcedureList);

    void setMachinProcedureById(InputObject inputObject, OutputObject outputObject);

    Map<String, MachinProcedure> queryMachinProcedureMapByMachinId(String machinId);

    void editStateById(String id, Integer state);

    /**
     * 判断当前工序的前置工序是否部分完成/全部完成
     *
     * @param machinProcedureId 当前加工单子单据工序id
     * @return true: 前置工序全部完成/部分完成; false: 前置工序未完成
     */
    boolean checkPrevMachinProcedureIsCompleted(String machinProcedureId);

    List<MachinProcedure> queryMachinProcedureByIds(List<String> machinProcedureIdList);
}
