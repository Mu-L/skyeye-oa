/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.type.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: StoreType
 * @Description: 门店商品分类实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 10:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "shop_store_type")
@ApiModel(value = "门店商品分类管理实体类")
public class StoreType extends BaseGeneralInfo {

    @TableField(value = "logo")
    @ApiModelProperty(value = "logo", required = "required")
    private String logo;

    @TableField(value = "order_by")
    @ApiModelProperty(value = "排序", required = "required")
    private Integer orderBy;

    @TableField("enabled")
    @ApiModelProperty(value = "启用状态1是0否", required = "required", enumClass = WhetherEnum.class)
    private Integer enabled;

    @TableField(value = "store_id")
    @ApiModelProperty(value = "门店id")
    private String storeId;

    @TableField(exist = false)
    @Property(value = "门店信息")
    private Map<String, Object> storeMation;
}
