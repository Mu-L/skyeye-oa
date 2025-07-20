/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.delivery.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: ShopDeliveryTemplateCharge
 * @Description: 快递运费模板计费配置信息管理
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 10:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
//@UniqueField
@TableName(value = "shop_delivery_template_charge")
@ApiModel("快递运费模板计费配置信息管理")
public class ShopDeliveryTemplateCharge extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "`template_id`")
    @ApiModelProperty(value = "模板ID",required = "required",fuzzyLike = true)
    private String templateId;

    @TableField(value = "`area_id`")
    @ApiModelProperty(value = "区域id，集合",required = "required")
    private String areaId;

    @TableField(value = "`start_count`")
    @ApiModelProperty(value = "首件数量(件数,重量，或体积)")
    private Double startCount;

    @TableField(value = "`start_price`")
    @ApiModelProperty(value = "起步价，单位：分")
    private String startPrice;

    @TableField(value = "`extra_count`")
    @ApiModelProperty(value = "续件数量(件, 重量，或体积)")
    private Double extraCount;

    @TableField(value = "`extra_price`")
    @ApiModelProperty(value = "额外价，单位：分")
    private String extraPrice;
}
