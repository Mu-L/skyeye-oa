/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.entity.explain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: ExExplain
 * @Description: 说明设置实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/6/22 14:43
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = {"type"})
@TableName(value = "ex_explain")
@ApiModel("说明设置实体类")
public class ExExplain extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "title")
    @ApiModelProperty(value = "标题", required = "required")
    private String title;

    @TableField(value = "content")
    @ApiModelProperty(value = "说明内容", required = "required")
    private String content;

    @TableField(value = "type")
    @ApiModelProperty(value = "类型", required = "required,num")
    private Integer type;

}
