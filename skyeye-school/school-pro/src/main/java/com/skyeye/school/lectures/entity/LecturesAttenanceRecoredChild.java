package com.skyeye.school.lectures.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.List;

@Data
@TableName(value = "school_lectures_attenance_recored_child")
@ApiModel(value = "授课成绩表")
public class LecturesAttenanceRecoredChild  extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键ID。为空时新增，不为空时编辑")
    private String id;

    @TableField("attenance_recored_id")
    @ApiModelProperty("听课记录表ID")
    private String attenanceRecordId;

    @TableField("`order`")
    @ApiModelProperty("排序(0表示总分，1-99表示其他)")
    private Integer order;

    @TableField("title")
    @ApiModelProperty("标题")
    private String title;

    @TableField("context")
    @ApiModelProperty("内容")
    private String context;

    @TableField("full_score")
    @ApiModelProperty("分值")
    private String fullScore;

    @TableField("get_score")
    @ApiModelProperty("得分")
    private String getScore;

    @TableField(exist = false)
    @ApiModelProperty("子节点")
    private List<LecturesAttenanceRecoredChild> children;

}
