package com.skyeye.school.lesson.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

import java.util.List;

@Data
@TableName(value = "school_lesson review_model")
@ApiModel(value = "听评表模型实体类")
public class LessonReviewModel extends BaseGeneralInfo {

    @TableField(exist = false)
    @ApiModelProperty("听评表角色管理列表")
    private List<LessonReviewType> LessonReviewTypeList;

}
