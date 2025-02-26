package com.skyeye.eve.forum.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;
/**
 * @ClassName: ForumHot
 * @Description: 热门帖子实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Data
@TableName("forum_hot")
@ApiModel("热门帖子和标签实体类")
public class ForumHot extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("forum_id")
    @ApiModelProperty(value = "帖子id")
    private String forumId;

    @TableField("tag_id")
    @ApiModelProperty(value = "标签id")
    private String tagId;

    @TableField("update_time")
    @ApiModelProperty(value = "更新时间")
    private String updateTime;

}
