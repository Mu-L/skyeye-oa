/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: Inventory
 * @Description: 盘点任务单据实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/18 15:35
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "erp:inventory", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "erp_inventory")
@ApiModel("盘点任务单据实体类")
public class Inventory extends SkyeyeFlowable {

    @TableField("oper_time")
    @ApiModelProperty(value = "单据日期", required = "required")
    private String operTime;

    @TableField("remark")
    @ApiModelProperty(value = "描述")
    private String remark;

    @TableField(value = "all_num")
    @ApiModelProperty(value = "盘点总数量")
    private String allNum;

    @TableField(value = "inventory_num")
    @ApiModelProperty(value = "已盘点数量")
    private String inventoryNum;

    @TableField(exist = false)
    @ApiModelProperty(value = "盘点任务明细信息", required = "required,json")
    private List<InventoryChild> inventoryChildList;

}
