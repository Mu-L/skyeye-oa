/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: ForumReportHi
 * @Description: 论坛帖子举报成功的记录表
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/9 9:22
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Data
@TableName("forum_report_hi")
@RedisCacheField(name = "forum:reporthi", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@ApiModel("论坛帖子举报成功的记录表")
public class ForumReportHi extends OperatorUserInfo {
    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "forum_id")
    @ApiModelProperty(value = "论坛帖id", required = "required")
    private String forumId;

    @TableField(value = "report_id")
    @ApiModelProperty(value = "举报人id", required = "required")
    private String reportId;

    @TableField(value = "report_time")
    @ApiModelProperty(value = "举报时间", required = "required")
    private String reportTime;

    @TableField(value = "examine_id")
    @ApiModelProperty(value = "审核人id", required = "required")
    private String examineId;

    @TableField(value = "examine_time")
    @ApiModelProperty(value = "审核时间", required = "required")
    private String examineTime;

    @TableField(exist = false)
    @Property(value = "forumContent")
    private ForumContent forumContentMation;
}
