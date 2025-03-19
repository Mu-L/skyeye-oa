/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.task.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: TaskAuthEnum
 * @Description: 任务权限控制枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2023/2/25 23:06
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum TaskAuthEnum implements SkyeyeEnumClass {

    LIST("list", "所有任务列表", true, false),
    MY_EXECUTE("myExecute", "我执行的任务列表", true, false),
    MY_CREATE("myCreate", "我创建的任务列表", true, false),
    SIMPLE_BY_DEPARTMENT("simpleByDepartment", "同部门人员执行的任务列表", true, false),
    ADD("add", "新增", true, false),
    EDIT("edit", "编辑", true, false),
    DELETE("delete", "删除", true, false),
    REVOKE("revoke", "撤销", true, false),
    INVALID("invalid", "作废", true, false),
    SUBMIT_TO_APPROVAL("submitToApproval", "提交审批", true, false),

    EXECUTING("executing", "执行中", true, false),
    COMPLETED("completed", "执行完成", true, false),
    CLOSE("close", "已关闭", true, false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
