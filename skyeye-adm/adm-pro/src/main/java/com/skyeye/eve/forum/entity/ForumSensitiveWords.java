package com.skyeye.eve.forum.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

@Data
@RedisCacheField(name = "forum:sensitiveWords")
@ApiModel(value = "敏感词管理")
@TableName("forum_sensitive_words")
public class ForumSensitiveWords extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "sensitive_words")
    @ApiModelProperty(value = "敏感词", required = "required")
    private String sensitiveWords;
}
