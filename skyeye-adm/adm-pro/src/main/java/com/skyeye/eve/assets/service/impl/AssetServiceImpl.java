/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.assets.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.assets.dao.AssetDao;
import com.skyeye.eve.assets.entity.Asset;
import com.skyeye.eve.assets.service.AssetReportService;
import com.skyeye.eve.assets.service.AssetService;
import com.skyeye.exception.CustomException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: AssetServiceImpl
 * @Description: 资产管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 13:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "资产管理", groupName = "资产模块")
public class AssetServiceImpl extends SkyeyeBusinessServiceImpl<AssetDao, Asset> implements AssetService {

    @Autowired
    private AssetReportService assetReportService;

    @Override
    public void validatorEntity(Asset entity) {
        super.validatorEntity(entity);
        QueryWrapper<Asset> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper ->
            wrapper.eq(MybatisPlusUtil.toColumns(Asset::getName), entity.getName())
                .or().eq(MybatisPlusUtil.toColumns(Asset::getNumberPrefix), entity.getNumberPrefix()));
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        Asset checkAssetMation = getOne(queryWrapper, false);
        if (ObjectUtil.isNotEmpty(checkAssetMation)) {
            throw new CustomException("资产名称或编号前缀已存在.");
        }
    }

    @Override
    public void deletePreExecution(String id) {
        Long assetReportNum = assetReportService.getAssetReportNumByAssetId(id);
        if (assetReportNum > 0) {
            throw new CustomException("无法删除拥有明细的资产.");
        }
    }

    @Override
    public Asset selectById(String id) {
        Asset asset = super.selectById(id);
        return asset;
    }

    @Override
    public List<Asset> selectByIds(String... ids) {
        List<Asset> assetList = super.selectByIds(ids);
        return assetList;
    }

}
