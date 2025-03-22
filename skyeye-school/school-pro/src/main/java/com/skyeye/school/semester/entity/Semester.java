/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.semester.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.school.subject.entity.Subject;
import com.skyeye.school.subject.entity.SubjectClasses;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: Semester
 * @Description: 学期实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/29 10:50
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = "school:semester", cacheTime = RedisConstants.A_YEAR_SECONDS)
@TableName(value = "school_semester")
@ApiModel(value = "学期实体类")
public class Semester extends BaseGeneralInfo {

    @TableField("start_time")
    @ApiModelProperty(value = "开始时间", required = "required")
    private String startTime;

    @TableField("end_time")
    @ApiModelProperty(value = "结束时间", required = "required")
    private String endTime;

    @TableField(exist = false)
    @Property("学期对应的课程班级信息")
    private List<SubjectClasses> subjectClassesList;

    @TableField(exist = false)
    @Property("学期对应的科目信息")
    private List<Subject> subjectList;

}
