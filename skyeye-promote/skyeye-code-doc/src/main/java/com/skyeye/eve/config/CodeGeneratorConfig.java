/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.config;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: CodeGeneratorConfig
 * @Description: 代码生成器配置实体
 * @author: skyeye云系列--卫志强
 * @date: 2024/12/19
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 */
@Data
@ApiModel(value = "代码生成器配置实体")
public class CodeGeneratorConfig {

    @ApiModelProperty("项目配置")
    private ProjectConfig projectConfig;

    @ApiModelProperty(value = "表配置", required = "required")
    private List<TableConfig> tableConfigs;

    @ApiModelProperty(value = "模板配置", required = "required")
    private List<TemplateConfig> templateConfigs;

    @ApiModelProperty(value = "生成配置", required = "required")
    private GenerateConfig generateConfig;

    @Data
    @ApiModel(value = "项目配置")
    public static class ProjectConfig {

        @ApiModelProperty(value = "包名", required = "required")
        private String packageName;

        @ApiModelProperty(value = "作者", required = "required")
        private String author;

        @ApiModelProperty(value = "父类得包名")
        private String inheritClassPath;

        @ApiModelProperty(value = "父类得类名")
        private String inheritClass;
    }

    @Data
    @ApiModel(value = "表配置")
    public static class TableConfig {
        @ApiModelProperty(value = "表名", required = "required")
        private String tableName;

        @ApiModelProperty(value = "实体类名", required = "required")
        private String entityName;

        @ApiModelProperty(value = "表注释")
        private String tableComment;

        @ApiModelProperty(value = "字段列表", required = "json")
        private List<FieldConfig> fields;

        @ApiModelProperty(value = "继承类名")
        private String inheritClass;

        @ApiModelProperty(value = "继承类路径")
        private String inheritClassPath;
    }

    @Data
    @ApiModel(value = "字段配置")
    public static class FieldConfig {
        @ApiModelProperty(value = "字段名", required = "required")
        private String columnName;

        @ApiModelProperty(value = "属性名", required = "required")
        private String propertyName;

        @ApiModelProperty(value = "字段类型", required = "required")
        private String columnType;

        @ApiModelProperty(value = "Java类型", required = "required")
        private String javaType;

        @ApiModelProperty(value = "字段注释")
        private String remarks;

        @ApiModelProperty(value = "是否主键")
        private Boolean isPrimaryKey = false;

        @ApiModelProperty(value = "是否可空")
        private Boolean nullable = true;

        @ApiModelProperty(value = "默认值")
        private String defaultValue;
    }

    @Data
    @ApiModel(value = "模板配置")
    public static class TemplateConfig {
        @ApiModelProperty(value = "模板ID", required = "required")
        private String templateId;

        @ApiModelProperty(value = "模板名称", required = "required")
        private String templateName;

        @ApiModelProperty(value = "模板类型", required = "required")
        private String templateType;

        @ApiModelProperty(value = "模板分组", required = "required")
        private String templateGroup;

        @ApiModelProperty(value = "输出路径", required = "required")
        private String outputPath;

        @ApiModelProperty(value = "文件名模板", required = "required")
        private String fileNameTemplate;
    }

    @Data
    @ApiModel(value = "生成配置")
    public static class GenerateConfig {
        @ApiModelProperty(value = "是否开启缓存", required = "required")
        private Boolean generateRedisCache = true;

        @ApiModelProperty(value = "缓存得键得前缀")
        private Boolean redisCachePrefix;
    }
}
