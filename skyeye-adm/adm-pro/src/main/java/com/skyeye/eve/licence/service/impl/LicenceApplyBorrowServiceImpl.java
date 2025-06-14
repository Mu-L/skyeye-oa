/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.licence.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeFlowableServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableChildStateEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.licence.dao.LicenceUseDao;
import com.skyeye.eve.licence.entity.LicenceUse;
import com.skyeye.eve.licence.entity.LicenceUseLink;
import com.skyeye.eve.licence.service.LicenceApplyBorrowService;
import com.skyeye.eve.licence.service.LicenceService;
import com.skyeye.eve.licence.service.LicenceUseLinkService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: LicenceApplyBorrowServiceImpl
 * @Description: 证照借用服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 22:58
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "证照借用", groupName = "证照模块", flowable = true)
public class LicenceApplyBorrowServiceImpl extends SkyeyeFlowableServiceImpl<LicenceUseDao, LicenceUse> implements LicenceApplyBorrowService {

    @Autowired
    private LicenceUseLinkService licenceUseLinkService;

    @Autowired
    private LicenceService licenceService;

    @Override
    protected QueryWrapper<LicenceUse> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<LicenceUse> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(LicenceUse::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        return queryWrapper;
    }

    @Override
    public void validatorEntity(LicenceUse entity) {
        chectOrderItem(entity.getUseLinks());
    }

    @Override
    public void writeChild(LicenceUse entity, String userId) {
        licenceUseLinkService.saveLinkList(entity.getId(), entity.getUseLinks());
        super.writeChild(entity, userId);
    }

    private void chectOrderItem(List<LicenceUseLink> useLinks) {
        if (CollectionUtil.isEmpty(useLinks)) {
            throw new CustomException("请最少选择一条证照信息");
        }
        List<String> licenceIds = useLinks.stream().map(LicenceUseLink::getLicenceId).distinct()
            .collect(Collectors.toList());
        if (useLinks.size() != licenceIds.size()) {
            throw new CustomException("单据中不允许出现同一证照信息");
        }
    }

    @Override
    public void submitToApprovalPostpose(String id, String processInstanceId) {
        super.submitToApprovalPostpose(id, processInstanceId);
        licenceUseLinkService.editStateByPId(id, FlowableChildStateEnum.IN_EXAMINE.getKey());
    }

    @Override
    public LicenceUse getDataFromDb(String id) {
        LicenceUse licenceUse = super.getDataFromDb(id);
        List<LicenceUseLink> licenceUseLinks = licenceUseLinkService.selectByPId(licenceUse.getId());
        licenceUse.setUseLinks(licenceUseLinks);
        return licenceUse;
    }

    @Override
    public LicenceUse selectById(String id) {
        LicenceUse licenceUse = super.selectById(id);
        // 获取证照信息
        licenceService.setDataMation(licenceUse.getUseLinks(), LicenceUseLink::getLicenceId);
        licenceUse.getUseLinks().forEach(bean -> {
            bean.setStateName(FlowableChildStateEnum.getStateName(bean.getState()));
        });
        licenceUse.setStateName(FlowableStateEnum.getStateName(licenceUse.getState()));
        iAuthUserService.setName(licenceUse, "createId", "createName");
        return licenceUse;
    }

    @Override
    public void revokePostpose(LicenceUse entity) {
        super.revokePostpose(entity);
        licenceUseLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.DRAFT.getKey());
    }

    @Override
    public void approvalEndIsSuccess(LicenceUse entity) {
        LicenceUse licenceUse = selectById(entity.getId());
        for (LicenceUseLink bean : licenceUse.getUseLinks()) {
            if (StrUtil.isEmpty(bean.getLicenceMation().getBorrowId())) {
                // 可以借用，给证照表中对应证照填上借用人
                licenceService.setLicenceUse(bean.getLicenceId(), licenceUse.getCreateId());
                licenceUseLinkService.editStateById(bean.getId(), FlowableChildStateEnum.ADEQUATE.getKey());
            } else {
                licenceUseLinkService.editStateById(bean.getId(), FlowableChildStateEnum.INSUFFICIENT.getKey());
            }
        }
    }

    @Override
    public void approvalEndIsFailed(LicenceUse entity) {
        licenceUseLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.REJECT.getKey());
    }

}
