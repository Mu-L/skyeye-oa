/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.customer.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.features.AreaGeneralInfo;
import lombok.Data;

/**
 * @ClassName: CustomerMation
 * @Description: 客户信息实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/10/24 15:58
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = CacheConstants.CRM_CUSTOMER_CACHE_KEY)
@TableName(value = "crm_customer", autoResultMap = true)
@ApiModel("客户信息实体类")
public class CustomerMation extends AreaGeneralInfo {

    @TableField(value = "combine")
    @ApiModelProperty(value = "拼音")
    private String combine;

    @TableField(value = "type_id")
    @ApiModelProperty(value = "客户所属分类ID", required = "required")
    private String typeId;

    @TableField(value = "from_id")
    @ApiModelProperty(value = "客户来源ID", required = "required")
    private String fromId;

    @TableField(value = "group_id")
    @ApiModelProperty(value = "客户所属分组ID", required = "required")
    private String groupId;

    @TableField(value = "industry_id")
    @ApiModelProperty(value = "客户所属行业ID", required = "required")
    private String industryId;

    @TableField(value = "cus_url")
    @ApiModelProperty(value = "客户网址")
    private String cusUrl;

    @TableField(value = "longitude")
    @ApiModelProperty(value = "经度")
    private String longitude;

    @TableField(value = "latitude")
    @ApiModelProperty(value = "纬度")
    private String latitude;

    @TableField(value = "social_credit_code")
    @ApiModelProperty(value = "营业执照注册号", fuzzyLike = true)
    private String socialCreditCode;

    @TableField(value = "postal_code")
    @ApiModelProperty(value = "邮政编码")
    private String postalCode;

    @TableField(value = "fax")
    @ApiModelProperty(value = "传真")
    private String fax;

    @TableField(value = "cor_representative")
    @ApiModelProperty(value = "法人代表")
    private String corRepresentative;

    @TableField(value = "reg_capital")
    @ApiModelProperty(value = "注册资本")
    private String regCapital;

    @TableField(value = "bank_account")
    @ApiModelProperty(value = "银行账号")
    private String bankAccount;

    @TableField(value = "account_name")
    @ApiModelProperty(value = "开户名称")
    private String accountName;

    @TableField(value = "bank_name")
    @ApiModelProperty(value = "开户银行名称")
    private String bankName;

    @TableField(value = "bank_address")
    @ApiModelProperty(value = "开户银行地址")
    private String bankAddress;

    @TableField(value = "duty_paragraph")
    @ApiModelProperty(value = "税号")
    private String dutyParagraph;

    @TableField(value = "finance_phone")
    @ApiModelProperty(value = "财务电话")
    private String financePhone;

    @TableField(value = "team_template_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "团队模板id")
    private String teamTemplateId;

    @TableField(value = "delete_flag")
    private Integer deleteFlag;

}
