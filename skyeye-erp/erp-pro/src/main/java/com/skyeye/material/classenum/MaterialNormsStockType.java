/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.material.classenum;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: MaterialNormsStockType
 * @Description: 商品规格库存类型枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/13 16:30
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum MaterialNormsStockType implements SkyeyeEnumClass {

    INIT_STOCK(1, "初始化库存", "初始化库存", StrUtil.EMPTY, true, true),
    ORDER_STOCK(2, "现有库存", "实际库存", StrUtil.EMPTY, true, false),
    IN_TRANSIT_STOCK(3, "在途物料/在制物料", "已经下达采购订单的，但是还未到货的物料/已经下达生产订单的，但是还未完成的物料", "inTransitStock", true, false),
    ALLOCATED_STOCK(4, "已分配量", "已经分配给销售订单的物料", "allocatedStock", true, false);

    private Integer key;

    private String value;

    private String description;

    private String defaultDepotId;

    private Boolean show;

    private Boolean isDefault;

}
