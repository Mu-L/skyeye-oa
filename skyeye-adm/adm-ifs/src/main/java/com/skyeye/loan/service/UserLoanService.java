/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.loan.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.loan.entity.UserLoan;

/**
 * @ClassName: UserLoanService
 * @Description: 用户借款金额服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/5 13:37
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface UserLoanService extends SkyeyeBusinessService<UserLoan> {

    void calcUserLoanPrice(String userId, String price, boolean type);

}
