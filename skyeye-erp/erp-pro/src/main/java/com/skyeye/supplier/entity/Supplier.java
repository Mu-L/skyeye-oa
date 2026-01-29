/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.supplier.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.features.AreaGeneralInfo;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import lombok.Data;

/**
 * @ClassName: Supplier
 * @Description: 供应商信息实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/02/15 13:28
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = CacheConstants.ERP_SUPPLIER_CACHE_KEY)
@TableName(value = "erp_supplier")
@ApiModel("供应商信息实体类")
public class Supplier extends AreaGeneralInfo {

    @TableField(value = "fax")
    @ApiModelProperty(value = "传真")
    private String fax;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "启用状态，参考#EnableEnum", required = "required,num")
    private Integer enabled;

    @TableField(value = "tax_num")
    @ApiModelProperty(value = "纳税人识别号")
    private String taxNum;

    @TableField(value = "duty_paragraph")
    @ApiModelProperty(value = "税号")
    private String dutyParagraph;

    @TableField(value = "cus_url")
    @ApiModelProperty(value = "供应商网址")
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

    @TableField(value = "cor_representative")
    @ApiModelProperty(value = "法人代表")
    private String corRepresentative;

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

    @TableField(value = "delete_flag")
    @Property(value = "删除标识", enumClass = DeleteFlagEnum.class)
    private Integer deleteFlag;

    @TableField(value = "team_template_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "团队模板id")
    private String teamTemplateId;

}
