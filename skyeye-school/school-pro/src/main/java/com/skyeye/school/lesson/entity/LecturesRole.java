package com.skyeye.school.lesson.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

@Data
@TableName(value = "school_lectures_role")
@ApiModel(value = "质评角色实体类")
public class LecturesRole extends BaseGeneralInfo {
}
