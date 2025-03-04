/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.jobdiary.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.base.handler.enclosure.bean.Enclosure;
import com.skyeye.common.base.handler.enclosure.bean.EnclosureFace;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.eve.jobdiary.classenum.JobDiaryState;
import com.skyeye.eve.jobdiary.classenum.JobDiaryType;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: JobDiary
 * @Description: 工作日志实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/24 11:52
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@UniqueField(value = {"name", "createId"})
@RedisCacheField(name = "job:diary", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "job_diary")
@ApiModel("工作日志实体类")
public class JobDiary extends OperatorUserInfo implements EnclosureFace {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "`name`")
    @ApiModelProperty(value = "名称")
    private String name;

    @TableField(value = "remark")
    @ApiModelProperty(value = "相关描述")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件", required = "json")
    private Enclosure enclosureInfo;

    @TableField(value = "completed_job")
    @ApiModelProperty(value = "已完成工作", required = "required")
    private String completedJob;

    @TableField(value = "incomplete_job")
    @ApiModelProperty(value = "未完成工作")
    private String incompleteJob;

    @TableField(value = "coordina_job")
    @ApiModelProperty(value = "需协调工作")
    private String coordinaJob;

    @TableField(value = "work_summary")
    @ApiModelProperty(value = "工作总结")
    private String workSummary;

    @TableField(value = "state")
    @Property(value = "状态", enumClass = JobDiaryState.class)
    private Integer state;

    @TableField(value = "type")
    @ApiModelProperty(value = "日志类型", enumClass = JobDiaryType.class, required = "required,num")
    private Integer type;

    @TableField(value = "delete_flag")
    private Integer deleteFlag;

    @TableField(exist = false)
    @ApiModelProperty(value = "接收人id", required = "required,json")
    private List<String> receivedId;

    @TableField(exist = false)
    @Property(value = "接收人信息")
    private List<Map<String, Object>> receivedMation;

}
