/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.delivery.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.delivery.dao.ShopDeliveryTemplateChargeDao;
import com.skyeye.delivery.entity.ShopDeliveryTemplate;
import com.skyeye.delivery.entity.ShopDeliveryTemplateCharge;
import com.skyeye.delivery.service.ShopDeliveryTemplateChargeService;
import com.skyeye.delivery.service.ShopDeliveryTemplateService;
import com.skyeye.exception.CustomException;
import com.skyeye.store.entity.ShopArea;
import com.skyeye.store.service.ShopAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ShopDeliveryTemplateChargeServiceImpl
 * @Description: 快递运费模板计费配置信息服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 10:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "快递运费模板计费配置信息管理", groupName = "快递运费模板计费配置信息管理")
public class ShopDeliveryTemplateChargeServiceImpl extends SkyeyeBusinessServiceImpl<ShopDeliveryTemplateChargeDao, ShopDeliveryTemplateCharge> implements ShopDeliveryTemplateChargeService {

    @Autowired
    private ShopDeliveryTemplateService shopDeliveryTemplateService;

    @Autowired
    private ShopAreaService shopAreaService;

    @Override
    public QueryWrapper<ShopDeliveryTemplateCharge> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ShopDeliveryTemplateCharge> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String templateId = commonPageInfo.getObjectId();
        if (StrUtil.isNotEmpty(templateId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ShopDeliveryTemplateCharge::getTemplateId), templateId);
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getTypeId())) {
            // 区域搜索条件   查找包含该区域的信息
//            queryWrapper.apply("JSON_CONTAINS(" + MybatisPlusUtil.toColumns(ShopDeliveryTemplateCharge::getAreaId) + ", '\"{0}\"', '$')", commonPageInfo.getTypeId());
            queryWrapper.like(MybatisPlusUtil.toColumns(ShopDeliveryTemplateCharge::getAreaId), commonPageInfo.getTypeId());
        }
        return queryWrapper;
    }

    protected List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isEmpty(beans)) {
            return new ArrayList<>();
        }
        shopDeliveryTemplateService.setMationForMap(beans, "templateId", "templateMation");
        // 开始处理区域信息
        List<ShopDeliveryTemplateCharge> list = JSONUtil.toList(JSONUtil.toJsonStr(beans), ShopDeliveryTemplateCharge.class);
        List<String> allAreaIdList = new ArrayList<>();
        for (ShopDeliveryTemplateCharge charge : list) {
            allAreaIdList.addAll(charge.getAreaId());
            charge.setAreaList(new ArrayList<>());
        }
        List<String> areaIdList = allAreaIdList.stream().distinct().collect(Collectors.toList());
        List<ShopArea> shopAreas = shopAreaService.selectByIds(areaIdList.toArray(new String[CommonNumConstants.NUM_ZERO]));
        Map<String, ShopArea> areaMap = shopAreas.stream().collect(Collectors.toMap(ShopArea::getId, bean -> bean, (bean1, bean2) -> bean1));
        for (ShopDeliveryTemplateCharge charge : list) {
            for (String areaId : charge.getAreaId()) {
                if (areaMap.containsKey(areaId)) {
                    charge.getAreaList().add(areaMap.get(areaId));
                }
            }
        }
        // 分页查询时获取数据
        return JSONUtil.toList(JSONUtil.toJsonStr(list), null);
    }

    @Override
    public List<Map<String, Object>> queryDataList(InputObject inputObject) {
        QueryWrapper<ShopDeliveryTemplateCharge> queryWrapper = new QueryWrapper<>();
        List<ShopDeliveryTemplateCharge> beans = list(queryWrapper);
        return JSONUtil.toList(JSONUtil.toJsonStr(beans), null);
    }

    @Override
    public void validatorEntity(ShopDeliveryTemplateCharge shopDeliveryTemplateCharge) {
        super.validatorEntity(shopDeliveryTemplateCharge);

        // 初步过滤区域Id
        List<String> areaIdList = shopDeliveryTemplateCharge.getAreaId().stream().filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
//        String areaIds = Joiner.on(CommonCharConstants.COMMA_MARK).join(areaIdList);
        List<ShopArea> shopAreaList = shopAreaService.selectByIds(areaIdList.toArray(new String[areaIdList.size()]));
        // 取出可用区域Id
        List<String> canUseAreaIdList = shopAreaList.stream().map(ShopArea::getId).collect(Collectors.toList());
        if (StrUtil.isEmpty(shopDeliveryTemplateCharge.getId())) {
            // 新增操作
            if (CollectionUtil.isEmpty(canUseAreaIdList)) {
                throw new CustomException("所传的所有区域id不可用");
            }
            shopDeliveryTemplateCharge.setAreaId(canUseAreaIdList);
        } else {
            // 更新操作
            canUseAreaIdList.addAll(shopDeliveryTemplateCharge.getAreaId());
            List<String> newAreaIdList = canUseAreaIdList.stream().distinct().collect(Collectors.toList());
            shopDeliveryTemplateCharge.setAreaId(newAreaIdList);
        }
        ShopDeliveryTemplate shopDeliveryTemplate = shopDeliveryTemplateService.selectById(shopDeliveryTemplateCharge.getTemplateId());
        // 判断shopDeliveryTemplate是否为空，如果为空则抛出异常
        if (StrUtil.isEmpty(shopDeliveryTemplate.getId())) {
            throw new CustomException("模板不存在: " + shopDeliveryTemplateCharge.getTemplateId());
        }
    }

    @Override
    public ShopDeliveryTemplateCharge selectById(String id) {
        ShopDeliveryTemplateCharge charge = super.selectById(id);
        shopDeliveryTemplateService.setDataMation(charge, ShopDeliveryTemplateCharge::getTemplateId);
        if (CollectionUtil.isNotEmpty(charge.getAreaId())) {
            List<ShopArea> shopAreas = shopAreaService.selectByIds(charge.getAreaId().toArray(new String[0]));
            charge.setAreaList(shopAreas);
        }
        return charge;
    }
}
