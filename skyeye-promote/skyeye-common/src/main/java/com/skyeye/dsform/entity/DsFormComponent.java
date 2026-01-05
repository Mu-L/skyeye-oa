/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dsform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.IconOrImgInfo;
import com.skyeye.dsform.classenum.ComponentApplyRange;
import com.skyeye.dsform.classenum.ComponentValueMergType;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: DsFormComponent
 * @Description: 表单组件实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/3 20:45
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = {"numCode"})
@RedisCacheField(name = "dsForm:component")
@TableName(value = "ds_form_component", autoResultMap = true)
@ApiModel("表单组件实体类")
public class DsFormComponent extends IconOrImgInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("num_code")
    @ApiModelProperty(value = "组件编码", required = "required", fuzzyLike = true)
    private String numCode;

    @TableField("`name`")
    @ApiModelProperty(value = "组件名称", required = "required", fuzzyLike = true)
    private String name;

    @TableField("type_id")
    @ApiModelProperty(value = "组件分类", required = "required")
    private String typeId;

    @TableField(exist = false)
    @Property("组件分类名称")
    private String typeName;

    @TableField("linked_data")
    @ApiModelProperty(value = "关联数据 1.是 2.否", required = "required,num")
    private Integer linkedData;

    @TableField("apply_range")
    @ApiModelProperty(value = "适用范围", enumClass = ComponentApplyRange.class, required = "required,num")
    private Integer applyRange;

    @TableField(value = "apply_object", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "局部适用对象", required = "json")
    private List<String> applyObject;

    @TableField("value_merg_type")
    @ApiModelProperty(value = "组件获取的值的合入接口入参的方式", enumClass = ComponentValueMergType.class, required = "required")
    private String valueMergType;

    @TableField("remark")
    @ApiModelProperty(value = "组件备注")
    private String remark;

}
