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

import java.math.BigInteger;

/**
 * @ClassName: ForumStatisticsDay
 * @Description: 论坛贴子每日的统计表
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/9 9:22
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Data
@ApiModel("论坛贴子每日的统计表")
@RedisCacheField(name = "forum:statisticsday", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName("forum_statistics_day")
public class ForumStatisticsDay extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "forum_id")
    @ApiModelProperty(value = "论坛帖id", required = "required")
    private String forumId;

    @TableField(value = "browse_num")
    @ApiModelProperty(value = "浏览量")
    private BigInteger browseNum;

    @TableField(value = "comment_num")
    @ApiModelProperty(value = "点赞量")
    private BigInteger commentNum;

    @TableField(exist = false)
    @Property(value = "forumContent")
    private ForumContent forumContentMation;
}
