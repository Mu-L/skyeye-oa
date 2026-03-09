/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.pay.core.PayClientConfig;
import com.skyeye.pay.enums.PayType;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: PayChannel
 * @Description: 支付渠道实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField({"codeNum", "appId"})
@RedisCacheField(name = "skyeye:payChannel", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "skyeye_pay_channel", autoResultMap = true)
@ApiModel("支付渠道实体类")
public class PayChannel extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("code_num")
    @ApiModelProperty(value = "渠道编码", enumClass = PayType.class, required = "required")
    private String codeNum;

    @TableField(exist = false)
    @Property("渠道编码对应的信息")
    private Map<String, Object> codeNumMation;

    @TableField("enabled")
    @ApiModelProperty(value = "启用状态", enumClass = EnableEnum.class, required = "required")
    private Integer enabled;

    @TableField("feeRate")
    @ApiModelProperty(value = "渠道费率，比如：0.001", required = "required")
    private String feeRate;

    @TableField("app_id")
    @ApiModelProperty(value = "应用id", required = "required")
    private String appId;

    @TableField(exist = false)
    @Property("应用信息")
    private PayApp appMation;

    @TableField(value = "config")
    @ApiModelProperty(value = "支付渠道配置", required = "required")
    private String config;

    @TableField(exist = false)
    @Property(value = "支付渠道配置信息")
    private PayClientConfig configMation;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;
}
