package com.skyeye.eve.entity.chat.group;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.eve.classenum.CompanyChatGroupState;
import lombok.Data;

@Data
@TableName(value = "school_talk_group")
@RedisCacheField(name = "School:talk:group")
@ApiModel("群组实体类")
public class CompanyChatGroup extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "group_name")
    @ApiModelProperty(value = "群组名称", required = "required", fuzzyLike = true)
    private String groupName;

    @TableField(value = "group_num")
    @ApiModelProperty(value = "群组号码", fuzzyLike = true)
    private String groupNum;

    @TableField(value = "group_img")
    @ApiModelProperty(value = "群组头像")
    private String groupImg;

    @TableField(value = "group_histroy_img")
    @Property(value = "群历史使用头像，中间逗号隔开")
    private String groupHistroyImg;

    @TableField(value = "group_user_num")
    @Property(value = "群总人数限制")
    private Integer groupUserNum;

    @TableField(value = "group_desc")
    @ApiModelProperty(value = "群简介")
    private String groupDesc;

    @TableField(value = "state")
    @Property(value = "群状态", enumClass = CompanyChatGroupState.class)
    private Integer state;

    @TableField(exist = false)
    @ApiModelProperty(value = "成员id，逗号隔开", required = "required")
    private String userIds;
}
