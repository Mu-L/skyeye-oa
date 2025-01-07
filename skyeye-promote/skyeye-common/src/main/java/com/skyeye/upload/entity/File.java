/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.upload.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

/**
 * @ClassName: File
 * @Description: 文件实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/18 19:51
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "skyeye_file", autoResultMap = true)
@ApiModel("文件实体类")
public class File extends BaseGeneralInfo {

    @TableField(value = "config_id")
    @ApiModelProperty(value = "文件配置id")
    private String configId;

    @TableField(value = "path")
    @ApiModelProperty(value = "文件路径", required = "required")
    private String path;

    @TableField(value = "url")
    @ApiModelProperty(value = "文件 URL", required = "required")
    private String url;

    @TableField(value = "type")
    @ApiModelProperty(value = "文件类型")
    private String type;

    @TableField(value = "size")
    @ApiModelProperty(value = "文件大小")
    private long size;

}
