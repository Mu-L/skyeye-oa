/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.print.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.print.enumclass.PaperSize;
import lombok.Data;

/**
 * @ClassName: PrintTemplate
 * @Description: 打印模板实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/5/14 22:45
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = {"name", "pageId"})
@RedisCacheField(name = "print:template")
@TableName(value = "print_template", autoResultMap = true)
@ApiModel("打印模板实体类")
public class PrintTemplate extends BaseGeneralInfo {

    @TableField("object_key")
    @ApiModelProperty(value = "业务对象的key", required = "required")
    private String objectKey;

    @TableField("page_id")
    @ApiModelProperty(value = "表单布局id", required = "required")
    private String pageId;

    @TableField("paper_size")
    @ApiModelProperty(value = "纸张大小", enumClass = PaperSize.class, required = "required")
    private String paperSize;

    @TableField("orientation")
    @ApiModelProperty(value = "纸张方向", required = "required")
    private String orientation;

    @TableField("enabled")
    @ApiModelProperty(value = "启用状态", enumClass = EnableEnum.class, required = "required")
    private Integer enabled;

    @TableField("is_default")
    @ApiModelProperty(value = "是否默认", enumClass = IsDefaultEnum.class, required = "required,num")
    private Integer isDefault;

    @TableField(value = "config_content")
    @ApiModelProperty(value = "模板配置内容(JSON)")
    private String configContent;

    @TableField(value = "margin")
    @ApiModelProperty(value = "外边距(JSON)")
    private String margin;

    @TableField(value = "width")
    @ApiModelProperty(value = "页宽")
    private String width;

    @TableField(value = "height")
    @ApiModelProperty(value = "页高")
    private String height;

}
