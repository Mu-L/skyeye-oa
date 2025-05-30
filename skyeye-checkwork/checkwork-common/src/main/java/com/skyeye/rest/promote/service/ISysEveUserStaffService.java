package com.skyeye.rest.promote.service;

import com.skyeye.base.rest.service.IService;

import java.util.List;
import java.util.Map;

public interface ISysEveUserStaffService extends IService {
    List<Map<String, Object>> queryAllStaffList();

    List<Map<String, Object>> queryEmployeeListByIds(String employeeIds);
}
