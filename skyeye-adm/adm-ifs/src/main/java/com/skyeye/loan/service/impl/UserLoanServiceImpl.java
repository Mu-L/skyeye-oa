/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.loan.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.loan.dao.UserLoanDao;
import com.skyeye.loan.entity.UserLoan;
import com.skyeye.loan.service.UserLoanService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: UserLoanServiceImpl
 * @Description: 用户借款金额服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/5 13:38
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "用户借款金额", groupName = "用户借款金额", manageShow = false)
public class UserLoanServiceImpl extends SkyeyeBusinessServiceImpl<UserLoanDao, UserLoan> implements UserLoanService {

    @Override
    public void calcUserLoanPrice(String userId, String price, boolean type) {
        UserLoan userLoan = selectById(userId);
        String allPrice = "0";
        if (ObjectUtil.isNotEmpty(userLoan) && StrUtil.isNotEmpty(userLoan.getId())) {
            allPrice = userLoan.getPrice();
        }
        if (type) {
            // 增加
            allPrice = CalculationUtil.add(CommonNumConstants.NUM_TWO, allPrice, price);
        } else {
            // 减少
            allPrice = CalculationUtil.subtract(allPrice, price, CommonNumConstants.NUM_TWO);
        }
        if (ObjectUtil.isNotEmpty(userLoan) && StrUtil.isNotEmpty(userLoan.getId())) {
            // 编辑
            userLoan.setPrice(allPrice);
            updateEntity(userLoan, StrUtil.EMPTY);
        } else {
            // 新增
            userLoan.setPrice(allPrice);
            userLoan.setUserId(userId);
            createEntity(userLoan, StrUtil.EMPTY);
        }
    }

}
