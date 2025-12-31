/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.eve.field.entity.FieldType;
import com.skyeye.eve.model.classenum.WagesModelFieldType;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: WagesModelField
 * @Description: 薪资模板关联的字段实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/21 13:51
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@ApiModel("薪资模板关联的字段实体类")
@TableName(value = "wages_model_field")
public class WagesModelField extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("model_id")
    @Property(value = "模板id")
    private String modelId;

    @TableField("field_key")
    @ApiModelProperty(value = "字段key", required = "required")
    private String fieldKey;

    @TableField(exist = false)
    @Property(value = "字段信息")
    private FieldType fieldKeyMation;

    @TableField("field_type")
    @ApiModelProperty(value = "字段类型", enumClass = WagesModelFieldType.class, required = "required,num")
    private Integer fieldType;

    @TableField(exist = false)
    @Property(value = "字段类型信息")
    private Map<String, Object> fieldTypeMation;

    @TableField("default_money")
    @ApiModelProperty(value = "默认金额", required = "double", defaultValue = "0")
    private String defaultMoney;

    @TableField(value = "order_by")
    @ApiModelProperty(value = "排序", required = "required,num")
    private Integer orderBy;

    @TableField("formula")
    @ApiModelProperty(value = "公式，用于计算该薪资字段金额")
    private String formula;

    @TableField(value = "remark")
    @ApiModelProperty(value = "相关描述")
    private String remark;

    @TableField(exist = false)
    @Property(value = "转换的值")
    private String moneyValue;

}
