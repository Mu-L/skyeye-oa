/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.eve.forum.classenum.ExamineStateEnum;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: ForumReport
 * @Description: 论坛举报实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName("forum_report")
@RedisCacheField(name = "forum:report", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@ApiModel(value = "论坛举报实体类")
public class ForumReport extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "forum_id")
    @ApiModelProperty(value = "所属话题id", required = "required")
    private String forumId;

    @TableField(exist = false)
    @Property(value = "所属话题信息")
    private Map<String, Object> forumMation;

    @TableField(value = "report_type_id")
    @ApiModelProperty(value = "举报类型id", required = "required")
    private String reportTypeId;

    @TableField(exist = false)
    @Property(value = "举报类型信息")
    private Map<String, Object> reportTypeMation;

    @TableField(value = "report_other_content")
    @ApiModelProperty(value = "举报类型为'其他'时，需要填写内容")
    private String reportOtherContent;

    @TableField(value = "report_desc")
    @ApiModelProperty(value = "备注")
    private String reportDesc;

    @TableField(value = "examine_state")
    @ApiModelProperty(value = "默认为1 .审核状态  1.未审核  2.审核通过  3.审核不通过", enumClass = ExamineStateEnum.class)
    private Integer examineState;

    @TableField(value = "examine_nopass_reason")
    @ApiModelProperty(value = "审核不通过原因")
    private String examineNopassReason;

    @TableField(value = "examine_id")
    @ApiModelProperty(value = "审核人id")
    private String examineId;

    @TableField(exist = false)
    @Property(value = "审核人信息")
    private Map<String, Object> examineMation;

    @TableField(value = "examine_time")
    @ApiModelProperty(value = "审核时间")
    private String examineTime;

    @TableField(value = "report_id")
    @ApiModelProperty(value = "举报人id")
    private String reportId;

    @TableField(exist = false)
    @Property(value = "举报人信息")
    private Map<String, Object> reportMation;

    @TableField(value = "report_time")
    @Property(value = "举报时间")
    private String reportTime;
}
