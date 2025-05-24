/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.email.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import lombok.Data;

/**
 * @ClassName: EmailUser
 * @Description: 用户绑定的邮箱实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/8 10:20
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = {"createId", "emailAddress"})
@RedisCacheField(name = "email:user")
@TableName(value = "email_user", autoResultMap = true)
@ApiModel("用户绑定的邮箱实体类")
public class EmailUser extends CommonInfo {

    @TableId("id")
    @Property("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("email_address")
    @ApiModelProperty(value = "邮箱地址", required = "required")
    private String emailAddress;

    @TableField("email_password")
    @ApiModelProperty(value = "登录密码", required = "required")
    private String emailPassword;

    @TableField("email_check")
    @Property(value = "当前用户默认设置的email", enumClass = WhetherEnum.class)
    private Integer emailCheck;

    @TableField("create_id")
    @Property(value = "绑定人")
    private String createId;

    @TableField("create_time")
    @Property(value = "绑定时间")
    private String createTime;
}
