package com.skyeye.rest.erp.service;

import com.skyeye.base.rest.service.IService;

import java.util.List;
import java.util.Map;

public interface IFarmStaffService extends IService {

    List<Map<String, Object>> queryAllFarmStaffList();
}
