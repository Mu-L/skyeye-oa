/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.app.enums.AppStoreType;
import com.skyeye.app.enums.Platform;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.EnableEnum;
import lombok.Data;

/**
 * @ClassName: AppStore
 * @Description: 应用商店实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/09/20 13:11
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "skyeye:app:store")
@TableName(value = "skyeye_app_store", autoResultMap = true)
@ApiModel("应用商店实体类")
public class AppStore extends BaseGeneralInfo {

    @TableField("store_key")
    @ApiModelProperty(value = "商店标识", enumClass = AppStoreType.class, required = "required")
    private String storeKey;

    @TableField("platform")
    @ApiModelProperty(value = "平台类型", enumClass = Platform.class, required = "required")
    private String platform;

    @TableField("api_config")
    @ApiModelProperty(value = "API配置信息", exampleDefault = "{\n" +
        "  \"clientId\": \"小米开发者账号ID\",\n" +
        "  \"clientSecret\": \"小米开发者账号密钥\",\n" +
        "  \"appId\": \"应用ID\",\n" +
        "  \"apiUrl\": \"API基础URL（可选，默认：https://api.developer.xiaomi.com）\",\n" +
        "  \"authUrl\": \"认证URL（可选，默认：https://account.xiaomi.com/oauth2/authorize）\"\n" +
        "}", required = "required,json")
    private String apiConfig;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "启用状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

}
