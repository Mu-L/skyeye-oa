/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machin.entity;

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
import com.skyeye.procedure.entity.WayProcedure;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: MachinChild
 * @Description: 加工单子单据
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/29 15:20
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "erp_machin_child", autoResultMap = true)
@ApiModel("加工单子单据实体类")
public class MachinChild extends CommonInfo {

    @TableId("id")
    @Property(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(exist = false)
    @Property(value = "新的id")
    private String newId;

    @TableField(value = "parent_id")
    @Property(value = "加工单id")
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

    @TableField(value = "oper_number")
    @ApiModelProperty(value = "数量", required = "required,num")
    private String operNumber;

    @TableField(value = "plan_start_time")
    @ApiModelProperty(value = "计划开始时间", required = "required")
    private String planStartTime;

    @TableField(value = "plan_end_time")
    @ApiModelProperty(value = "计划结束时间", required = "required")
    private String planEndTime;

    @TableField("delivery_time")
    @ApiModelProperty(value = "交货日期", required = "required")
    private String deliveryTime;

    @TableField(value = "bom_id")
    @ApiModelProperty(value = "bom方案id", required = "required")
    private String bomId;

    @TableField(exist = false)
    @Property(value = "bom方案信息")
    private Bom bomMation;

    @TableField(exist = false)
    @Property(value = "该规格对应的所有bom方案信息列表")
    private List<Bom> bomList;

    @TableField(exist = false)
    @Property(value = "工序是否完成加工的状态，true:完成，false:未完成")
    private Boolean checkComplateFlag;

    @TableField(exist = false)
    @Property(value = "最后加工完成的数量")
    private String lastProcedureNum;

}
