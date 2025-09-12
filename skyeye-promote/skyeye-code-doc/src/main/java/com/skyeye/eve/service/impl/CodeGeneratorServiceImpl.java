/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.tenant.TenantIsolation;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.config.CodeGeneratorConfig;
import com.skyeye.eve.service.CodeGeneratorService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @ClassName: CodeGeneratorServiceImpl
 * @Description: 代码生成器服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2024/12/19
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 */
@Slf4j
@Service
public class CodeGeneratorServiceImpl implements CodeGeneratorService {

    @Autowired
    private DataSource dataSource;

    private final Configuration freemarkerConfig;

    @Value("${IMAGES_PATH}")
    private String tPath;

    public CodeGeneratorServiceImpl() {
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_31);
        this.freemarkerConfig.setDefaultEncoding("UTF-8");
    }

    @Override
    @TenantIsolation(TenantEnum.PLATE)
    public void getDatabaseTables(InputObject inputObject, OutputObject outputObject) {
        try (Connection connection = dataSource.getConnection()) {
            String catalog = connection.getCatalog();
            List<Map<String, Object>> tables = new ArrayList<>();

            // 直接使用SQL查询一次性获取所有表信息，包括表注释
            String sql = "SELECT TABLE_NAME, TABLE_TYPE, TABLE_COMMENT FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ? AND TABLE_TYPE = 'BASE TABLE' ORDER BY TABLE_NAME";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, catalog);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> table = new HashMap<>();
                        table.put("tableName", rs.getString("TABLE_NAME"));
                        table.put("tableType", rs.getString("TABLE_TYPE"));
                        table.put("tableComment", rs.getString("TABLE_COMMENT"));
                        tables.add(table);
                    }
                }
            }

            outputObject.setBeans(tables);
            outputObject.settotal(tables.size());
        } catch (SQLException e) {
            outputObject.setreturnMessage("获取数据库表列表失败：" + e.getMessage());
        }
    }

    @Override
    @TenantIsolation(TenantEnum.PLATE)
    public void getTableColumns(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String tableName = params.get("tableName").toString();

        try (Connection connection = dataSource.getConnection()) {
            String catalog = connection.getCatalog();
            List<Map<String, Object>> columns = new ArrayList<>();

            // 使用SQL查询一次性获取所有字段信息，包括字段注释
            String sql = "SELECT " +
                "COLUMN_NAME, " +
                "DATA_TYPE, " +
                "CHARACTER_MAXIMUM_LENGTH, " +
                "IS_NULLABLE, " +
                "COLUMN_DEFAULT, " +
                "COLUMN_COMMENT, " +
                "ORDINAL_POSITION, " +
                "COLUMN_KEY " +
                "FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? " +
                "ORDER BY ORDINAL_POSITION";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, catalog);
                ps.setString(2, tableName);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> column = new HashMap<>();
                        String columnName = rs.getString("COLUMN_NAME");
                        String dataType = rs.getString("DATA_TYPE");
                        String columnKey = rs.getString("COLUMN_KEY");

                        column.put("columnName", columnName);
                        column.put("columnType", dataType);
                        column.put("columnSize", rs.getInt("CHARACTER_MAXIMUM_LENGTH"));
                        column.put("nullable", "YES".equals(rs.getString("IS_NULLABLE")));
                        column.put("columnDefault", rs.getString("COLUMN_DEFAULT"));
                        column.put("remarks", rs.getString("COLUMN_COMMENT"));
                        column.put("ordinalPosition", rs.getInt("ORDINAL_POSITION"));
                        column.put("isPrimaryKey", "PRI".equals(columnKey));
                        column.put("isAutoIncrement", false); // 可以通过额外查询获取

                        // 转换为Java类型和属性名
                        column.put("javaType", convertToJavaType(dataType));
                        column.put("propertyName", convertToPropertyName(columnName));

                        columns.add(column);
                    }
                }
            }

            outputObject.setBeans(columns);
            outputObject.settotal(columns.size());
        } catch (SQLException e) {
            outputObject.setreturnMessage("获取表字段信息失败：" + e.getMessage());
        }
    }

    @Override
    @TenantIsolation(TenantEnum.PLATE)
    public void previewCode(InputObject inputObject, OutputObject outputObject) {
        try {
            CodeGeneratorConfig config = inputObject.getParams(CodeGeneratorConfig.class);
            Map<String, String> previewResults = new HashMap<>();

            // 获取启用的模板
            List<Map<String, Object>> templates = getEnabledTemplates(config.getTemplateConfigs());

            for (Map<String, Object> template : templates) {
                String templateId = template.get("id").toString();
                String templateContent = template.get("modelContent").toString();

                // 使用FreeMarker渲染模板
                Template freemarkerTemplate = new Template(templateId, templateContent, freemarkerConfig);
                StringWriter writer = new StringWriter();

                Map<String, Object> dataModel = buildDataModel(config);
                freemarkerTemplate.process(dataModel, writer);

                previewResults.put(templateId, writer.toString());
            }

            outputObject.setBean(previewResults);
            outputObject.settotal(CommonNumConstants.NUM_ONE);
        } catch (Exception e) {
            outputObject.setreturnMessage("预览代码失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    @TenantIsolation(TenantEnum.PLATE)
    public void downloadCode(InputObject inputObject, OutputObject outputObject) {
        ZipOutputStream out = null;
        try {
            Integer type = 23;
            CodeGeneratorConfig config = inputObject.getParams(CodeGeneratorConfig.class);
            String zipName = ToolUtil.getSurFaceId() + ".zip";
            String basePath = tPath + FileConstants.FileUploadPath.getSavePath(type);
            FileUtil.createDirs(basePath);
            String strZipPath = basePath + "/" + zipName;

            // 生成代码文件
            Map<String, String> generatedFiles = new HashMap<>();
            List<Map<String, Object>> templates = getEnabledTemplates(config.getTemplateConfigs());

            log.info("文件地址：" + strZipPath);
            out = new ZipOutputStream(new FileOutputStream(strZipPath));
            byte[] buffer = new byte[1024];
            for (Map<String, Object> template : templates) {
                String templateId = template.get("id").toString();
                String templateContent = template.get("modelContent").toString();
                String fileNameTemplate = template.get("fileNameTemplate").toString();

                // 渲染模板
                Template freemarkerTemplate = new Template(templateId, templateContent, freemarkerConfig);
                StringWriter writer = new StringWriter();
                Map<String, Object> dataModel = buildDataModel(config);
                freemarkerTemplate.process(dataModel, writer);

                // 生成文件名
                String fileName = generateFileName(fileNameTemplate, dataModel);

                // 加入压缩包
                ByteArrayInputStream stream = new ByteArrayInputStream(writer.toString().getBytes());
                out.putNextEntry(new ZipEntry(fileName));
                int len;
                // 读入需要下载的文件的内容，打包到zip文件
                while ((len = stream.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                out.closeEntry();

                generatedFiles.put(fileName, writer.toString());
            }
            Map<String, Object> result = new HashMap<>();
            result.put("fileName", config.getTableConfigs().get(0).getTableName() + ".zip");
            result.put("path", FileConstants.FileUploadPath.getVisitPath(type) + zipName);
            outputObject.setBean(result);
            outputObject.settotal(CommonNumConstants.NUM_ONE);
        } catch (Exception e) {
            outputObject.setreturnMessage("生成代码失败：" + e.getMessage());
        } finally {
            FileUtil.close(out);
        }
    }

    @Override
    public void getAvailableTemplates(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String templateGroup = params.get("templateGroup").toString();

        // 从文件系统加载模板
        List<Map<String, Object>> templates = loadTemplatesFromFileSystem(templateGroup);
        outputObject.setBeans(templates);
        outputObject.settotal(templates.size());
    }

    /**
     * 获取启用的模板
     */
    private List<Map<String, Object>> getEnabledTemplates(List<CodeGeneratorConfig.TemplateConfig> templateConfigs) {
        List<Map<String, Object>> templates = new ArrayList<>();

        for (CodeGeneratorConfig.TemplateConfig config : templateConfigs) {
            // 从配置中获取模板分组，如果没有则默认为business
            String templateGroup = config.getTemplateGroup() != null ? config.getTemplateGroup() : "business";
            Map<String, Object> template = loadTemplateFromFileSystem(config.getTemplateId(), templateGroup);
            if (CollectionUtil.isNotEmpty(template)) {
                template.put("fileNameTemplate", config.getFileNameTemplate());
                template.put("outputPath", config.getOutputPath());
                template.put("packageName", config.getPackageName());
                templates.add(template);
            }
        }

        return templates;
    }

    /**
     * 构建数据模型
     */
    private Map<String, Object> buildDataModel(CodeGeneratorConfig config) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("project", config.getProjectConfig());
        dataModel.put("tables", config.getTableConfigs());
        dataModel.put("generate", config.getGenerateConfig());
        dataModel.put("date", new Date());
        dataModel.put("author", config.getProjectConfig().getAuthor());
        // 项目配置
        if (config.getProjectConfig() != null) {
            dataModel.put("packageName", config.getProjectConfig().getPackageName());
            dataModel.put("author", config.getProjectConfig().getAuthor());
        }
        // 表配置
        if (CollectionUtil.isNotEmpty(config.getTableConfigs())) {
            CodeGeneratorConfig.TableConfig tableConfig = config.getTableConfigs().get(0);
            dataModel.put("table", tableConfig);
            dataModel.put("tableName", tableConfig.getTableName());
            dataModel.put("className", tableConfig.getEntityName());
            dataModel.put("entityName", tableConfig.getEntityName());
            dataModel.put("tableComment", tableConfig.getTableComment());
        }
        return dataModel;
    }

    /**
     * 生成文件名
     */
    private String generateFileName(String fileNameTemplate, Map<String, Object> dataModel) {
        try {
            Template template = new Template("fileName", fileNameTemplate, freemarkerConfig);
            StringWriter writer = new StringWriter();
            template.process(dataModel, writer);
            return writer.toString();
        } catch (Exception e) {
            return "generated_file_" + System.currentTimeMillis();
        }
    }

    /**
     * 转换数据库类型为Java类型
     */
    private String convertToJavaType(String dbType) {
        if (StrUtil.isBlank(dbType)) {
            return "String";
        }

        String type = dbType.toLowerCase();
        if (type.contains("varchar") || type.contains("text") || type.contains("char")) {
            return "String";
        } else if (type.contains("int")) {
            return "Integer";
        } else if (type.contains("bigint")) {
            return "Long";
        } else if (type.contains("decimal") || type.contains("numeric")) {
            return "BigDecimal";
        } else if (type.contains("date") || type.contains("time")) {
            return "Date";
        } else if (type.contains("boolean") || type.contains("bit")) {
            return "Boolean";
        } else {
            return "String";
        }
    }

    /**
     * 转换字段名为属性名
     */
    private String convertToPropertyName(String columnName) {
        if (StrUtil.isBlank(columnName)) {
            return "";
        }

        // 下划线转驼峰
        String[] parts = columnName.split("_");
        StringBuilder result = new StringBuilder(parts[0].toLowerCase());

        for (int i = 1; i < parts.length; i++) {
            if (StrUtil.isNotBlank(parts[i])) {
                result.append(parts[i].substring(0, 1).toUpperCase())
                    .append(parts[i].substring(1).toLowerCase());
            }
        }

        return result.toString();
    }

    /**
     * 从文件系统加载模板列表
     */
    private List<Map<String, Object>> loadTemplatesFromFileSystem(String templateGroup) {
        List<Map<String, Object>> templates = new ArrayList<>();

        // 根据模板分组加载对应的模板
        Map<String, Map<String, Object>> predefinedTemplates = getPredefinedTemplates(templateGroup);
        templates.addAll(predefinedTemplates.values());

        return templates;
    }

    /**
     * 从文件系统加载单个模板
     */
    private Map<String, Object> loadTemplateFromFileSystem(String templateId, String templateGroup) {
        Map<String, Map<String, Object>> predefinedTemplates = getPredefinedTemplates(templateGroup);
        return predefinedTemplates.get(templateId);
    }

    /**
     * 获取预定义的模板
     */
    private Map<String, Map<String, Object>> getPredefinedTemplates(String templateGroup) {
        Map<String, Map<String, Object>> templates = new HashMap<>();

        // 根据模板分组加载对应的模板文件
        String groupPath = templateGroup + "/";

        // Entity模板
        Map<String, Object> entityTemplate = new HashMap<>();
        entityTemplate.put("id", "entity");
        entityTemplate.put("modelName", "实体类模板");
        entityTemplate.put("modelType", "entity");
        entityTemplate.put("modelContent", loadTemplateContent(groupPath + "entity_" + templateGroup + ".ftl"));
        templates.put("entity", entityTemplate);

        // Controller模板
        Map<String, Object> controllerTemplate = new HashMap<>();
        controllerTemplate.put("id", "controller");
        controllerTemplate.put("modelName", "控制器模板");
        controllerTemplate.put("modelType", "controller");
        controllerTemplate.put("modelContent", loadTemplateContent(groupPath + "controller_" + templateGroup + ".ftl"));
        templates.put("controller", controllerTemplate);

        // Service模板
        Map<String, Object> serviceTemplate = new HashMap<>();
        serviceTemplate.put("id", "service");
        serviceTemplate.put("modelName", "服务接口模板");
        serviceTemplate.put("modelType", "service");
        serviceTemplate.put("modelContent", loadTemplateContent(groupPath + "service_" + templateGroup + ".ftl"));
        templates.put("service", serviceTemplate);

        // ServiceImpl模板
        Map<String, Object> serviceImplTemplate = new HashMap<>();
        serviceImplTemplate.put("id", "serviceImpl");
        serviceImplTemplate.put("modelName", "服务实现模板");
        serviceImplTemplate.put("modelType", "serviceImpl");
        serviceImplTemplate.put("modelContent", loadTemplateContent(groupPath + "serviceImpl_" + templateGroup + ".ftl"));
        templates.put("serviceImpl", serviceImplTemplate);

        // DAO模板
        Map<String, Object> daoTemplate = new HashMap<>();
        daoTemplate.put("id", "dao");
        daoTemplate.put("modelName", "DAO接口模板");
        daoTemplate.put("modelType", "dao");
        daoTemplate.put("modelContent", loadTemplateContent(groupPath + "dao_" + templateGroup + ".ftl"));
        templates.put("dao", daoTemplate);
        return templates;
    }

    /**
     * 加载模板内容
     */
    private String loadTemplateContent(String templateFileName) {
        try {
            // 从classpath加载模板文件
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates/" + templateFileName);
            if (inputStream != null) {
                return IOUtils.toString(inputStream, "utf-8");
            }
        } catch (Exception e) {
            throw new RuntimeException("加载模板失败：" + templateFileName, e);
        }
        return StrUtil.EMPTY;
    }
}
