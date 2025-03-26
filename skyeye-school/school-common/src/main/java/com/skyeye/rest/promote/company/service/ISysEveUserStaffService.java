package com.skyeye.rest.promote.company.service;

import com.skyeye.base.rest.service.IService;
import com.skyeye.common.entity.search.CommonPageInfo;

import java.util.List;
import java.util.Map;

public interface ISysEveUserStaffService extends IService {

    List<Map<String, Object>> querySysUserStaffList(CommonPageInfo commonPageInfo);
}
