/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.material.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * @ClassName: NormsCalcStock
 * @Description: ERP商品规格库存实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/17 15:58
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("ERP商品规格参数实体类")
public class NormsCalcStock extends CommonInfo {

    @Property(value = "总库存")
    private String allStock;

    @Property(value = "初始化的总库存")
    private String initialTock;

    @Property(value = "可盘点的总库存")
    private String inventoryTock;

    public NormsCalcStock(String allStock, String initialTock, String inventoryTock) {
        this.allStock = allStock;
        this.initialTock = initialTock;
        this.inventoryTock = inventoryTock;
    }
}
