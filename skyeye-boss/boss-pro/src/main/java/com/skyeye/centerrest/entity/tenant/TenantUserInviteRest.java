/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.centerrest.entity.tenant;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.enumeration.UserStaffState;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: TenantUserInviteRest
 * @Description: 租户与用户邀请关系管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/4/27 10:13
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("员工信息对象")
public class TenantUserInviteRest implements Serializable {

    @ApiModelProperty(value = "接受邀请链接的邮箱", required = "required")
    private String email;

    @ApiModelProperty(value = "联系方式", required = "required,phone")
    private String phone;

    @ApiModelProperty(value = "企业id", required = "required")
    private String companyId;

    @ApiModelProperty(value = "部门id", required = "required")
    private String departmentId;

    @ApiModelProperty(value = "岗位id", required = "required")
    private String jobId;

    @ApiModelProperty(value = "职位定级id")
    private String jobScoreId;

    @ApiModelProperty(value = "员工在职状态", enumClass = UserStaffState.class, required = "required,num")
    private Integer state;

    @ApiModelProperty(value = "参加工作时间")
    private String workTime;

    @ApiModelProperty(value = "入职时间，也是到岗时间")
    private String entryTime;

    @ApiModelProperty(value = "如果有试用期，则为试用期到期时间。当state=4时，该字段必填")
    private String trialTime;

    @ApiModelProperty(value = "关联的面试安排信息id")
    private String interviewArrangementId;

}
