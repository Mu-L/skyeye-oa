/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.multifllblank.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;


/**
 * @ClassName: DwQuMultiFillblank
 * @Description: 多行填空题实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:35
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
//@UniqueField
@TableName(value = "dw_qu_multi_fillblank")
@ApiModel(value = "多行填空题实体类")
public class DwQuMultiFillblank extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("qu_id")
    @ApiModelProperty(value = "所属题ID")
    private String quId;

    @TableField("belong_id")
    @ApiModelProperty(value = "所属问卷id")
    private String belongId;

    @TableField("option_name")
    @ApiModelProperty(value = "选项问题")
    private String optionName;

    @TableField("option_title")
    @ApiModelProperty(value = "选项标题")
    private String optionTitle;

    @TableField("check_type")
    @ApiModelProperty(value = "说明的验证方式")
    private Integer checkType;

    @TableField("order_by_id")
    @ApiModelProperty(value = "排序ID")
    private Integer orderById;

    @TableField("visibility")
    @ApiModelProperty(value = "是否显示  0不显示  1显示")
    private Integer visibility;

    @TableField("is_default_answer")
    @ApiModelProperty(value = "是否是默认答案  1.是  2.否")
    private String isDefaultAnswer;

    @TableField(exist = false)
    @ApiModelProperty(value = "选项id")
    private String optionId;

}
