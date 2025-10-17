/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.licence.service.impl;

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
import com.skyeye.eve.licence.dao.LicenceRevertDao;
import com.skyeye.eve.licence.entity.LicenceRevert;
import com.skyeye.eve.licence.entity.LicenceRevertLink;
import com.skyeye.eve.licence.service.LicenceApplyRevertService;
import com.skyeye.eve.licence.service.LicenceRevertLinkService;
import com.skyeye.eve.licence.service.LicenceService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: LicenceApplyRevertServiceImpl
 * @Description: 证照归还申请服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 10:49
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "证照归还", groupName = "证照模块", flowable = true)
public class LicenceApplyRevertServiceImpl extends SkyeyeBusinessServiceImpl<LicenceRevertDao, LicenceRevert> implements LicenceApplyRevertService {

    @Autowired
    private LicenceRevertLinkService licenceRevertLinkService;

    @Autowired
    private LicenceService licenceService;

    @Override
    protected QueryWrapper<LicenceRevert> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<LicenceRevert> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(LicenceRevert::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        return queryWrapper;
    }

    @Override
    public void validatorEntity(LicenceRevert entity) {
        chectOrderItem(entity.getRevertLinks());
    }

    @Override
    public void writePostpose(LicenceRevert entity, String userId) {
        licenceRevertLinkService.saveLinkList(entity.getId(), entity.getRevertLinks());
        super.writePostpose(entity, userId);
    }

    private void chectOrderItem(List<LicenceRevertLink> revertLinks) {
        if (CollectionUtil.isEmpty(revertLinks)) {
            throw new CustomException("请最少选择一条证照信息");
        }
        List<String> licenceIds = revertLinks.stream().map(LicenceRevertLink::getLicenceId).distinct()
            .collect(Collectors.toList());
        if (revertLinks.size() != licenceIds.size()) {
            throw new CustomException("单据中不允许出现同一证照信息");
        }
    }

    @Override
    public void submitToApprovalPostpose(String id, String processInstanceId) {
        super.submitToApprovalPostpose(id, processInstanceId);
        licenceRevertLinkService.editStateByPId(id, FlowableChildStateEnum.IN_EXAMINE.getKey());
    }

    @Override
    public LicenceRevert getDataFromDb(String id) {
        LicenceRevert licenceRevert = super.getDataFromDb(id);
        List<LicenceRevertLink> licenceRevertLinks = licenceRevertLinkService.selectByPId(licenceRevert.getId());
        licenceRevert.setRevertLinks(licenceRevertLinks);
        return licenceRevert;
    }

    @Override
    public LicenceRevert selectById(String id) {
        LicenceRevert licenceRevert = super.selectById(id);
        // 获取证照信息
        licenceService.setDataMation(licenceRevert.getRevertLinks(), LicenceRevertLink::getLicenceId);
        licenceRevert.getRevertLinks().forEach(bean -> {
            bean.setStateName(FlowableChildStateEnum.getStateName(bean.getState()));
        });
        licenceRevert.setStateName(FlowableStateEnum.getStateName(licenceRevert.getState()));
        iAuthUserService.setName(licenceRevert, "createId", "createName");
        return licenceRevert;
    }

    @Override
    public void revokePostpose(LicenceRevert entity) {
        super.revokePostpose(entity);
        licenceRevertLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.DRAFT.getKey());
    }

    @Override
    public void approvalEndIsSuccess(LicenceRevert entity) {
        LicenceRevert licenceRevert = selectById(entity.getId());
        for (LicenceRevertLink bean : licenceRevert.getRevertLinks()) {
            if (StrUtil.equals(bean.getLicenceMation().getBorrowId(), licenceRevert.getCreateId())) {
                // 如果当前借用人为归还单申请人可以归还
                licenceService.setLicenceRevert(bean.getLicenceId());
                licenceRevertLinkService.editStateById(bean.getId(), FlowableChildStateEnum.ADEQUATE.getKey());
            } else {
                licenceRevertLinkService.editStateById(bean.getId(), FlowableChildStateEnum.INSUFFICIENT.getKey());
            }
        }
    }

    @Override
    public void approvalEndIsFailed(LicenceRevert entity) {
        licenceRevertLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.REJECT.getKey());
    }

}
