package com.skyeye.rest.erp.service;

import com.skyeye.base.rest.service.IService;

import java.util.List;
import java.util.Map;

public interface IFarmStationService extends IService {

    List<Map<String, Object>> queryFarmStationById(String workId);

}
