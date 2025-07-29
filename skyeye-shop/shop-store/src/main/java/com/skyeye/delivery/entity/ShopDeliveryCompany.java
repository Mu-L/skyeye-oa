/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.delivery.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: ShopDeliveryCompany
 * @Description: 快递公司管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 10:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "shop_delivery_company")
@ApiModel("快递公司管理")
public class ShopDeliveryCompany extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "`code_num`")
    @ApiModelProperty(value = "快递公司 code", required = "required")
    private String codeNum;

    @TableField(value = "`name`")
    @ApiModelProperty(value = "快递公司名称", required = "required", fuzzyLike = true)
    private String name;

    @TableField(value = "`logo`")
    @ApiModelProperty(value = "快递公司 logo", required = "required")
    private String logo;

    @TableField(value = "`remark`")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(value = "`enabled`")
    @ApiModelProperty(value = "状态", required = "required")
    private String enabled;

    @TableField(value = "`order_by`")
    @ApiModelProperty(value = "排序", required = "required")
    private Integer orderBy;

    @TableField(value = "`store_id`")
    @ApiModelProperty(value = "门店id", required = "required")
    private String storeId;

    @TableField(exist = false)
    @Property(value = "门店信息")
    private Map<String, Object> storeMation;
}
