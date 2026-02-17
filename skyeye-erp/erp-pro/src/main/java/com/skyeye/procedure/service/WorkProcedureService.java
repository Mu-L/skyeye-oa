/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.procedure.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.farm.entity.Farm;
import com.skyeye.procedure.entity.WorkProcedure;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: WorkProcedureService
 * @Description: 工序管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/23 21:43
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface WorkProcedureService extends SkyeyeBusinessService<WorkProcedure> {

    void queryAllWorkProcedureList(InputObject inputObject, OutputObject outputObject);

    void queryExecuteFarmByWorkProcedureId(InputObject inputObject, OutputObject outputObject);

    List<Farm> queryExecuteFarmByWorkProcedureId(String workProcedureId);

    /**
     * 批量查询工序可执行车间：根据多个工序ID一次性查询每个工序可执行的车间列表。
     *
     * @param workProcedureIds 工序ID列表
     * @return 工序ID -> 可执行车间列表 的映射，无设备配置的工序不在结果中
     */
    Map<String, List<Farm>> queryExecuteFarmByWorkProcedureIds(List<String> workProcedureIds);
}
