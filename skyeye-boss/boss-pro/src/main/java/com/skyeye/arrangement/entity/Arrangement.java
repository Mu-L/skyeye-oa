/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.arrangement.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.interviewee.entity.Interviewee;
import com.skyeye.personrequire.entity.PersonRequire;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: Arrangement
 * @Description: 面试安排表实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/4/14 16:24
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "boss_interview_arrangement")
@ApiModel("面试安排表实体类")
public class Arrangement extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("odd_number")
    @Property("单据编号")
    private String oddNumber;

    @TableField(value = "interview_id")
    @ApiModelProperty(value = "面试者id", required = "required")
    private String interviewId;

    @TableField(exist = false)
    @Property(value = "面试者信息")
    private Interviewee interviewMation;

    @TableField(value = "interview_time")
    @ApiModelProperty(value = "面试时间", required = "required")
    private String interviewTime;

    @TableField(value = "person_require_id")
    @ApiModelProperty(value = "人员需求申请id", required = "required")
    private String personRequireId;

    @TableField(exist = false)
    @Property(value = "人员需求申请信息")
    private PersonRequire personRequireMation;

    @TableField(value = "interviewer")
    @ApiModelProperty(value = "面试官id，需要由人员需求的申请人进行人员安排")
    private String interviewer;

    @TableField(exist = false)
    @Property(value = "面试官信息")
    private Map<String, Object> interviewerMation;

    @TableField(value = "job_score_id")
    @ApiModelProperty(value = "面试通过后，需要面试官进行定级，如果该岗位没有职级，可以不做定级")
    private String jobScoreId;

    @TableField(exist = false)
    @Property(value = "岗位定级信息")
    private Map<String, Object> jobScoreMation;

    @TableField(value = "state")
    @ApiModelProperty(value = "状态，参考#ArrangementState")
    private Integer state;

    @TableField(value = "evaluation")
    @ApiModelProperty(value = "面试评价")
    private String evaluation;

    @TableField(value = "reason")
    @ApiModelProperty(value = "拒绝入职的原因")
    private String reason;

}
