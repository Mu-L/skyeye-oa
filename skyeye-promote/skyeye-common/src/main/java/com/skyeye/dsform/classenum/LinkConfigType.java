/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dsform.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: LinkConfigType
 * @Description: 连接配置类型枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/5 18:02
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum LinkConfigType implements SkyeyeEnumClass {

    NONE_TYPE("none", "无链接", true, true),
    PROCESS_TYPE("process", "流程链接", true, false),
    CUSTOM_TYPE("custom", "自定义链接", true, false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
