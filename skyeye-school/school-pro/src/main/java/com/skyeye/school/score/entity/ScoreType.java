/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.score.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.IsDefaultEnum;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: ScoreType
 * @Description: 成绩类型实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/29 10:53
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName("school_score_type")
@ApiModel(value = "成绩类型实体类")
public class ScoreType extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("is_default")
    @Property(value = "类型(是否默认，默认不可删除)", enumClass = IsDefaultEnum.class)
    private Integer isDefault;

    @TableField("name")
    @ApiModelProperty(value = "名称", required = "required")
    private String name;

    @TableField("subject_id")
    @ApiModelProperty(value = "科目id", required = "required")
    private String subjectId;

    @TableField("class_id")
    @ApiModelProperty(value = "班级id", required = "required")
    private String classId;

    @TableField("max_min_id")
    @Property(value = "最高分最低分信息id")
    private String maxMinId;

    @TableField(exist = false)
    @Property(value = "最高分最低分信息")
    private ScoreMaxMin scoreMaxMin;

    @TableField("proportion")
    @ApiModelProperty(value = "占比(80即占比80%)", required = "required")
    private String proportion;

    @TableField(exist = false)
    @Property(value = "同表子成绩类型信息")
    private List<ScoreType> sameTableChildDateList;

    @TableField(exist = false)
    @Property(value = "成绩列表")
    private List<ScoreSum> scoreSumAndChildList;

    @TableField(exist = false)
    @Property(value = "不同表成绩类型数据")
    private List<ScoreTypeChild> differentTableChildDateList;
}
