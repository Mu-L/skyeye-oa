/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.delivery.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.delivery.dao.ShopDeliveryCompanyDao;
import com.skyeye.delivery.entity.ShopDeliveryCompany;
import com.skyeye.delivery.service.ShopDeliveryCompanyService;
import com.skyeye.exception.CustomException;
import com.skyeye.store.entity.ShopStore;
import com.skyeye.store.service.ShopStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ShopDeliveryCompanyServiceImpl
 * @Description: 快递运费模版服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 10:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "快递公司管理", groupName = "快递公司管理")
public class ShopDeliveryCompanyServiceImpl extends SkyeyeBusinessServiceImpl<ShopDeliveryCompanyDao, ShopDeliveryCompany> implements ShopDeliveryCompanyService {

    @Autowired
    private ShopStoreService shopStoreService;

    /**
     * 分页查询-快递公司
     *
     * @param commonPageInfo
     * @return
     */
    @Override
    public QueryWrapper<ShopDeliveryCompany> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ShopDeliveryCompany> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ShopDeliveryCompany::getStoreId), commonPageInfo.getObjectId());
        }
        if (commonPageInfo.getEnabled() != null) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ShopDeliveryCompany::getEnabled), commonPageInfo.getEnabled());
        }
        return queryWrapper;
    }

    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isNotEmpty(beans)) {
            shopStoreService.setMationForMap(beans, "storeId", "storeMation");
        }
        // 分页查询时获取数据
        return beans;
    }

    /**
     * 获取全部已启用广告位管理信息
     *
     * @param inputObject 入参以及用户信息等获取对象
     * @return
     */
    @Override
    public List<Map<String, Object>> queryDataList(InputObject inputObject) {
        QueryWrapper<ShopDeliveryCompany> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopDeliveryCompany::getEnabled), EnableEnum.ENABLE_USING.getKey());
        List<ShopDeliveryCompany> beans = list(queryWrapper);
        return JSONUtil.toList(JSONUtil.toJsonStr(beans), null);
    }

    /**
     * 重写新增编辑前置条件快递公司管理信息
     */
    @Override
    public void validatorEntity(ShopDeliveryCompany shopDeliveryCompany) {
        super.validatorEntity(shopDeliveryCompany);
        if (StrUtil.isNotEmpty(shopDeliveryCompany.getCodeNum()) && shopDeliveryCompany.getCodeNum().length() > 50) {
            throw new CustomException("快递公司 code过长");
        }
        if (StrUtil.isNotEmpty(shopDeliveryCompany.getName()) && shopDeliveryCompany.getName().length() > 50) {
            throw new CustomException("快递公司名称过长");
        }
        if (shopDeliveryCompany.getOrderBy() < -128 || shopDeliveryCompany.getOrderBy() > 127) {
            throw new CustomException("运费模板排序值超出范围");
        }
        //判断StoreId是否存在
        if (ObjectUtil.isNotNull(shopDeliveryCompany.getStoreId())) {
            ShopStore shopStore = shopStoreService.selectById(shopDeliveryCompany.getStoreId());
            //判断shopStore是否为空，如果为空，则抛出异常
            if (StrUtil.isEmpty(shopStore.getId())) {
                throw new CustomException("门店不存在: " + shopDeliveryCompany.getStoreId());
            }
        }
    }

    @Override
    public ShopDeliveryCompany selectById(String id) {
        ShopDeliveryCompany shopDeliveryCompany = super.selectById(id);
        if (StrUtil.isNotEmpty(shopDeliveryCompany.getId()) && StrUtil.isNotEmpty(shopDeliveryCompany.getStoreId())) {
            shopStoreService.setDataMation(shopDeliveryCompany, ShopDeliveryCompany::getStoreId);
        }
        return shopDeliveryCompany;
    }
}
