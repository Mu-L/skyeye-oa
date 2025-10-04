/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.lifecycle.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: LifecycleTemplateNode
 * @Description: 生命周期模板节点实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/3 20:45
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "lifecycle_template_node", autoResultMap = true)
@ApiModel("生命周期模板节点实体类")
public class LifecycleTemplateNode extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "node_id")
    @ApiModelProperty(value = "节点id", required = "required")
    private String nodeId;

    @TableField(value = "type")
    @ApiModelProperty(value = "节点类型", required = "required")
    private String type;

    @TableField(value = "template_id")
    @Property(value = "模板id")
    private String templateId;

    @TableField(value = "position", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "位置")
    private Map<String, Object> position;

    @TableField(value = "style", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "样式")
    private Map<String, Object> style;

}
