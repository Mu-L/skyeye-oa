package com.skyeye.notice.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.picture.entity.Picture;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: Notice
 * @Description: 通知实体层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "wall_notice")
@ApiModel(value = "通知实体层")
public class Notice extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id")
    private String id;

    @TableField("content")
    @ApiModelProperty(value = "通知内容")
    private String content;

    @TableField("state")
    @ApiModelProperty(value = "状态 0未读 1已读")
    private Integer state;

    @TableField("send_id")
    @ApiModelProperty(value = "发送人id",required = "required")
    private String sendId;

    @TableField(exist = false)
    @Property(value = "发送人信息")
    private Map<String,Object> sendMation;

    @TableField("receive_id")
    @ApiModelProperty(value = "接收人id",required = "required")
    private String receiveId;

    @TableField(exist = false)
    @Property(value = "接收人信息")
    private Map<String ,Object> receiveMation;

    @TableField("object_id")
    @ApiModelProperty(value = "业务员对象id(视频id等）")
    private String objectId;

    @TableField(exist = false)
    @Property(value = "业务对象信息")
    private Map<String,Object> objectMation;

    @TableField("object_key")
    @ApiModelProperty(value = "业务对象key")
    private String objectKey;

    @TableField("notice_type")
    @ApiModelProperty(value = "通知分类，0:圈子，1，视频，2表白墙",required = "required")
    private Integer noticeType;

    @TableField("type")
    @ApiModelProperty(value = "通知类型，0:评论，1，点赞,2,分享",required = "required")
    private Integer type;

    @TableField("comment_id")
    @ApiModelProperty(value = "评论id")
    private String commentId;

    @TableField("comment_key")
    @ApiModelProperty(value = "评论key")
    private String commentKey;

    @TableField(exist = false)
    @Property(value = "评论内容")
    Map<String,Object> commentMation;

    @TableField("circle_id")
    @ApiModelProperty(value = "圈子id")
    private String circleId;

    @TableField(exist = false)
    @ApiModelProperty(value = "评论图片")
    private Picture picture;

    @TableField("description")
    @ApiModelProperty(value = "描述")
    private String description;
}
