package com.skyeye.rest.promote.company.service;

import com.skyeye.base.rest.service.IService;

import java.util.List;
import java.util.Map;

public interface ISysEveUserService extends IService {
    List<Map<String, Object>> queryUserMationList(String userIds, String staffIds);
}
