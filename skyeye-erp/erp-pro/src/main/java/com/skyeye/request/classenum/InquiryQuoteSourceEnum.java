/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.request.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 询价报价来源枚举：区分后端添加的报价与供应商自己提交的报价。
 * 后端询价报价时通过 saveList 保存，此时为 BACKEND；供应商端报价为 SUPPLIER。
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum InquiryQuoteSourceEnum implements SkyeyeEnumClass {

    BACKEND("1", "后端添加", true, true),
    SUPPLIER("2", "供应商报价", true, false);

    private String key;
    private String value;
    private Boolean show;
    private Boolean isDefault;
}
