package com.skyeye.scheduling.classenum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ScheduleType {

    AUTOMATIC(1, "自动排班", false, false),
    MANUAL(2, "手动排班", false, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
