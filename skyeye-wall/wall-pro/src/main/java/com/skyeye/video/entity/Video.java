package com.skyeye.video.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.videocomment.entity.VideoComment;
import com.skyeye.videotag.entity.VideoTag;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: Video
 * @Description: 视频实体类
 * @author: skyeye云系列--lqy
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "wall_video")
@ApiModel(value = "视频实体类")
public class Video extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("topic")
    @ApiModelProperty(value = "标题", required = "required",fuzzyLike = true)
    private String topic;

    @TableField("content")
    @ApiModelProperty(value = "内容", required = "required")
    private String content;

    @TableField("video_src")
    @ApiModelProperty(value = "视频地址", required = "required")
    private String videoSrc;

    @TableField("collection_num")
    @ApiModelProperty(value = "收藏数量，默认0",defaultValue = "0")
    private String collectionNum;

    @TableField("visit_num")
    @ApiModelProperty(value = "浏览数量，默认0",defaultValue = "0")
    private String visitNum;

    @TableField("tasn_num")
    @ApiModelProperty(value = "点赞数量，默认0", defaultValue = "0")
    private String tasnNum;

    @TableField("remark_num")
    @ApiModelProperty(value = "评论数量，默认0", defaultValue = "0")
    private String remarkNum;

    @TableField("video_duration")
    @ApiModelProperty(value = "视频时长，默认0", defaultValue = "0")
    private Integer videoDuration;

    @TableField("tag_id")
    @ApiModelProperty(value = "标签，最多三个标签", required = "required")
    private String tagId;

    @TableField(exist = false)
    @ApiModelProperty(value = "标签名列表")
    private List<VideoTag> tagMation;

    @TableField(exist = false)
    @Property(value = "当前登陆人是否点赞")
    private Boolean checkUpvote;

    @TableField(exist = false)
    @Property(value = "当前登陆人是否收藏")
    private Boolean checkCollection;

    @TableField(exist = false)
    @Property(value = "当前登陆人是否关注")
    private Boolean checkFocus;

    @TableField(exist = false)
    @Property(value = "视频评论")
    private List<VideoComment> videoComment;

}
