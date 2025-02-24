/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.mailInviteInbox.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;


/**
 * @ClassName: DwMailInviteInbox
 * @Description: 答案是非题结果保存实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:35
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@TableName(value = "dw_mail_invite_inbox")
@ApiModel(value = "答案是非题结果保存实体类")
public class DwMailInviteInbox extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("email")
    @ApiModelProperty(value = "邮箱")
    private String email;

    @TableField("name")
    @ApiModelProperty(value = "姓名")
    private String name;

    @TableField("sendcloud_id")
    @ApiModelProperty(value = "返回的任务id")
    private String sendcloudId;

    @TableField("status")
    @ApiModelProperty(value = "0未发送 1已提交 2请求＝投递 3发送 4打开 5点击 100发送失败201取消订阅 202软退信 203垃圾举报 204无效邮件", required = "required")
    private Integer status;

    @TableField("survey_mail_invite_id")
    @ApiModelProperty(value = "调查邀请邮件id")
    private String surveyMailInviteId;

    @TableField("us_contacts_id")
    @ApiModelProperty(value = "联系人id")
    private String usContactsId;

}


