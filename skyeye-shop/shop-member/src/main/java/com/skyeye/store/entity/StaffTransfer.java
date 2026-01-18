/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: StaffTransfer
 * @Description: 员工调拨申请实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/01/XX XX:XX
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "store:staffTransfer", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "shop_staff_transfer", autoResultMap = true)
@ApiModel("员工调拨申请实体类")
public class StaffTransfer extends SkyeyeFlowable {

    @TableField(value = "`name`")
    @ApiModelProperty(value = "标题", required = "required")
    private String name;

    @TableField(value = "staff_id")
    @ApiModelProperty(value = "员工ID", required = "required")
    private String staffId;

    @TableField(exist = false)
    @ApiModelProperty(value = "员工信息")
    private Map<String, Object> staffMation;

    @TableField(value = "from_store_id")
    @ApiModelProperty(value = "原门店ID", required = "required")
    private String fromStoreId;

    @TableField(exist = false)
    @ApiModelProperty(value = "原门店信息")
    private ShopStore fromStoreMation;

    @TableField(value = "to_store_id")
    @ApiModelProperty(value = "目标门店ID", required = "required")
    private String toStoreId;

    @TableField(exist = false)
    @ApiModelProperty(value = "目标门店信息")
    private ShopStore toStoreMation;

    @TableField(value = "remark")
    @ApiModelProperty(value = "调拨原因")
    private String remark;

}

