/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.pay.service;

import com.skyeye.base.rest.service.IService;
import com.skyeye.common.object.ResultEntity;

import java.util.Map;

/**
 * @ClassName: IPayService
 * @Description: 支付接口
 * @author: skyeye云系列--卫志强
 * @date: 2024/11/21 9:35
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface IPayService extends IService {

    ResultEntity payment(Map<String, Object> data, String channelCode, String returnUrl, String channelExtras, String notifyUrl);

    /**
     * 生成支付二维码
     *
     * @param data        订单数据
     * @param channelCode 支付渠道
     * @param ip          支付请求的IP地址
     * @param notifyUrl   支付结果通知地址
     * @return
     */
    Map<String, Object> generatePayRrCode(Map<String, Object> data, String channelCode, String ip, String notifyUrl);

}
