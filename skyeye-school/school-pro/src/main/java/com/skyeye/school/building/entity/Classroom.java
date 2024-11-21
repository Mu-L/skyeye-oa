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
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: Classroom
 * @Description: 教室管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/9/5 16:51
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "school_classroom")
@ApiModel(value = "教室管理实体类")
public class Classroom extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id，为空时新增，不为空时编辑")
    private String id;

    @TableField("name")
    @ApiModelProperty(value = "教室名称", required = "required")
    private String name;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private Integer remark;
}
