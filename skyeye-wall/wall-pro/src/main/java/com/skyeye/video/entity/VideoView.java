package com.skyeye.video.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;


/**
 * @ClassName: VideoView
 * @Description: 视频观看记录实体类
 * @author: skyeye云系列--lqy
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "wall_video_view")
public class VideoView extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("video_id")
    @ApiModelProperty(value = "视频id", required = "required")
    private String videoId;

    @TableField("user_id")
    @ApiModelProperty(value = "用户id", required = "required")
    private String userId;

    @TableField("view_count")
    @ApiModelProperty(value = "浏览次数")
    private Integer viewCount;

    @TableField("view_duration")
    @ApiModelProperty(value = "观看时间")
    private Integer viewDuration;
}
