package com.skyeye.eve.forum.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.EnableEnum;
import lombok.Data;

/**
 * @ClassName: ForumTag
 * @Description: 论坛标签实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/9 9:22
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Data
@ApiModel(value = "论坛标签实体类")
@RedisCacheField(name = "forum:tag", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName("forum_tag")
public class ForumTag extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "tag_name")
    @ApiModelProperty(value = "标签名称", required = "required", fuzzyLike = true)
    private String tagName;

    @TableField(value = "order_by")
    @ApiModelProperty(value = "排序，值越大越往后", required = "required")
    private Integer orderBy;

    @TableField(value = "state")
    @ApiModelProperty(value = "状态  1.启用 2.禁用",required = "required", enumClass = EnableEnum.class)
    private Integer state;
}
