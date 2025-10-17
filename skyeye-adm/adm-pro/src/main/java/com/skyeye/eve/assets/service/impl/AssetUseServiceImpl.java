/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.assets.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableChildStateEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.assets.dao.AssetUseDao;
import com.skyeye.eve.assets.entity.AssetReport;
import com.skyeye.eve.assets.entity.AssetUse;
import com.skyeye.eve.assets.entity.AssetUseLink;
import com.skyeye.eve.assets.service.AssetReportService;
import com.skyeye.eve.assets.service.AssetService;
import com.skyeye.eve.assets.service.AssetUseLinkService;
import com.skyeye.eve.assets.service.AssetUseService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: AssetUseServiceImpl
 * @Description: 资产领用服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/18 17:11
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "资产领用", groupName = "资产模块", flowable = true)
public class AssetUseServiceImpl extends SkyeyeBusinessServiceImpl<AssetUseDao, AssetUse> implements AssetUseService {

    @Autowired
    private AssetUseLinkService assetUseLinkService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private AssetReportService assetReportService;

    @Override
    protected QueryWrapper<AssetUse> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<AssetUse> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AssetUse::getCreateId), userId);
        return queryWrapper;
    }

    @Override
    public void validatorEntity(AssetUse entity) {
        chectOrderItem(entity.getUseLinks());
        setOtherMation(entity);
    }

    @Override
    public void writePostpose(AssetUse entity, String userId) {
        assetUseLinkService.saveLinkList(entity.getId(), entity.getUseLinks());
        super.writePostpose(entity, userId);
    }

    private void chectOrderItem(List<AssetUseLink> useLinks) {
        if (CollectionUtil.isEmpty(useLinks)) {
            throw new CustomException("请最少选择一条资产信息");
        }
        List<String> assetReportId = useLinks.stream()
            .map(AssetUseLink::getAssetReportId).distinct().collect(Collectors.toList());
        if (useLinks.size() != assetReportId.size()) {
            throw new CustomException("单据中不允许出现同一个资产信息");
        }
    }

    private void setOtherMation(AssetUse entity) {
        // 设置资产id
        List<String> assetReportId = entity.getUseLinks().stream().map(AssetUseLink::getAssetReportId).collect(Collectors.toList());
        Map<String, AssetReport> assetReportMap = assetReportService.selectMapByIds(assetReportId);
        entity.getUseLinks().forEach(assetUseLink -> {
            assetUseLink.setAssetId(assetReportMap.get(assetUseLink.getAssetReportId()).getAssetId());
        });
    }

    @Override
    public void submitToApprovalPostpose(String id, String processInstanceId) {
        super.submitToApprovalPostpose(id, processInstanceId);
        assetUseLinkService.editStateByPId(id, FlowableChildStateEnum.IN_EXAMINE.getKey());
    }

    @Override
    public AssetUse getDataFromDb(String id) {
        AssetUse assetUse = super.getDataFromDb(id);
        List<AssetUseLink> assetUseLinks = assetUseLinkService.selectByPId(assetUse.getId());
        assetUse.setUseLinks(assetUseLinks);
        return assetUse;
    }

    @Override
    public AssetUse selectById(String id) {
        AssetUse assetUse = super.selectById(id);
        // 获取资产信息
        assetService.setDataMation(assetUse.getUseLinks(), AssetUseLink::getAssetId);
        assetUse.getUseLinks().forEach(bean -> {
            bean.setStateName(FlowableChildStateEnum.getStateName(bean.getState()));
        });
        // 设置资产明细数据
        assetReportService.setDataMation(assetUse.getUseLinks(), AssetUseLink::getAssetReportId);
        assetUse.setStateName(FlowableStateEnum.getStateName(assetUse.getState()));
        iAuthUserService.setName(assetUse, "createId", "createName");
        return assetUse;
    }

    @Override
    public void revokePostpose(AssetUse entity) {
        super.revokePostpose(entity);
        assetUseLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.DRAFT.getKey());
    }

    @Override
    protected void approvalEndIsSuccess(AssetUse entity) {
        AssetUse assetUse = selectById(entity.getId());
        for (AssetUseLink bean : assetUse.getUseLinks()) {
            // 当前资产的领用人为空
            if (StrUtil.isEmpty(bean.getAssetReportMation().getUseUserId())) {
                // 设置资产的领用人
                assetReportService.setAssetReportEmployee(bean.getAssetReportId(), entity.getId(), entity.getCreateId());
                assetUseLinkService.editStateById(bean.getId(), FlowableChildStateEnum.ADEQUATE.getKey());
            } else {
                // 当前库存不足，修改资产领用状态，审批不通过
                assetUseLinkService.editStateById(bean.getId(), FlowableChildStateEnum.INSUFFICIENT.getKey());
            }
        }
    }

    @Override
    protected void approvalEndIsFailed(AssetUse entity) {
        assetUseLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.REJECT.getKey());
    }

}
