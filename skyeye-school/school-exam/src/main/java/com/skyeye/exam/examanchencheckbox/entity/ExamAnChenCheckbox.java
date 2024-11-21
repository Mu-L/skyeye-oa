package com.skyeye.exam.examanchencheckbox.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * @ClassName: ExamAnChenCheckbox
 * @Description: 答卷 矩阵多选题实体类
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Data
@RedisCacheField(name = "Exam:checkbox")
@TableName(value = "exam_an_chen_checkbox")
@ApiModel("答卷 矩阵多选题实体类")
public class ExamAnChenCheckbox extends CommonInfo {
    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("belong_answer_id")
    @ApiModelProperty(value = "所属矩阵多选题答案id")
    private String belongAnswerId;

    @TableField("belong_id")
    @ApiModelProperty(value = "所属矩阵多选题id")
    private String belongId;

    @TableField("qu_id")
    @ApiModelProperty(value = "所属矩阵多选题")
    private String quId;

    @TableField("qu_col_id")
    @ApiModelProperty(value = "所属矩阵多选题列id")
    private String quColId;

    @TableField("qu_row_id")
    @ApiModelProperty(value = "所属矩阵多选题行id")
    private String quRowId;

    @TableField("visibility")
    @ApiModelProperty(value = "是否显示  0不显示  1显示")
    private Integer visibility;
}
