/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.ordertype.entity.SealOrderType;
import com.skyeye.patrol.classenum.PatrolItemSummaryType;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: PatrolItem
 * @Description: 巡检项目实体类
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "seal:patrol:item", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "crm_service_patrol_item")
@ApiModel("巡检项目实体类")
public class PatrolItem extends BaseGeneralInfo {

    @TableField(value = "odd_number")
    @Property(value = "项目编号", fuzzyLike = true)
    private String oddNumber;

    @TableField(value = "order_type_id")
    @ApiModelProperty(value = "巡检服务类型（工单类型ID）", required = "required")
    private String orderTypeId;

    @TableField(exist = false)
    @Property(value = "工单类型信息")
    private SealOrderType orderTypeMation;

    @TableField(value = "store_id")
    @ApiModelProperty(value = "所属网点（门店ID）")
    private String storeId;

    @TableField(exist = false)
    @Property(value = "门店信息")
    private Map<String, Object> storeMation;

    @TableField(value = "is_store_visible")
    @ApiModelProperty(value = "网点是否可见", enumClass = WhetherEnum.class, required = "required,num")
    private Integer isStoreVisible;

    @TableField(value = "requirements")
    @ApiModelProperty(value = "巡检要求")
    private String requirements;

    @TableField(value = "summary_type")
    @ApiModelProperty(value = "运行情况总结", enumClass = PatrolItemSummaryType.class, required = "required,num")
    private Integer summaryType;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

}

