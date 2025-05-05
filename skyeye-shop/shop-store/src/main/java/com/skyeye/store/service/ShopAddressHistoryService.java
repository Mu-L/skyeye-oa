package com.skyeye.store.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.store.entity.ShopAddressHistory;

import java.util.List;
import java.util.Map;

public interface ShopAddressHistoryService extends SkyeyeBusinessService<ShopAddressHistory> {
    Map<String,Map<String, Object>> queryListByIds(List<String> addressHistoryIdList);
}
