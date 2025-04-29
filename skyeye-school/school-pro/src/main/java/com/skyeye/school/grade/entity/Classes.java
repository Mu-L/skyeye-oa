/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.grade.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.eve.entity.School;
import com.skyeye.school.faculty.entity.Faculty;
import com.skyeye.school.major.entity.Major;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: Classes
 * @Description: 班级实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/9 15:51
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = {"name","schoolId"})
@RedisCacheField(name = CacheConstants.SC_CLASS_CACHE_KEY, cacheTime = RedisConstants.A_YEAR_SECONDS)
@TableName(value = "school_classes")
@ApiModel(value = "班级实体类")
public class Classes extends BaseGeneralInfo {

    @TableField("school_id")
    @ApiModelProperty(value = "所属学校id", required = "required")
    private String schoolId;

    @TableField(exist = false)
    @Property(value = "所属学校信息")
    private School schoolMation;

    @TableField("faculty_id")
    @ApiModelProperty(value = "所属院系id", required = "required")
    private String facultyId;

    @TableField(exist = false)
    @Property(value = "所属院系信息")
    private Faculty facultyMation;

    @TableField("major_id")
    @ApiModelProperty(value = "所属专业", required = "required")
    private String majorId;

    @TableField(exist = false)
    @Property(value = "所属专业信息")
    private Major majorMation;

    @TableField("year")
    @ApiModelProperty(value = "入学年份", required = "required,num")
    private Integer year;

    @TableField("master_user_id")
    @ApiModelProperty(value = "班主任（用户id）", required = "required")
    private String masterUserId;

    @TableField(exist = false)
    @Property(value = "班主任信息")
    private Map<String, Object> masterUserMation;

    @TableField(exist = false)
    @Property(value = "年制")
    private List<YearSystem> yearSystem;

}
