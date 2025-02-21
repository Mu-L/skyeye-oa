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

import java.util.List;

/**
 * @ClassName: Insurance
 * @Description: 车辆保险实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/5/3 18:16
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "assistant:vehicle:insurance", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "vehicle_insurance")
@ApiModel("车辆保险实体类")
public class Insurance extends BaseGeneralInfo {

    @TableField("vehicle_id")
    @ApiModelProperty(value = "车辆id", required = "required")
    private String vehicleId;

    @TableField(exist = false)
    @Property(value = "车辆信息")
    private Vehicle vehicleMation;

    @TableField("insurance_company")
    @ApiModelProperty(value = "投保公司", required = "required")
    private String insuranceCompany;

    @TableField("insured_telephone")
    @ApiModelProperty(value = "投保电话", required = "phone")
    private String insuredTelephone;

    @TableField("validity_start_time")
    @ApiModelProperty(value = "投保有效期开始时间", required = "required")
    private String validityStartTime;

    @TableField("validity_end_time")
    @ApiModelProperty(value = "投保有效期结束时间", required = "required")
    private String validityEndTime;

    @TableField("insurance_all_price")
    @Property(value = "投保总费用")
    private String insuranceAllPrice;

    @TableField(exist = false)
    @ApiModelProperty(value = "保险险种信息", required = "required,json")
    private List<InsuranceCoverage> vehicleInsuranceCoverages;

}
