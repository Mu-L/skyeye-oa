package com.skyeye.exam.examanorder.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: ExamAnOrder
 * @Description: 答卷 评分题实体类
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
//@RedisCacheField(name = "Exam:order")
@TableName(value = "exam_an_order")
@ApiModel("答卷 排序题实体类")
public class ExamAnOrder extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("belong_answer_id")
    @ApiModelProperty(value = "所属排序题答案id")
    private String belongAnswerId;

    @TableField("belong_id")
    @ApiModelProperty(value = "所属试卷id")
    private String belongId;

    @TableField("ordery_num")
    @ApiModelProperty(value = "评分的分数")
    private Integer orderyNum;

    @TableField("qu_id")
    @ApiModelProperty(value = "所属排序题")
    private String quId;

    @TableField("qu_item_id")
    @ApiModelProperty(value = "结果选项ID")
    private String quItemId;

    @TableField(exist = false)
    @ApiModelProperty(value = "排序题答案信息")
    private List<ExamAnOrder> orderByAn;

    @TableField("visibility")
    @ApiModelProperty(value = "是否显示  0不显示  1显示")
    private Integer visibility;
}
