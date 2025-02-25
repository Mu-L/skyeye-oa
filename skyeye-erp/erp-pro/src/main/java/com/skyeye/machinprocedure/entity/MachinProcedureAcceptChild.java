/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machinprocedure.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.machinprocedure.classenum.MachinProcedureAcceptChildType;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: MachinProcedureAcceptChild
 * @Description: 工序验收子单据实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/25 17:21
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "erp_machin_procedure_accept_child", autoResultMap = true)
@ApiModel("工序验收子单据实体类")
public class MachinProcedureAcceptChild extends CommonInfo {

    @TableId("id")
    @Property(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "parent_id")
    @Property(value = "加工单id")
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

    @TableField(value = "oper_number")
    @ApiModelProperty(value = "数量", required = "required,num")
    private Integer operNumber;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品规格条形码编号")
    private String normsCode;

    @TableField(exist = false)
    @Property(value = "商品规格条形码编号集合")
    private List<String> normsCodeList;

    @TableField(value = "type")
    @ApiModelProperty(value = "类型", required = "required,num", enumClass = MachinProcedureAcceptChildType.class)
    private Integer type;

    @TableField(exist = false)
    @Property(value = "类型信息")
    private Map<String, Object> typeMation;

}
