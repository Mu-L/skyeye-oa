package com.skyeye.school.route.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.school.route.routeenum.RouteTypeEnum;
import lombok.Data;

import java.util.List;
import java.util.Map;

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
public class Routes extends BaseGeneralInfo {

    @TableField("school_id")
    @ApiModelProperty(value = "学校id", required = "required")
    private String schoolId;

    @TableField("route_length")
    @Property(value = "路线长度")
    private Double routeLength;

    @TableField("route_type")
    @ApiModelProperty(value = "路线类型", enumClass = RouteTypeEnum.class)
    private Integer routeType;

    @TableField("enabled")
    @ApiModelProperty(value = "是否启用", enumClass = EnableEnum.class)
    private Integer enabled;

    @TableField(exist = false)
    @ApiModelProperty(value = "停靠点列表")
    private List<RouteStop> routeStopList;

    @TableField(exist = false)
    @Property(value = "学校信息")
    private Map<String, Object> schoolMation;

}
