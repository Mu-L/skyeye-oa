/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.keepfit.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: KeepFitOrderConsume
 * @Description: 保养订单关联耗材实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/8 15:16
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "shop_keepfit_order_consume")
@ApiModel("保养订单关联耗材实体类")
public class KeepFitOrderConsume extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("order_id")
    @Property(value = "订单id")
    private String orderId;

    @TableField(value = "material_id")
    @ApiModelProperty(value = "商品id", required = "required")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "商品信息")
    private Map<String, Object> materialMation;

    @TableField(value = "norms_id")
    @ApiModelProperty(value = "规格id", required = "required")
    private String normsId;

    @TableField(exist = false)
    @Property(value = "规格信息")
    private Map<String, Object> normsMation;

    @TableField(value = "code_num", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "规格物品编码", fuzzyLike = true)
    private String codeNum;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品条形码编号集合", required = "json")
    private List<String> normsCodeList;

    @TableField(exist = false)
    @Property(value = "规格物品编码信息")
    private Map<String, Object> codeNumMation;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField("oper_number")
    @ApiModelProperty(value = "申领数量", required = "required,num")
    private String operNumber;

    @TableField("unit_price")
    @ApiModelProperty(value = "单价", required = "double", defaultValue = "0")
    private String unitPrice;

    @TableField("all_price")
    @ApiModelProperty(value = "总金额", required = "double", defaultValue = "0")
    private String allPrice;

}
