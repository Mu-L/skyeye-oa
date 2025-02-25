/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * @ClassName: HistoryPost
 * @Description: 历史帖子实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "wall_history_view")
@RedisCacheField(name = "forum:historyview", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@ApiModel(value = "历史帖子实体类")
public class ForumHistoryView extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("create_id")
    @ApiModelProperty(value = "创建人id")
    private String createId;

    @TableField("forum_id")
    @ApiModelProperty(value = "帖子id", required = "required")
    private String forumId;

    @TableField("create_time")
    @ApiModelProperty("创建人时间")
    private String createTime;
}