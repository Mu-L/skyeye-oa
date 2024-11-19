package com.skyeye.school.test.dao;

import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.school.test.entity.Test;

import java.util.List;
import java.util.Map;

public interface TestDao extends SkyeyeBaseMapper<Test>{


    List<Map<String, Object>> selectTestList(CommonPageInfo commonPageInfo);
}
