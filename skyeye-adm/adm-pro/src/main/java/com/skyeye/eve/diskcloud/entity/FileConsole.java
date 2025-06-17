/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.diskcloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.eve.diskcloud.classenum.FileType;
import lombok.Data;

/**
 * @ClassName: FileConsole
 * @Description: 文件实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/17 12:32
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@TableName(value = "file_catelog_papers")
@RedisCacheField(name = "file:console", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@ApiModel("文件实体类")
public class FileConsole extends OperatorUserInfo {

    @TableId("id")
    @Property(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "`name`")
    @Property(value = "文件名称")
    private String name;

    @TableField(value = "thumbnail")
    @Property(value = "文件缩略图")
    private String thumbnail;

    @TableField(value = "address")
    @Property(value = "文件地址")
    private String address;

    @TableField(value = "type")
    @Property(value = "文件类型", enumClass = FileType.class)
    private String type;

    @TableField(value = "size")
    @Property(value = "文件大小")
    private Integer size;

    @TableField(value = "size_type")
    @Property(value = "文件大小单位  bytes")
    private String sizeType;

    @TableField(value = "chunk")
    @Property(value = "分块上传，块下标")
    private Integer chunk;

    @TableField(value = "chunk_size")
    @Property(value = "分块上传时，块的大小，用于最后合并")
    private String chunkSize;

    @TableField(value = "file_md5")
    @Property(value = "文件唯一标示")
    private String fileMd5;

    @TableField(value = "parent_id")
    @Property(value = "所属目录")
    private String parentId;

    @TableField(value = "source_id")
    @Property(value = "来源id")
    private String sourceId;

    @TableField(value = "delete_flag")
    private Integer deleteFlag;

    @TableField(exist = false)
    @Property("转换后的文件/文件夹的大小")
    private String turnSize;

}
