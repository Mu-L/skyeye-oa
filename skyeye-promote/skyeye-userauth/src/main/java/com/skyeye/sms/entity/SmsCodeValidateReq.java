/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sms.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.enumeration.SmsSceneEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: SmsCodeSendReq
 * @Description: 短信验证码的校验 Request
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/28 22:15
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("短信验证码的校验 Request")
public class SmsCodeValidateReq implements Serializable {

    @ApiModelProperty(value = "手机号", required = "required,phone")
    private String mobile;

    @ApiModelProperty(value = "发送场景", enumClass = SmsSceneEnum.class, required = "required")
    private Integer scene;

    @ApiModelProperty(value = "验证码", required = "required")
    private String smsCode;

}
