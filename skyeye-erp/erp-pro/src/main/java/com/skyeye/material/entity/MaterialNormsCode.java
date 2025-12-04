/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.material.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.depot.entity.Depot;
import com.skyeye.farm.entity.Farm;
import com.skyeye.machinprocedure.classenum.MachinProcedureAcceptChildType;
import com.skyeye.material.classenum.MaterialNormsCodeInDepot;
import com.skyeye.material.classenum.MaterialNormsCodeType;
import com.skyeye.pick.classenum.PickNormsCodeUseState;
import com.skyeye.shop.classenum.StoreNormsCodeUseState;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: MaterialNormsCode
 * @Description: 商品条形码实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/21 8:17
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "erp_material_norms_code")
@ApiModel("商品条形码实体类")
public class MaterialNormsCode extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "code_num", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "规格物品编码", fuzzyLike = true)
    private String codeNum;

    @TableField(value = "material_id", updateStrategy = FieldStrategy.NEVER)
    @Property("商品id")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "商品信息")
    private Material materialMation;

    @TableField(value = "norms_id", updateStrategy = FieldStrategy.NEVER)
    @Property("规格id")
    private String normsId;

    @TableField(exist = false)
    @Property(value = "规格信息")
    private MaterialNorms normsMation;

    @TableField(value = "depot_id")
    @Property(value = "仓库id")
    private String depotId;

    @TableField(exist = false)
    @Property(value = "仓库信息")
    private Depot depotMation;

    @TableField(value = "create_depot_id", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "由哪个仓库生成的")
    private String createDepotId;

    @TableField(exist = false)
    @Property(value = "仓库信息")
    private Depot createDepotMation;

    @TableField(value = "department_id")
    @ApiModelProperty(value = "部门id", required = "required")
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

    @TableField("pick_use_state")
    @Property(value = "加工时，物料使用状态", enumClass = PickNormsCodeUseState.class)
    private Integer pickUseState;

    @TableField("pick_state")
    @Property(value = "加工使用结果状态", enumClass = MachinProcedureAcceptChildType.class)
    private Integer pickState;

    @TableField(value = "store_id")
    @Property(value = "门店id")
    private String storeId;

    @TableField(exist = false)
    @Property(value = "门店信息")
    private Map<String, Object> storeMation;

    @TableField("store_use_state")
    @Property(value = "门店使用状态", enumClass = StoreNormsCodeUseState.class)
    private Integer storeUseState;

    @TableField(value = "in_depot")
    @Property(value = "入库状态", enumClass = MaterialNormsCodeInDepot.class)
    private Integer inDepot;

    @TableField(value = "type")
    @Property(value = "类型", enumClass = MaterialNormsCodeType.class)
    private Integer type;

    @TableField(value = "from_object_id")
    @Property(value = "来源单据的id")
    private String fromObjectId;

    @TableField(value = "from_object_key")
    @Property(value = "来源单据的key")
    private String fromObjectKey;

    @TableField(value = "warehousing_time")
    @Property(value = "入库时间")
    private String warehousingTime;

    @TableField(value = "to_object_id")
    @Property(value = "出库单据的id")
    private String toObjectId;

    @TableField(value = "to_object_key")
    @Property(value = "出库单据的key")
    private String toObjectKey;

    @TableField(value = "outbound_time")
    @Property(value = "出库时间")
    private String outboundTime;

    @TableField(exist = false)
    @Property(value = "条形码信息")
    private Map<String, Object> barCodeMation;

    @TableField(value = "create_time", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "创建时间")
    private String createTime;

}
