/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.farm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: FarmStaff
 * @Description: 车间与员工的关系实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 21:32
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "erp_farm_staff", autoResultMap = true)
@ApiModel("车间与员工的关系实体类")
public class FarmStaff extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "farm_id")
    @ApiModelProperty(value = "车间ID", required = "required")
    private String farmId;

    @TableField(value = "staff_id")
    @ApiModelProperty(value = "员工ID", required = "required")
    private String staffId;

    @TableField(exist = false)
    @Property(value = "员工信息")
    private Map<String, Object> staffMation;

    @TableField(value = "farm_station_id")
    @ApiModelProperty(value = "员工所在岗位ID")
    private String farmStationId;

    @TableField(exist = false)
    @Property(value = "员工所在岗位信息")
    private Map<String, Object> farmStationMation;

}
