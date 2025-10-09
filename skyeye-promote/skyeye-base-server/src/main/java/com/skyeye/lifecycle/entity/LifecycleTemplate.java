/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.lifecycle.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.Version;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: LifecycleTemplate
 * @Description: 生命周期模板实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/3 20:45
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = CacheConstants.LIFECYCLE_TEMPLATE_CACHE_KEY, cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "lifecycle_template")
@ApiModel("生命周期模板实体类")
public class LifecycleTemplate extends Version {

    @TableField(value = "master_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "生命周期模板主表id", required = "required")
    private String masterId;

    @TableField(exist = false)
    @Property("生命周期模板主表需不需")
    private LifecycleTemplateMaster masterMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "节点数据", required = "json")
    private List<LifecycleTemplateNode> nodes;

    @TableField(exist = false)
    @ApiModelProperty(value = "连线数据", required = "json")
    private List<LifecycleTemplateEdges> edges;

}
