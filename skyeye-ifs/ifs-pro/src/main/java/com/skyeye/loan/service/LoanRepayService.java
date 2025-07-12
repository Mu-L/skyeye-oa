/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.loan.service;

import com.skyeye.base.business.service.SkyeyeFlowableService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.loan.entity.LoanRepay;

import java.util.List;

/**
 * @ClassName: LoanRepayService
 * @Description: 还款单服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/5 14:20
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface LoanRepayService extends SkyeyeFlowableService<LoanRepay> {

    List<LoanRepay> queryLoanRepayList(String  month);
}
