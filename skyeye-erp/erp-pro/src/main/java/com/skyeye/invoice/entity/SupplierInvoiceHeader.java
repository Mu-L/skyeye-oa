/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.invoice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.base.handler.enclosure.bean.Enclosure;
import com.skyeye.common.base.handler.enclosure.bean.EnclosureFace;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeTeamAuth;
import lombok.Data;

/**
 * @ClassName: SupplierInvoiceHeader
 * @Description: 发票抬头实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/3 14:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "erp:invoiceHeader", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "erp_invoice_header")
@ApiModel("发票抬头实体类")
public class SupplierInvoiceHeader extends SkyeyeTeamAuth implements EnclosureFace {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "`name`")
    @ApiModelProperty(value = "发票抬头", required = "required", fuzzyLike = true)
    private String name;

    @TableField(value = "identification_number")
    @ApiModelProperty(value = "纳税识别号", fuzzyLike = true)
    private String identificationNumber;

    @TableField(value = "opening_bank")
    @ApiModelProperty(value = "开户行")
    private String openingBank;

    @TableField(value = "opening_account")
    @ApiModelProperty(value = "开户帐号")
    private String openingAccount;

    @TableField(value = "billing_address")
    @ApiModelProperty(value = "开票地址")
    private String billingAddress;

    @TableField(value = "phone")
    @ApiModelProperty(value = "电话")
    private String phone;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件", required = "json")
    private Enclosure enclosureInfo;

}
