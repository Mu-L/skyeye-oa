/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.material.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.depot.entity.Depot;
import com.skyeye.material.classenum.MaterialNormsStockType;
import lombok.Data;

/**
 * @ClassName: MaterialNormsStock
 * @Description: 规格初始化库存信息
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/21 15:06
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "erp_material_norms_stock")
@ApiModel("ERP规格初始化库存信息")
public class MaterialNormsStock extends CommonInfo {

    @TableField(value = "material_id")
    @Property("商品id")
    private String materialId;

    @TableField(value = "depot_id")
    @ApiModelProperty(value = "仓库id", required = "required")
    private String depotId;

    @TableField(exist = false)
    @Property(value = "仓库信息")
    private Depot depotMation;

    @TableField(value = "norms_id")
    @Property("规格id")
    private String normsId;

    @TableField(value = "stock")
    @ApiModelProperty(value = "数量", required = "required,num")
    private String stock;

    @TableField(value = "type")
    @Property(value = "商品规格库存类型", enumClass = MaterialNormsStockType.class)
    private Integer type = MaterialNormsStockType.INIT_STOCK.getKey();

}
