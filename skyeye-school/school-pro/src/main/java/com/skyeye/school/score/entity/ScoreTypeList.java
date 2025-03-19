package com.skyeye.school.score.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "成绩类型实体类列表")
public class ScoreTypeList extends CommonInfo {

    @TableField(exist = false)
    @ApiModelProperty(value = "成绩类型列表", required = "required,json")
    private List<ScoreType> scoreTypeList;
}
