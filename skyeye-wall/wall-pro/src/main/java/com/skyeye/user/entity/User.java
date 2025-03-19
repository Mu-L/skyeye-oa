/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.user.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: User
 * @Description: 用户实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField("accountNumber")
@RedisCacheField(name = CacheConstants.WALL_USER_CACHE_KEY)
@TableName(value = "wall_user")
@ApiModel(value = "表白墙实体类")
public class User extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("`name`")
    @ApiModelProperty(value = "名字", required = "required")
    private String name;

    @TableField("real_name")
    @ApiModelProperty(value = "真实名字")
    private String realName;

    @TableField("img")
    @ApiModelProperty(value = "头像")
    private String img;

    @TableField("sex")
    @ApiModelProperty(value = "性别,參考#SexEnum")
    private Integer sex;

    @TableField("student_number")
    @ApiModelProperty(value = "学号")
    private String studentNumber;

    @TableField("signature")
    @ApiModelProperty(value = "签名")
    private String signature;

    @TableField("background_image")
    @ApiModelProperty(value = "背景图片")
    private String backgroundImage;

    @TableField(value = "account_number", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "账号")
    private String accountNumber;

    @TableField("password")
    @ApiModelProperty(value = "密码")
    private String password;

    @TableField("create_time")
    @Property(value = "创建时间")
    private String createTime;

    @TableField(exist = false)
    @Property(value = "认证状态")
    private Integer state;

    @TableField(exist = false)
    @Property(value = "学校学生的基本信息")
    private List<Map<String,Object>> schoolStudentMation;
}