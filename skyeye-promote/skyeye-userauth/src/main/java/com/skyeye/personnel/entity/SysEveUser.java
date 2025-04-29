/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.personnel.classenum.UserIsTermOfValidity;
import com.skyeye.personnel.classenum.UserLockState;
import lombok.Data;

/**
 * @ClassName: SysEveUser
 * @Description: 用户管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/16 15:02
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "sys_eve_user")
@ApiModel("用户管理实体类")
public class SysEveUser extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(exist = false)
    @ApiModelProperty(value = "员工id", required = "required")
    private String staffId;

    @TableField("user_code")
    @ApiModelProperty(value = "用户名账号", required = "required")
    private String userCode;

    @TableField("pwd_num_enc")
    @Property(value = "用户密码加密次数")
    private Integer pwdNumEnc;

    @TableField("password")
    @ApiModelProperty(value = "密码", required = "required")
    private String password;

    @TableField("role_id")
    @ApiModelProperty(value = "角色ID，多个逗号隔开")
    private String roleId;

    @TableField("user_lock")
    @Property(value = "用户账号是否锁定", enumClass = UserLockState.class)
    private Integer userLock;

    @TableField("is_term_of_validity")
    @ApiModelProperty(value = "是否长期有效", enumClass = UserIsTermOfValidity.class, required = "required,num")
    private Integer isTermOfValidity;

    @TableField("start_time")
    @ApiModelProperty(value = "有效期开始时间")
    private String startTime;

    @TableField("end_time")
    @ApiModelProperty(value = "有效期结束时间")
    private String endTime;

}
