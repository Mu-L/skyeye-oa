package com.skyeye.receivable.service.impl;

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
import com.skyeye.contract.service.CrmContractService;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.exception.CustomException;
import com.skyeye.payment.entity.PaymentCollection;
import com.skyeye.receivable.classenum.CrmPayStateEnum;
import com.skyeye.receivable.classenum.CrmReceivableAuthEnum;
import com.skyeye.receivable.dao.ReceivableDao;
import com.skyeye.receivable.entity.Receivable;
import com.skyeye.receivable.service.ReceivableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ReceivableServiceImpl
 * @Description: 应收事项服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/5/2 20:34
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "应收事项管理", groupName = "应收事项管理", flowable = true, teamAuth = true)
public class ReceivableServiceImpl extends SkyeyeFlowableServiceImpl<ReceivableDao, Receivable> implements ReceivableService {

    @Autowired
    private CrmContractService crmContractService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Override
    public Class getAuthEnumClass() {
        return CrmReceivableAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(CrmReceivableAuthEnum.ADD.getKey(), CrmReceivableAuthEnum.EDIT.getKey(), CrmReceivableAuthEnum.DELETE.getKey(),
                CrmReceivableAuthEnum.REVOKE.getKey(), CrmReceivableAuthEnum.INVALID.getKey(), CrmReceivableAuthEnum.SUBMIT_TO_APPROVAL.getKey(),
                CrmReceivableAuthEnum.LIST.getKey());
    }

    @Override
    public void validatorEntity(Receivable entity) {
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
                entity.setPayState(CrmPayStateEnum.PAID_STATE.getKey());
            } else {
                throw new CustomException("已付金额只能等于应付金额");
            }
        }
    }

    @Override
    public QueryWrapper<Receivable> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Receivable> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Receivable::getObjectId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        crmContractService.setMationForMap(beans, "contractId", "contractMation");
        iAuthUserService.setMationForMap(beans, "contactId", "contactMation");
        return beans;
    }

    @Override
    public Receivable selectById(String id) {
        Receivable receivable = super.selectById(id);
        crmContractService.setDataMation(receivable, Receivable::getContractId);
        iAuthUserService.setDataMation(receivable, Receivable::getContactId);
        return receivable;
    }

    @Override
    public void queryReceivableByContractId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String contractId = map.get("contractId").toString();
        if (StrUtil.isEmpty(contractId)) {
            return;
        }
        QueryWrapper<Receivable> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Receivable::getContractId), contractId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Receivable::getState), FlowableStateEnum.PASS.getKey());
        List<Receivable> receivableList = list(queryWrapper);
        receivableList.forEach(item -> {
            item.setName(item.getOddNumber());
        });
        outputObject.setBeans(receivableList);
        outputObject.settotal(receivableList.size());
    }
}
