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
import com.skyeye.common.enumeration.LifecycleTemplateEdgesType;
import com.skyeye.common.enumeration.WhetherEnum;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: LifecycleTemplateEdges
 * @Description: 生命周期模板连线实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/3 20:45
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "lifecycle_template_edges", autoResultMap = true)
@ApiModel("生命周期模板连线实体类")
public class LifecycleTemplateEdges extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "edges_id")
    @ApiModelProperty(value = "连线id", required = "required")
    private String edgesId;

    @TableField(value = "source")
    @ApiModelProperty(value = "起始节点id")
    private String source;

    @TableField(value = "target")
    @ApiModelProperty(value = "目标节点id")
    private String target;

    @TableField(value = "type")
    @ApiModelProperty(value = "设计器默认的连线类型", required = "required")
    private String type;

    @TableField(value = "edge_type")
    @ApiModelProperty(value = "后端存储的连线类型", enumClass = LifecycleTemplateEdgesType.class, required = "required")
    private String edgeType;

    @TableField(value = "animated")
    @ApiModelProperty(value = "是否开启动画效果", enumClass = WhetherEnum.class)
    private Integer animated;

    @TableField(value = "template_id")
    @Property(value = "模板id")
    private String templateId;

    @TableField(value = "marker_end", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "结束箭头样式")
    private Map<String, Object> markerEnd;

    @TableField(value = "style", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "样式")
    private Map<String, Object> style;

    @TableField(value = "label")
    @ApiModelProperty(value = "名称")
    private String label;

    @TableField(value = "label_style", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "标签样式")
    private Map<String, Object> labelStyle;

    @TableField(value = "label_bg_style", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "标签背景样式")
    private Map<String, Object> labelBgStyle;

    @TableField(exist = false)
    @ApiModelProperty(value = "节点数据")
    private LifecycleTemplateEdgesData data;

}
