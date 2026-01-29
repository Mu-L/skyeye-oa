package com.skyeye.construction.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: ProConstructionMaterial
 * @Description: 施工材料清单实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "pro_construction_material")
@ApiModel("施工材料清单实体类")
public class ProConstructionMaterial extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "construction_id")
    @ApiModelProperty(value = "施工方案id", required = "required")
    private String constructionId;

    @TableField(exist = false)
    @Property(value = "施工方案信息")
    private ProConstruction constructionMation;

    @TableField(value = "material_id")
    @ApiModelProperty(value = "ERP商品ID", required = "required")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "ERP商品信息")
    private Map<String, Object> materialMation;

    @TableField(value = "material_norms_id")
    @ApiModelProperty(value = "ERP商品规格ID", required = "required")
    private String materialNormsId;

    @TableField(exist = false)
    @Property(value = "ERP商品规格信息")
    private Map<String, Object> materialNormsMation;

    @TableField(value = "estimated_quantity")
    @ApiModelProperty(value = "预估数量", required = "required,num")
    private String estimatedQuantity;

    @TableField(value = "unit_price")
    @ApiModelProperty(value = "参考单价", required = "required,num")
    private String unitPrice;

    @TableField(value = "total_price")
    @Property(value = "预估总价")
    private String totalPrice;

    @TableField(value = "delivery_time")
    @ApiModelProperty(value = "到货时间")
    private String deliveryTime;

    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

}