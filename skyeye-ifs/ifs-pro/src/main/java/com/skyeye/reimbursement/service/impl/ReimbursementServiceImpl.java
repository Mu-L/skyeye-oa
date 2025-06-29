/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.reimbursement.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeFlowableServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableChildStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.reimbursement.dao.ReimbursementDao;
import com.skyeye.reimbursement.entity.Reimbursement;
import com.skyeye.reimbursement.entity.ReimbursementChild;
import com.skyeye.reimbursement.service.ReimbursementChildService;
import com.skyeye.reimbursement.service.ReimbursementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: ReimbursementServiceImpl
 * @Description: 报销订单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/4 16:27
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "报销订单", groupName = "报销订单", flowable = true)
public class ReimbursementServiceImpl extends SkyeyeFlowableServiceImpl<ReimbursementDao, Reimbursement> implements ReimbursementService {

    @Autowired
    private ReimbursementChildService reimbursementChildService;

    @Override
    public QueryWrapper<Reimbursement> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Reimbursement> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.equals(commonPageInfo.getType(), "myCreate")) {
            // 我创建的
            queryWrapper.eq(MybatisPlusUtil.toColumns(Reimbursement::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        }
        return queryWrapper;
    }

    @Override
    public void validatorEntity(Reimbursement entity) {
        super.validatorEntity(entity);
        entity.setPrice(reimbursementChildService.calcOrderAllTotalPrice(entity.getReimbursementChildList()));
    }

    @Override
    public void writeChild(Reimbursement entity, String userId) {
        reimbursementChildService.saveLinkList(entity.getId(), entity.getReimbursementChildList());
        super.writeChild(entity, userId);
    }

    @Override
    public void submitToApprovalPostpose(String id, String processInstanceId) {
        super.submitToApprovalPostpose(id, processInstanceId);
        reimbursementChildService.editStateByPId(id, FlowableChildStateEnum.IN_EXAMINE.getKey());
    }

    @Override
    public Reimbursement getDataFromDb(String id) {
        Reimbursement reimbursement = super.getDataFromDb(id);
        List<ReimbursementChild> reimbursementChildList = reimbursementChildService.selectByPId(reimbursement.getId());
        reimbursement.setReimbursementChildList(reimbursementChildList);
        return reimbursement;
    }

    @Override
    public Reimbursement selectById(String id) {
        Reimbursement reimbursement = super.selectById(id);
        iSysDictDataService.setDataMation(reimbursement, Reimbursement::getPayTypeId);
        iSysDictDataService.setDataMation(reimbursement.getReimbursementChildList(), ReimbursementChild::getReimburseProId);
        return reimbursement;
    }

    @Override
    public void deletePostpose(String id) {
        reimbursementChildService.deleteByPId(id);
    }

    @Override
    public void revokePostpose(Reimbursement entity) {
        super.revokePostpose(entity);
        reimbursementChildService.editStateByPId(entity.getId(), FlowableChildStateEnum.DRAFT.getKey());
    }

    @Override
    protected void approvalEndIsSuccess(Reimbursement entity) {
        reimbursementChildService.editStateByPId(entity.getId(), FlowableChildStateEnum.ADEQUATE.getKey());
    }

    @Override
    protected void approvalEndIsFailed(Reimbursement entity) {
        reimbursementChildService.editStateByPId(entity.getId(), FlowableChildStateEnum.REJECT.getKey());
    }

    @Override
    public void queryCostAnalysis(InputObject inputObject, OutputObject outputObject) {
        reimbursementChildService.queryReimbursementAnalysis(inputObject, outputObject);
    }
}
