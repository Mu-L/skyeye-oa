/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import com.skyeye.patrol.classenum.PatrolItemSummaryType;
import lombok.Data;

/**
 * @ClassName: PatrolRecord
 * @Description: 巡检记录实体类
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "seal:patrol:record", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "crm_service_patrol_record", autoResultMap = true)
@ApiModel("巡检记录实体类")
public class PatrolRecord extends SkyeyeFlowable {

    @TableField(value = "odd_number")
    @Property(value = "记录编号", fuzzyLike = true)
    private String oddNumber;

    @TableField(value = "task_id")
    @ApiModelProperty(value = "巡检任务ID", required = "required")
    private String taskId;

    @TableField(exist = false)
    @Property(value = "任务信息")
    private PatrolTask taskMation;

    @TableField(value = "item_id")
    @ApiModelProperty(value = "巡检项目ID", required = "required")
    private String itemId;

    @TableField(exist = false)
    @Property(value = "项目信息")
    private PatrolItem itemMation;

    @TableField(value = "check_result")
    @ApiModelProperty(value = "检查结果", enumClass = PatrolItemSummaryType.class, required = "required,num")
    private Integer checkResult;

    @TableField(value = "check_content")
    @ApiModelProperty(value = "检查内容/描述")
    private String checkContent;

    @TableField(value = "check_images")
    @ApiModelProperty(value = "检查图片（多个图片URL，用逗号分隔）")
    private String checkImages;

}

