/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.material.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: MaterialNormsMation
 * @Description: ERP商品规格参数实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/17 15:58
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = CacheConstants.ERP_NORMS_CACHE_KEY)
@TableName(value = "erp_material_norms")
@ApiModel("ERP商品规格参数实体类")
public class MaterialNorms extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "`name`")
    @Property(value = "规格名称")
    private String name;

    @TableField(value = "material_id")
    @Property(value = "商品id")
    private String materialId;

    @TableField(value = "logo")
    @ApiModelProperty(value = "商品规格图片")
    private String logo;

    @TableField(value = "table_num")
    @ApiModelProperty(value = "商品规格编号，同一个商品下唯一。多规格的商品具备该字段")
    private String tableNum;

    @TableField(value = "safety_tock")
    @ApiModelProperty(value = "安全存量，当库存低于这个存量时，做邮件等提醒", required = "required,num")
    private String safetyTock;

    @TableField(value = "retail_price")
    @ApiModelProperty(value = "零售价", required = "required,double")
    private String retailPrice;

    @TableField(value = "low_price")
    @ApiModelProperty(value = "最低售价", required = "required,double")
    private String lowPrice;

    @TableField(value = "estimate_purchase_price")
    @ApiModelProperty(value = "采购价/成本价", required = "required,double")
    private String estimatePurchasePrice;

    @TableField(value = "sale_price")
    @ApiModelProperty(value = "销售价", required = "required,double")
    private String salePrice;

    @TableField(exist = false)
    @ApiModelProperty(value = "规格初始化库存信息")
    private List<MaterialNormsStock> normsStock;

    @TableField(exist = false)
    @Property(value = "现有库存信息")
    private List<MaterialNormsStock> orderStock;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "状态，参考#EnableEnum", required = "required,num")
    private Integer enabled;

    @TableField(value = "order_by")
    @ApiModelProperty(value = "排序", required = "required,num")
    private Integer orderBy;

    @TableField(exist = false)
    @Property(value = "总库存")
    private NormsCalcStock overAllStock;

    @TableField(exist = false)
    @Property(value = "指定仓库的库存")
    private NormsCalcStock depotTock;

}
