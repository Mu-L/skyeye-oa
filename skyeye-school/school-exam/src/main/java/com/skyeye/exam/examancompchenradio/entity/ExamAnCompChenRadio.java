package com.skyeye.exam.examancompchenradio.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * @ClassName: ExamAnCompChenRadio
 * @Description: 答卷 复合矩阵单选题实体类
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Data
@RedisCacheField(name = "Exam:radio")
@TableName(value = "exam_an_comp_chen_radio")
@ApiModel("答卷 复合矩阵单选题实体类")
public class ExamAnCompChenRadio extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableId("belong_answer_id")
    @ApiModelProperty(value = "所属复合矩阵单选题答案id")
    private String belongAnswerId;

    @TableId("belong_id")
    @ApiModelProperty(value = "所属复合矩阵单选题id")
    private String belongId;

    @TableId("qu_col_id")
    @ApiModelProperty(value = "所属复合矩阵单选题列id")
    private String quColId;

    @TableId("qu_id")
    @ApiModelProperty(value = "所属复合矩阵单选题")
    private String quId;

    @TableId("qu_option_id")
    @ApiModelProperty(value = "请求选项标识")
    private String quOptionId;

    @TableId("qu_row_id")
    @ApiModelProperty(value = "所属复合矩阵单选题行id")
    private String quRowId;

    @TableId("visibility")
    @ApiModelProperty(value = "是否显示  0不显示  1显示")
    private Integer visibility;
}
