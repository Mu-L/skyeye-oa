/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.mail.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.eve.mail.classenum.MailCategory;
import lombok.Data;

/**
 * @ClassName: Mail
 * @Description: 通讯录实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/23 13:23
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@UniqueField
@RedisCacheField(name = "mail:list")
@TableName(value = "sys_mail_list", autoResultMap = true)
@ApiModel("通讯录实体类")
public class Mail extends BaseGeneralInfo {

    @TableField(value = "category")
    @ApiModelProperty(value = "通讯录类型", enumClass = MailCategory.class, required = "required,num")
    private Integer category;

    @TableField(value = "type_id")
    @ApiModelProperty(value = "所属分组")
    private String typeId;

    @TableField(exist = false)
    @Property(value = "所属分组信息")
    private MailType typeMation;

    @TableField(value = "company")
    @ApiModelProperty(value = "公司")
    private String company;

    @TableField(value = "department")
    @ApiModelProperty(value = "部门")
    private String department;

    @TableField(value = "personal_phone")
    @ApiModelProperty(value = "个人电话")
    private String personalPhone;

    @TableField(value = "work_phone")
    @ApiModelProperty(value = "办公电话")
    private String workPhone;

    @TableField(value = "fax")
    @ApiModelProperty(value = "传真")
    private String fax;

    @TableField(value = "email")
    @ApiModelProperty(value = "邮箱")
    private String email;

    @TableField(value = "other_phone")
    @ApiModelProperty(value = "其他电话")
    private String otherPhone;

    @TableField(value = "other_email")
    @ApiModelProperty(value = "其他邮箱")
    private String otherEmail;

    @TableField(value = "work_address")
    @ApiModelProperty(value = "商务地址")
    private String workAddress;

    @TableField(value = "work_code")
    @ApiModelProperty(value = "商务邮编")
    private String workCode;

    @TableField(value = "person_address")
    @ApiModelProperty(value = "个人地址")
    private String personAddress;

    @TableField(value = "person_code")
    @ApiModelProperty(value = "个人邮编")
    private String personCode;

    @TableField(value = "company_url")
    @ApiModelProperty(value = "公司网址")
    private String companyUrl;

    @TableField(value = "person_url")
    @ApiModelProperty(value = "个人网址")
    private String personUrl;

    @TableField(value = "birthday")
    @ApiModelProperty(value = "生日   年月日")
    private String birthday;

    @TableField(value = "wechat_num")
    @ApiModelProperty(value = "微信号")
    private String wechat_num;

}
