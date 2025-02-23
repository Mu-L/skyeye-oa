package com.skyeye.eve.order.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * @ClassName: ExamAnOrder
 * @Description: 答卷 评分题实体类
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
//@RedisCacheField(name = "Dw:order")
@TableName(value = "dw_an_order")
@ApiModel("答卷 评分题实体类")
public class DwAnOrder extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("belong_answer_id")
    @ApiModelProperty(value = "所属评分题答案id")
    private String belongAnswerId;

    @TableField("belong_id")
    @ApiModelProperty(value = "所属试卷id")
    private String belongId;

    @TableField("ordery_num")
    @ApiModelProperty(value = "订单编号")
    private String orderyNum;

    @TableField("qu_id")
    @ApiModelProperty(value = "所属评分题")
    private String quId;

    @TableField("qu_row_id")
    @ApiModelProperty(value = "所属评分题行id")
    private String quRowId;

    @TableField("visibility")
    @ApiModelProperty(value = "是否显示  0不显示  1显示")
    private Integer visibility;
}
