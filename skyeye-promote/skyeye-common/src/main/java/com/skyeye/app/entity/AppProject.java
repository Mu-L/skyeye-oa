/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.enumeration.EnableEnum;
import lombok.Data;

/**
 * @ClassName: AppProject
 * @Description: APP项目实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/09/20 13:11
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = "skyeye:app:project")
@TableName(value = "skyeye_app_project", autoResultMap = true)
@ApiModel("APP项目实体类")
public class AppProject extends BaseGeneralInfo {

    @TableField("project_key")
    @ApiModelProperty(value = "项目唯一标识", required = "required")
    private String projectKey;

    @TableField("icon_url")
    @ApiModelProperty(value = "项目图标")
    private String iconUrl;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "启用状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

    @TableField("android_package_name")
    @ApiModelProperty(value = "Android包名")
    private String androidPackageName;

    @TableField("android_app_name")
    @ApiModelProperty(value = "Android应用名称")
    private String androidAppName;

    @TableField("android_icon_url")
    @ApiModelProperty(value = "Android应用图标")
    private String androidIconUrl;

    @TableField("ios_bundle_id")
    @ApiModelProperty(value = "iOS Bundle ID")
    private String iosBundleId;

    @TableField("ios_app_name")
    @ApiModelProperty(value = "iOS应用名称")
    private String iosAppName;

    @TableField("ios_icon_url")
    @ApiModelProperty(value = "iOS应用图标")
    private String iosIconUrl;

    @TableField(value = "delete_flag")
    @Property(value = "删除标记", enumClass = DeleteFlagEnum.class)
    private Integer deleteFlag;
}
