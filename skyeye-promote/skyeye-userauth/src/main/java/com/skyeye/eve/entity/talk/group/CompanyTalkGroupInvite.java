/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.entity.talk.group;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.eve.enumclass.CompanyTalkGroupInviteInGroupType;
import com.skyeye.eve.enumclass.CompanyTalkGroupInviteState;
import lombok.Data;

/**
 * @ClassName: CompanyTalkGroupInvite
 * @Description: 群组邀请实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/2/28 16:37
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "sys_talk_group_invite_mation")
@ApiModel("群组邀请实体类")
public class CompanyTalkGroupInvite extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "invite_user_id")
    @ApiModelProperty(value = "被邀请人id", required = "required")
    private String inviteUserId;

    @TableField(value = "group_id")
    @ApiModelProperty(value = "分组id", required = "required")
    private String groupId;

    @TableField(exist = false)
    @Property(value = "分组名称")
    private CompanyTalkGroup groupMation;

    @TableField(exist = false)
    @Property(value = "分组名称")
    private String groupName;

    @TableField(value = "state")
    @Property(value = "邀请状态", enumClass = CompanyTalkGroupInviteState.class)
    private Integer state;

    @TableField(value = "in_group_type")
    @Property(value = "进群方式", enumClass = CompanyTalkGroupInviteInGroupType.class)
    private Integer inGroupType;

    @TableField(value = "operator")
    @ApiModelProperty(value = "操作人")
    private String operator;

    @TableField(value = "whether_read")
    @Property(value = "是否已读", enumClass = WhetherEnum.class)
    private Integer whetherRead;

    @TableField(exist = false)
    @Property(value = "创建人名称")
    private String userName;

    @TableField(exist = false)
    @Property(value = "创建人头像")
    private String userPhoto;

}
