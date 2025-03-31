/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.score.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ScoreSum
 * @Description: 总成绩实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/29 10:53
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName("school_score_sum")
@ApiModel(value = "总成绩实体类")
public class ScoreSum extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("score")
    @ApiModelProperty(value = "成绩", required = "required")
    private String score;

    @TableField("proportion")
    @ApiModelProperty(value = "占比(80即占比80%)")
    private String proportion;

    @TableField("object_id")
    @ApiModelProperty(value = "第三方业务数据id(成绩类型主表id或成绩类型子表id)", required = "required")
    private String objectId;

    @TableField(exist = false)
    @ApiModelProperty(value = "第三方业务数据信息")
    private Map<String, Object> objectMation;

    @TableField("stu_no")
    @ApiModelProperty(value = "学号", required = "required")
    private String stuNo;

    @TableField(exist = false)
    @ApiModelProperty(value = "学生信息")
    private Map<String, Object> stuMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "成绩部分集合")
    private List<ScorePart> scorePartList;
}
