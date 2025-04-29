/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.IsUsedEnum;
import com.skyeye.common.enumeration.UserStaffState;
import com.skyeye.organization.entity.Company;
import com.skyeye.organization.entity.CompanyJob;
import com.skyeye.organization.entity.Department;
import com.skyeye.organization.entity.JobScore;
import com.skyeye.tenant.classenum.TenantUserJoinType;
import lombok.Data;

/**
 * @ClassName: TenantUserInvite
 * @Description: 租户下的用户邀请信息实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/4/26 22:42
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "tenant_user_invite")
@ApiModel("租户下的用户邀请信息实体类")
public class TenantUserInvite extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "email")
    @ApiModelProperty(value = "接受邀请链接的邮箱", required = "required", fuzzyLike = true)
    private String email;

    @TableField(value = "phone")
    @ApiModelProperty(value = "联系方式", required = "required,phone", fuzzyLike = true)
    private String phone;

    @TableField("is_used")
    @Property(value = "是否使用", enumClass = IsUsedEnum.class)
    private Integer isUsed;

    @TableField("join_type")
    @Property(value = "加入类型", enumClass = TenantUserJoinType.class)
    private Integer joinType;

    @TableField(value = "company_id")
    @ApiModelProperty(value = "企业id", required = "required")
    private String companyId;

    @TableField(exist = false)
    @Property(value = "企业信息")
    private Company companyMation;

    @TableField(value = "department_id")
    @ApiModelProperty(value = "部门id", required = "required")
    private String departmentId;

    @TableField(exist = false)
    @Property(value = "部门信息")
    private Department departmentMation;

    @TableField(value = "job_id")
    @ApiModelProperty(value = "岗位id", required = "required")
    private String jobId;

    @TableField(exist = false)
    @Property(value = "岗位信息")
    private CompanyJob jobMation;

    @TableField("job_score_id")
    @ApiModelProperty(value = "职位定级id")
    private String jobScoreId;

    @TableField(exist = false)
    @Property(value = "职位定级信息")
    private JobScore jobScoreMation;

    @TableField("state")
    @ApiModelProperty(value = "员工在职状态", enumClass = UserStaffState.class, required = "required,num")
    private Integer state;

    @TableField(exist = false)
    @Property(value = "员工在职状态名称")
    private String stateName;

    @TableField("work_time")
    @ApiModelProperty(value = "参加工作时间")
    private String workTime;

    @TableField("entry_time")
    @ApiModelProperty(value = "入职时间，也是到岗时间")
    private String entryTime;

    @TableField("trial_time")
    @ApiModelProperty(value = "如果有试用期，则为试用期到期时间。当state=4时，该字段必填")
    private String trialTime;

    @TableField("interview_arrangement_id")
    @ApiModelProperty(value = "关联的面试安排信息id")
    private String interviewArrangementId;

    @TableField(value = "tenant_id")
    @ApiModelProperty(value = "租户id", required = "required")
    private String tenantId;
}
