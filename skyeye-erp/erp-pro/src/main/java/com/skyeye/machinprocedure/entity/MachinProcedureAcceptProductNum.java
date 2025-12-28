package com.skyeye.machinprocedure.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.machinprocedure.classenum.MachinProcedureAcceptChildType;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@TableName(value = "erp_machin_procedure_accept_product_num", autoResultMap = true)
@ApiModel("工序验收员工生产数量实体类")
public class MachinProcedureAcceptProductNum extends CommonInfo {

    @TableId("id")
    @Property(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "parent_id")
    @Property(value = "加工单工序验收id")
    private String parentId;

    @TableField(value = "material_id")
    @ApiModelProperty(value = "商品id")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "商品信息")
    private Material materialMation;

    @TableField(value = "norms_id")
    @ApiModelProperty(value = "规格id")
    private String normsId;

    @TableField(exist = false)
    @Property(value = "规格信息")
    private MaterialNorms normsMation;

    @TableField(value = "all_number")
    @ApiModelProperty(value = "总数量", required = "required,num", defaultValue = "0")
    private String allNumber;

    @TableField("qualified_num")
    @ApiModelProperty(value = "合格数量", required = "required,num", defaultValue = "0")
    private String qualifiedNum;

    @TableField("rework_num")
    @ApiModelProperty(value = "返工数量", required = "required,num", defaultValue = "0")
    private String reworkNum;

    @TableField("scrap_num")
    @ApiModelProperty(value = "报废数量", required = "required,num", defaultValue = "0")
    private String scrapNum;

    @TableField(value = "staff_id")
    @ApiModelProperty(value = "员工id")
    private String staffId;

    @TableField(exist = false)
    @Property(value = "员工信息")
    private Map<String, Object> staffMation;
}
