package com.skyeye.scheduling.classenum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ScheduleLeaveType {

    APPLIED(1, "已申请", false, false),
    APPROVED(2, "已批准", false, false),
    REJECTED(3, "已拒绝", false, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
