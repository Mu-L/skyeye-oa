/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.core.factory;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.core.auth.Auth;
import com.skyeye.ai.core.enums.AiPlatformEnum;
import com.skyeye.exception.CustomException;
import com.skyeye.key.entity.AiApiKey;
import io.github.briqt.spark4j.SparkClient;

/**
 * @ClassName: AiFactoryImpl
 * @Description: AI Model 模型工厂的实现类
 * @author: skyeye云系列--卫志强
 * @date: 2024/10/5 14:09
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class AiFactoryImpl implements AiFactory {

    @Override
    public Object getOrCreateChatModel(AiPlatformEnum platform, String appId, String apiKey, String secretKey, String url) {
        String cacheKey = buildClientCacheKey(platform, apiKey, url);
        return Singleton.get(cacheKey, (Func0<Object>) () -> {
            switch (platform) {
                case YI_YAN:
                    return buildYiYanChatModel(apiKey, secretKey);
                case XUN_FEI:
                    return buildXunFeiClient(appId, apiKey, secretKey);
                case TONG_YI:
                    return buildTongYiChatClient();
                default:
                    throw new IllegalArgumentException(StrUtil.format("未知平台({})", platform));
            }
        });
    }

    @Override
    public Object getDefaultChatModel(AiPlatformEnum platform, AiApiKey aiApiKey) {
        return getOrCreateChatModel(platform,
            aiApiKey.getApiAppId(),
            aiApiKey.getApiKey(),
            aiApiKey.getSecretKey(),
            null);
    }

    @Override
    public Object getDefaultImageModel(AiPlatformEnum platform) {
        switch (platform) {
            case YI_YAN:
//               return SpringUtil.getBean(Qianfan.class);
            default:
                throw new CustomException(StrUtil.format("未知平台({})", platform));
        }
    }

    @Override
    public Object getOrCreateImageModel(AiPlatformEnum platform, String apiKey, String url) {
        switch (platform) {
            case YI_YAN:
            default:
                throw new CustomException(StrUtil.format("未知平台({})", platform));
        }
    }

    private static String buildClientCacheKey(Object... params) {
        if (ArrayUtil.isEmpty(params)) {
            throw new CustomException("请指定参数");
        }
        return StrUtil.format("{}", ArrayUtil.join(params, "_"));
    }

    // ========== 各种创建 spring-ai 客户端的方法 ==========

    private static Qianfan buildYiYanChatModel(String apiKey, String secretKey) {
        Qianfan qianFanApi = new Qianfan(Auth.TYPE_OAUTH, apiKey, secretKey);
        return qianFanApi;
    }

    private static SparkClient buildXunFeiClient(String appId, String apiKey, String secretKey) {
        SparkClient xunFeiApi = new SparkClient();
        xunFeiApi.appid = appId;
        xunFeiApi.apiKey = apiKey;
        xunFeiApi.apiSecret = secretKey;
        return xunFeiApi;
    }


    private static Generation buildTongYiChatClient() {
        return new Generation();
    }

}
