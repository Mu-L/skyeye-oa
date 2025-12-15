/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.enterprise.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.enterprise.enums.UserEnterpriseState;
import com.skyeye.enterprise.enums.UserEnterpriseType;
import lombok.Data;

/**
 * @ClassName: UserEnterprise
 * @Description: 企业账号实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/15 14:14
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@RedisCacheField(name = "skyeye:enterprise:user")
@TableName(value = "sys_eve_user_enterprise")
@ApiModel(value = "企业账号实体类")
public class UserEnterprise extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "user_code", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "用户名账号", required = "required", fuzzyLike = true)
    private String userCode;

    @TableField("password")
    @ApiModelProperty(value = "密码", required = "required")
    private String password;

    @TableField(value = "company_name")
    @ApiModelProperty(value = "企业名称", required = "required", fuzzyLike = true)
    private String companyName;

    @TableField(value = "social_credit_code")
    @ApiModelProperty(value = "营业执照注册号", required = "required", fuzzyLike = true)
    private String socialCreditCode;

    @TableField(value = "business_license_logo")
    @ApiModelProperty(value = "营业执照图片路径", required = "required")
    private String businessLicenseLogo;

    @TableField(value = "`type`")
    @ApiModelProperty(value = "类型", enumClass = UserEnterpriseType.class, required = "required,num")
    private Integer type;

    @TableField(value = "`name`")
    @ApiModelProperty(value = "管理员姓名", required = "required", fuzzyLike = true)
    private String name;

    @TableField("id_card")
    @ApiModelProperty(value = "管理员身份证", required = "required,idcard", fuzzyLike = true)
    private String idCard;

    @TableField("id_card_front_logo")
    @ApiModelProperty(value = "身份证正面照", required = "required")
    private String idCardFrontLogo;

    @TableField("id_card_back_logo")
    @ApiModelProperty(value = "身份证反面照", required = "required")
    private String idCardBackLogo;

    @TableField("phone")
    @ApiModelProperty(value = "管理员手机号", required = "required,phone", fuzzyLike = true)
    private String phone;

    @TableField("state")
    @Property(value = "认证状态", enumClass = UserEnterpriseState.class)
    private Integer state;

    @TableField(value = "create_time", updateStrategy = FieldStrategy.NEVER)
    @Property("注册时间")
    private String createTime;

    @TableField(exist = false)
    @Property(value = "用户token")
    private String userToken;

}
