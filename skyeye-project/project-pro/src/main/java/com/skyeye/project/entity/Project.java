/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.project.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.base.handler.enclosure.bean.Enclosure;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: Project
 * @Description: 项目实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/1 15:52
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@UniqueField
@RedisCacheField(name = CacheConstants.PM_PROJECT_CACHE_KEY, cacheTime = RedisConstants.A_YEAR_SECONDS)
@TableName(value = "pro_project", autoResultMap = true)
@ApiModel("项目实体类")
public class Project extends SkyeyeFlowable {

    @TableField("`name`")
    @ApiModelProperty(value = "名称", required = "required")
    private String name;

    @TableField(value = "number_code")
    @ApiModelProperty(value = "项目编号")
    private String numberCode;

    @TableField(value = "type_id")
    @ApiModelProperty(value = "项目分类，数据来源数据字典", required = "required")
    private String typeId;

    @TableField(value = "department_id")
    @ApiModelProperty(value = "所属部门id")
    private String departmentId;

    @TableField(exist = false)
    @Property(value = "所属部门信息")
    private Map<String, Object> departmentMation;

    @TableField("start_time")
    @ApiModelProperty(value = "计划开始时间", required = "required")
    private String startTime;

    @TableField("end_time")
    @ApiModelProperty(value = "计划结束时间", required = "required")
    private String endTime;

    @TableField("holder_id")
    @ApiModelProperty(value = "关联的客户/供应商id", required = "required")
    private String holderId;

    @TableField(exist = false)
    @Property(value = "关联的客户/供应商")
    private Map<String, Object> holderMation;

    @TableField("holder_key")
    @ApiModelProperty(value = "关联的客户/供应商的className")
    private String holderKey;

    @TableField(value = "estimated_workload")
    @ApiModelProperty(value = "预估工作量")
    private String estimatedWorkload;

    @TableField(value = "estimated_cost")
    @ApiModelProperty(value = "预估成本费用", required = "double", defaultValue = "0")
    private String estimatedCost;

    @TableField(value = "business_content")
    @ApiModelProperty(value = "业务需求和目标", required = "required")
    private String businessContent;

    @TableField(value = "project_content")
    @ApiModelProperty(value = "项目组织和分工")
    private String projectContent;

    @TableField(value = "plan_content")
    @ApiModelProperty(value = "实施计划和方案")
    private String planContent;

    @TableField("actual_start_time")
    @ApiModelProperty(value = "实际开始时间，项目完结时必填")
    private String actualStartTime;

    @TableField("actual_end_time")
    @ApiModelProperty(value = "实际结束时间，项目完结时必填")
    private String actualEndTime;

    @TableField("results_content")
    @ApiModelProperty(value = "总结，项目完结时必填")
    private String resultsContent;

    @TableField("speed_of_progress")
    @ApiModelProperty(value = "实施进度  默认为0，最大为100", defaultValue = "0")
    private Integer speedOfProgress;

    @TableField(value = "team_template_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "团队模板id")
    private String teamTemplateId;

    @TableField(exist = false)
    @ApiModelProperty(value = "项目组织附件", required = "json")
    private Enclosure projectEnclosureInfo;

    @TableField(exist = false)
    @ApiModelProperty(value = "实施计划附件", required = "json")
    private Enclosure planEnclosureInfo;

    @TableField(exist = false)
    @ApiModelProperty(value = "项目成果附件，项目完结时填写", required = "json")
    private Enclosure resultsEnclosureInfo;

}
