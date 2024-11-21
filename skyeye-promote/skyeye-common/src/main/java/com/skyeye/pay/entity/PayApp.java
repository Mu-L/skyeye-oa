/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

/**
 * @ClassName: PayApp
 * @Description: 支付应用实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = "appKey")
@TableName(value = "skyeye_pay_app")
@ApiModel("支付应用实体类")
public class PayApp extends BaseGeneralInfo {

    @TableField("app_key")
    @ApiModelProperty(value = "应用名", required = "required")
    private String appKey;

    @TableField("enabled")
    @ApiModelProperty(value = "状态，参考#EnableEnum", required = "required")
    private Integer enabled;

    @TableField("order_notify_url")
    @ApiModelProperty(value = "支付结果的回调地址", required = "required")
    private String orderNotifyUrl;

    @TableField("refund_notify_url")
    @ApiModelProperty(value = "退款结果的回调地址", required = "required")
    private String refundNotifyUrl;

    @TableField("transfer_notify_url")
    @ApiModelProperty(value = "转账结果的回调地址")
    private String transferNotifyUrl;
}