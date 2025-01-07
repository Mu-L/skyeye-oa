/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.question.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * @ClassName: DwSurveyMailInvite
 * @Description:问卷选择发送邮件调查时的邮件服务实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:35
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@TableName(value = "dw_survey_mail_invite")
@ApiModel(value = "问卷选择发送邮件调查时的邮件服务实体类")
public class DwSurveyMailInvite extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("dw_send_user_mail")
    @ApiModelProperty(value = "发件人邮箱", required = "required")
    private String dwSendUserMail;

    @TableField("error_msg")
    @ApiModelProperty(value = "错误信息")
    private String error_msg;

    @TableField("audit")
    @ApiModelProperty(value = "审核 0未审核  1审核通过  2审核拒绝  3审核中", required = "required")
    private Integer audit;

    @TableField("sendcloud_msg_id")
    @ApiModelProperty(value = "sendcloudMsgId")
    private String sendcloudMsgId;

    @TableField("status")
    @ApiModelProperty(value = "状态 0未发送 1正在发送 2发送完成 3发送失败  4发送异常", required = "required")
    private Integer status;

    @TableField("subject")
    @ApiModelProperty(value = "标题")
    private String subject;

    @TableField("inbox_num")
    @ApiModelProperty(value = "总收件人数")
    private Integer inbox_num;

    @TableField("send_num")
    @ApiModelProperty(value = "已经发送的数")
    private Integer send_num;

    @TableField("success_num")
    @ApiModelProperty(value = "发送中成功的数")
    private Integer success_num;

    @TableField("fail_num")
    @ApiModelProperty(value = "发送中失败的数")
    private Integer fail_num;

    @TableField("survey_id")
    @ApiModelProperty(value = "问卷ID", required = "required")
    private String survey_id;

    @TableField("create_id")
    @ApiModelProperty(value = "发送人", required = "required")
    private String createId;

    @TableField("create_time")
    @ApiModelProperty(value = "发送时间", required = "required")
    private String createTime;
}


