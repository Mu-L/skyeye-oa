package com.skyeye.school.lesson.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import lombok.Data;

@Data
@TableName(value = "school_lesson_review_type")
@ApiModel(value = "听评表角色子类管理实体类")
public class LessonReviewTypeChild extends LessonReviewType {
}
