/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.diskcloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.eve.diskcloud.classenum.DickCloudType;
import com.skyeye.eve.diskcloud.classenum.ShareState;
import com.skyeye.eve.diskcloud.classenum.ShareType;
import lombok.Data;

/**
 * @ClassName: FileShare
 * @Description: 文件分享实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/18 11:35
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@TableName(value = "file_catalog_share")
@RedisCacheField(name = "file:share", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@ApiModel("文件分享实体类")
public class FileShare extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "file_id")
    @ApiModelProperty(value = "文件或文件夹id", required = "required")
    private String fileId;

    @TableField(value = "file_type")
    @Property(value = "文件类型", enumClass = DickCloudType.class)
    private String fileType;

    @TableField(value = "share_name")
    @Property(value = "分享名称")
    private String shareName;

    @TableField(value = "share_code")
    @Property(value = "分享code  唯一性")
    private String shareCode;

    @TableField(value = "share_url")
    @Property(value = "分享链接")
    private String shareUrl;

    @TableField(value = "share_type")
    @ApiModelProperty(value = "分享类型", enumClass = ShareType.class, required = "required,num")
    private Integer shareType;

    @TableField(value = "share_password")
    @Property(value = "如果是私密分享，则是分享秘钥")
    private String sharePassword;

    @TableField(value = "state")
    @Property(value = "分享状态", enumClass = ShareState.class)
    private Integer state;

}
