package com.skyeye.exam.examSurveyAnswer.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: ExamSurveyAnswer
 * @Description: 试卷回答信息表实体类
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "Exam:answer")
@TableName(value = "exam_survey_answer")
@ApiModel("试卷回答信息表实体类")
public class ExamSurveyAnswer extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("survey_id")
    @ApiModelProperty(value = "问卷ID", required = "required")
    private String surveyId;

    @TableField("bg_an_date")
    @ApiModelProperty(value = "回答开始时间", required = "required")
    private LocalDateTime bgAnDate;

    @TableField("end_an_date")
    @ApiModelProperty(value = "回答结束时间", required = "required")
    private LocalDateTime endAnDate;

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
    @ApiModelProperty(value = "是否完成  1完成 0未完成", required = "required")
    private Integer isComplete;

    @TableField("is_effective")
    @ApiModelProperty(value = "是否是有效数据  1有效  0无效", required = "required")
    private Integer isEffective;

    @TableField("qu_num")
    @ApiModelProperty(value = "回答的题数", required = "required")
    private Integer quNum;

    @TableField("total_time")
    @ApiModelProperty(value = "用时", required = "required")
    private Float totalTime;

    @TableField("create_id")
    @ApiModelProperty(value = "学生ID", required = "required")
    private String createId;

    @TableField("state")
    @ApiModelProperty(value = "教师是否阅卷  1.否  2.是", required = "required")
    private Integer state;

    @TableField("mark_fraction")
    @ApiModelProperty(value = "最后得分")
    private Integer markFraction;

    @TableField("mark_people")
    @ApiModelProperty(value = "阅卷人")
    private String markPeople;

    @TableField("mark_start_time")
    @ApiModelProperty(value = "开始阅卷时间")
    private LocalDateTime markStartTime;

    @TableField("mark_end_time")
    @ApiModelProperty(value = "结束阅卷时间")
    private LocalDateTime markEndTime;
}