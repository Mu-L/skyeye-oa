package com.skyeye.receivepayment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeFlowableServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.contacts.service.IContactsService;
import com.skyeye.receivepayment.classenum.ReceivePaymentKeyEnum;
import com.skyeye.receivepayment.dao.ReceivePaymentDao;
import com.skyeye.receivepayment.entity.ReceivePayment;
import com.skyeye.receivepayment.service.ReceivePaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ReceivePaymentServiceImpl
 * @Description: 收付款管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/4 16:23
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "收付款管理", groupName = "收付款管理")
public class ReceivePaymentServiceImpl extends SkyeyeFlowableServiceImpl<ReceivePaymentDao, ReceivePayment> implements ReceivePaymentService {


    @Autowired
    private IContactsService iContactsService;

    @Override
    public QueryWrapper<ReceivePayment> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ReceivePayment> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ReceivePayment::getObjectId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        return super.queryPageDataList(inputObject);
    }

    @Override
    public void queryReceivePaymentByContractId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String contractId = map.get("contractId").toString();
        if (StrUtil.isEmpty(contractId)) {
            return;
        }
        QueryWrapper<ReceivePayment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ReceivePayment::getContractId), contractId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ReceivePayment::getState), FlowableStateEnum.PASS.getKey());
        List<ReceivePayment> receivableList = list(queryWrapper);
        receivableList.forEach(item -> {
            item.setName(item.getOddNumber());
        });
        outputObject.setBeans(receivableList);
        outputObject.settotal(receivableList.size());
    }

    @Override
    public ReceivePayment selectById(String id) {
        ReceivePayment receivePayment = super.selectById(id);
        iContactsService.setDataMation(receivePayment, ReceivePayment::getContactId);
        if(ReceivePaymentKeyEnum.CRM_PAYMENT_KEY.getKey().equals(receivePayment.getObjectKey())){
            // TODO 远程调用---crm
            // 查询合同信息
            // 查询回款信息
        }else {
            // TODO ---erp
        }
        return receivePayment;
    }
}
