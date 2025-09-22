/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.app.enums.Platform;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: AppVersion
 * @Description: APP版本实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/09/20 13:11
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "skyeye:app:version")
@TableName(value = "skyeye_app_version", autoResultMap = true)
@ApiModel("APP版本实体类")
public class AppVersion extends BaseGeneralInfo {

    @TableField("project_id")
    @ApiModelProperty(value = "项目ID", required = "required")
    private String projectId;

    @TableField("platform")
    @ApiModelProperty(value = "平台类型", enumClass = Platform.class, required = "required")
    private String platform;

    @TableField("version_code")
    @ApiModelProperty(value = "版本代码", required = "required")
    private Integer versionCode;

    @TableField("file_size")
    @ApiModelProperty(value = "文件大小")
    private String fileSize;

    @TableField("file_path")
    @ApiModelProperty(value = "文件路径", required = "required")
    private String filePath;

    @TableField("is_force_update")
    @ApiModelProperty(value = "是否强制更新", enumClass = WhetherEnum.class)
    private Integer isForceUpdate;

    @TableField(exist = false)
    @ApiModelProperty(value = "发布得应用商店id", required = "json")
    private List<String> storeIdList;

}
