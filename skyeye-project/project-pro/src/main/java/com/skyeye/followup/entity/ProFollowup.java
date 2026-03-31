package com.skyeye.followup.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.followup.classenum.FollowupPriorityEnum;
import com.skyeye.followup.classenum.FollowupStateEnum;
import com.skyeye.followup.classenum.FollowupTypeEnum;
import com.skyeye.project.entity.Project;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: ProFollowup
 * @Description: 项目跟进实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "pm:projectFollowup", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "pro_followup")
@ApiModel("项目跟进实体类")
public class ProFollowup extends BaseGeneralInfo {

    @TableField(value = "odd_number", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "单据编号", fuzzyLike = true)
    private String oddNumber;

    @TableField(value = "project_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "项目id", required = "required")
    private String projectId;

    @TableField(exist = false)
    @Property(value = "项目信息")
    private Project projectMation;

    @TableField(value = "followup_content")
    @ApiModelProperty(value = "跟进内容", required = "required")
    private String followupContent;

    @TableField(value = "followup_time")
    @ApiModelProperty(value = "跟进时间", required = "required")
    private String followupTime;

    @TableField(value = "followup_person_id")
    @ApiModelProperty(value = "跟进人id", required = "required")
    private String followupPersonId;

    @TableField(exist = false)
    @Property(value = "跟进人信息")
    private Map<String, Object> followupPersonMation;

    @TableField(value = "state")
    @ApiModelProperty(value = "跟进状态", enumClass = FollowupStateEnum.class)
    private String state;

    @TableField(value = "next_followup_time")
    @ApiModelProperty(value = "下次跟进时间")
    private String nextFollowupTime;

    @TableField(value = "reminder_enabled")
    @ApiModelProperty(value = "是否启用提醒", enumClass = WhetherEnum.class, required = "required,num")
    private Integer reminderEnabled;

    @TableField(value = "reminder_time")
    @ApiModelProperty(value = "提醒时间")
    private String reminderTime;

    @TableField(value = "contact_person")
    @ApiModelProperty(value = "联系人")
    private String contactPerson;

    @TableField(value = "contact_phone")
    @ApiModelProperty(value = "联系电话")
    private String contactPhone;

    @TableField(value = "contact_result")
    @ApiModelProperty(value = "联系结果")
    private String contactResult;

    @TableField(value = "followup_type")
    @ApiModelProperty(value = "跟进类型", enumClass = FollowupTypeEnum.class)
    private String followupType;

    @TableField(value = "priority")
    @ApiModelProperty(value = "优先级", enumClass = FollowupPriorityEnum.class)
    private String priority;

}