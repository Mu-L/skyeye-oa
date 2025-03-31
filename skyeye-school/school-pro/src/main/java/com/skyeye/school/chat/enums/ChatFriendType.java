package com.skyeye.school.chat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ChatFriendType {

    PENDING_REQUEST("pendingRequest",0, "请求中", true, false),
    ACCEPTED("accepted", 1,"已接受", true, false),
    REJECTED("rejected", 2,"已拒绝", true, false),
    BLOCKED("blocked", 3,"已拉黑", true, false);

    private String key;

    private Integer Index;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
