/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: TenantApp
 * @Description: 租户应用管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/29 16:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = "tenant:app", cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "tenant_app")
@ApiModel("租户应用管理实体类")
public class TenantApp extends BaseGeneralInfo {

    @TableField(value = "year_unit_price")
    @ApiModelProperty(value = "应用年费单价(元/年)", required = "required")
    private String yearUnitPrice;

    @TableField(exist = false)
    @ApiModelProperty(value = "PC端菜单权限")
    private List<String> menuIds;

    @TableField(exist = false)
    @ApiModelProperty(value = "手机端菜单权限")
    private List<String> appMenuIds;

}
