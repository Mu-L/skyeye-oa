/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * @Description: 保养计划明细
 */
@Data
@TableName("erp_equipment_maintenance_plan_item")
@ApiModel("保养计划明细")
public class MaintenancePlanItem extends CommonInfo {

    @TableId("id")
    @Property(value = "主键id")
    private String id;

    @TableField("parent_id")
    @Property(value = "父节点id")
    private String parentId;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(value = "maintain_item")
    @ApiModelProperty(value = "保养项", required = "required")
    private String maintainItem;

    @TableField(value = "maintain_content")
    @ApiModelProperty(value = "保养内容及要求", required = "required")
    private String maintainContent;

}
