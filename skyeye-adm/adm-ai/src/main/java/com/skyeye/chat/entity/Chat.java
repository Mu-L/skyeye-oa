package com.skyeye.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.key.entity.AiApiKey;
import lombok.Data;

/**
 * @ClassName: Chat
 * @Description: 聊天记录实体层
 * @author: skyeye云系列--lqy
 * @date: 2024/10/5 17:24
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "skyeye_ai_chat")
@ApiModel(value = "聊天记录实体类")
public class Chat extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("message")
    @ApiModelProperty(value = "问题", required = "required")
    private String message;

    @TableField("content")
    @ApiModelProperty(value = "回答的问题")
    private String content;

    @TableField("platform")
    @ApiModelProperty(value = "平台",required = "required")
    private String platform;

    @TableField("api_key_id")
    @ApiModelProperty(value = "API配置id",required = "required")
    private String apiKeyId;

    @TableField(exist = false)
    @Property("AI配置")
    private AiApiKey apiKeyMation;
}
