/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pick.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.farm.entity.Farm;
import com.skyeye.material.classenum.MaterialNormsStockType;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: DepartmentStock
 * @Description: 部门/车间物料库存信息
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/31 16:50
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "erp_department_stock")
@ApiModel("部门/车间物料库存信息")
public class DepartmentStock extends CommonInfo {

    @TableField(value = "material_id")
    @Property(value = "产品id")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "商品信息")
    private Material materialMation;

    @TableField(value = "norms_id")
    @Property(value = "规格id")
    private String normsId;

    @TableField(exist = false)
    @Property(value = "规格信息")
    private MaterialNorms normsMation;

    @TableField(value = "department_id")
    @Property(value = "部门id")
    private String departmentId;

    @TableField(exist = false)
    @Property(value = "部门信息")
    private Map<String, Object> departmentMation;

    @TableField(value = "farm_id")
    @ApiModelProperty(value = "车间id")
    private String farmId;

    @TableField(exist = false)
    @Property(value = "车间信息")
    private Farm farmMation;

    @TableField(value = "stock")
    @Property(value = "数量")
    private String stock;

    @TableField("create_time")
    @Property("创建时间")
    private String createTime;

    @TableField("last_update_time")
    @Property("最后更新时间")
    private String lastUpdateTime;

    @TableField(value = "type")
    @ApiModelProperty(value = "商品规格库存类型", enumClass = MaterialNormsStockType.class)
    private Integer type;

}
