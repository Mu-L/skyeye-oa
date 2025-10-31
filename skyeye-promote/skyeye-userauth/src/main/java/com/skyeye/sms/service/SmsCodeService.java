/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sms.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.sms.entity.SmsCodeSendReq;
import com.skyeye.sms.entity.SmsCodeValidateReq;

/**
 * @ClassName: SmsCodeService
 * @Description: 短信验证码服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/30 12:52
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface SmsCodeService {

    void sendSmsCodeReq(InputObject inputObject, OutputObject outputObject);

    void sendSmsCodeReq(SmsCodeSendReq smsCodeSendReq);

    void validateSmsCode(InputObject inputObject, OutputObject outputObject);

    void validateSmsCode(SmsCodeValidateReq smsCodeValidateReq);
}
