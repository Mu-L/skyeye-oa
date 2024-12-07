package com.skyeye.school.building.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: LocationRange
 * @Description: 地点范围实体层
 * @author: skyeye云系列--lqy
 * @date: 2024/11/10 15:17
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName("school_location_range")
@ApiModel(description = "地点范围")
public class LocationRange extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id，为空时新增，不为空时编辑")
    private String id;

    @TableField("name")
    @ApiModelProperty(value = "地点范围名称", required = "required")
    private String name;

    @TableField("logo")
    @ApiModelProperty(value = "地点范围logo",required = "required")
    private String logo;

    @TableField("description")
    @ApiModelProperty(value = "地点范围描述")
    private String description;

    @TableField("vertex_num")
    @ApiModelProperty(value = "顶点数量",required = "required")
    private Integer vertexNum;


    @TableField(exist = false)
    @ApiModelProperty(value = "顶点表信息")
    private List<RangeVertex> rangeVertexMation;
}
