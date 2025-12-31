/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.order.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.skyeye.account.service.AccountService;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.books.service.IfsSetOfBooksService;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.CorrespondentEnterEnum;
import com.skyeye.common.enumeration.FlowableChildStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.crm.service.ICustomerService;
import com.skyeye.erp.service.ISupplierService;
import com.skyeye.order.dao.IncomeOrderDao;
import com.skyeye.order.entity.IncomeOrder;
import com.skyeye.order.entity.IncomeOrderItem;
import com.skyeye.order.service.IncomeOrderItemService;
import com.skyeye.order.service.IncomeOrderService;
import com.skyeye.subject.service.IfsAccountSubjectService;
import com.skyeye.voucher.classenum.VoucherState;
import com.skyeye.voucher.service.IfsVoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: IncomeOrderServiceImpl
 * @Description: 明细账管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 21:31
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "财务凭证管理", groupName = "财务凭证管理", flowable = true)
public class IncomeOrderServiceImpl extends SkyeyeBusinessServiceImpl<IncomeOrderDao, IncomeOrder> implements IncomeOrderService {

    @Autowired
    private IncomeOrderItemService incomeOrderItemService;

    @Autowired
    private IfsVoucherService ifsVoucherService;

    @Autowired
    private ICustomerService iCustomerService;

    @Autowired
    private ISupplierService iSupplierService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private IfsSetOfBooksService ifsSetOfBooksService;

    @Autowired
    private IfsAccountSubjectService ifsAccountSubjectService;

    @Override
    public QueryWrapper<IncomeOrder> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<IncomeOrder> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.equals(commonPageInfo.getType(), "myCreate")) {
            // 我创建的
            queryWrapper.eq(MybatisPlusUtil.toColumns(IncomeOrder::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getTypeId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(IncomeOrder::getType), commonPageInfo.getTypeId());
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iAuthUserService.setNameForMap(beans, "handsPersonId", "handsPersonName");
        setHolderMation(beans);
        return beans;
    }

    /**
     * 设置往来单位信息
     *
     * @param beans
     */
    private void setHolderMation(List<Map<String, Object>> beans) {
        // 客户
        List<String> customIds = beans.stream().filter(bean -> StrUtil.equals(bean.get("holderKey").toString(), CorrespondentEnterEnum.CUSTOM.getKey()))
            .map(bean -> bean.get("holderId").toString()).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(customIds)) {
            Map<String, Map<String, Object>> customMap = iCustomerService.queryDataMationForMapByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(customIds));
            beans.forEach(bean -> {
                if (StrUtil.equals(bean.get("holderKey").toString(), CorrespondentEnterEnum.CUSTOM.getKey())) {
                    String holderId = bean.get("holderId").toString();
                    bean.put("holderMation", customMap.get(holderId));
                }
            });
        }
        // 供应商
        List<String> supplierIds = beans.stream().filter(bean -> StrUtil.equals(bean.get("holderKey").toString(), CorrespondentEnterEnum.SUPPLIER.getKey()))
            .map(bean -> bean.get("holderId").toString()).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(supplierIds)) {
            Map<String, Map<String, Object>> supplierMap = iSupplierService.queryDataMationForMapByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(supplierIds));
            beans.forEach(bean -> {
                if (StrUtil.equals(bean.get("holderKey").toString(), CorrespondentEnterEnum.SUPPLIER.getKey())) {
                    String holderId = bean.get("holderId").toString();
                    bean.put("holderMation", supplierMap.get(holderId));
                }
            });
        }
    }

    @Override
    public void writePostpose(IncomeOrder entity, String userId) {
        incomeOrderItemService.saveLinkList(entity.getId(), entity.getInitem());
        super.writePostpose(entity, userId);
    }

    @Override
    public void submitToApprovalPostpose(String id, String processInstanceId) {
        super.submitToApprovalPostpose(id, processInstanceId);
        incomeOrderItemService.editStateByPId(id, FlowableChildStateEnum.IN_EXAMINE.getKey());
    }

    @Override
    public IncomeOrder selectById(String id) {
        IncomeOrder incomeOrder = super.selectById(id);
        // 往来单位
        if (StrUtil.equals(incomeOrder.getHolderKey(), CorrespondentEnterEnum.CUSTOM.getKey())) {
            incomeOrder.setHolderMation(iCustomerService.queryDataMationById(incomeOrder.getHolderId()));
        } else if (StrUtil.equals(incomeOrder.getHolderKey(), CorrespondentEnterEnum.SUPPLIER.getKey())) {
            incomeOrder.setHolderMation(iSupplierService.queryDataMationById(incomeOrder.getHolderId()));
        }
        // 经手人
        incomeOrder.setHandsPersonMation(iAuthUserService.queryDataMationById(incomeOrder.getHandsPersonId()));
        // 账户
        incomeOrder.setAccountMation(accountService.selectMapById(incomeOrder.getAccountId()));
        // 账套
        incomeOrder.setSetOfBooksMation(ifsSetOfBooksService.selectMapById(incomeOrder.getSetOfBooksId()));
        // 凭证信息/会计科目信息
        List<String> voucherIds = incomeOrder.getInitem().stream().map(IncomeOrderItem::getVoucherId).collect(Collectors.toList());
        Map<String, Map<String, Object>> voucherMap = ifsVoucherService.selectValIsMapByIds(voucherIds);
        List<String> subjectIds = incomeOrder.getInitem().stream().map(IncomeOrderItem::getSubjectId).collect(Collectors.toList());
        Map<String, Map<String, Object>> subjectMap = ifsAccountSubjectService.selectValIsMapByIds(subjectIds);
        incomeOrder.getInitem().forEach(incomeOrderItem -> {
            incomeOrderItem.setVoucherMation(voucherMap.get(incomeOrderItem.getVoucherId()));
            incomeOrderItem.setSubjectMation(subjectMap.get(incomeOrderItem.getSubjectId()));
        });

        return incomeOrder;
    }

    @Override
    public IncomeOrder getDataFromDb(String id) {
        IncomeOrder incomeOrder = super.getDataFromDb(id);
        List<IncomeOrderItem> incomeOrderItemList = incomeOrderItemService.selectByPId(incomeOrder.getId());
        incomeOrderItemList.forEach(incomeOrderItem -> {
            incomeOrderItem.setEachAmount(CalculationUtil.formatNoScale(incomeOrderItem.getEachAmount()));
        });
        incomeOrder.setInitem(incomeOrderItemList);
        return incomeOrder;
    }

    /**
     * 撤销完成的后置执行
     *
     * @param entity
     */
    @Override
    public void revokePostpose(IncomeOrder entity) {
        super.revokePostpose(entity);
        incomeOrderItemService.editStateByPId(entity.getId(), FlowableChildStateEnum.DRAFT.getKey());
    }

    /**
     * 审批成功的回调函数
     *
     * @param entity
     */
    @Override
    protected void approvalEndIsSuccess(IncomeOrder entity) {
        incomeOrderItemService.editStateByPId(entity.getId(), FlowableChildStateEnum.ADEQUATE.getKey());
        List<IncomeOrderItem> incomeOrderItemList = incomeOrderItemService.selectByPId(entity.getId());
        // 修改凭证状态为已整理
        incomeOrderItemList.forEach(incomeOrderItem -> {
            ifsVoucherService.editIfsVoucherState(incomeOrderItem.getVoucherId(), VoucherState.CLUTTERED.getKey());
        });
    }

    /**
     * 审批失败的回调函数
     *
     * @param entity
     */
    @Override
    protected void approvalEndIsFailed(IncomeOrder entity) {
        incomeOrderItemService.editStateByPId(entity.getId(), FlowableChildStateEnum.REJECT.getKey());
    }

}
