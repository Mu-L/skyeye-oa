package com.skyeye.depot.classenum;


import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import com.skyeye.depot.service.DepotPutService;
import com.skyeye.depot.service.impl.DepotOutServiceImpl;
import com.skyeye.depot.service.impl.DepotPutServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: DepotOutFromType
 * @Description: 仓库出库单来源单据类型
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/22 10:58
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum DepotIdKeyEnum  implements SkyeyeEnumClass {

    // 仓库出库
    DEPOT_OUT(1, DepotOutServiceImpl.class.getName(), "仓库出库", true, true),
    // 仓库入库
    DEPOT_PUT(2,DepotPutServiceImpl.class.getName(), "仓库入库", true, false);

    private Integer key;

    private String idKey;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
