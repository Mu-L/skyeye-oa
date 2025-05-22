package com.skyeye.payable.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeFlowableServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.contract.service.SupplierContractService;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.exception.CustomException;
import com.skyeye.payable.classenum.ErpPayStateEnum;
import com.skyeye.payable.classenum.ErpSupplierPayableAuthEnum;
import com.skyeye.payable.dao.PayableDao;
import com.skyeye.payable.entity.Payable;
import com.skyeye.payable.service.PayableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: PayableServiceImpl
 * @Description: 应付事项服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/5/2 20:34
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "应付事项管理", groupName = "应付事项管理", flowable = true, teamAuth = true)
public class PayableServiceImpl extends SkyeyeFlowableServiceImpl<PayableDao, Payable> implements PayableService {

    @Autowired
    private SupplierContractService supplierContractService;

    @Autowired
    private IAuthUserService iAuthUserService;
    
    @Override
    public Class getAuthEnumClass() {
        return ErpSupplierPayableAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(ErpSupplierPayableAuthEnum.ADD.getKey(), ErpSupplierPayableAuthEnum.EDIT.getKey(), ErpSupplierPayableAuthEnum.DELETE.getKey(),
                ErpSupplierPayableAuthEnum.REVOKE.getKey(), ErpSupplierPayableAuthEnum.INVALID.getKey(), ErpSupplierPayableAuthEnum.SUBMIT_TO_APPROVAL.getKey(),
                ErpSupplierPayableAuthEnum.LIST.getKey());
    }

    @Override
    public void validatorEntity(Payable entity) {
        super.validatorEntity(entity);
        // 如果单据日期不为空并且时间早于当前时间
        if (StrUtil.isNotEmpty(entity.getInvoiceDate()) &&
                DateUtil.compare(entity.getInvoiceDate(), DateUtil.getTimeAndToString())) {
            throw new CustomException("单据日期不能早于当前时间");
        }else {
            entity.setInvoiceDate(null);
        }
        if (!entity.getPaidPrice().equals(String.valueOf(CommonNumConstants.NUM_ZERO))) {
            if (entity.getPaidPrice().equals(entity.getAmountPrice())) {
                entity.setPayState(ErpPayStateEnum.PAID_STATE.getKey());
            } else {
                throw new CustomException("已付金额只能等于应付金额");
            }
        }
    }

    @Override
    public QueryWrapper<Payable> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Payable> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Payable::getObjectId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        supplierContractService.setMationForMap(beans, "contractId", "contractMation");
        iAuthUserService.setMationForMap(beans, "contactId", "contactMation");
        return beans;
    }

    @Override
    public Payable selectById(String id) {
        Payable payable = super.selectById(id);
        supplierContractService.setDataMation(payable, Payable::getContractId);
        iAuthUserService.setDataMation(payable, Payable::getContactId);
        return payable;
    }

    @Override
    public void queryPayableByContractId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String contractId = map.get("contractId").toString();
        if (StrUtil.isEmpty(contractId)) {
            return;
        }
        QueryWrapper<Payable> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Payable::getContractId), contractId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Payable::getState), FlowableStateEnum.PASS.getKey());
        List<Payable> payableList = list(queryWrapper);
        payableList.forEach(item -> {
            item.setName(item.getOddNumber());
        });
        outputObject.setBeans(payableList);
        outputObject.settotal(payableList.size());
    }
}
