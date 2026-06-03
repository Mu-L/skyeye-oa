package com.skyeye.equipmentcheckstandard.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * @ClassName: EquipmentCheckStandardItem
 * @Description: 设备点检标准明细实体类
 */
@Data
@TableName(value = "erp_equipment_check_standard_item", autoResultMap = true)
@ApiModel("设备点检标准明细实体类")
public class EquipmentCheckStandardItem extends CommonInfo {

    @TableId("id")
    @Property("主键id")
    private String id;

    @TableField("parent_id")
    @Property("点检标准id")
    private String parentId;

    @TableField("check_item")
    @ApiModelProperty(value = "检查项", required = "required")
    private String checkItem;

    @TableField("check_method")
    @ApiModelProperty(value = "检查方法", required = "required")
    private String checkMethod;

    @TableField("min_value")
    @ApiModelProperty(value = "最小值")
    private String minValue;

    @TableField("max_value")
    @ApiModelProperty(value = "最大值")
    private String maxValue;

    @TableField("sort_no")
    @ApiModelProperty(value = "排序号", defaultValue = "1")
    private Integer sortNo;
}

