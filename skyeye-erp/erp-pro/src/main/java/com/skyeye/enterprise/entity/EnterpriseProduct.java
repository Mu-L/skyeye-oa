/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.enterprise.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.unit.MaterialUnit;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: EnterpriseProduct
 * @Description: 企业商品信息
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/21
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "skyeye:enterprise:product", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "erp_enterprise_product", autoResultMap = true)
@ApiModel("企业商品信息")
public class EnterpriseProduct extends BaseGeneralInfo {

    @TableField(value = "enterprise_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "企业账户ID", required = "required")
    private String enterpriseId;

    @TableField(exist = false)
    @Property(value = "企业账户信息")
    private Map<String, Object> enterpriseMation;

    @TableField(value = "material_id")
    @ApiModelProperty(value = "ERP商品ID（可选，用于关联ERP商品）")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "ERP商品信息")
    private Material materialMation;

    @TableField(value = "product_code")
    @ApiModelProperty(value = "商品编码", required = "required", fuzzyLike = true)
    private String productCode;

    @TableField(value = "norms_spec", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "商品规格类型，多规格时具备该数据")
    private List<Map<String, Object>> normsSpec;

    @TableField(exist = false)
    @Property(value = "所属分类信息")
    private Map<String, Object> categoryMation;

    @TableField(value = "model")
    @ApiModelProperty(value = "型号", fuzzyLike = true)
    private String model;

    @TableField(value = "unit")
    @ApiModelProperty(value = "单位", enumClass = MaterialUnit.class, required = "required,num")
    private String unit;

    @TableField(value = "unit_name")
    @ApiModelProperty(value = "计量单位  当unit=1时，必有")
    private String unitName;

    @TableField(value = "images")
    @ApiModelProperty(value = "商品图片（多个图片URL，用逗号分隔）")
    private String images;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "启用状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

    @TableField(exist = false)
    @Property(value = "商品规格列表")
    private List<EnterpriseProductNorms> materialNorms;

}