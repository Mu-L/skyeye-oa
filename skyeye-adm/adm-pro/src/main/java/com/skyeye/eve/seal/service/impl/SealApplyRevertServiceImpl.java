/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.seal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeFlowableServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableChildStateEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.seal.dao.SealApplyRevertDao;
import com.skyeye.eve.seal.entity.SealRevert;
import com.skyeye.eve.seal.entity.SealRevertLink;
import com.skyeye.eve.seal.service.SealApplyRevertService;
import com.skyeye.eve.seal.service.SealRevertLinkService;
import com.skyeye.eve.seal.service.SealService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: SealApplyRevertServiceImpl
 * @Description: 印章归还申请服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 17:40
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "印章归还", groupName = "印章模块", flowable = true)
public class SealApplyRevertServiceImpl extends SkyeyeFlowableServiceImpl<SealApplyRevertDao, SealRevert> implements SealApplyRevertService {

    @Autowired
    private SealService sealService;

    @Autowired
    private SealRevertLinkService sealRevertLinkService;

    @Override
    protected QueryWrapper<SealRevert> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<SealRevert> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SealRevert::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        return queryWrapper;
    }

    @Override
    public void validatorEntity(SealRevert entity) {
        chectOrderItem(entity.getRevertLinks());
    }

    @Override
    public void writeChild(SealRevert entity, String userId) {
        sealRevertLinkService.saveLinkList(entity.getId(), entity.getRevertLinks());
        super.writeChild(entity, userId);
    }

    private void chectOrderItem(List<SealRevertLink> revertLinks) {
        if (CollectionUtil.isEmpty(revertLinks)) {
            throw new CustomException("请最少选择一条印章信息");
        }
        List<String> sealIds = revertLinks.stream().map(SealRevertLink::getSealId).distinct()
            .collect(Collectors.toList());
        if (revertLinks.size() != sealIds.size()) {
            throw new CustomException("单据中不允许出现同一印章信息");
        }
    }

    @Override
    public void submitToApprovalPostpose(String id, String processInstanceId) {
        super.submitToApprovalPostpose(id, processInstanceId);
        sealRevertLinkService.editStateByPId(id, FlowableChildStateEnum.IN_EXAMINE.getKey());
    }

    @Override
    public SealRevert getDataFromDb(String id) {
        SealRevert sealRevert = super.getDataFromDb(id);
        List<SealRevertLink> sealRevertLinks = sealRevertLinkService.selectByPId(sealRevert.getId());
        sealRevert.setRevertLinks(sealRevertLinks);
        return sealRevert;
    }

    @Override
    public SealRevert selectById(String id) {
        SealRevert sealRevert = super.selectById(id);
        // 获取印章信息
        sealService.setDataMation(sealRevert.getRevertLinks(), SealRevertLink::getSealId);
        sealRevert.getRevertLinks().forEach(bean -> {
            bean.setStateName(FlowableChildStateEnum.getStateName(bean.getState()));
        });
        sealRevert.setStateName(FlowableStateEnum.getStateName(sealRevert.getState()));
        iAuthUserService.setName(sealRevert, "createId", "createName");
        return sealRevert;
    }

    @Override
    public void revokePostpose(SealRevert entity) {
        super.revokePostpose(entity);
        sealRevertLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.DRAFT.getKey());
    }

    @Override
    public void approvalEndIsSuccess(SealRevert entity) {
        SealRevert sealRevert = selectById(entity.getId());
        for (SealRevertLink bean : sealRevert.getRevertLinks()) {
            sealService.setSealRevert(bean.getSealId());
        }
        sealRevertLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.ADEQUATE.getKey());
    }

    @Override
    public void approvalEndIsFailed(SealRevert entity) {
        sealRevertLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.REJECT.getKey());
    }

}
