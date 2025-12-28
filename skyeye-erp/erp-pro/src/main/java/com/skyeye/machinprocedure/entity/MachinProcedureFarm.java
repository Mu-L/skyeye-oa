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
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.farm.entity.Farm;
import com.skyeye.machin.entity.Machin;
import com.skyeye.machin.entity.MachinPut;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: MachinProcedureFarm
 * @Description: 车间任务实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/24 18:40
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "erp_machin_procedure_farm", autoResultMap = true)
@ApiModel("车间任务实体类")
public class MachinProcedureFarm extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "odd_number", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "单据编号", fuzzyLike = true)
    private String oddNumber;

    @TableField(value = "machin_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "加工单id")
    private String machinId;

    @TableField(exist = false)
    @Property(value = "加工单信息")
    private Machin machinMation;

    @TableField(value = "machin_procedure_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "加工单子单据工序id")
    private String machinProcedureId;

    @TableField(exist = false)
    @Property(value = "加工单子单据工序信息")
    private MachinProcedure machinProcedureMation;

    @TableField(value = "farm_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "车间id", required = "required")
    private String farmId;

    @TableField(exist = false)
    @Property(value = "车间信息")
    private Farm farmMation;

    @TableField("target_num")
    @ApiModelProperty(value = "目标数量", required = "required,num")
    private String targetNum;

    @TableField(value = "state")
    @Property(value = "状态，参考#MachinProcedureFarmState")
    private String state;

    @TableField(exist = false)
    @Property(value = "状态信息")
    private Map<String, Object> stateMation;

    @TableField(exist = false)
    @Property(value = "车间工序验收单列表")
    private List<MachinProcedureAccept> machinProcedureAcceptList;

    @TableField(exist = false)
    @Property(value = "车间加工入库单列表")
    private List<MachinPut> machinPutList;

}
