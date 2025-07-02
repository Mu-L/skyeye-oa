/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.datasync.enums;

import cn.hutool.core.util.ArrayUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import com.skyeye.datasync.AbstractSyncClient;
import com.skyeye.datasync.service.impl.guangkeshi.GksClient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: SyncFromType
 * @Description: 同步来源类型枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2025/7/2 8:35
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SyncFromType implements SkyeyeEnumClass {

    GKS("gks", "广西科技师范学院数据同步", GksClient.class, false, false);

    private String key;

    private String value;

    private Class<? extends AbstractSyncClient> configClass;

    private Boolean show;

    private Boolean isDefault;

    public static SyncFromType getByCode(String code) {
        return ArrayUtil.firstMatch(o -> o.getKey().equals(code), values());
    }

}
