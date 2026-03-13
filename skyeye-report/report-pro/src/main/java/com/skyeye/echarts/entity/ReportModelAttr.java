/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.echarts.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * @ClassName: ReportModelAttr
 * @Description: Echarts报表模型属性
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/20 16:22
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "report_model_attr", autoResultMap = true)
@ApiModel("Echarts报表模型属性实体类")
@ExcelTarget("ReportModelAttr")
public class ReportModelAttr extends CommonInfo {

    @TableId("id")
    @Property("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("report_model_id")
    @Property(value = "模型id")
    private String reportModelId;

    @TableField("attr_code")
    @Property(value = "属性id，也是属性code")
    @Excel(name = "属性id", width = 10, isImportField = "true_st", orderNum = "1")
    private String attrCode;

    @TableField("type_name")
    @Property(value = "属性分类名称")
    @Excel(name = "属性分类", width = 10, isImportField = "true_st", orderNum = "2")
    private String typeName;

    @TableField("name")
    @Property(value = "属性名称")
    @Excel(name = "属性名称", width = 10, isImportField = "true_st", orderNum = "3")
    private String name;

    @TableField("remark")
    @Property(value = "介绍")
    @Excel(name = "介绍", width = 10, isImportField = "true_st", orderNum = "4")
    private String remark;

    @TableField("default_value")
    @Property(value = "默认值")
    @Excel(name = "默认值", width = 10, isImportField = "true_st", orderNum = "5")
    private String defaultValue;

    @TableField("edit")
    @Property(value = "是否可编辑")
    @Excel(name = "是否可编辑", width = 10, isImportField = "true_st", replace = {"是_1", "否_2"}, orderNum = "6")
    private String edit;

    @TableField("editor_type")
    @Property(value = "编辑器")
    @Excel(name = "编辑器", width = 10, isImportField = "true_st", replace = {"单选框_1", "输入框_2", "颜色选择器_3",
        "数字输入框_4", "多行颜色选择器_5", "下拉框_6", "多选框_7", "滑块_8", "动态数据_9",
        "只读的输入框_98", "数据源选择_99", "折线系列配置编辑器_105"}, orderNum = "7")
    private String editorType;

    @TableField("optional_value")
    @Property(value = "可选值")
    @Excel(name = "可选值", width = 10, isImportField = "true_st", orderNum = "8")
    private String optionalValue;

}
