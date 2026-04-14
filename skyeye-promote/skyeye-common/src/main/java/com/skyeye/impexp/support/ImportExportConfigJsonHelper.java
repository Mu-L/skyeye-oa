/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.impexp.support;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析 config_json：根级可配表头/数据行高、默认表头色；items 中可配列宽与表头样式。
 * <pre>
 * {
 *   "headerRowHeight": 22,
 *   "dataRowHeight": 18,
 *   "defaultHeaderBackgroundColor": "#4472C4",
 *   "defaultHeaderFontColor": "#FFFFFF",
 *   "items": [{
 *     "attrKey": "name",
 *     "columnTitle": "名称",
 *     "columnWidth": 5000,
 *     "headerBackgroundColor": "#E2EFDA",
 *     "headerFontColor": "#000000"
 *   }]
 * }
 * </pre>
 * columnWidth 与 POI Sheet#setColumnWidth 一致（1/256 字符宽）。
 */
public final class ImportExportConfigJsonHelper {

    private ImportExportConfigJsonHelper() {
    }

    @Data
    public static class SheetLayoutOptions {
        /**
         * 表头行高（磅），如 15～30
         */
        private Float headerRowHeight;
        /**
         * 数据行行高（磅）
         */
        private Float dataRowHeight;
        /**
         * 表头默认背景色 #RRGGBB
         */
        private String defaultHeaderBackgroundColor;
        /**
         * 表头默认字体色 #RRGGBB
         */
        private String defaultHeaderFontColor;
    }

    @Data
    public static class ColumnSpec {
        private String attrKey;
        /**
         * 配置里显式写的列名，可为空
         */
        private String columnTitle;
        /**
         * 列宽，POI 单位，与 Sheet#setColumnWidth 一致；为空则用系统默认
         */
        private Integer columnWidth;
        /**
         * 该列表头背景色，可覆盖根级 default
         */
        private String headerBackgroundColor;
        /**
         * 该列表头字体色
         */
        private String headerFontColor;
    }

    @Data
    public static class ParsedConfig {
        private SheetLayoutOptions layout = new SheetLayoutOptions();
        private List<ColumnSpec> items = new ArrayList<>();
    }

    public static ParsedConfig parseConfig(String configJson) {
        ParsedConfig out = new ParsedConfig();
        if (StrUtil.isBlank(configJson)) {
            return out;
        }
        JSONObject root = JSONUtil.parseObj(configJson);
        SheetLayoutOptions layout = out.getLayout();
        if (root.containsKey("headerRowHeight")) {
            layout.setHeaderRowHeight(root.getFloat("headerRowHeight"));
        }
        if (root.containsKey("dataRowHeight")) {
            layout.setDataRowHeight(root.getFloat("dataRowHeight"));
        }
        layout.setDefaultHeaderBackgroundColor(StrUtil.blankToDefault(root.getStr("defaultHeaderBackgroundColor"), null));
        layout.setDefaultHeaderFontColor(StrUtil.blankToDefault(root.getStr("defaultHeaderFontColor"), null));

        JSONArray items = root.getJSONArray("items");
        if (items == null || items.isEmpty()) {
            return out;
        }
        List<ColumnSpec> result = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            JSONObject row = items.getJSONObject(i);
            if (row == null) {
                continue;
            }
            String attrKey = row.getStr("attrKey");
            if (StrUtil.isBlank(attrKey)) {
                continue;
            }
            ColumnSpec spec = new ColumnSpec();
            spec.setAttrKey(attrKey);
            String title = row.getStr("columnTitle");
            if (StrUtil.isBlank(title)) {
                title = row.getStr("title");
            }
            if (StrUtil.isBlank(title)) {
                title = row.getStr("name");
            }
            spec.setColumnTitle(StrUtil.isBlank(title) ? null : title);
            if (row.containsKey("columnWidth")) {
                spec.setColumnWidth(row.getInt("columnWidth"));
            }
            spec.setHeaderBackgroundColor(StrUtil.blankToDefault(row.getStr("headerBackgroundColor"), null));
            spec.setHeaderFontColor(StrUtil.blankToDefault(row.getStr("headerFontColor"), null));
            result.add(spec);
        }
        out.setItems(result);
        return out;
    }
}
