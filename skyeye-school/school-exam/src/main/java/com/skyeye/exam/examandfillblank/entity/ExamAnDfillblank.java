package com.skyeye.exam.examandfillblank.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * @ClassName: ExamAnDfillblank
 * @Description: 答卷 多行填空题保存表实体类
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Data
@RedisCacheField(name = "Exam:dfillblank")
@TableName(value = "exam_an_dfillblank")
@ApiModel("答卷 多行填空题保存表实体类")
public class ExamAnDfillblank extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("answer")
    @ApiModelProperty(value = "多行填空题答案")
    private String answer;

    @TableField("belong_answer_id")
    @ApiModelProperty(value = "所属多行填空题答案id")
    private String belongAnswerId;

    @TableField("belong_id")
    @ApiModelProperty(value = "所属试卷id")
    private String belongId;

    @TableField("qu_id")
    @ApiModelProperty(value = "所属多行填空题")
    private String quId;

    @TableField("qu_item_id")
    @ApiModelProperty(value = "项目编号")
    private String quItemId;

    @TableField("visibility")
    @ApiModelProperty(value = "是否显示  0不显示  1显示")
    private Integer visibility;



}
