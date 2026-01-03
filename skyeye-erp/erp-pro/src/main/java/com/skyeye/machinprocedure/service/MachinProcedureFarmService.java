/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machinprocedure.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.machinprocedure.entity.MachinProcedureFarm;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: MachinProcedureFarmService
 * @Description: 车间任务服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/24 19:07
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface MachinProcedureFarmService extends SkyeyeBusinessService<MachinProcedureFarm> {

    Map<String, List<MachinProcedureFarm>> queryMachinProcedureFarmMapByMachinId(String machinId);

    Map<String, Map<String, List<MachinProcedureFarm>>> queryMachinProcedureFarmMapByMachinIds(List<String> machinIds);

    void receiveMachinProcedureFarm(InputObject inputObject, OutputObject outputObject);

    void receptionReceiveMachinProcedureFarm(InputObject inputObject, OutputObject outputObject);

    void editStateById(String id, String state);

    void deleteMachinProcedureFarmByMachinProcedureId(String machinProcedureId);

    List<MachinProcedureFarm> queryMachinProcedureFarmByMachinProcedureId(String machinProcedureId);

    List<MachinProcedureFarm> queryAllMachinProcedureFarmByMachinProcedureId(String machinProcedureId);

    void queryMachinProcedureFarmToInOrOutList(InputObject inputObject, OutputObject outputObject);

    void setOrderMationByFromId(List<Map<String, Object>> beans, String idKey, String mationKey);

    /**
     * 增加车间任务的验收数量、合格数量、返工数量、报废数量
     * 根据给定的增量值，在现有值基础上增加
     *
     * @param id           车间任务id
     * @param acceptNum    验收数量增量（可为空，为空则不更新）
     * @param qualifiedNum 合格数量增量（可为空，为空则不更新）
     * @param reworkNum    返工数量增量（可为空，为空则不更新）
     * @param scrapNum     报废数量增量（可为空，为空则不更新）
     */
    void addAcceptNumsById(String id, String acceptNum, String qualifiedNum, String reworkNum, String scrapNum);

}
