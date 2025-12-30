/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.overtime.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeLinkDataServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.overtime.dao.OverTimeSlotDao;
import com.skyeye.overtime.entity.OverTimeSlot;
import com.skyeye.overtime.service.OverTimeSlotService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: OverTimeSlotServiceImpl
 * @Description: 加班申请加班时间段服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/4/5 21:50
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "加班申请加班时间段", groupName = "加班申请", manageShow = false)
public class OverTimeSlotServiceImpl extends SkyeyeLinkDataServiceImpl<OverTimeSlotDao, OverTimeSlot> implements OverTimeSlotService {

    /**
     * 修改结算状态
     *
     * @param id
     * @param settleState
     */
    @Override
    public void editSettleStateById(String id, Integer settleState) {
        UpdateWrapper<OverTimeSlot> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(OverTimeSlot::getSettleState), settleState);
        update(updateWrapper);
    }

    /**
     * 修改结算类型
     *
     * @param id
     * @param settlementType
     */
    @Override
    public void editSettlementTypeById(String id, Integer settlementType) {
        UpdateWrapper<OverTimeSlot> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(OverTimeSlot::getOvertimeSettlementType), settlementType);
        update(updateWrapper);
    }

}
