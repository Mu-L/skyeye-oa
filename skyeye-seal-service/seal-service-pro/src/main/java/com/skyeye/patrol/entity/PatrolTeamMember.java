/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: PatrolTeamMember
 * @Description: 巡检班组人员实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/01/XX XX:XX
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = {"teamId", "staffId"})
@TableName(value = "crm_service_patrol_team_member")
@ApiModel("巡检班组人员实体类")
public class PatrolTeamMember extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "team_id")
    @ApiModelProperty(value = "班组ID", required = "required")
    private String teamId;

    @TableField(value = "staff_id")
    @ApiModelProperty(value = "员工ID", required = "required")
    private String staffId;

    @TableField(exist = false)
    @ApiModelProperty(value = "员工信息")
    private Map<String, Object> staffMation;

}

