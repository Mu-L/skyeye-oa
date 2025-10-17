/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.seal.service.impl;

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
import com.skyeye.eve.seal.dao.SealApplyBorrowDao;
import com.skyeye.eve.seal.entity.SealUse;
import com.skyeye.eve.seal.entity.SealUseLink;
import com.skyeye.eve.seal.service.SealApplyBorrowService;
import com.skyeye.eve.seal.service.SealService;
import com.skyeye.eve.seal.service.SealUseLinkService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: SealApplyBorrowServiceImpl
 * @Description: 印章借用服务类--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 15:57
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "印章借用", groupName = "印章模块", flowable = true)
public class SealApplyBorrowServiceImpl extends SkyeyeBusinessServiceImpl<SealApplyBorrowDao, SealUse> implements SealApplyBorrowService {

    @Autowired
    private SealService sealService;

    @Autowired
    private SealUseLinkService sealUseLinkService;

    @Override
    protected QueryWrapper<SealUse> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<SealUse> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SealUse::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        return queryWrapper;
    }

    @Override
    public void validatorEntity(SealUse entity) {
        chectOrderItem(entity.getUseLinks());
    }

    @Override
    public void writePostpose(SealUse entity, String userId) {
        sealUseLinkService.saveLinkList(entity.getId(), entity.getUseLinks());
        super.writePostpose(entity, userId);
    }

    @Override
    public void submitToApprovalPostpose(String id, String processInstanceId) {
        super.submitToApprovalPostpose(id, processInstanceId);
        sealUseLinkService.editStateByPId(id, FlowableChildStateEnum.IN_EXAMINE.getKey());
    }

    private void chectOrderItem(List<SealUseLink> useLinks) {
        if (CollectionUtil.isEmpty(useLinks)) {
            throw new CustomException("请最少选择一条印章信息");
        }
        List<String> sealIds = useLinks.stream().map(SealUseLink::getSealId).distinct()
            .collect(Collectors.toList());
        if (useLinks.size() != sealIds.size()) {
            throw new CustomException("单据中不允许出现同一印章信息");
        }
    }

    @Override
    public SealUse getDataFromDb(String id) {
        SealUse sealUse = super.getDataFromDb(id);
        List<SealUseLink> sealUseLinks = sealUseLinkService.selectByPId(sealUse.getId());
        sealUse.setUseLinks(sealUseLinks);
        return sealUse;
    }

    @Override
    public SealUse selectById(String id) {
        SealUse sealUse = super.selectById(id);
        // 获取印章信息
        sealService.setDataMation(sealUse.getUseLinks(), SealUseLink::getSealId);
        sealUse.getUseLinks().forEach(bean -> {
            bean.setStateName(FlowableChildStateEnum.getStateName(bean.getState()));
        });
        sealUse.setStateName(FlowableStateEnum.getStateName(sealUse.getState()));
        iAuthUserService.setName(sealUse, "createId", "createName");
        return sealUse;
    }

    @Override
    public void revokePostpose(SealUse entity) {
        super.revokePostpose(entity);
        sealUseLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.DRAFT.getKey());
    }

    @Override
    public void approvalEndIsSuccess(SealUse entity) {
        SealUse sealUse = selectById(entity.getId());
        for (SealUseLink bean : sealUse.getUseLinks()) {
            if (StrUtil.isEmpty(bean.getSealMation().getBorrowId())) {
                // 可以借用，给印章表中对应印章填上借用人
                sealService.setSealUse(bean.getSealId(), sealUse.getCreateId());
                sealUseLinkService.editStateById(bean.getId(), FlowableChildStateEnum.ADEQUATE.getKey());
            } else {
                sealUseLinkService.editStateById(bean.getId(), FlowableChildStateEnum.INSUFFICIENT.getKey());
            }
        }
    }

    @Override
    public void approvalEndIsFailed(SealUse entity) {
        sealUseLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.REJECT.getKey());
    }

}
