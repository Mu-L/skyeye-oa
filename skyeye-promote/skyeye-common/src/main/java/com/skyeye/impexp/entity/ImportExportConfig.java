/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.impexp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.IsDefaultEnum;
import lombok.Data;

/**
 * @ClassName: ImportExportConfig
 * @Description: 业务对象导入导出配置实体类
 * @author: skyeye云系列--卫志强
 * @date: 2026/4/8 22:00
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName("skyeye_import_export_config")
@ApiModel("业务对象导入导出配置实体类")
public class ImportExportConfig extends BaseGeneralInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("app_id")
    @ApiModelProperty(value = "应用的appId", required = "required")
    private String appId;

    @TableField("class_name")
    @ApiModelProperty(value = "业务对象的className", required = "required")
    private String className;

    @TableField("is_default")
    @ApiModelProperty(value = "是否默认配置", enumClass = IsDefaultEnum.class, required = "required,num")
    private Integer isDefault;

    @TableField("sort_no")
    @ApiModelProperty(value = "排序号")
    private Integer sortNo;

    @TableField("import_config")
    @ApiModelProperty(value = "导入配置JSON字符串，示例：{\"items\":[{\"attrKey\":\"name\",\"required\":true}]}")
    private String importConfig;

    @TableField("export_config")
    @ApiModelProperty(value = "导出配置JSON字符串，示例：{\"items\":[{\"attrKey\":\"name\",\"checked\":true}]}")
    private String exportConfig;
}

