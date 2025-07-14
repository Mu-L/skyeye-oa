/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.material.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.brand.entity.Brand;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.material.classenum.MaterialItemCode;
import com.skyeye.material.entity.unit.MaterialUnit;
import com.skyeye.material.entity.unit.MaterialUnitGroup;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: Material
 * @Description: 商品信息
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/23 15:58
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = CacheConstants.ERP_MATERIAL_CACHE_KEY)
@TableName(value = "erp_material", autoResultMap = true)
@ApiModel("商品信息")
public class Material extends BaseGeneralInfo {

    @TableField(value = "category_id")
    @ApiModelProperty(value = "所属分类id", required = "required")
    private String categoryId;

    @TableField(exist = false)
    @Property(value = "所属分类信息")
    private Map<String, Object> categoryMation;

    @TableField(value = "unit")
    @ApiModelProperty(value = "规格类型，参考#MaterialUnit", required = "required,num")
    private Integer unit;

    @TableField(value = "model")
    @ApiModelProperty(value = "型号", required = "required", fuzzyLike = true)
    private String model;

    @TableField(value = "unit_name")
    @ApiModelProperty(value = "计量单位  当unit=1时，必有")
    private String unitName;

    @TableField(value = "unit_group_id")
    @ApiModelProperty(value = "计量单位组id  当unit=2时，必填")
    private String unitGroupId;

    @TableField(exist = false)
    @Property(value = "计量单位组信息  当unit=2时，必有")
    private MaterialUnitGroup unitGroupMation;

    @TableField(value = "first_in_unit")
    @ApiModelProperty(value = "首选入库单位")
    private String firstInUnit;

    @TableField(exist = false)
    @Property(value = "首选入库单位信息")
    private MaterialUnit firstInUnitMation;

    @TableField(value = "first_out_unit")
    @ApiModelProperty(value = "首选出库单位")
    private String firstOutUnit;

    @TableField(exist = false)
    @Property(value = "首选出库单位信息")
    private MaterialUnit firstOutUnitMation;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "启用，参考#EnableEnum", required = "required,num")
    private Integer enabled;

    @TableField(value = "delete_flag")
    private Integer deleteFlag;

    @TableField(value = "norms_spec", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "商品规格类型，多规格时具备该数据")
    private List<Map<String, Object>> normsSpec;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品规格信息", required = "required,json")
    private List<MaterialNorms> materialNorms;

    @TableField(value = "from_type")
    @ApiModelProperty(value = "商品来源类型，参考#MaterialFromType", required = "required,num")
    private Integer fromType;

    @TableField(value = "`type`")
    @ApiModelProperty(value = "商品类型，参考#MaterialType", required = "required,num")
    private Integer type;

    @TableField(value = "item_code", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "条形码开启类型", enumClass = MaterialItemCode.class)
    private Integer itemCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序的json字符串")
    private List<MaterialProcedure> materialProcedure;

    @TableField("is_used")
    @Property(value = "是否使用中，参考#IsUsedEnum")
    private Integer isUsed;

    @TableField(value = "shelves_state")
    @Property(value = "上下架状态，参考#MaterialShelvesState")
    private Integer shelvesState;

    @TableField(value = "brand_id")
    @ApiModelProperty(value = "品牌id")
    private String brandId;

    @TableField(exist = false)
    @ApiModelProperty(value = "品牌信息")
    private Brand brandMation;

}
