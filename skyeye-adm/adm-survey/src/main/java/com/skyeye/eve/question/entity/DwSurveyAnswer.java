/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.question.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: DwSurveyAnswer
 * @Description:问卷回答信息表实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:35
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
//@UniqueField
@TableName(value = "dw_survey_answer")
@ApiModel(value = "问卷回答信息表实体类")
public class DwSurveyAnswer extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("survey_id")
    @ApiModelProperty(value = "问卷ID", required = "required")
    private String surveyId;

    @TableField("bg_an_date")
    @ApiModelProperty(value = "回答开始时间", required = "required")
    private String bgAnDate;

    @TableField("end_an_date")
    @ApiModelProperty(value = "回答结束时间", required = "required")
    private String endAnDate;

    @TableField("complete_num")
    @ApiModelProperty(value = "回答的题数")
    private Integer completeNum;

    @TableField("complete_item_num")
    @ApiModelProperty(value = "回答的题项目数 ---- 表示有些题下面会有多重回答")
    private Integer completeItemNum;

    @TableField("data_source")
    @ApiModelProperty(value = "数据来源  0网调  1录入数据 2移动数据 3导入数据", required = "required")
    private Integer dataSource;

    @TableField("handle_state")
    @ApiModelProperty(value = "审核状态  0未处理 1通过 2不通过", required = "required")
    private Integer handleState;

    @TableField("ip_addr")
    @ApiModelProperty(value = "回答者IP")
    private String ipAddr;

    @TableField("addr")
    @ApiModelProperty(value = "回答者是详细地址")
    private String addr;

    @TableField("city")
    @ApiModelProperty(value = "回答者城市 ")
    private String city;

    @TableField("is_complete")
    @ApiModelProperty(value = "是否完成  1完成 0未完成")
    private Integer isComplete;

    @TableField("is_effective")
    @ApiModelProperty(value = "是否是有效数据  1有效  0无效")
    private Integer isEffective;

    @TableField("pc_mac")
    @ApiModelProperty(value = "回答者MAC")
    private String pcMac;

    @TableField("qu_num")
    @ApiModelProperty(value = "回答的题数")
    private Integer quNum;

    @TableField("total_time")
    @ApiModelProperty(value = "用时")
    private Float totalTime;

    @TableField("create_id")
    @ApiModelProperty(value = "回答者ID")
    private String createId;

    @TableField("state")
    @ApiModelProperty(value = "教师是否阅卷  1.否  2.是")
    private Integer state;

    @TableField("mark_fraction")
    @ApiModelProperty(value = "最后得分")
    private Integer markFraction;

    @TableField("mark_people")
    @ApiModelProperty(value = "阅卷人")
    private Integer markPeople;

    @TableField("mark_start_time")
    @ApiModelProperty(value = "开始阅卷时间")
    private String markStartTime;

    @TableField("mark_end_time")
    @ApiModelProperty(value = "结束阅卷时间")
    private String markEndTime;

    @TableField(exist = false)
    @Property(value = "问卷信息")
    private DwSurveyDirectory surveyMation;

}


