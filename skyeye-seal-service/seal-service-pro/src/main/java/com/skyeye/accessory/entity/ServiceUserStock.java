/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.accessory.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: ServiceUserStock
 * @Description: 用户配件申领单审核通过后的库存信息
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/15 11:06
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "crm_service_apply_user_stock")
@ApiModel("用户配件申领单审核通过后的库存信息")
public class ServiceUserStock extends CommonInfo {

    @TableField(value = "user_id")
    @Property(value = "用户id")
    private String userId;

    @TableField(value = "material_id")
    @Property(value = "商品id")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "产品信息")
    private Map<String, Object> materialMation;

    @TableField(value = "norms_id")
    @Property(value = "规格id")
    private String normsId;

    @TableField(exist = false)
    @Property(value = "规格信息")
    private Map<String, Object> normsMation;

    @TableField(value = "stock")
    @Property(value = "数量")
    private String stock;

}
