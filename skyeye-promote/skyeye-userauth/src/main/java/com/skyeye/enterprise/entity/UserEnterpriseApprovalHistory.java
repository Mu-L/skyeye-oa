/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.enterprise.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.enterprise.enums.UserEnterpriseApprovalResult;
import lombok.Data;

/**
 * @ClassName: UserEnterpriseApprovalHistory
 * @Description: 企业账号审批历史实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/16 8:51
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@TableName(value = "sys_eve_user_enterprise_approval_history")
@ApiModel(value = "企业账号审批历史实体类")
public class UserEnterpriseApprovalHistory extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "user_enterprise_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "企业用户账号id", required = "required")
    private String userEnterpriseId;

    @TableField(value = "approval_result", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "审批结果", enumClass = UserEnterpriseApprovalResult.class, required = "required")
    private Integer approvalResult;

    @TableField(value = "approval_content", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "审批时填写的备注内容")
    private Integer approvalContent;

}
