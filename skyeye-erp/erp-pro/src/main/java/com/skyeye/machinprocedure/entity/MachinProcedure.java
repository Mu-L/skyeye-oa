/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machinprocedure.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.machinprocedure.classenum.MachinProcedureState;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.procedure.entity.WorkProcedure;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: MachinProcedure
 * @Description: 加工单子单据工序信息实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/24 12:36
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "erp_machin_procedure", autoResultMap = true)
@ApiModel("加工单子单据工序信息实体类")
public class MachinProcedure extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "parent_id", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "加工单id")
    private String parentId;

    @TableField(value = "child_id", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "加工单子单据id")
    private String childId;

    @TableField(value = "bom_child_id", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "bom子件清单的id")
    private String bomChildId;

    @TableField(value = "way_procedure_id", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "工艺id")
    private String wayProcedureId;

    @TableField(value = "material_id", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "商品id")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "商品信息")
    private Material materialMation;

    @TableField(value = "norms_id", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "规格id")
    private String normsId;

    @TableField(exist = false)
    @Property(value = "规格信息")
    private MaterialNorms normsMation;

    @TableField(value = "procedure_id", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "工序id")
    private String procedureId;

    @TableField(exist = false)
    @Property(value = "工序")
    private WorkProcedure procedureMation;

    @TableField(value = "order_by", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "排序")
    private Integer orderBy;

    @TableField("state")
    @Property(value = "状态", enumClass = MachinProcedureState.class)
    private Integer state;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(value = "plan_start_time")
    @ApiModelProperty(value = "计划开始时间", required = "required")
    private String planStartTime;

    @TableField(value = "plan_end_time")
    @ApiModelProperty(value = "计划结束时间", required = "required")
    private String planEndTime;

    @TableField(value = "actual_start_time")
    @ApiModelProperty(value = "实际开始时间")
    private String actualStartTime;

    @TableField(value = "actual_end_time")
    @ApiModelProperty(value = "实际结束时间")
    private String actualEndTime;

    @TableField(exist = false)
    @Property(value = "关联的车间任务")
    private List<MachinProcedureFarm> machinProcedureFarmList;

}
