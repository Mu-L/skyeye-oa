package com.skyeye.school.building.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;



@Data
@TableName("school_floor_info")
@ApiModel(description = "楼层教室服务管理实体类")
public class FloorInfo extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id，为空时新增，不为空时编辑")
    private String id;

    @TableField("location_id")
    @ApiModelProperty(value = "所属地点id",required = "required")
    private String locationId;

    @TableField("name")
    @ApiModelProperty(value = "名称",required = "required")
    private String name;

    @TableField("node_type")
    @ApiModelProperty(value = "节点，1楼层，2教室、3服务",required = "required")
    private Integer nodeType;

    @TableField("parent_id")
    @ApiModelProperty(value = "父级id",defaultValue = "0",required = "required")
    private String parentId;

    @TableField("level")
    @ApiModelProperty(value = "层级标识")
    private Integer  level;

    @TableField("status")
    @ApiModelProperty(value = "状态，1：正常，2：禁用")
    private Integer status;

    @TableField("sort_order")
    @ApiModelProperty(value = "排序id,默认1")
    private Integer sortOrder;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;
}
