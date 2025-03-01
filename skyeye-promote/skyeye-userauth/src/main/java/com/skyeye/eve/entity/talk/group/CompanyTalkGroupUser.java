/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.entity.talk.group;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * @ClassName: CompanyTalkGroupUser
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2025/2/28 17:12
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "sys_talk_group_user")
@ApiModel("群组成员实体类")
public class CompanyTalkGroupUser extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "user_id")
    @ApiModelProperty(value = "用户id", required = "required")
    private String userId;

    @TableField(value = "group_id")
    @ApiModelProperty(value = "分组id", required = "required")
    private String groupId;

    @Property("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private String createTime;
}
