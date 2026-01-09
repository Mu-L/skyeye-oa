/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ordertype.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: SealOrderTypeAllowStaff
 * @Description: 工单类型允许的接单人实体类
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/24
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "crm_service_order_type_allow_staff", autoResultMap = true)
@ApiModel("工单类型允许的接单人实体类")
public class SealOrderTypeAllowStaff extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "order_type_id")
    @ApiModelProperty(value = "工单类型ID", required = "required")
    private String orderTypeId;

    @TableField(value = "staff_id")
    @ApiModelProperty(value = "允许接单的员工ID", required = "required")
    private String staffId;

}

