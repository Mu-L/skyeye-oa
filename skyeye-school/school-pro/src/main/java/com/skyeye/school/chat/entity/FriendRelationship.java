/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.Map;

@Data
@TableName(value = "school_talk_friend_relationship")
@ApiModel(value = "好友关系表实体类")
public class FriendRelationship extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("user_id")
    @ApiModelProperty(value = "申请人ID")
    private String userId;

    @TableField("friend_id")
    @ApiModelProperty(value = "被申请人ID")
    private String friendId;

    @TableField("talk_request_id")
    @ApiModelProperty(value = "好友申请表的Id")
    private String talkRequestId;

    @TableField("status")
    @ApiModelProperty(value = "关系状态：0-请求中, 1-已接受, 2-已拒绝, 3-已拉黑")
    private Integer status;

    @TableField(exist = false)
    @ApiModelProperty(value = "学生信息")
    private Map<String, Object> studentMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "老师信息")
    private Map<String, Object> teacherMation;

    @TableField(exist = false)
    @Property(value = "是否是好友")
    private Boolean isFriend;

}
