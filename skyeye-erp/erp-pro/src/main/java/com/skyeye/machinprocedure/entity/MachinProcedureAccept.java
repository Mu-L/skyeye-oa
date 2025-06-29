/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machinprocedure.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import com.skyeye.equipment.entity.Equipment;
import com.skyeye.farm.entity.Farm;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: MachinProcedureAccept
 * @Description: 工序验收实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/24 20:02
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "erp_machin_procedure_accept", autoResultMap = true)
@ApiModel("工序验收实体类")
public class MachinProcedureAccept extends SkyeyeFlowable {

    @TableField(value = "machin_id", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "加工单id")
    private String machinId;

    @TableField(value = "machin_procedure_id", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "加工单子单据工序id")
    private String machinProcedureId;

    @TableField(value = "machin_procedure_farm_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "车间任务id")
    private String machinProcedureFarmId;

    @TableField("accept_num")
    @ApiModelProperty(value = "验收数量", required = "required,num")
    private Integer acceptNum;

    @TableField("qualified_num")
    @ApiModelProperty(value = "合格数量", required = "required,num")
    private Integer qualifiedNum;

    @TableField("rework_num")
    @ApiModelProperty(value = "返工数量", required = "required,num")
    private Integer reworkNum;

    @TableField(value = "rework_reason")
    @ApiModelProperty(value = "返工原因")
    private String reworkReason;

    @TableField("scrap_num")
    @ApiModelProperty(value = "报废数量", required = "required,num")
    private Integer scrapNum;

    @TableField(value = "scrap_reason")
    @ApiModelProperty(value = "报废原因")
    private String scrapReason;

    @TableField(value = "department_id", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "部门id")
    private String departmentId;

    @TableField(exist = false)
    @Property(value = "部门信息")
    private Map<String, Object> departmentMation;

    @TableField(value = "farm_id", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "车间id")
    private String farmId;

    @TableField(exist = false)
    @Property(value = "车间信息")
    private Farm farmMation;

    @TableField(value = "equipment_id")
    @ApiModelProperty(value = "设备id")
    private String equipmentId;

    @TableField(exist = false)
    @Property(value = "设备信息")
    private Equipment equipmentMation;

    @TableField(value = "next_farm_id")
    @ApiModelProperty(value = "流转车间id")
    private String nextFarmId;

    @TableField(exist = false)
    @Property(value = "流转车间信息")
    private Farm nextFarmMation;

    @TableField(value = "accept_user_id")
    @ApiModelProperty(value = "验收人id")
    private String acceptUserId;

    @TableField(exist = false)
    @Property(value = "验收人信息")
    private Map<String, Object> acceptUserMation;

    @TableField("remark")
    @ApiModelProperty(value = "描述")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "正常耗材", required = "json")
    private List<MachinProcedureAcceptChild> machinProcedureAcceptChildList;

    @TableField(exist = false)
    @ApiModelProperty(value = "报废耗材", required = "json")
    private List<MachinProcedureAcceptChild> machinScrapProcedureAcceptChildList;

    @TableField(exist = false)
    @ApiModelProperty(value = "验收人生产数量列表", required = "json")
    private List<MachinProcedureAcceptProductNum> machinProcedureAcceptProductNumList;

}
