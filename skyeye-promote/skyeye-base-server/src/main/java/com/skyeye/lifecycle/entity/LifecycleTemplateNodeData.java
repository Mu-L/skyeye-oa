/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.lifecycle.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: LifecycleTemplateNodeData
 * @Description: 生命周期模板节点数据实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/10/4 14:31
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@TableName(value = "lifecycle_template_node_data", autoResultMap = true)
@ApiModel("生命周期模板节点数据实体类")
public class LifecycleTemplateNodeData extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "node_id")
    @Property(value = "节点id")
    private String nodeId;

    @TableField(value = "node_type")
    @ApiModelProperty(value = "节点类型", required = "required")
    private String nodeType;

    @TableField(value = "template_id")
    @Property(value = "模板id")
    private String templateId;

    @TableField(value = "label")
    @ApiModelProperty(value = "名称")
    private String label;

    @TableField(value = "description")
    @ApiModelProperty(value = "描述")
    private String description;

    @TableField(value = "state")
    @ApiModelProperty(value = "状态")
    private String state;

    @TableField(exist = false)
    @Property(value = "状态信息")
    private LifecycleState stateMation;

    @TableField(value = "process_type")
    @ApiModelProperty(value = "流程类型")
    private String processType;

    @TableField(value = "process_id")
    @ApiModelProperty(value = "流程id")
    private String processId;

}
