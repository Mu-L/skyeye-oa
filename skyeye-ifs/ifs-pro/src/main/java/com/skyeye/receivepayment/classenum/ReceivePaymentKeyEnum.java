package com.skyeye.receivepayment.classenum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ReceivePaymentKeyEnum
 * @Description: 收付款管理业务keyl类型枚举
 * @author: skyeye云系列--卫志强
 * @date: 2022/11/24 22:46
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ReceivePaymentKeyEnum {

    CRM_PAYMENT_KEY("com.skyeye.customer.service.impl.CustomerServiceImpl","客户",true,true),
    SUPPLIER_PAYMENT_KEY("com.skyeye.supplier.service.impl.SupplierServiceImpl","供应商",true,false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
