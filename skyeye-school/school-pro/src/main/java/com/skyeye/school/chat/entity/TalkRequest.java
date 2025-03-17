package com.skyeye.school.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.school.personnel.entity.SysEveUserStaff;
import com.skyeye.school.student.entity.Student;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@TableName(value = "school_talk_request")
@ApiModel(value = "好友申请表实体类")
public class TalkRequest extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("applicant_id")
    @ApiModelProperty(value = "申请人ID")
    private String applicantId;

    @TableField("recipient_id")
    @ApiModelProperty(value = "被申请人ID")
    private String recipientId;

    @TableField("status")
    @ApiModelProperty(value = "申请状态: 0-待处理, 1-已同意, 2-已拒绝, 3-已过期")
    private Integer status;

    @TableField("apply_reason")
    @ApiModelProperty(value = "申请理由")
    private String applyReason;

    @TableField("expire_time")
    @ApiModelProperty(value = "申请过期时间")
    private LocalDateTime expireTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "学生申请人信息")
    private List<Map<String,Object>> studentApplicantMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "学生被申请人信息")
    private List<Map<String,Object>> studentRecipientMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "老师申请人信息")
    private SysEveUserStaff teacherApplicantMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "老师被申请人信息")
    private SysEveUserStaff teacherRecipientMation;
}
