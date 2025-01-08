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
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: Accident
 * @Description: 用车事故实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/5/3 18:16
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "assistant:vehicle:accident", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "vehicle_accident")
@ApiModel("用车事故实体类")
public class Accident extends BaseGeneralInfo {

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

    @TableField("accident_time")
    @ApiModelProperty(value = "事故时间", required = "required")
    private String accidentTime;

    @TableField("accident_area")
    @ApiModelProperty(value = "事故地点", required = "required")
    private String accidentArea;

    @TableField("accident_detail")
    @ApiModelProperty(value = "事故详情")
    private String accidentDetail;

    @TableField("confirmation_responsibility")
    @ApiModelProperty(value = "责任认定")
    private String confirmationResponsibility;

    @TableField("manufacturer")
    @ApiModelProperty(value = "维修厂家")
    private String manufacturer;

    @TableField("repair_start_time")
    @ApiModelProperty(value = "送修开始时间")
    private String repairStartTime;

    @TableField("repair_end_time")
    @ApiModelProperty(value = "送修结束时间")
    private String repairEndTime;

    @TableField("repair_price")
    @ApiModelProperty(value = "维修费用", required = "double", defaultValue = "0")
    private String repairPrice;

    @TableField("repair_content")
    @ApiModelProperty(value = "维修内容")
    private String repairContent;

    @TableField("loss_fee_price")
    @ApiModelProperty(value = "车损费", required = "double", defaultValue = "0")
    private String lossFeePrice;

    @TableField("claims_fee_price")
    @ApiModelProperty(value = "保险理赔金额", required = "double", defaultValue = "0")
    private String claimsFeePrice;

    @TableField("driver_bear_price")
    @ApiModelProperty(value = "驾驶员承担费用", required = "double", defaultValue = "0")
    private String driverBearPrice;

}
