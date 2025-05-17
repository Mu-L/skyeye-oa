/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.print.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.skyeye.exception.CustomException;
import com.skyeye.print.entity.PrintTemplate;
import com.skyeye.print.service.PrintHtmlGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @ClassName: PrintHtmlGeneratorImpl
 * @Description: 打印html生成器实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/5/15 8:49
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PrintHtmlGeneratorImpl implements PrintHtmlGenerator {

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Override
    public String generateHtml(PrintTemplate printTemplate, Map<String, Object> data) {
        try {
            // 解析模板配置
            JSONObject config = JSON.parseObject(printTemplate.getConfigContent());

            // 准备模板元素
            List<Map<String, Object>> elements = prepareElements(config.getJSONArray("elements"), data);

            // 准备模板上下文
            Map<String, Object> context = new HashMap<>();
            context.put("template", printTemplate);
            context.put("elements", elements);
            context.put("data", data);

            // 渲染模板
            Context thymeleafContext = new Context();
            thymeleafContext.setVariables(context);

            return templateEngine.process("print-template", thymeleafContext);
        } catch (Exception e) {
            log.error("生成打印HTML失败", e);
            throw new CustomException("生成打印HTML失败: " + e.getMessage());
        }
    }

    @Override
    public String generatePreviewImage(String html) {
        try {
            // 此处应实现实际的HTML转图片逻辑
            // 例如使用html2canvas+phantomjs或其他工具
            // 以下为模拟实现，生成一个唯一的预览图URL
            String previewId = UUID.randomUUID().toString().replace("-", "");
            return "/api/print/preview/image/" + previewId + ".png";
        } catch (Exception e) {
            log.error("生成预览图片失败", e);
            throw new CustomException("生成预览图片失败: " + e.getMessage());
        }
    }

    private List<Map<String, Object>> prepareElements(JSONArray elements, Map<String, Object> data) {
        List<Map<String, Object>> result = new ArrayList<>();

        if (elements == null) {
            return result;
        }

        for (int i = 0; i < elements.size(); i++) {
            JSONObject element = elements.getJSONObject(i);
            Map<String, Object> processedElement = new HashMap<>(element);
            
            // 确保style属性存在
            if (!processedElement.containsKey("style")) {
                processedElement.put("style", "");
            }

            // 处理动态数据
            if (element.containsKey("dataField") && StringUtils.isNotBlank(element.getString("dataField"))) {
                String dataField = element.getString("dataField");
                Object value = getValueByPath(data, dataField);
                processedElement.put("content", value);
            }

            // 处理特殊元素类型
            if ("table".equals(element.getString("type"))) {
                processTableElement(processedElement, data);
            } else if ("barcode".equals(element.getString("type"))) {
                processBarcodeElement(processedElement, data);
            }

            result.add(processedElement);
        }

        return result;
    }

    private void processTableElement(Map<String, Object> element, Map<String, Object> data) {
        // 处理表格数据源
        if (element.containsKey("dataSource")) {
            String dataSource = (String) element.get("dataSource");
            Object tableData = getValueByPath(data, dataSource);
            if (tableData instanceof Collection) {
                element.put("rows", tableData);
            }
        }
    }

    private void processBarcodeElement(Map<String, Object> element, Map<String, Object> data) {
        // 处理条码数据
        if (element.containsKey("valueField")) {
            String valueField = (String) element.get("valueField");
            Object value = getValueByPath(data, valueField);
            if (value != null) {
                element.put("value", value.toString());
            }
        }
    }

    private Object getValueByPath(Map<String, Object> data, String path) {
        if (data == null || StringUtils.isBlank(path)) {
            return null;
        }

        // 支持嵌套属性访问，如 orderInfo.orderNo
        String[] parts = path.split("\\.");
        Object current = data;

        for (String part : parts) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(part);
                if (current == null) {
                    return null;
                }
            } else {
                return null;
            }
        }

        return current;
    }
}
