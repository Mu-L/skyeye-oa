/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.core.dto.refund;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: PayRefundUnifiedReqDTO
 * @Description: 统一退款
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/10 8:46
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("统一退款")
public class PayRefundUnifiedReqDTO {

    @ApiModelProperty(value = "外部订单编号，对应 PayOrderExtensionDO 的 no 字段", required = "required")
    private String outTradeNo;

    @ApiModelProperty(value = "外部退款号，对应 PayRefundDO 的 no 字段", required = "required")
    private String outRefundNo;

    @ApiModelProperty(value = "退款原因", required = "required")
    private String reason;

    @ApiModelProperty(value = "支付金额，单位：分", required = "required")
    private String payPrice;

    @ApiModelProperty(value = "退款金额，单位：分", required = "required")
    private String refundPrice;

    @ApiModelProperty(value = "支付结果的 notify 回调地址", required = "required")
    private String notifyUrl;

}
