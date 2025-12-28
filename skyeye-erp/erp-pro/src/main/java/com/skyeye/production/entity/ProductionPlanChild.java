/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.production.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.bom.entity.Bom;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: ProductionPlanChild
 * @Description: 出货计划单子单据实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/28 21:19
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "erp_production_plan_child", autoResultMap = true)
@ApiModel("出货计划单子单据实体类")
public class ProductionPlanChild extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "parent_id")
    @Property(value = "单据id")
    private String parentId;

    @TableField(value = "material_id")
    @ApiModelProperty(value = "商品id", required = "required")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "商品信息")
    private Material materialMation;

    @TableField(value = "norms_id")
    @ApiModelProperty(value = "规格id", required = "required")
    private String normsId;

    @TableField(exist = false)
    @Property(value = "规格信息")
    private MaterialNorms normsMation;

    @TableField(exist = false)
    @Property(value = "该规格对应的所有bom方案信息列表")
    private List<Bom> bomList;

    @TableField(value = "oper_number")
    @ApiModelProperty(value = "数量", required = "required,num")
    private String operNumber;

    @TableField("delivery_time")
    @ApiModelProperty(value = "交货日期", required = "required")
    private String deliveryTime;

    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

}
