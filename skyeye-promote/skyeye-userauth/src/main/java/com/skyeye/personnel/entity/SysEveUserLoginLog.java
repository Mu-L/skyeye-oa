/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.personnel.classenum.UserLoginLogDeviceType;
import com.skyeye.personnel.classenum.UserLoginLogStatus;
import lombok.Data;

/**
 * @ClassName: SysEveUserLoginLog
 * @Description: 用户登录日志实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/18 20:30
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "sys_eve_user_login_log", autoResultMap = true)
@ApiModel("用户登录日志实体类")
public class SysEveUserLoginLog extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "user_id")
    @ApiModelProperty(value = "用户ID", required = "required")
    private String userId;

    @TableField(value = "user_code")
    @ApiModelProperty(value = "用户账号", required = "required")
    private String userCode;

    @TableField(value = "login_ip")
    @ApiModelProperty(value = "登录IP地址")
    private String loginIp;

    @TableField(value = "login_city")
    @ApiModelProperty(value = "登录城市")
    private String loginCity;

    @TableField(value = "login_time")
    @ApiModelProperty(value = "登录时间", required = "required")
    private String loginTime;

    @TableField(value = "login_status")
    @ApiModelProperty(value = "登录状态", enumClass = UserLoginLogStatus.class, required = "required,num")
    private Integer loginStatus;

    @TableField(value = "login_message")
    @ApiModelProperty(value = "登录结果消息")
    private String loginMessage;

    @TableField(value = "device_type")
    @ApiModelProperty(value = "设备类型", enumClass = UserLoginLogDeviceType.class)
    private Integer deviceType;
} 