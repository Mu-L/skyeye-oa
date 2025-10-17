/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.assets.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableChildStateEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.assets.dao.AssetReturnDao;
import com.skyeye.eve.assets.entity.AssetReport;
import com.skyeye.eve.assets.entity.AssetReturn;
import com.skyeye.eve.assets.entity.AssetReturnLink;
import com.skyeye.eve.assets.service.AssetReportService;
import com.skyeye.eve.assets.service.AssetReturnLinkService;
import com.skyeye.eve.assets.service.AssetReturnService;
import com.skyeye.eve.assets.service.AssetService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: AssetReturnServiceImpl
 * @Description: 资产归还单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/20 22:38
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "资产归还", groupName = "资产模块", flowable = true)
public class AssetReturnServiceImpl extends SkyeyeBusinessServiceImpl<AssetReturnDao, AssetReturn> implements AssetReturnService {

    @Autowired
    private AssetReturnLinkService assetReturnLinkService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private AssetReportService assetReportService;

    @Override
    protected QueryWrapper<AssetReturn> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<AssetReturn> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AssetReturn::getCreateId), userId);
        return queryWrapper;
    }

    @Override
    public void validatorEntity(AssetReturn entity) {
        chectOrderItem(entity.getReturnLinks());
        setOtherMation(entity);
    }

    @Override
    public void writePostpose(AssetReturn entity, String userId) {
        assetReturnLinkService.saveLinkList(entity.getId(), entity.getReturnLinks());
        super.writePostpose(entity, userId);
    }

    private void chectOrderItem(List<AssetReturnLink> returnLinks) {
        if (CollectionUtil.isEmpty(returnLinks)) {
            throw new CustomException("请最少选择一条资产信息");
        }
        List<String> assetReportId = returnLinks.stream()
            .map(AssetReturnLink::getAssetReportId).distinct().collect(Collectors.toList());
        if (returnLinks.size() != assetReportId.size()) {
            throw new CustomException("单据中不允许出现同一个资产信息");
        }
    }

    private void setOtherMation(AssetReturn entity) {
        // 设置资产id
        List<String> assetReportId = entity.getReturnLinks().stream().map(AssetReturnLink::getAssetReportId).collect(Collectors.toList());
        Map<String, AssetReport> assetReportMap = assetReportService.selectMapByIds(assetReportId);
        entity.getReturnLinks().forEach(assetReturnLink -> {
            assetReturnLink.setAssetId(assetReportMap.get(assetReturnLink.getAssetReportId()).getAssetId());
        });
    }

    @Override
    public void submitToApprovalPostpose(String id, String processInstanceId) {
        super.submitToApprovalPostpose(id, processInstanceId);
        assetReturnLinkService.editStateByPId(id, FlowableChildStateEnum.IN_EXAMINE.getKey());
    }

    @Override
    public AssetReturn getDataFromDb(String id) {
        AssetReturn assetReturn = super.getDataFromDb(id);
        List<AssetReturnLink> assetReturnLinks = assetReturnLinkService.selectByPId(assetReturn.getId());
        assetReturn.setReturnLinks(assetReturnLinks);
        return assetReturn;
    }

    @Override
    public AssetReturn selectById(String id) {
        AssetReturn assetReturn = super.selectById(id);
        // 获取资产信息
        assetService.setDataMation(assetReturn.getReturnLinks(), AssetReturnLink::getAssetId);
        assetReturn.getReturnLinks().forEach(bean -> {
            bean.setStateName(FlowableChildStateEnum.getStateName(bean.getState()));
        });
        // 设置资产明细数据
        assetReportService.setDataMation(assetReturn.getReturnLinks(), AssetReturnLink::getAssetReportId);
        assetReturn.setStateName(FlowableStateEnum.getStateName(assetReturn.getState()));
        iAuthUserService.setName(assetReturn, "createId", "createName");
        return assetReturn;
    }

    @Override
    public void revokePostpose(AssetReturn entity) {
        super.revokePostpose(entity);
        assetReturnLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.DRAFT.getKey());
    }

    @Override
    protected void approvalEndIsSuccess(AssetReturn entity) {
        AssetReturn assetReturn = selectById(entity.getId());
        for (AssetReturnLink bean : assetReturn.getReturnLinks()) {
            assetReportService.setAssetReportRevert(bean.getAssetReportId(), entity.getId(), entity.getCreateId());
        }
        assetReturnLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.ADEQUATE.getKey());
    }

    @Override
    protected void approvalEndIsFailed(AssetReturn entity) {
        assetReturnLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.REJECT.getKey());
    }

}
