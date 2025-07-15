/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.IAreaService;
import com.skyeye.store.dao.ShopAddressHistoryDao;
import com.skyeye.store.entity.ShopAddressHistory;
import com.skyeye.store.service.ShopAddressHistoryService;
import com.skyeye.store.service.ShopAddressLabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ShopAddressHistoryServiceImpl
 * @Description: 历史收件地址管理服务层--不隔离
 * @author: skyeye云系列--卫志强
 * @date: 2025/6/8 14:56
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "历史收件地址管理", groupName = "历史收件地址管理", tenant = TenantEnum.NO_ISOLATION)
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

    @Override
    public void queryMyShopAddressHistoryPageList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String currentUserId = inputObject.getLogParams().get("id").toString();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ShopAddressHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopAddressHistory::getCreateId), currentUserId)
            .orderByDesc(MybatisPlusUtil.toColumns(ShopAddressHistory::getCreateTime));
        if (StrUtil.isNotEmpty(commonPageInfo.getTypeId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ShopAddressHistory::getOrderId), commonPageInfo.getTypeId());
        }
        List<ShopAddressHistory> beans = list(queryWrapper);
        shopAddressLabelService.setDataMation(beans, ShopAddressHistory::getLabelId);
        outputObject.setBeans(beans);
        outputObject.settotal(page.getTotal());
    }
}
