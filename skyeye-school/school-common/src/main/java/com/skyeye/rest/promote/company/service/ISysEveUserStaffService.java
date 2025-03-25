package com.skyeye.rest.promote.company.service;

import com.skyeye.base.rest.service.IService;

import java.util.List;
import java.util.Map;

public interface ISysEveUserStaffService extends IService {

    List<Map<String, Object>> selectByName(String serviceClassName, String keyword, Integer limit, Integer pages);
}
