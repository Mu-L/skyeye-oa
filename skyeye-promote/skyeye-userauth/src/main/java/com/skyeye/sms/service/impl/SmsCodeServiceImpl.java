/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sms.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.enumeration.SmsSceneEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.sms.core.config.SmsCodeProperties;
import com.skyeye.sms.entity.SmsCodeSendReq;
import com.skyeye.sms.entity.SmsCodeUseReq;
import com.skyeye.sms.entity.SmsCodeValidateReq;
import com.skyeye.sms.service.SmsCodeService;
import com.skyeye.sms.service.SmsSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static cn.hutool.core.util.RandomUtil.randomInt;

/**
 * @ClassName: SmsCodeServiceImpl
 * @Description: 短信验证码服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/30 12:52
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "短信验证码", groupName = "短信验证码")
public class SmsCodeServiceImpl implements SmsCodeService {

    @Autowired
    private SmsCodeProperties smsCodeProperties;

    @Autowired
    private SmsSendService smsSendService;

    @Autowired
    public JedisClientService jedisClientService;

    /**
     * 缓存key：手机号-场景
     */
    private static final String MOBILE_SMS_CODE = "sms:mobile:code:%s:%s";

    /**
     * 缓存key：日期-手机号
     * 不区分场景
     */
    private static final String MOBILE_SMS_DAY_SEND_NUM = "sms:mobile:daySendNum:%s:%s";

    @Value("${sms.template.enabled}")
    private Boolean smsTemplateEnabled;

    @Value("${sms.template.code}")
    private String smsTemplateCode;

    @Override
    public void sendSmsCodeReq(InputObject inputObject, OutputObject outputObject) {
        SmsCodeSendReq smsCodeSendReq = inputObject.getParams(SmsCodeSendReq.class);
        sendSmsCodeReq(smsCodeSendReq);
    }

    @Override
    public void sendSmsCodeReq(SmsCodeSendReq smsCodeSendReq) {
        SmsSceneEnum sceneEnum = SmsSceneEnum.getCodeByScene(smsCodeSendReq.getScene());
        Assert.notNull(sceneEnum, "验证码场景({}) 查找不到配置", smsCodeSendReq.getScene());
        if (smsTemplateEnabled) {
            return;
        }
        // 创建验证码
        String code = createSmsCode(smsCodeSendReq.getMobile(), smsCodeSendReq.getScene());
        // 发送验证码
        smsSendService.sendSingleSms(smsCodeSendReq.getMobile(), null,
            sceneEnum.getKey().toString(), MapUtil.of("code", code));
    }

    private String createSmsCode(String mobile, Integer scene) {
        String key = String.format(MOBILE_SMS_CODE, mobile, scene);
        // 校验是否可以发送验证码
        if (jedisClientService.exists(key)) {
            throw new CustomException("验证码发送过于频繁，请稍后再试");
        }
        // 创建验证码记录
        String code = String.format("%0" + smsCodeProperties.getEndCode().toString().length() + "d",
            randomInt(smsCodeProperties.getBeginCode(), smsCodeProperties.getEndCode() + 1));
        jedisClientService.set(key, code, (int) smsCodeProperties.getExpireTimes().getSeconds());

        // 校验次数
        String key2 = String.format(MOBILE_SMS_DAY_SEND_NUM, DateUtil.getYmdTimeAndToString(), mobile);
        String ss = jedisClientService.get(key2);
        int number = StrUtil.isEmpty(ss) ? CommonNumConstants.NUM_ZERO : Integer.parseInt(ss);
        if (number >= smsCodeProperties.getSendMaximumQuantityPerDay()) {
            throw new CustomException("超过当天发送的上限");
        }
        number++;
        // 失效时间为 1 天
        jedisClientService.set(key2, String.valueOf(number), RedisConstants.ONE_DAY_SECONDS);
        return code;
    }

    @Override
    public void useSmsCodeReq(InputObject inputObject, OutputObject outputObject) {
        SmsCodeUseReq smsCodeUseReq = inputObject.getParams(SmsCodeUseReq.class);
        useSmsCodeReq(smsCodeUseReq);
    }

    @Override
    public void useSmsCodeReq(SmsCodeUseReq smsCodeUseReq) {
        // 检测验证码是否有效
        String chcheCode = validateSmsCode0(smsCodeUseReq.getMobile(), smsCodeUseReq.getScene());
        if (StrUtil.equals(chcheCode, smsCodeUseReq.getCode())) {
            // 验证码使用过后，删除缓存
            String key = String.format(MOBILE_SMS_CODE, smsCodeUseReq.getMobile(), smsCodeUseReq.getScene());
            jedisClientService.del(key);
        }
    }

    @Override
    public void validateSmsCode(InputObject inputObject, OutputObject outputObject) {
        SmsCodeValidateReq smsCodeValidateReq = inputObject.getParams(SmsCodeValidateReq.class);
        validateSmsCode(smsCodeValidateReq);
    }

    @Override
    public void validateSmsCode(SmsCodeValidateReq smsCodeValidateReq) {
        if (smsTemplateEnabled) {
            if (!StrUtil.equals(smsTemplateCode, smsCodeValidateReq.getSmsCode())) {
                throw new CustomException("验证码错误");
            }
            return;
        }
        String chcheCode = validateSmsCode0(smsCodeValidateReq.getMobile(), smsCodeValidateReq.getScene());
        if (!StrUtil.equals(chcheCode, smsCodeValidateReq.getSmsCode())) {
            throw new CustomException("验证码错误");
        }
        // 验证码使用过后，删除缓存
        String key = String.format(MOBILE_SMS_CODE, smsCodeValidateReq.getMobile(), smsCodeValidateReq.getScene());
        jedisClientService.del(key);
    }

    private String validateSmsCode0(String mobile, Integer scene) {
        String key = String.format(MOBILE_SMS_CODE, mobile, scene);
        // 校验是否可以发送验证码
        if (!jedisClientService.exists(key)) {
            throw new CustomException("验证码不存在或已过期");
        }
        return jedisClientService.get(key);
    }
}
