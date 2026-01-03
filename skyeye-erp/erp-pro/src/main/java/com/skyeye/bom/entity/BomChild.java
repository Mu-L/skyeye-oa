/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.bom.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.procedure.entity.WayProcedure;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: BomChild
 * @Description: bom表子件清单实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/27 14:09
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = CacheConstants.MES_BOM_CACHE_KEY)
@TableName(value = "erp_bom_child", autoResultMap = true)
@ApiModel("bom表子件清单实体类")
public class BomChild extends CommonInfo {

    @TableId("id")
    private String id;

    @TableField(exist = false)
    @Property(value = "新的id")
    private String newId;

    @TableField(exist = false)
    @Property(value = "新的父节点id")
    private String newParentId;

    @TableField(value = "bom_id")
    @Property(value = "bom表id")
    private String bomId;

    @TableField(value = "parent_id")
    @ApiModelProperty(value = "从属关系的商品id", required = "required", defaultValue = "0")
    private String parentId;

    @TableField(value = "material_id")
    @ApiModelProperty(value = "商品id", required = "required")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "商品信息")
    private Material materialMation;

    @TableField(value = "norms_id")
    @ApiModelProperty(value = "规格id", required = "required")
    private String normsId;

    @TableField(exist = false)
    @Property(value = "规格信息")
    private MaterialNorms normsMation;

    @TableField(value = "need_num")
    @ApiModelProperty(value = "需要的数量", required = "required,num")
    private String needNum;

    @TableField(value = "consumables_price")
    @ApiModelProperty(value = "耗材总费用")
    private String consumablesPrice;

    @TableField(value = "all_price")
    @Property(value = "总费用")
    private String allPrice;

    @TableField(value = "remark")
    @ApiModelProperty(value = "相关描述")
    private String remark;

    @TableField(value = "way_procedure_id")
    @ApiModelProperty(value = "工艺id")
    private String wayProcedureId;

    @TableField(value = "order_by")
    @ApiModelProperty(value = "排序", required = "required,num")
    private Integer orderBy;

    @TableField(exist = false)
    @Property(value = "工艺信息")
    private WayProcedure wayProcedureMation;

    @TableField(exist = false)
    @Property(value = "树节点是否展开")
    private boolean open;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序耗材列表", required = "json")
    private List<BomProcedureConsumables> procedureConsumablesList;

}
