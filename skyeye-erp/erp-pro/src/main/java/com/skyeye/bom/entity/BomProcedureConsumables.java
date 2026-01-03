/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.bom.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.procedure.entity.WorkProcedure;
import lombok.Data;

/**
 * @ClassName: BomProcedureConsumables
 * @Description: BOM工序耗材实体类
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/03
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "erp_bom_procedure_consumables", autoResultMap = true)
@ApiModel("BOM工序耗材实体类")
public class BomProcedureConsumables extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField(value = "bom_id")
    @Property(value = "bom表id")
    private String bomId;

    @TableField(value = "bom_child_id")
    @Property(value = "BOM子件id")
    private String bomChildId;

    @TableField(value = "procedure_id")
    @ApiModelProperty(value = "工序id", required = "required")
    private String procedureId;

    @TableField(exist = false)
    @Property(value = "工序信息")
    private WorkProcedure procedureMation;

    @TableField(value = "material_id")
    @ApiModelProperty(value = "耗材商品id", required = "required")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "耗材商品信息")
    private Material materialMation;

    @TableField(value = "norms_id")
    @ApiModelProperty(value = "耗材规格id", required = "required")
    private String normsId;

    @TableField(exist = false)
    @Property(value = "耗材规格信息")
    private MaterialNorms normsMation;

    @TableField(value = "need_num")
    @ApiModelProperty(value = "需要的数量", required = "required,num")
    private String needNum;

    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

}

