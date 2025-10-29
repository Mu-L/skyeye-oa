/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.type.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.EnableEnum;
import lombok.Data;

/**
 * @ClassName: MaterialType
 * @Description: 商城商品分类实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/10/29 9:33
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@UniqueField
@TableName(value = "shop_material_type", autoResultMap = true)
@ApiModel("商城商品分类实体类")
public class MaterialType extends BaseGeneralInfo {

    @TableField(value = "order_by")
    @ApiModelProperty(value = "排序，值越大越往后", required = "required")
    private Integer orderBy;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "启用状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

    @TableField("logo")
    @ApiModelProperty(value = "分类logo")
    private String logo;

}
