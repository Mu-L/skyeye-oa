/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.centerrest.entity.staff;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: UserStaffLeaveRest
 * @Description: 员工离职实体入参
 * @author: skyeye云系列--卫志强
 * @date: 2022/4/26 11:22
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("员工离职实体入参")
public class UserStaffLeaveRest implements Serializable {

    @ApiModelProperty(value = "员工id", required = "required")
    private String id;

    @ApiModelProperty(value = "离职时间")
    private String quitTime;

    @ApiModelProperty(value = "离职原因")
    private String quitReason;

}
