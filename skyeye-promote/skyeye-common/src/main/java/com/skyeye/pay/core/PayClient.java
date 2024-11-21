/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.core;

import com.skyeye.pay.core.dto.order.PayOrderRespDTO;
import com.skyeye.pay.core.dto.order.PayOrderUnifiedReqDTO;
import com.skyeye.pay.core.dto.refund.PayRefundRespDTO;
import com.skyeye.pay.core.dto.refund.PayRefundUnifiedReqDTO;
import com.skyeye.pay.core.dto.transfer.PayTransferRespDTO;
import com.skyeye.pay.core.dto.transfer.PayTransferUnifiedReqDTO;
import com.skyeye.pay.enums.PayTransferType;

import java.util.Map;

/**
 * @ClassName: PayClient
 * @Description: 支付客户端，用于对接各支付渠道的 SDK，实现发起支付、退款等功能
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/10 8:23
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface PayClient {

    /**
     * 获得渠道编号
     *
     * @return 渠道编号
     */
    String getId();

    // ============ 支付相关 ==========

    /**
     * 调用支付渠道，统一下单
     *
     * @param reqDTO 下单信息
     * @return 支付订单信息
     */
    PayOrderRespDTO unifiedOrder(PayOrderUnifiedReqDTO reqDTO);

    /**
     * 解析 order 回调数据
     *
     * @param params HTTP 回调接口 content type 为 application/x-www-form-urlencoded 的所有参数
     * @param body   HTTP 回调接口的 request body
     * @return 支付订单信息
     */
    PayOrderRespDTO parseOrderNotify(Map<String, String> params, String body);

    /**
     * 获得支付订单信息
     *
     * @param outTradeNo 外部订单号
     * @return 支付订单信息
     */
    PayOrderRespDTO getOrder(String outTradeNo);

    // ============ 退款相关 ==========

    /**
     * 调用支付渠道，进行退款
     *
     * @param reqDTO 统一退款请求信息
     * @return 退款信息
     */
    PayRefundRespDTO unifiedRefund(PayRefundUnifiedReqDTO reqDTO);

    /**
     * 解析 refund 回调数据
     *
     * @param params HTTP 回调接口 content type 为 application/x-www-form-urlencoded 的所有参数
     * @param body   HTTP 回调接口的 request body
     * @return 支付订单信息
     */
    PayRefundRespDTO parseRefundNotify(Map<String, String> params, String body);

    /**
     * 获得退款订单信息
     *
     * @param outTradeNo  外部订单号
     * @param outRefundNo 外部退款号
     * @return 退款订单信息
     */
    PayRefundRespDTO getRefund(String outTradeNo, String outRefundNo);

    /**
     * 调用渠道，进行转账
     *
     * @param reqDTO 统一转账请求信息
     * @return 转账信息
     */
    PayTransferRespDTO unifiedTransfer(PayTransferUnifiedReqDTO reqDTO);

    /**
     * 获得转账订单信息
     *
     * @param outTradeNo 外部订单号
     * @param type       转账类型
     * @return 转账信息
     */
    PayTransferRespDTO getTransfer(String outTradeNo, PayTransferType type);

    /**
     * @param outTradeNo 订单号
     * @param body       订单描述
     * @param totalFee   订单金额，单位：分
     * @param ip         用户 IP
     * @param notifyUrl  支付结果通知 URL
     * @return
     */
    String generateRrCode(String outTradeNo, String body, String totalFee, String ip, String notifyUrl);
}
