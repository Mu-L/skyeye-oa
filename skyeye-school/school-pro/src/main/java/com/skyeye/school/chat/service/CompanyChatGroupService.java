package com.skyeye.school.chat.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.school.chat.entity.CompanyChatGroup;

import java.util.List;
import java.util.Map;

public interface CompanyChatGroupService extends SkyeyeBusinessService<CompanyChatGroup> {
    List<Map<String, Object>> queryChatLogByPerToPer(Map<String, Object> map);
}
