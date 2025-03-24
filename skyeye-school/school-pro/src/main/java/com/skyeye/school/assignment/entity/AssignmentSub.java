/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.assignment.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.base.handler.enclosure.bean.Enclosure;
import com.skyeye.common.base.handler.enclosure.bean.EnclosureFace;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: AssignmentSub
 * @Description: 作业提交实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/2 11:00
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "school:assignmentSub", cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "school_assignment_sub")
@ApiModel(value = "作业提交实体类")
public class AssignmentSub extends OperatorUserInfo implements EnclosureFace {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "assignment_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "作业id", required = "required")
    private String assignmentId;

    @TableField(value = "state")
    @Property(value = "批阅状态，参考#AssignmentCorrectState")
    private String state;

    @TableField(value = "content")
    @ApiModelProperty(value = "内容")
    private String content;

    @TableField(value = "score")
    @ApiModelProperty(value = "得分")
    private String score;

    @TableField(value = "comment")
    @ApiModelProperty(value = "评语")
    private String comment;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件", required = "json")
    private Enclosure enclosureInfo;

}
