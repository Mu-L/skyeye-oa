package com.skyeye.videocomment.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.picture.entity.Picture;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: VideoComment
 * @Description: 视频评论实体类
 * @author: skyeye云系列--lqy
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Data
@TableName(value = "wall_video_comment")
@ApiModel(value = "视频评论实体类")
public class VideoComment extends OperatorUserInfo {
    @TableId("id")
    @ApiModelProperty("视频id。为空时新增，不为空时编辑")
    private String id;

    @TableField("content")
    @ApiModelProperty(value = "评论内容",required = "required")
    private String content;

    @TableField("ip")
    @Property(value = "IP属地")
    private String ip;

    @TableField("upvote_num")
    @ApiModelProperty(value = "点赞数量",defaultValue = "0")
    private String upvoteNum;

    @TableField("parent_id")
    @ApiModelProperty(value = "父节点id")
    private String parentId;

    @TableField("user_id")
    @ApiModelProperty(value = "回复人id")
    private String userId;

    @TableField("video_id")
    @ApiModelProperty(value = "视频id", required = "required")
    private String videoId;

    @TableField(exist = false)
    @ApiModelProperty(value = "评论图片")
    private Picture picture;

    @TableField(exist = false)
    @Property(value = "当前登陆人是否点赞")
    private Boolean checkUpvote;
}
