/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.vehicle.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: VehicleUse
 * @Description: 用车申请实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/5/3 18:16
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "assistant:vehicle:use", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "vehicle_use")
@ApiModel("用车申请实体类")
public class VehicleUse extends SkyeyeFlowable {

    @TableField("title")
    @ApiModelProperty(value = "标题", required = "required", fuzzyLike = true)
    private String title;

    @TableField("vehicle_id")
    @ApiModelProperty(value = "车辆id", required = "required")
    private String vehicleId;

    @TableField(exist = false)
    @Property(value = "车辆信息")
    private Vehicle vehicleMation;

    @TableField("driver_id")
    @ApiModelProperty(value = "驾驶员id")
    private String driverId;

    @TableField(exist = false)
    @Property(value = "驾驶员信息")
    private Map<String, Object> driverMation;

    @TableField("passenger_num")
    @ApiModelProperty(value = "乘车人数", required = "required,num")
    private Integer passengerNum;

    @TableField("departure_time")
    @ApiModelProperty(value = "出发时间", required = "required")
    private String departureTime;

    @TableField("return_time")
    @ApiModelProperty(value = "返回时间", required = "required")
    private String returnTime;

    @TableField("reasons_for_using_car")
    @ApiModelProperty(value = "用车事由", required = "required")
    private String reasonsForUsingCar;

    @TableField("remark")
    @ApiModelProperty(value = "描述")
    private String remark;

}
