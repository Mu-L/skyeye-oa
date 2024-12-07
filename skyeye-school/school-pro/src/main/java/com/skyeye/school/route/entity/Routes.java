package com.skyeye.school.route.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.eve.entity.School;
import com.skyeye.school.building.entity.TeachBuilding;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: Routes
 * @Description: 路线实体类
 * @author: skyeye云系列--lqy
 * @date: 2024/12/1 14:35
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName("school_routes")
@ApiModel(value = "路线实体类")
public class Routes extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("school_id")
    @ApiModelProperty(value = "学校id",required = "required")
    private String schoolId;

    @TableField("start_id")
    @ApiModelProperty(value = "起始地点id",required = "required")
    private String startId;

    @TableField("end_id")
    @ApiModelProperty(value = "终点地点id",required = "required")
    private String endId;

    @TableField("route_length")
    @ApiModelProperty(value = "路线长度",required = "required")
    private Float routeLength;

    @TableField("route_type")
    @ApiModelProperty(value = "路线类型")
    private Integer routeType;

    @TableField("enabled")
    @ApiModelProperty(value = "是否启用,默认启用，1启用，2禁用")
    private Integer enabled;

    @TableField("description")
    @ApiModelProperty(value = "描述")
    private String description;

    @TableField(exist = false)
    @ApiModelProperty(value = "停靠点列表")
    private List<RouteStop> routeStopList;

    @TableField(exist = false)
    @Property(value = "学校信息")
    private School schoolMation;

    @TableField(exist = false)
    @Property(value = "起始地点信息")
    private TeachBuilding startMation;

    @TableField(exist = false)
    @Property(value = "终点地点信息")
    private TeachBuilding endMation;
}
