/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.cancleleave.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeLinkDataServiceImpl;
import com.skyeye.cancleleave.dao.CancelLeaveTimeSlotDao;
import com.skyeye.cancleleave.entity.CancelLeaveTimeSlot;
import com.skyeye.cancleleave.service.CancelLeaveTimeSlotService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: CancelLeaveTimeSlotServiceImpl
 * @Description: 销假申请销假时间段服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/4/6 14:27
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "销假时间段", groupName = "销假申请", manageShow = false)
public class CancelLeaveTimeSlotServiceImpl extends SkyeyeLinkDataServiceImpl<CancelLeaveTimeSlotDao, CancelLeaveTimeSlot> implements CancelLeaveTimeSlotService {

}
