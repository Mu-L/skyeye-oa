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
import com.skyeye.holder.classenum.HolderNormsChildState;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: HolderNormsChild
 * @Description: 关联的客户/供应商/会员购买或者出售的商品子信息实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/9/2 21:25
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@TableName(value = "erp_holder_norms_child", autoResultMap = true)
@ApiModel("关联的客户/供应商/会员购买或者出售的商品子信息实体类")
public class HolderNormsChild extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "holder_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "关联的客户/供应商/会员id", required = "required")
    private String holderId;

    @TableField(exist = false)
    @Property(value = "关联的客户/供应商/会员的信息")
    private Map<String, Object> holderMation;

    @TableField(value = "holder_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "关联的客户/供应商/会员的className", required = "required")
    private String holderKey;

    @TableField("parent_id")
    @ApiModelProperty(value = "父节点id", required = "required")
    private String parentId;

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

    @TableField("norms_code_num")
    @ApiModelProperty(value = "条形码编号", required = "required", fuzzyLike = true)
    private String normsCodeNum;

    @TableField("state")
    @ApiModelProperty(value = "状态", enumClass = HolderNormsChildState.class, required = "required")
    private Integer state;

}
