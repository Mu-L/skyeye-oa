/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.courseware.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.school.courseware.classenum.CoursewareStudyState;
import lombok.Data;

/**
 * @ClassName: CoursewareStudy
 * @Description: 互动课件学习信息实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/2 9:49
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "school:coursewareStudy", cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "school_courseware_study")
@ApiModel(value = "互动课件学习信息实体类")
public class CoursewareStudy extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "courseware_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "互动课件id", required = "required")
    private String coursewareId;

    @TableField(value = "state")
    @Property(value = "状态", enumClass = CoursewareStudyState.class)
    private String state;

}
