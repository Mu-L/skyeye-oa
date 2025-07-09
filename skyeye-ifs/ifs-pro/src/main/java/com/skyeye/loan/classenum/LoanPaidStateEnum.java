package com.skyeye.loan.classenum;


import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: LoanPaidStateEnum
 * @Description: 借款单还款状态枚举
 * @author: skyeye云系列--卫志强
 * @date: 2022/11/24 22:46
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum LoanPaidStateEnum implements SkyeyeEnumClass {

    NOT_PAID(0, "未还款","red" ,true, false),
    PART_PAID(1, "部分还款","yellow", true, false),
    PAID(2, "已还款", "green",true, false);


    private Integer key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

}
