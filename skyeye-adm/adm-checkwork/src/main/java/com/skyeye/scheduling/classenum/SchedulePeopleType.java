package com.skyeye.scheduling.classenum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SchedulePeopleType {

    ONDUTY(1, "在职中", false, false),
    ONLEAVE(2, "请假中", false, false),
    ONBUSINESSTRIP(3, "出差中", false, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
