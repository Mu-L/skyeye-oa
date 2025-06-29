package com.skyeye.loan.classenum;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: LoanBorrowTypeEnum
 * @Description: 借款类型枚举
 * @author: skyeye云系列--卫志强
 * @date: 2022/11/24 22:46
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum LoanBorrowTypeEnum {

    PERSONAL(0 ,"个人借款", true, true),
    DEPARTMENT(1, "部门借款", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
