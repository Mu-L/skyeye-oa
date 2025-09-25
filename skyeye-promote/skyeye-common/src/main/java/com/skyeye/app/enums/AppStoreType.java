/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.enums;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: AppStoreType
 * @Description: 应用商店类型枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2025/9/20 12:36
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AppStoreType implements SkyeyeEnumClass {

    TEST_APP_STORE("test", "测试", true, false,
        "{}"),

    XIAOMI_APP_STORE("xiaomi", "小米", true, false,
        "{\n" +
            "  \"clientId\": \"小米开发者账号ID\",\n" +
            "  \"clientSecret\": \"小米开发者账号密钥\",\n" +
            "  \"appId\": \"应用ID\",\n" +
            "  \"apiUrl\": \"API基础URL（可选，默认：https://api.developer.xiaomi.com）\",\n" +
            "  \"authUrl\": \"认证URL（可选，默认：https://account.xiaomi.com/oauth2/authorize）\"\n" +
            "}"),

    HUAWEI_APP_STORE("huawei", "华为", true, false,
        "{\n" +
            "  \"clientId\": \"华为开发者账号ID\",\n" +
            "  \"clientSecret\": \"华为开发者账号密钥\",\n" +
            "  \"appId\": \"应用ID\",\n" +
            "  \"apiUrl\": \"API基础URL（可选，默认：https://connect-api.cloud.huawei.com/api）\",\n" +
            "  \"authUrl\": \"认证URL（可选，默认：https://oauth-login.cloud.huawei.com/oauth2/v3/token）\"\n" +
            "}"),

    OPPO_APP_STORE("oppo", "OPPO", true, false,
        "{\n" +
            "  \"clientId\": \"OPPO开发者账号ID\",\n" +
            "  \"clientSecret\": \"OPPO开发者账号密钥\",\n" +
            "  \"appId\": \"应用ID\",\n" +
            "  \"apiUrl\": \"API基础URL（可选，默认：https://api.oppo.com）\",\n" +
            "  \"authUrl\": \"认证URL（可选，默认：https://api.oppo.com/oauth2/token）\"\n" +
            "}"),

    VIVO_APP_STORE("vivo", "VIVO", true, false,
        "{\n" +
            "  \"clientId\": \"VIVO开发者账号ID\",\n" +
            "  \"clientSecret\": \"VIVO开发者账号密钥\",\n" +
            "  \"appId\": \"应用ID\",\n" +
            "  \"apiUrl\": \"API基础URL（可选，默认：https://developer.vivo.com.cn/api）\",\n" +
            "  \"authUrl\": \"认证URL（可选，默认：https://developer.vivo.com.cn/oauth2/token）\"\n" +
            "}");

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    private String requiredFields;

    public static String getName(String key) {
        for (AppStoreType bean : AppStoreType.values()) {
            if (StrUtil.equals(key, bean.getKey())) {
                return bean.getValue();
            }
        }
        return StrUtil.EMPTY;
    }

    /**
     * 根据应用商店标识获取必须字段的JSON字符串
     *
     * @param key 应用商店标识
     * @return 必须字段的JSON字符串
     */
    public static String getRequiredFields(String key) {
        for (AppStoreType bean : AppStoreType.values()) {
            if (StrUtil.equals(key, bean.getKey())) {
                return bean.getRequiredFields();
            }
        }
        return StrUtil.EMPTY;
    }

    /**
     * 根据应用商店标识获取应用商店类型枚举
     *
     * @param key 应用商店标识
     * @return 应用商店类型枚举
     */
    public static AppStoreType getByKey(String key) {
        for (AppStoreType bean : AppStoreType.values()) {
            if (StrUtil.equals(key, bean.getKey())) {
                return bean;
            }
        }
        return null;
    }

}
