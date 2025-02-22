package com.skyeye.eve.forum.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.eve.forum.classenum.ExamineStateEnum;
import lombok.Data;

@Data
@TableName("forum_report")
@ApiModel(value = "论坛举报实体类")
public class ForumReport extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "forum_id")
    @ApiModelProperty(value = "所属话题id", required = "required")
    private String forumId;

    @TableField(value = "report_type_id")
    @ApiModelProperty(value = "举报类型id", required = "required")
    private String reportTypeId;

    @TableField(value = "report_other_content")
    @ApiModelProperty(value = "举报类型为'其他'时，需要填写内容")
    private String reportOtherContent;

    @TableField(value = "report_desc")
    @ApiModelProperty(value = "备注")
    private String reportDesc;

    @TableField(value = "examine_state")
    @ApiModelProperty(value = "审核状态  1.未审核  2.审核通过  3.审核不通过", required = "required", enumClass = ExamineStateEnum.class)
    private Integer examineState;

    @TableField(value = "examine_nopass_reason")
    @ApiModelProperty(value = "审核不通过原因")
    private String examineNopassReason;

    @TableField(value = "examine_id")
    @Property(value = "审核人id")
    private String examineId;

    @TableField(value = "examine_time")
    @Property(value = "审核时间")
    private String examineTime;

    @TableField(value = "report_id")
    @Property(value = "举报人id")
    private String reportId;

    @TableField(value = "report_time")
    @Property(value = "举报时间")
    private String reportTime;

    @TableField(value = "tenant_id")
    @ApiModelProperty(value = "租户id")
    private String tenantId;
}
