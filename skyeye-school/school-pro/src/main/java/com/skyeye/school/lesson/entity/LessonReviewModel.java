package com.skyeye.school.lesson.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.Version;
import com.skyeye.school.lectures.entity.LecturesAttenanceRecored;
import com.skyeye.school.lectures.entity.LecturesAttenanceRecoredChild;
import lombok.Data;

import java.util.List;

@Data
@TableName(value = "school_lesson_review_model")
@ApiModel(value = "听评表模型实体类")
public class LessonReviewModel extends Version {

    @TableField(exist = false)
    @ApiModelProperty(value = "听评表角色管理列表", required = "json")
    private List<LessonReviewType> LessonReviewTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "质评-听课记录表管理列表", required = "json")
    private List<LecturesAttenanceRecored> LecturesAttenanceRecoredList;

}
