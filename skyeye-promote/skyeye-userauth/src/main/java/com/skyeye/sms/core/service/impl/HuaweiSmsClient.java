/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sms.core.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skyeye.common.entity.KeyValue;
import com.skyeye.common.util.DateUtil;
import com.skyeye.sms.classenum.SmsTemplateAuditStatusEnum;
import com.skyeye.sms.core.entity.SmsReceiveResp;
import com.skyeye.sms.core.entity.SmsSendResp;
import com.skyeye.sms.core.entity.SmsTemplateResp;
import com.skyeye.sms.entity.SmsChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.crypto.digest.DigestUtil.sha256Hex;

/**
 * @ClassName: HuaweiSmsClient
 * @Description: 华为短信客户端的实现类
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/28 23:26
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
public class HuaweiSmsClient extends AbstractSmsClient {

    /**
     * 调用成功 code
     */
    public static final String URL = "https://smsapi.cn-north-4.myhuaweicloud.com:443/sms/batchSendSms/v1";//APP接入地址+接口访问URI
    public static final String HOST = "smsapi.cn-north-4.myhuaweicloud.com:443";
    public static final String SIGNEDHEADERS = "content-type;host;x-sdk-date";

    @Override
    protected void doInit() {
    }

    public HuaweiSmsClient(SmsChannel properties) {
        super(properties);
        Assert.notEmpty(properties.getApiKey(), "apiKey 不能为空");
        Assert.notEmpty(properties.getApiSecret(), "apiSecret 不能为空");
    }

    @Override
    public SmsSendResp sendSms(String mobile, String apiTemplateId,
                               List<KeyValue<String, Object>> templateParams) throws Throwable {
        // 参考链接 https://support.huaweicloud.com/api-msgsms/sms_05_0001.html
        // 相比较阿里短信，华为短信发送的时候需要额外的参数“通道号”，考虑到不破坏原有的的结构
        // 所以将 通道号 拼接到 apiTemplateId 字段中，格式为 "apiTemplateId 通道号"。空格为分隔符。
        String sender = StrUtil.subAfter(apiTemplateId, " ", true); //中国大陆短信签名通道号或全球短信通道号
        String templateId = StrUtil.subBefore(apiTemplateId, " ", true); //模板ID

        //选填,短信状态报告接收地址,推荐使用域名,为空或者不填表示不接收状态报告
        String statusCallBack = properties.getCallbackUrl();

        List<String> templateParas = templateParams.stream().map(bean -> String.valueOf(bean.getValue())).collect(Collectors.toList());

        JSONObject JsonResponse = sendSmsRequest(sender, mobile, templateId, templateParas, statusCallBack);
        SmsResponse smsResponse = getSmsSendResponse(JsonResponse);

        return new SmsSendResp().setSuccess(smsResponse.success).setApiMsg(smsResponse.data.toString());
    }

    JSONObject sendSmsRequest(String sender, String mobile, String templateId, List<String> templateParas, String statusCallBack) throws UnsupportedEncodingException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String sdkDate = sdf.format(new Date());

        // ************* 步骤 1：拼接规范请求串 *************
        String httpRequestMethod = "POST";
        String canonicalUri = "/sms/batchSendSms/v1/";
        String canonicalQueryString = "";//查询参数为空
        String canonicalHeaders = "content-type:application/x-www-form-urlencoded\n"
            + "host:" + HOST + "\n"
            + "x-sdk-date:" + sdkDate + "\n";
        //请求Body,不携带签名名称时,signature请填null
        String body = buildRequestBody(sender, mobile, templateId, templateParas, statusCallBack, null);
        if (null == body || body.isEmpty()) {
            return null;
        }
        String hashedRequestBody = sha256Hex(body);
        String canonicalRequest = httpRequestMethod + "\n" + canonicalUri + "\n" + canonicalQueryString + "\n"
            + canonicalHeaders + "\n" + SIGNEDHEADERS + "\n" + hashedRequestBody;

        // ************* 步骤 2：拼接待签名字符串 *************
        String hashedCanonicalRequest = sha256Hex(canonicalRequest);
        String stringToSign = "SDK-HMAC-SHA256" + "\n" + sdkDate + "\n" + hashedCanonicalRequest;

        // ************* 步骤 3：计算签名 *************
        String signature = SecureUtil.hmacSha256(properties.getApiSecret()).digestHex(stringToSign);

        // ************* 步骤 4：拼接 Authorization *************
        String authorization = "SDK-HMAC-SHA256" + " " + "Access=" + properties.getApiKey() + ", "
            + "SignedHeaders=" + SIGNEDHEADERS + ", " + "Signature=" + signature;

        // ************* 步骤 5：构造HttpRequest 并执行request请求，获得response *************
        HttpResponse response = HttpRequest.post(URL)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("X-Sdk-Date", sdkDate)
            .header("host", HOST)
            .header("Authorization", authorization)
            .body(body)
            .execute();

        return JSONUtil.parseObj(response.body());
    }

    private SmsResponse getSmsSendResponse(JSONObject resJson) {
        SmsResponse smsResponse = new SmsResponse();
        smsResponse.setSuccess("000000".equals(resJson.getStr("code")));
        smsResponse.setData(resJson);
        return smsResponse;
    }

    static String buildRequestBody(String sender, String receiver, String templateId, List<String> templateParas,
                                   String statusCallBack, String signature) throws UnsupportedEncodingException {
        if (null == sender || null == receiver || null == templateId || sender.isEmpty() || receiver.isEmpty()
            || templateId.isEmpty()) {
            System.out.println("buildRequestBody(): sender, receiver or templateId is null.");
            return null;
        }

        StringBuilder body = new StringBuilder();
        appendToBody(body, "from=", sender);
        appendToBody(body, "&to=", receiver);
        appendToBody(body, "&templateId=", templateId);
        appendToBody(body, "&templateParas=", JSONUtil.toJsonStr(templateParas));
        appendToBody(body, "&statusCallback=", statusCallBack);
        appendToBody(body, "&signature=", signature);
        return body.toString();
    }

    private static void appendToBody(StringBuilder body, String key, String val) throws UnsupportedEncodingException {
        if (null != val && !val.isEmpty()) {
            body.append(key).append(URLEncoder.encode(val, "UTF-8"));
        }
    }

    @Override
    public List<SmsReceiveResp> parseSmsReceiveStatus(String text) {
        List<SmsReceiveStatus> statuses = JSONUtil.toList(text, SmsReceiveStatus.class);
        return statuses.stream().map(status -> {
            return new SmsReceiveResp().setSuccess(Objects.equals(status.getStatus(), "DELIVRD"))
                .setErrorCode(status.getStatus()).setErrorMsg(status.getStatus())
                .setMobile(status.getPhoneNumber()).setReceiveTime(status.getUpdateTime())
                .setSerialNo(status.getSmsMsgId());
        }).collect(Collectors.toList());
    }

    @Override
    public SmsTemplateResp getSmsTemplate(String apiTemplateId) throws Throwable {
        //华为短信模板查询和发送短信，是不同的两套key和secret，与阿里、腾讯的区别较大，这里模板查询校验暂不实现。
        return new SmsTemplateResp().setId(null).setContent(null)
            .setAuditStatus(SmsTemplateAuditStatusEnum.SUCCESS.getKey()).setAuditReason(null);

    }

    @Data
    public static class SmsResponse {

        /**
         * 是否成功
         */
        private boolean success;

        /**
         * 厂商原返回体
         */
        private Object data;

    }


    /**
     * 短信接收状态
     * <p>
     * 参见 <a href="https://support.huaweicloud.com/api-msgsms/sms_05_0003.html">文档</a>
     *
     * @author scholar
     */
    @Data
    public static class SmsReceiveStatus {

        /**
         * 本条状态报告对应的短信的接收方号码，仅当状态报告中携带了extend参数时才会同时携带该参数
         */
        @JsonProperty("to")
        private String phoneNumber;

        /**
         * 短信资源的更新时间，通常为短信平台接收短信状态报告的时间
         */
        @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS, timezone = DateUtil.TIME_ZONE_DEFAULT)
        private LocalDateTime updateTime;

        /**
         * 短信状态报告枚举值
         */
        private String status;

        /**
         * 发送短信成功时返回的短信唯一标识。
         */
        private String smsMsgId;
    }

}
