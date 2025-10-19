/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: TenantAppBuyOrder
 * @Description: 订单管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/29 16:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "tenant:order", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "tenant_app_buy_order")
@ApiModel("订单管理实体类")
public class TenantAppBuyOrder extends SkyeyeFlowable {

    @TableField("oper_time")
    @ApiModelProperty(value = "单据日期", required = "required")
    private String operTime;

    @TableField(value = "buy_tenant_id")
    @ApiModelProperty(value = "租户id", required = "required")
    private String buyTenantId;

    @TableField(exist = false)
    @Property("租户信息")
    private Tenant buyTenantMation;

    @TableField(value = "lifecycle_template_id")
    @ApiModelProperty(value = "生命周期模板id")
    private String lifecycleTemplateId;

    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(value = "all_price")
    @Property(value = "总价")
    private String allPrice;

    @TableField(exist = false)
    @ApiModelProperty(value = "租户数量信息", required = "json")
    private List<TenantAppBuyOrderNum> tenantAppBuyOrderNumList;

    @TableField(exist = false)
    @ApiModelProperty(value = "租户应用信息", required = "json")
    private List<TenantAppBuyOrderYear> tenantAppBuyOrderYearList;

}
