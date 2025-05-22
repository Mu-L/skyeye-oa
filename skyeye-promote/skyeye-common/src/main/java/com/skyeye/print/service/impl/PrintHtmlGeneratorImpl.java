/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.print.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.skyeye.exception.CustomException;
import com.skyeye.print.entity.PrintElement;
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
            List<PrintElement> elements = prepareElements(config.getJSONArray("elements"), data);

            // 准备模板上下文
            Map<String, Object> context = new java.util.HashMap<>();
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

    private List<PrintElement> prepareElements(JSONArray elements, Map<String, Object> data) {
        List<PrintElement> result = new ArrayList<>();

        if (elements == null) {
            return result;
        }

        for (int i = 0; i < elements.size(); i++) {
            JSONObject element = elements.getJSONObject(i);
            PrintElement processedElement = JSON.parseObject(element.toJSONString(), PrintElement.class);
            
            // 处理动态数据
            if (StringUtils.isNotBlank(element.getString("dataField"))) {
                String dataField = element.getString("dataField");
                Object value = getValueByPath(data, dataField);
                processedElement.setContent(value != null ? value.toString() : null);
            }

            // 处理特殊元素类型
            if ("table".equals(processedElement.getType())) {
                processTableElement(processedElement, data);
            } else if ("barcode".equals(processedElement.getType())) {
                processBarcodeElement(processedElement, data);
            }

            result.add(processedElement);
        }

        return result;
    }

    private void processTableElement(PrintElement element, Map<String, Object> data) {
        // 处理表格数据源
        if (element.getRows() == null && StringUtils.isNotBlank(element.getContent())) {
            Object tableData = getValueByPath(data, element.getContent());
            if (tableData instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> rows = (List<Map<String, Object>>) tableData;
                element.setRows(rows);
            }
        }
    }

    private void processBarcodeElement(PrintElement element, Map<String, Object> data) {
        // 处理条码数据
        if (StringUtils.isBlank(element.getValue()) && StringUtils.isNotBlank(element.getContent())) {
            Object value = getValueByPath(data, element.getContent());
            if (value != null) {
                element.setValue(value.toString());
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
