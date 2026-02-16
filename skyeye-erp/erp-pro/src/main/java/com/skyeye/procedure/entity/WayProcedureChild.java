/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.procedure.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.machinprocedure.entity.MachinProcedure;
import lombok.Data;

/**
 * @ClassName: WayProcedureChild
 * @Description: 工艺路线关联的工序实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/24 13:05
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = CacheConstants.MES_WAY_PROCEDURE_CACHE_KEY)
@TableName(value = "erp_way_procedure_child")
@ApiModel("工艺路线关联的工序实体类")
public class WayProcedureChild extends CommonInfo {

    @TableId("id")
    private String id;

    @TableField(exist = false)
    @Property(value = "新的id")
    private String newId;

    @TableField(value = "way_id")
    @Property(value = "工艺id")
    private String wayId;

    @TableField(value = "procedure_id")
    @ApiModelProperty(value = "工序id", required = "required")
    private String procedureId;

    @TableField(exist = false)
    @Property(value = "工序信息")
    private WorkProcedure procedureMation;

    @TableField(value = "order_by")
    @ApiModelProperty(value = "工序排序  值越大越往后", required = "required,num")
    private Integer orderBy;

    @TableField(value = "price")
    @ApiModelProperty(value = "单价", required = "required,double", defaultValue = "0")
    private String price;

    @TableField(value = "quota_capacity")
    @ApiModelProperty(value = "定额能力（每小时件数）", required = "required,num")
    private Integer quotaCapacity;

    @TableField(value = "homework_ability")
    @ApiModelProperty(value = "作业人数", required = "required,num")
    private Integer homeworkAbility;

    /**
     * 标准工时(分钟/件)，保存时由服务层根据定额能力自动计算：60/定额能力。
     * 用于APS排产：加工时长(分钟) = 数量 * standardTimeMinutes。
     */
    @TableField(value = "standard_time_minutes")
    @ApiModelProperty(value = "标准工时(分钟/件)，用于APS排产")
    private String standardTimeMinutes;

    @TableField(exist = false)
    @Property(value = "加工单子单据关联的工序信息----加工单特有")
    private MachinProcedure machinProcedureMation;

}
