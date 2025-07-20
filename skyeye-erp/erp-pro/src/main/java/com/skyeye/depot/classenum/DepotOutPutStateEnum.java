package com.skyeye.depot.classenum;


import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: DepotOutPutStateEnum
 * @Description: 仓库出/入库记录状态枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/31 11:54
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum DepotOutPutStateEnum implements SkyeyeEnumClass {

    // 未归还
    NOT_RETURN(0, "未归还", "red", true, false),
    // 部分归还
    PART_RETURN(1, "部分归还", "blue", true, false),
    // 已归还
    RETURNED(2, "已归还", "green", true, false);

    private Integer key;
    private String value;
    private String color;
    private Boolean show;
    private Boolean isDefault;
}
