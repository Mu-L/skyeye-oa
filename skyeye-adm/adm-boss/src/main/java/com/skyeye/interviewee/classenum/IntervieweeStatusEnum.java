/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.interviewee.classenum;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: IntervieweeStatusEnum
 * @Description: 面试者状态的枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/17 22:46
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum IntervieweeStatusEnum implements SkyeyeEnumClass {

    PENDING_INTERVIEW_STATUS(0, "待面试", true, false),
    INTERVIEW_STATUS(1, "面试中", true, false),
    INTERVIEW_PASS_STATUS(2, "面试通过", true, false),
    INTERVIEW_FAILED_STATUS(3, "面试失败", true, false),
    REJECTED_STATUS(4, "拒绝入职", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    public static String getIntervieweeStateName(Integer status) {
        for (IntervieweeStatusEnum q : IntervieweeStatusEnum.values()) {
            if (q.getKey().equals(status)) {
                return q.getValue();
            }
        }
        return StrUtil.EMPTY;
    }

}
