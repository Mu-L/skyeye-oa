/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.depot.classenum;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import com.skyeye.machin.service.impl.MachinPutServiceImpl;
import com.skyeye.other.service.impl.OtherWareHousServiceImpl;
import com.skyeye.pick.service.impl.ReturnPutServiceImpl;
import com.skyeye.pickconfirm.service.impl.ConfirmReturnServiceImpl;
import com.skyeye.purchase.service.impl.PurchasePutServiceImpl;
import com.skyeye.retail.service.impl.RetailReturnsServiceImpl;
import com.skyeye.seal.service.impl.SalesExchangesServiceImpl;
import com.skyeye.seal.service.impl.SalesReturnsServiceImpl;
import com.skyeye.shop.service.impl.ShopConfirmReturnServiceImpl;
import com.skyeye.shop.service.impl.ShopReturnsServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: DepotPutFromType
 * @Description: 仓库入库单来源单据类型
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/22 10:58
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum DepotPutFromType implements SkyeyeEnumClass {

    PURCHASE_PUT(1, "采购入库单", PurchasePutServiceImpl.class.getName(), true, false),
    SEAL_RETURNS(2, "销售退货单", SalesReturnsServiceImpl.class.getName(), true, false),
    RETAIL_RETURNS(3, "零售退货单", RetailReturnsServiceImpl.class.getName(), true, false),
    OTHER_WARE_HOUS(4, "其他入库单", OtherWareHousServiceImpl.class.getName(), true, false),
    RETURN_PUT(5, "退料入库单", ReturnPutServiceImpl.class.getName(), true, false),
    CONFIRM_RETURN(6, "物料退货单", ConfirmReturnServiceImpl.class.getName(), true, false),
    MACHIN_PUT(7, "加工入库单", MachinPutServiceImpl.class.getName(), true, false),
    SHOP_RETURNS(8, "门店退货单", ShopReturnsServiceImpl.class.getName(), true, false),
    SHOP_CONFIRM_RETURNS(9, "门店物料退货单", ShopConfirmReturnServiceImpl.class.getName(), true, false),
    SALES_EXCHANGES(10, "销售换货单", SalesExchangesServiceImpl.class.getName(), false, false);


    private Integer key;

    private String value;

    private String idKey;

    private Boolean show;

    private Boolean isDefault;

    public static Integer getItemKey(String idKey) {
        for (DepotPutFromType bean : DepotPutFromType.values()) {
            if (StrUtil.equals(idKey, bean.getIdKey())) {
                return bean.getKey();
            }
        }
        return null;
    }

    public static String getItemIdKey(Integer key) {
        for (DepotPutFromType bean : DepotPutFromType.values()) {
            if (key == bean.getKey()) {
                return bean.getIdKey();
            }
        }
        return StrUtil.EMPTY;
    }

    public static List<String> getAllIdKeys() {
        return Arrays.stream(DepotPutFromType.values()).map(DepotPutFromType::getIdKey).collect(Collectors.toList());
    }

}
