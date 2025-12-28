/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.otherwise.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.business.service.impl.SkyeyeErpOrderServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotOutFromType;
import com.skyeye.depot.entity.DepotOut;
import com.skyeye.depot.service.DepotOutService;
import com.skyeye.exception.CustomException;
import com.skyeye.otherwise.dao.OtherWiseOrderDao;
import com.skyeye.otherwise.entity.OtherWiseOrder;
import com.skyeye.otherwise.service.OtherWiseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: OtherWiseOrderServiceImpl
 * @Description: 其他微服务订单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/20 11:44
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "其他微服务订单", groupName = "其他微服务订单", flowable = true)
public class OtherWiseOrderServiceImpl extends SkyeyeErpOrderServiceImpl<OtherWiseOrderDao, OtherWiseOrder> implements OtherWiseOrderService {

    @Autowired
    private DepotOutService depotOutService;

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void createApprovelSuccessOrder(InputObject inputObject, OutputObject outputObject) {
        OtherWiseOrder otherWiseOrder = inputObject.getParams(OtherWiseOrder.class);
        save(otherWiseOrder);
        skyeyeErpOrderItemService.saveLinkList(otherWiseOrder.getId(), otherWiseOrder.getErpOrderItemList());
    }

    @Override
    public void queryOtherWiseOrderTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        OtherWiseOrder otherWiseOrder = selectById(id);
        // 该其他微服务订单下的已经下达仓库出库单(审核通过)的数量
        Map<String, String> depotNumMap = depotOutService.calcMaterialNormsNumByFromId(otherWiseOrder.getId());
        // 设置未下达商品数量-----其他微服务订单数量 - 已出库数量
        super.setOrCheckOperNumber(otherWiseOrder.getErpOrderItemList(), true, depotNumMap);
        // 过滤掉数量为0的商品信息
        otherWiseOrder.setErpOrderItemList(otherWiseOrder.getErpOrderItemList().stream()
            .filter(erpOrderItem -> {
                String operNumber = StrUtil.isEmpty(erpOrderItem.getOperNumber())
                    ? CommonNumConstants.NUM_ZERO.toString()
                    : erpOrderItem.getOperNumber();
                return CalculationUtil.compareTo(operNumber, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0;
            }).collect(Collectors.toList()));
        outputObject.setBean(otherWiseOrder);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void insertOtherWiseOrderToDepotOut(InputObject inputObject, OutputObject outputObject) {
        DepotOut depotOut = inputObject.getParams(DepotOut.class);
        // 获取采购退货单状态
        OtherWiseOrder otherWiseOrder = selectById(depotOut.getId());
        if (ObjectUtil.isEmpty(otherWiseOrder)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转到仓库出库单
        if (FlowableStateEnum.PASS.getKey().equals(otherWiseOrder.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            depotOut.setFromId(depotOut.getId());
            depotOut.setFromTypeId(DepotOutFromType.getItemKey(otherWiseOrder.getIdKey()));
            depotOut.setId(StrUtil.EMPTY);
            depotOutService.createEntity(depotOut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达仓库出库单.");
        }
    }

}
