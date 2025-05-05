package com.skyeye.store.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.IAreaService;
import com.skyeye.store.dao.ShopAddressHistoryDao;
import com.skyeye.store.entity.ShopAddress;
import com.skyeye.store.entity.ShopAddressHistory;
import com.skyeye.store.service.ShopAddressHistoryService;
import com.skyeye.store.service.ShopAddressLabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "历史收件地址管理", groupName = "历史收件地址管理")
public class ShopAddressHistoryServiceImpl extends SkyeyeBusinessServiceImpl<ShopAddressHistoryDao, ShopAddressHistory> implements ShopAddressHistoryService {

    @Autowired
    private IAreaService iAreaService;

    @Autowired
    private ShopAddressLabelService shopAddressLabelService;

    @Override
    public QueryWrapper<ShopAddressHistory> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ShopAddressHistory> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getTypeId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ShopAddressHistory::getOrderId), commonPageInfo.getTypeId());
        }
        return queryWrapper;
    }

    @Override
    public Map<String, Map<String, Object>> queryListByIds(List<String> addressHistoryIdList) {
        List<ShopAddressHistory> list = selectByIds(addressHistoryIdList.toArray(new String[]{}));
        iAreaService.setDataMation(list, ShopAddressHistory::getProvinceId);
        iAreaService.setDataMation(list, ShopAddressHistory::getCityId);
        iAreaService.setDataMation(list, ShopAddressHistory::getAreaId);
        iAreaService.setDataMation(list, ShopAddressHistory::getTownshipId);
        shopAddressLabelService.setDataMation(list, ShopAddressHistory::getLabelId);
        Map<String, Map<String, Object>> result = list.stream().collect(
                Collectors.toMap(ShopAddressHistory::getId, shopAddress -> JSONUtil.toBean(JSONUtil.toJsonStr(shopAddress), null), (key1, key2) -> key2));
        return result;
    }
}
