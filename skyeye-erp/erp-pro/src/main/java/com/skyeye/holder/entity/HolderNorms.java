/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.holder.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.depot.entity.Depot;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: HolderNorms
 * @Description: 关联的客户/供应商/会员购买或者出售的商品信息实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/9/2 21:25
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@TableName(value = "erp_holder_norms", autoResultMap = true)
@ApiModel("关联的客户/供应商/会员购买或者出售的商品信息实体类")
public class HolderNorms extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(exist = false)
    @Property(value = "名称")
    private String name;

    @TableField(value = "holder_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "关联的客户/供应商/会员id", required = "required")
    private String holderId;

    @TableField(exist = false)
    @Property(value = "关联的客户/供应商/会员的信息")
    private Map<String, Object> holderMation;

    @TableField(value = "holder_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "关联的客户/供应商/会员的className", required = "required")
    private String holderKey;

    @TableField("material_id")
    @ApiModelProperty(value = "产品id", required = "required")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "产品信息")
    private Material materialMation;

    @TableField("norms_id")
    @ApiModelProperty(value = "规格id", required = "required")
    private String normsId;

    @TableField(exist = false)
    @Property(value = "规格信息")
    private MaterialNorms normsMation;

    @TableField("oper_number")
    @ApiModelProperty(value = "交易数量", required = "required")
    private String operNumber;

    @TableField(value = "sum(oper_number)", insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    @Property(value = "交易数量合计")
    private String allOperNumber;

    @TableField("create_time")
    @Property(value = "创建时间")
    private String createTime;

    @TableField(exist = false)
    @Property(value = "商品规格条形码编号集合")
    private List<String> normsCodeList;

    @TableField(value = "order_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "单据id")
    private String orderId;

    @TableField(value = "order_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "单据的className")
    private String orderKey;

    @TableField(exist = false)
    @Property(value = "关联的单据信息")
    private Map<String, Object> orderMation;

    @TableField(value = "depot_id")
    @ApiModelProperty(value = "仓库id", required = "required")
    private String depotId;

    @TableField(exist = false)
    @Property(value = "仓库信息")
    private Depot depotMation;

}
