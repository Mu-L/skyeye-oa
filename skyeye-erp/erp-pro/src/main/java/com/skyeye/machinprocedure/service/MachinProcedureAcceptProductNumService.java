package com.skyeye.machinprocedure.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.machinprocedure.entity.MachinProcedureAcceptProductNum;

import java.util.List;

public interface MachinProcedureAcceptProductNumService extends SkyeyeBusinessService<MachinProcedureAcceptProductNum> {
    void writeList(String parentId, List<MachinProcedureAcceptProductNum> machinProcedureAcceptProductNumList);

    List<MachinProcedureAcceptProductNum> queryListByParentId(String parentId);

    void deleteByParentId(String parentId);

    List<MachinProcedureAcceptProductNum> queryListByParentIds(List<String> acceptIdList);

    List<MachinProcedureAcceptProductNum> queryMachinProcedureAcceptProductNumByStaffId(String staffId);
}
