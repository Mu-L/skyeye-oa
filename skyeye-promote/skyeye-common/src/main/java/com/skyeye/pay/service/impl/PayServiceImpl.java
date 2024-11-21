/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exception.CustomException;
import com.skyeye.pay.config.PayProperties;
import com.skyeye.pay.core.PayClient;
import com.skyeye.pay.core.dto.order.PayOrderRespDTO;
import com.skyeye.pay.core.dto.order.PayOrderUnifiedReqDTO;
import com.skyeye.pay.entity.PayChannel;
import com.skyeye.pay.enums.PayOrderStatusResp;
import com.skyeye.pay.enums.PayType;
import com.skyeye.pay.service.PayChannelService;
import com.skyeye.pay.service.PayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @ClassName: PayServiceImpl
 * @Description: 统一支付接口实现类
 * @author: skyeye云系列--卫志强
 * @date: 2024/11/21 8:46
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "统一支付", groupName = "统一支付")
public class PayServiceImpl implements PayService {

    private static Logger log = LoggerFactory.getLogger(PayServiceImpl.class);

    @Autowired
    private PayChannelService payChannelService;

    @Autowired
    private PayProperties payProperties;

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void payment(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> data = JSONUtil.toBean(params.get("data").toString(), null);
        String channelCode = params.get("channelCode").toString();
        String returnUrl = params.get("returnUrl").toString();
        String channelExtrasStr = params.get("channelExtras").toString();
        PayOrderUnifiedReqDTO reqDTO = new PayOrderUnifiedReqDTO();

        // 1. 钱包支付事，需要额外传 user_id 和 user_type
        if (Objects.equals(channelCode, PayType.WALLET.getKey())) {
            Map<String, String> channelExtras = StrUtil.isBlank(channelExtrasStr) ?
                Maps.newHashMapWithExpectedSize(2) : JSONUtil.toBean(channelExtrasStr, null);
            String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
            channelExtras.put(CommonConstants.USER_ID_KEY, userId);
            reqDTO.setChannelExtras(channelExtras);
        }

        // 支付渠道
        PayChannel payChannel = payChannelService.getPayChannelByCode(channelCode);
        PayClient client = payChannelService.getPayClient(payChannel.getId());
        // 2. 调用支付渠道接口
        PayOrderUnifiedReqDTO unifiedReqDTO = new PayOrderUnifiedReqDTO();
        unifiedReqDTO.setOutTradeNo(data.get("oddNumber").toString());
        unifiedReqDTO.setSubject("购买商品");
        unifiedReqDTO.setBody("购买商品信息");
        unifiedReqDTO.setNotifyUrl(genChannelOrderNotifyUrl(payChannel));
        unifiedReqDTO.setReturnUrl(returnUrl);
        unifiedReqDTO.setPrice(Integer.parseInt(data.get("payPrice").toString()));
        PayOrderRespDTO payOrderRespDTO = client.unifiedOrder(unifiedReqDTO);

        // 3. 如果调用直接支付成功，则直接更新支付单状态为成功。例如说：付款码支付，免密支付时，就直接验证支付成功
        if (payOrderRespDTO != null) {
            notifyOrder(payOrderRespDTO);
            log.info("[submitOrder][order(%s) payChannel(%s) 支付结果(%s)]",
                data, payChannel, payOrderRespDTO);
            // 如有渠道错误码，则抛出业务异常，提示用户
            if (StrUtil.isNotEmpty(payOrderRespDTO.getChannelErrorCode())) {
                throw new CustomException(String.format("发起支付报错，错误码：%s，错误提示：%s", payOrderRespDTO.getChannelErrorCode(), payOrderRespDTO.getChannelErrorMsg()));
            }
            Map<String, Object> result = new HashMap<>();
            result.put("payChannel", JSONUtil.toJsonStr(payChannel));
            result.put("payOrderRespDTO", JSONUtil.toJsonStr(payOrderRespDTO));
            outputObject.setBean(result);
            outputObject.settotal(CommonNumConstants.NUM_ONE);
        } else {
            throw new CustomException("发起支付失败，请稍后重试");
        }
    }

    public void notifyOrder(PayOrderRespDTO notify) {
        // 情况一：支付成功的回调
        if (PayOrderStatusResp.isSuccess(notify.getStatus())) {
            return;
        }
        // 情况二：支付失败的回调
        if (PayOrderStatusResp.isClosed(notify.getStatus())) {
            throw new CustomException("支付失败，请稍后重试");
        }
        // 情况三：WAITING：无需处理
        // 情况四：REFUND：通过退款回调处理
    }

    /**
     * 根据支付渠道的编码，生成支付渠道的回调地址
     *
     * @param channel 支付渠道
     * @return 支付渠道的回调地址  配置地址 + "/" + channel id
     */
    private String genChannelOrderNotifyUrl(PayChannel channel) {
        return payProperties.getOrderNotifyUrl() + "/" + channel.getId();
    }

}
