/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: PayService
 * @Description: 统一支付接口
 * @author: skyeye云系列--卫志强
 * @date: 2024/11/21 8:46
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface PayService {

    void payment(InputObject inputObject, OutputObject outputObject);

    void generatePayRrCode(InputObject inputObject, OutputObject outputObject);
}
