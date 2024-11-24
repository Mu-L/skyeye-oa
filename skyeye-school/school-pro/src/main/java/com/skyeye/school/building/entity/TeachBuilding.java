/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.building.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.AreaGeneralInfo;
import com.skyeye.eve.entity.School;
import lombok.Data;

/**
 * @ClassName: TeachBuilding
 * @Description: 教学楼信息实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/27 15:17
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = "school:teachBuilding")
@TableName(value = "school_teach_building")
@ApiModel(value = "教学楼信息实体类")
public class TeachBuilding extends AreaGeneralInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("type_id")
    @ApiModelProperty(value = "地点类型id",required = "required")
    private String typeId;

    @TableField("school_id")
    @ApiModelProperty(value = "所属学校id", required = "required")
    private String schoolId;

    @TableField("name")
    @ApiModelProperty(value = "地点名称",required = "required")
    private String name;

    @TableField("longitude")
    @ApiModelProperty(value = "经度",required = "required")
    private Float longitude;

    @TableField("latitude")
    @ApiModelProperty(value = "纬度",required = "required")
    private Float latitude;

    @TableField("logo")
    @ApiModelProperty(value = "地点logo" ,required = "required")
    private String logo;

    @TableField("p_id")
    @ApiModelProperty(value = "父级id")
    private Integer pId;

    @TableField("remark")
    @ApiModelProperty(value = "简介")
    private String remark;

    @TableField(exist = false)
    @Property(value = "所属学校信息")
    private School schoolMation;
}
