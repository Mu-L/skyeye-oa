/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sms.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.sms.entity.SmsCodeSendReq;
import com.skyeye.sms.entity.SmsCodeValidateReq;
import com.skyeye.sms.service.SmsCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SmsCodeController
 * @Description: 短信验证码控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/30 12:51
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "短信验证码", tags = "短信验证码", modelName = "短信验证码")
public class SmsCodeController {

    @Autowired
    private SmsCodeService smsCodeService;

    @ApiOperation(id = "sendSmsCodeReq", value = "创建短信验证码，并进行发送", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = SmsCodeSendReq.class)
    @RequestMapping("/post/SmsCodeController/sendSmsCodeReq")
    public void sendSmsCodeReq(InputObject inputObject, OutputObject outputObject) {
        smsCodeService.sendSmsCodeReq(inputObject, outputObject);
    }

    @ApiOperation(id = "validateSmsCode", value = "检查验证码是否有效", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = SmsCodeValidateReq.class)
    @RequestMapping("/post/SmsCodeController/validateSmsCode")
    public void validateSmsCode(InputObject inputObject, OutputObject outputObject) {
        smsCodeService.validateSmsCode(inputObject, outputObject);
    }

}
