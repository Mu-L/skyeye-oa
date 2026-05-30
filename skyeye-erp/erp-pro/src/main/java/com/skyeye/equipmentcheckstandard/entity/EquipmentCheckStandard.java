package com.skyeye.equipmentcheckstandard.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

import java.util.List;

/**
 * 设备点检标准实体类
 */
@Data
@RedisCacheField(name = "erp:equipment:check:standard")
@TableName(value = "erp_equipment_check_standard", autoResultMap = true)
@ApiModel("设备点检标准实体类")
public class EquipmentCheckStandard extends SkyeyeFlowable {

    @TableField(value = "odd_number", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "点检标准编码")
    private String oddNumber;

    @TableField(value = "name")
    @ApiModelProperty(value = "点检标准名称", required = "required")
    private String name;

    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "点检标准项目明细", required = "required,json")
    private List<EquipmentCheckStandardItem> itemList;
}

