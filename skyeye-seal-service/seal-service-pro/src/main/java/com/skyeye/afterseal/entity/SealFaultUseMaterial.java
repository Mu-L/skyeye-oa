/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.accessory.entity.ServiceUserStock;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.SkyeyeLinkData;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: SealFaultUseMaterial
 * @Description: 售后服务故障配件使用信息实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/12 17:44
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@TableName(value = "crm_service_fault_use_material")
@ApiModel("售后服务故障配件使用信息实体类")
public class SealFaultUseMaterial extends SkyeyeLinkData {

    @TableField("material_id")
    @ApiModelProperty(value = "产品id", required = "required")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "产品信息")
    private Map<String, Object> materialMation;

    @TableField("norms_id")
    @ApiModelProperty(value = "商品规格id", required = "required")
    private String normsId;

    @TableField(exist = false)
    @Property(value = "规格信息")
    private Map<String, Object> normsMation;

    @TableField("oper_number")
    @ApiModelProperty(value = "使用数量", required = "required,num")
    private String operNumber;

    @TableField("unit_price")
    @ApiModelProperty(value = "单价", required = "double", defaultValue = "0")
    private String unitPrice;

    @TableField("all_price")
    @ApiModelProperty(value = "总金额", required = "double", defaultValue = "0")
    private String allPrice;

    @TableField("create_id")
    @Property(value = "使用人ID")
    private String createId;

    @TableField("create_time")
    @Property(value = "创建时间")
    private String createTime;

    @TableField(exist = false)
    @Property(value = "我的库存信息")
    private ServiceUserStock serviceUserStock;

}
