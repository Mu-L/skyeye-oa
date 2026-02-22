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

    void queryProcedureConsumablesByFarmId(InputObject inputObject, OutputObject outputObject);

    void queryPendingAcceptNumByFarmId(InputObject inputObject, OutputObject outputObject);

    /**
     * 修改车间任务信息（计划开始时间、计划结束时间等），仅待执行、部分完成状态可操作。
     *
     * @param inputObject  入参：id 必填；planStartTime、planEndTime 可选，后续可扩展其他字段
     * @param outputObject 出参
     */
    void editMachinProcedureFarmInfo(InputObject inputObject, OutputObject outputObject);

    /**
     * 统计各车间待接收/待执行任务数量（用于负载均衡）
     *
     * @param farmIds 车间id列表
     * @return 车间id -> 任务条数
     */
    Map<String, Long> countPendingTaskByFarmIds(List<String> farmIds);

    /**
     * 统计各车间待接收/待执行任务的加工数量之和（用于加工数量最少策略）
     *
     * @param farmIds 车间id列表
     * @return 车间id -> targetNum 之和（字符串）
     */
    Map<String, String> sumPendingTargetNumByFarmIds(List<String> farmIds);

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

    /**
     * 按月份查询车间任务列表（用于排产甘特图），支持按 type/objectId 筛选，返回带工序名称的列表。
     */
    void queryGanttListByMonth(InputObject inputObject, OutputObject outputObject);

}
