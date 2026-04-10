/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.impexp.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.attr.entity.AttrDefinition;
import com.skyeye.attr.entity.AttrDefinitionCustom;
import com.skyeye.attr.service.AttrDefinitionCustomService;
import com.skyeye.attr.service.AttrDefinitionService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.VerificationParamsEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.ExcelUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.impexp.dao.ImportExportConfigDao;
import com.skyeye.impexp.entity.ImportExportConfig;
import com.skyeye.impexp.entity.ImportExportFieldOption;
import com.skyeye.impexp.service.ImportExportConfigService;
import com.skyeye.impexp.service.ImportExportDataProvider;
import com.skyeye.impexp.support.ImportExportConfigJsonHelper;
import com.skyeye.impexp.support.ImportExportConfigJsonHelper.ColumnSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName: ImportExportConfigServiceImpl
 * @Description: 业务对象导入导出配置服务层
 * @author: skyeye云系列--卫志强
 * @date: 2026/4/8 22:10
 */
@Service
@SkyeyeService(name = "导入导出配置", groupName = "系统公共模块", tenant = TenantEnum.WEAK_ISOLATION)
public class ImportExportConfigServiceImpl extends SkyeyeBusinessServiceImpl<ImportExportConfigDao, ImportExportConfig> implements ImportExportConfigService {

    @Autowired
    private AttrDefinitionService attrDefinitionService;

    @Autowired
    private AttrDefinitionCustomService attrDefinitionCustomService;

    @Autowired(required = false)
    private List<ImportExportDataProvider> importExportDataProviders;

    @Autowired
    private ImportExportRouteDataProvider importExportRouteDataProvider;

    @Override
    protected void validatorEntity(ImportExportConfig entity) {
        // 同一业务对象下配置名称唯一，避免用户选择配置时出现重名歧义。
        QueryWrapper<ImportExportConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getAppId), entity.getAppId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getClassName), entity.getClassName());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getName), entity.getName());
        if (StrUtil.isNotBlank(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        ImportExportConfig check = getOne(queryWrapper, false);
        if (check != null) {
            throw new CustomException("该业务对象导入导出配置的名称已存在.");
        }
    }

    @Override
    public void writePostpose(ImportExportConfig entity, String userId) {
        super.writePostpose(entity, userId);
        if (Objects.equals(entity.getIsDefault(), IsDefaultEnum.IS_DEFAULT.getKey())) {
            // 一个业务对象只能有一个默认配置：将其他配置统一更新为非默认。
            UpdateWrapper<ImportExportConfig> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getAppId), entity.getAppId());
            updateWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getClassName), entity.getClassName());
            updateWrapper.ne(CommonConstants.ID, entity.getId());
            updateWrapper.set(MybatisPlusUtil.toColumns(ImportExportConfig::getIsDefault), IsDefaultEnum.NOT_DEFAULT.getKey());
            update(updateWrapper);
        }
    }

    @Override
    public void queryImportExportConfigList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String appId = params.get("appId").toString();
        String className = params.get("className").toString();
        // 列表按“默认优先 -> 排序号 -> 最近更新时间”排序，方便前端直接展示与选择。
        QueryWrapper<ImportExportConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getAppId), appId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getClassName), className);
        List<ImportExportConfig> list = list(queryWrapper);
        List<ImportExportConfig> configList = list.stream().sorted(Comparator
                .comparing((ImportExportConfig item) -> item.getIsDefault(), Comparator.reverseOrder())
                .thenComparing(item -> item.getSortNo())
                .thenComparing(item -> item.getLastUpdateTime(), Comparator.reverseOrder()))
            .collect(Collectors.toList());
        outputObject.setBeans(configList);
        outputObject.settotal(configList.size());
    }

    @Override
    public void queryImportExportFieldOptions(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String appId = params.get("appId").toString();
        String className = params.get("className").toString();
        // 字段来源于业务对象属性定义，保证导入导出配置项与模型字段保持一致。
        List<AttrDefinition> attrDefinitionList = attrDefinitionService.queryAttrDefinitionList(appId, className);
        if (CollectionUtil.isEmpty(attrDefinitionList)) {
            outputObject.setBeans(CollectionUtil.newArrayList());
            outputObject.settotal(0);
            return;
        }
        List<String> attrKeyList = attrDefinitionList.stream().map(AttrDefinition::getAttrKey).collect(Collectors.toList());
        Map<String, AttrDefinitionCustom> customMap = attrDefinitionCustomService.queryAttrDefinitionCustomMap(appId, className, attrKeyList);

        List<ImportExportFieldOption> result = attrDefinitionList.stream().map(attrDefinition -> {
            ImportExportFieldOption option = new ImportExportFieldOption();
            option.setAttrKey(attrDefinition.getAttrKey());
            option.setAttrType(attrDefinition.getAttrType());
            option.setFieldType(attrDefinition.getFieldType());
            option.setWhetherInputParams(attrDefinition.getWhetherInputParams());
            option.setRemark(attrDefinition.getRemark());

            AttrDefinitionCustom custom = customMap.get(attrDefinition.getAttrKey());
            // 优先展示自定义属性名称，没有自定义则回退到原始属性名。
            if (custom != null && StrUtil.isNotBlank(custom.getName())) {
                option.setName(custom.getName());
            } else {
                option.setName(attrDefinition.getName());
            }

            boolean canImport = attrDefinition.getWhetherInputParams() != null
                && attrDefinition.getWhetherInputParams().equals(WhetherEnum.ENABLE_USING.getKey());
            // required 规则命中时，标记为“导入必填且前端不可取消”。
            boolean importRequiredFixed = canImport && StrUtil.containsIgnoreCase(attrDefinition.getRequired(), VerificationParamsEnum.REQUIRED.getKey());
            option.setDefaultImportChecked(canImport);
            option.setImportRequiredFixed(importRequiredFixed);
            option.setDefaultExportChecked(true);
            return option;
        }).collect(Collectors.toList());

        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    @Override
    public void queryImportExportConfigForUse(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        ImportExportConfig config = resolveConfigForDownload(params);
        outputObject.setBean(config);
        outputObject.settotal(config == null ? 0 : 1);
    }

    @Override
    public void queryImportExportColumnsForUse(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        ImportExportConfig config = resolveConfigForDownload(params);
        if (config == null) {
            throw new CustomException("未找到导入导出配置，请先保存配置。");
        }
        String appId = params.get("appId").toString();
        String className = params.get("className").toString();
        Map<String, String> titleMap = buildAttrKeyTitleMap(appId, className);

        List<ColumnSpec> importSpecs = ImportExportConfigJsonHelper.parseColumnSpecs(config.getImportConfig());
        if (CollectionUtil.isEmpty(importSpecs)) {
            importSpecs = buildDefaultImportColumnSpecs(appId, className, titleMap);
        }
        List<ColumnSpec> exportSpecs = ImportExportConfigJsonHelper.parseColumnSpecs(config.getExportConfig());
        if (CollectionUtil.isEmpty(exportSpecs)) {
            exportSpecs = buildDefaultExportColumnSpecs(appId, className, titleMap);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("config", config);
        result.put("importColumns", convertColumnSpec(importSpecs, titleMap));
        result.put("exportColumns", convertColumnSpec(exportSpecs, titleMap));
        outputObject.setBean(result);
        outputObject.settotal(1);
    }

    @Override
    public void downloadImportTemplate(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        ImportExportConfig config = resolveConfigForDownload(params);
        if (config == null) {
            throw new CustomException("未找到导入导出配置，请先保存配置。");
        }
        List<ColumnSpec> specs = ImportExportConfigJsonHelper.parseColumnSpecs(config.getImportConfig());
        String appId = params.get("appId").toString();
        String className = params.get("className").toString();
        Map<String, String> titleMap = buildAttrKeyTitleMap(appId, className);
        if (CollectionUtil.isEmpty(specs)) {
            specs = buildDefaultImportColumnSpecs(appId, className, titleMap);
        }
        if (CollectionUtil.isEmpty(specs)) {
            throw new CustomException("未配置导入列且无可用属性，无法生成模板。");
        }
        writeExcelTemplate(config.getName(), "导入模板", specs, titleMap);
    }

    @Override
    public void downloadExportTemplate(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        ImportExportConfig config = resolveConfigForDownload(params);
        if (config == null) {
            throw new CustomException("未找到导入导出配置，请先保存配置。");
        }
        List<ColumnSpec> specs = ImportExportConfigJsonHelper.parseColumnSpecs(config.getExportConfig());
        String appId = params.get("appId").toString();
        String className = params.get("className").toString();
        Map<String, String> titleMap = buildAttrKeyTitleMap(appId, className);
        if (CollectionUtil.isEmpty(specs)) {
            specs = buildDefaultExportColumnSpecs(appId, className, titleMap);
        }
        if (CollectionUtil.isEmpty(specs)) {
            throw new CustomException("未配置导出列且无可用属性，无法生成模板。");
        }
        writeExcelTemplate(config.getName(), "导出模板", specs, titleMap);
    }

    @Override
    public void exportByConfig(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        ImportExportConfig config = resolveConfigForDownload(params);
        if (config == null) {
            throw new CustomException("未找到导入导出配置，请先保存配置。");
        }
        String appId = params.get("appId").toString();
        String className = params.get("className").toString();
        Map<String, String> titleMap = buildAttrKeyTitleMap(appId, className);
        List<ColumnSpec> specs = ImportExportConfigJsonHelper.parseColumnSpecs(config.getExportConfig());
        if (CollectionUtil.isEmpty(specs)) {
            specs = buildDefaultExportColumnSpecs(appId, className, titleMap);
        }
        if (CollectionUtil.isEmpty(specs)) {
            throw new CustomException("未配置导出列且无可用属性，无法导出。");
        }
        Map<String, Object> filters = parseFilters(params.get("filters"));
        List<Map<String, Object>> rows = resolveExportRows(appId, className, filters, inputObject);
        writeExcelTemplate(config.getName(), "导出数据", specs, titleMap, rows);
    }

    /**
     * 按 appId、className、可选 id 解析配置；未传 id 时取默认，再取排序第一条。
     */
    private ImportExportConfig resolveConfigForDownload(Map<String, Object> params) {
        String appId = params.get("appId").toString();
        String className = params.get("className").toString();
        String id = params.get("id") == null ? null : params.get("id").toString();
        ImportExportConfig config = null;
        if (StrUtil.isNotBlank(id)) {
            config = selectById(id);
            if (config != null && (!StrUtil.equals(appId, config.getAppId()) || !StrUtil.equals(className, config.getClassName()))) {
                throw new CustomException("该配置不属于当前业务对象.");
            }
        }
        if (config == null) {
            QueryWrapper<ImportExportConfig> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getAppId), appId);
            queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getClassName), className);
            queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getIsDefault), IsDefaultEnum.IS_DEFAULT.getKey());
            config = getOne(queryWrapper, false);
        }
        if (config == null) {
            QueryWrapper<ImportExportConfig> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getAppId), appId);
            queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getClassName), className);
            List<ImportExportConfig> list = list(queryWrapper);
            if (CollectionUtil.isEmpty(list)) {
                return null;
            }
            list.sort(Comparator
                .comparing((ImportExportConfig item) -> item.getIsDefault(), Comparator.reverseOrder())
                .thenComparing(ImportExportConfig::getSortNo, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(ImportExportConfig::getLastUpdateTime, Comparator.nullsLast(Comparator.reverseOrder())));
            config = list.get(0);
        }
        return config;
    }

    private Map<String, String> buildAttrKeyTitleMap(String appId, String className) {
        List<AttrDefinition> attrDefinitionList = attrDefinitionService.queryAttrDefinitionList(appId, className);
        if (CollectionUtil.isEmpty(attrDefinitionList)) {
            return new LinkedHashMap<>();
        }
        List<String> attrKeyList = attrDefinitionList.stream().map(AttrDefinition::getAttrKey).collect(Collectors.toList());
        Map<String, AttrDefinitionCustom> customMap = attrDefinitionCustomService.queryAttrDefinitionCustomMap(appId, className, attrKeyList);
        Map<String, String> map = new LinkedHashMap<>();
        for (AttrDefinition attrDefinition : attrDefinitionList) {
            AttrDefinitionCustom custom = customMap.get(attrDefinition.getAttrKey());
            String title = custom != null && StrUtil.isNotBlank(custom.getName()) ? custom.getName() : attrDefinition.getName();
            map.put(attrDefinition.getAttrKey(), title);
        }
        return map;
    }

    /** import_config 未配 items 时：默认可导入字段 */
    private List<ColumnSpec> buildDefaultImportColumnSpecs(String appId, String className, Map<String, String> titleMap) {
        List<AttrDefinition> attrDefinitionList = attrDefinitionService.queryAttrDefinitionList(appId, className);
        if (CollectionUtil.isEmpty(attrDefinitionList)) {
            return new ArrayList<>();
        }
        List<ColumnSpec> list = new ArrayList<>();
        for (AttrDefinition attrDefinition : attrDefinitionList) {
            boolean canImport = attrDefinition.getWhetherInputParams() != null
                && attrDefinition.getWhetherInputParams().equals(WhetherEnum.ENABLE_USING.getKey());
            if (!canImport) {
                continue;
            }
            ColumnSpec spec = new ColumnSpec();
            spec.setAttrKey(attrDefinition.getAttrKey());
            spec.setColumnTitle(titleMap.get(attrDefinition.getAttrKey()));
            list.add(spec);
        }
        return list;
    }

    /** export_config 未配 items 时：默认导出全部属性列 */
    private List<ColumnSpec> buildDefaultExportColumnSpecs(String appId, String className, Map<String, String> titleMap) {
        List<AttrDefinition> attrDefinitionList = attrDefinitionService.queryAttrDefinitionList(appId, className);
        if (CollectionUtil.isEmpty(attrDefinitionList)) {
            return new ArrayList<>();
        }
        List<ColumnSpec> list = new ArrayList<>();
        for (AttrDefinition attrDefinition : attrDefinitionList) {
            ColumnSpec spec = new ColumnSpec();
            spec.setAttrKey(attrDefinition.getAttrKey());
            spec.setColumnTitle(titleMap.get(attrDefinition.getAttrKey()));
            list.add(spec);
        }
        return list;
    }

    private void writeExcelTemplate(String configName, String sheetName, List<ColumnSpec> specs, Map<String, String> titleMap) {
        writeExcelTemplate(configName, sheetName, specs, titleMap, null);
    }

    private void writeExcelTemplate(String configName, String sheetName, List<ColumnSpec> specs, Map<String, String> titleMap,
                                    List<Map<String, Object>> rows) {
        int n = specs.size();
        String[] keys = new String[n];
        String[] columnNames = new String[n];
        for (int i = 0; i < n; i++) {
            ColumnSpec spec = specs.get(i);
            keys[i] = spec.getAttrKey();
            String header = StrUtil.isNotBlank(spec.getColumnTitle()) ? spec.getColumnTitle() : titleMap.get(spec.getAttrKey());
            if (StrUtil.isBlank(header)) {
                header = spec.getAttrKey();
            }
            columnNames[i] = header;
        }
        String[] dataType = new String[0];
        String safeName = StrUtil.blankToDefault(configName, "导入导出");
        ExcelUtil.createWorkBook(safeName + sheetName, sheetName, rows, keys, columnNames, dataType, PutObject.getResponse());
    }

    private List<Map<String, Object>> convertColumnSpec(List<ColumnSpec> specs, Map<String, String> titleMap) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (ColumnSpec spec : specs) {
            Map<String, Object> one = new LinkedHashMap<>();
            one.put("attrKey", spec.getAttrKey());
            String header = StrUtil.isNotBlank(spec.getColumnTitle()) ? spec.getColumnTitle() : titleMap.get(spec.getAttrKey());
            if (StrUtil.isBlank(header)) {
                header = spec.getAttrKey();
            }
            one.put("columnTitle", header);
            result.add(one);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseFilters(Object filtersObj) {
        if (filtersObj == null) {
            return Collections.emptyMap();
        }
        if (filtersObj instanceof Map) {
            return (Map<String, Object>) filtersObj;
        }
        String filtersStr = filtersObj.toString();
        if (StrUtil.isBlank(filtersStr)) {
            return Collections.emptyMap();
        }
        if (!JSONUtil.isTypeJSON(filtersStr)) {
            throw new CustomException("filters必须是json对象字符串.");
        }
        return JSONUtil.parseObj(filtersStr);
    }

    private List<Map<String, Object>> resolveExportRows(String appId, String className, Map<String, Object> filters, InputObject inputObject) {
        // 先走业务模块自定义Provider，满足复杂聚合/多表场景。
        if (CollectionUtil.isNotEmpty(importExportDataProviders)) {
            for (ImportExportDataProvider provider : importExportDataProviders) {
                if (provider.support(appId, className)) {
                    List<Map<String, Object>> rows = provider.queryExportRows(appId, className, filters, inputObject);
                    return rows == null ? CollectionUtil.newArrayList() : rows;
                }
            }
        }
        // 未实现Provider时，回落到统一路由查询，复用VirtualBusinessController主干链路。
        return importExportRouteDataProvider.queryExportRowsByRoute(appId, className, filters);
        return null;
    }

}

