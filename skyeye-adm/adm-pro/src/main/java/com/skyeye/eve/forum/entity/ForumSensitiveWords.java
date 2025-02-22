package com.skyeye.eve.forum.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

@Data
@ApiModel(value = "敏感词管理")
@TableName("forum_sensitive_words")
public class ForumSensitiveWords extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "sensitive_words")
    @ApiModelProperty(value = "敏感词", required = "required")
    private String sensitiveWords;

    @TableField(value = "create_id")
    @Property(value = "创建人id")
    private String createId;

    @TableField(value = "create_time")
    @Property(value = "创建时间")
    private String createTime;

    @TableField(value = "tenant_id")
    @ApiModelProperty(value = "租户id")
    private String tenantId;

}
