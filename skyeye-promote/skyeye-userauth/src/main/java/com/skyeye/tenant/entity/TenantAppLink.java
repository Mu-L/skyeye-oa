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
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * @ClassName: TenantAppLink
 * @Description: 租户与应用的关系管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/29 16:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "tenant_app_link")
@ApiModel("租户与应用的关系管理实体类")
public class TenantAppLink extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "buy_tenant_id")
    @Property(value = "租户id")
    private String buyTenantId;

    @TableField(value = "app_id")
    @Property(value = "应用id")
    private String appId;

    @TableField(exist = false)
    @Property("应用信息")
    private TenantApp appMation;

    @TableField(value = "start_time")
    @Property(value = "有效期开始时间")
    private String startTime;

    @TableField(value = "end_time")
    @Property(value = "有效期结束时间")
    private String endTime;

}
