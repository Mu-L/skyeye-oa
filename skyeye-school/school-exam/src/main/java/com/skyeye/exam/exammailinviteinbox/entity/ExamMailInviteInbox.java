package com.skyeye.exam.exammailinviteinbox.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

@Data
//@RedisCacheField(name = "Exam:mailinviteinbox")
@TableName(value = "exam_mail_invite_inbox")
@ApiModel("是非题结果保存表实体类")
public class ExamMailInviteInbox extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("email")
    @ApiModelProperty(value = "邮箱")
    private String email;

    @TableField("name")
    @ApiModelProperty(value = "是非题结果名")
    private String name;

    @TableField("sendcloud_id")
    @ApiModelProperty(value = "sendclound返回的任务id", required = "required")
    private String sendcloudId;

    @TableField("status")
    @ApiModelProperty(value = "0未发送 1已提交 2请求＝投递 3发送 4打开 5点击 100发送失败201取消订阅 202软退信 203垃圾举报 204无效邮件", required = "required")
    private Integer status;

    @TableField("survey_mail_invite_id")
    @ApiModelProperty(value = "调查邀请邮件id", required = "required")
    private String surveyMailInviteId;

    @TableField("us_contacts_id")
    @ApiModelProperty(value = "联系人id", required = "required")
    private String usContactsId;

}