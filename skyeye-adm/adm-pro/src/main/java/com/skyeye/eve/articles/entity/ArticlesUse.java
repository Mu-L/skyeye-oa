/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.articles.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: ArticlesUse
 * @Description: 用品领用申请实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/12/19 18:11
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "assistant:articles:applyUse", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "asset_articles_use")
@ApiModel("用品领用申请实体类")
public class ArticlesUse extends SkyeyeFlowable {

    @TableField("title")
    @ApiModelProperty(value = "标题", required = "required", fuzzyLike = true)
    private String title;

    @TableField("remark")
    @ApiModelProperty(value = "描述")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "用品信息", required = "required,json")
    private List<ArticlesUseLink> applyUseLnk;

}
