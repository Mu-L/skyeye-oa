/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.serviceitems.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.EnableEnum;
import lombok.Data;

/**
 * @ClassName: ServiceItems
 * @Description: 售后服务项目实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/01/23
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = "seal:server:serviceItems")
@TableName(value = "crm_service_items")
@ApiModel("售后服务项目实体类")
public class ServiceItems extends BaseGeneralInfo {

    @TableField(value = "odd_number")
    @ApiModelProperty(value = "服务编码", fuzzyLike = true)
    private String oddNumber;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

    @TableField(value = "type")
    @ApiModelProperty(value = "项目分类", required = "required")
    private String type;

    @TableField(value = "seals_price")
    @ApiModelProperty(value = "销售价格", required = "required,double", defaultValue = "0")
    private String sealsPrice;

    @TableField(value = "cost_price")
    @ApiModelProperty(value = "成本价格", required = "required,double", defaultValue = "0")
    private String costPrice;

    @TableField(value = "employee_settlement_price")
    @ApiModelProperty(value = "员工结算价格", required = "required,double", defaultValue = "0")
    private String employeeSettlementPrice;

    @TableField(value = "sales_volume")
    @ApiModelProperty(value = "总销量", defaultValue = "0")
    private String salesVolume;

}

