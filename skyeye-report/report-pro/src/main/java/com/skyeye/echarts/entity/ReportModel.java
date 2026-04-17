/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.echarts.entity;

import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.EnableEnum;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: ReportModel
 * @Description: 模型版本实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/3 9:51
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "report_model", autoResultMap = true)
@ApiModel("Echarts模型实体类")
@ExcelTarget("ReportModel")
public class ReportModel extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("import_model_id")
    @ApiModelProperty(value = "ImportModel模型id", required = "required")
    private String importModelId;

    @TableField("default_width")
    @ApiModelProperty(value = "默认宽度", required = "required")
    private String defaultWidth;

    @TableField("default_height")
    @ApiModelProperty(value = "默认高度", required = "required")
    private String defaultHeight;

    @TableField("min_width")
    @ApiModelProperty(value = "最小宽度", required = "required")
    private String minWidth;

    @TableField("min_height")
    @ApiModelProperty(value = "最小高度", required = "required")
    private String minHeight;

    @TableField("default_bg_color")
    @ApiModelProperty(value = "默认背景色", required = "required")
    private String defaultBgColor;

    @TableField("bg_transparency")
    @ApiModelProperty(value = "透明度", required = "required")
    private String bgTransparency;

    @TableField("logo_path")
    @ApiModelProperty(value = "logo地址", required = "required")
    private String logoPath;

    @TableField("software_version")
    @Property(value = "版本")
    private Integer softwareVersion;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "启用状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

    @TableField(exist = false)
    @ApiModelProperty(value = "属性信息", required = "required,json")
    private List<ReportModelAttr> reportModelAttrList;

}
