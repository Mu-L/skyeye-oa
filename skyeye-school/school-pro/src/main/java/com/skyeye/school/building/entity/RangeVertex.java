package com.skyeye.school.building.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: RangeVertex
 * @Description: 范围顶点管理实体类
 * @author: skyeye云系列--lqy
 * @date: 2024/11/6 11:40
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName("school_vertex")
@ApiModel(description = "范围顶点管理实体类")
public class RangeVertex extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id，为空时新增，不为空时编辑")
    private String id;

    @TableField("range_id")
    @ApiModelProperty(value = "范围id")
    private String rangeId;

    @TableField("order_num")
    @ApiModelProperty(value = "顺序")
    private Integer orderNum;

    @TableField("longitude")
    @ApiModelProperty(value = "经度",required = "required")
    private Float longitude;

    @TableField("latitude")
    @ApiModelProperty(value = "纬度",required = "required")
    private Float latitude;

}
