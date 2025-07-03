/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.articles.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ArticlesPurchase
 * @Description: 用品采购申请实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/12/19 18:11
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "assistant:articles:purchase", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "asset_articles_purchase")
@ApiModel("用品采购申请实体类")
public class ArticlesPurchase extends SkyeyeFlowable {

    @TableField("title")
    @ApiModelProperty(value = "标题", required = "required")
    private String title;

    @TableField("remark")
    @ApiModelProperty(value = "描述")
    private String remark;

    @TableField(value = "all_price")
    @ApiModelProperty(value = "总金额")
    private String allPrice;

    @TableField(exist = false)
    @ApiModelProperty(value = "用品信息", required = "required,json")
    private List<ArticlesPurchaseLink> purchaseLink;

    @TableField("project_id")
    @ApiModelProperty(value = "项目id")
    private String projectId;

    @TableField(exist = false)
    @Property("项目信息")
    private Map<String, Object> projectMation;

}
