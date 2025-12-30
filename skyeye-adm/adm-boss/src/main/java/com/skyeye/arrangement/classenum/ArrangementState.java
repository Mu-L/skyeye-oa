/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.arrangement.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: BossInterviewArrangementState
 * @Description: 面试安排状态枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/13 16:30
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ArrangementState implements SkyeyeEnumClass {

    SUBMIT(2, "已提交待安排面试人员", "#FFA500", true, false),
    TO_BE_INTERVIEWED(3, "已提交待面试", "#00BFFF", true, false),
    INTERVIEWED_PASS(4, "面试通过", "#008000", true, false),
    INTERVIEWED_FAIL(5, "面试不通过", "#FF0000", true, false),
    COMPLATE(6, "已完成入职", "#0000FF", true, false),
    COMPLATE_REFUSE(7, "已完成拒绝入职", "#FF0000", true, false),
    NULLIFY(8, "作废(HR操作)", "#808080", true, false),
    INDUCTION_OTHER(9, "入职其他部门", "#0000FF", false, false);

    private Integer key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

}
