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
 * 解析 config_json 中的 items 列表。
 * 约定 JSON：{"items":[{"attrKey":"xxx","columnTitle":"可选列标题"}]}
 */
public final class ImportExportConfigJsonHelper {

    private ImportExportConfigJsonHelper() {
    }

    @Data
    public static class ColumnSpec {
        private String attrKey;
        /** 配置里显式写的列名，可为空 */
        private String columnTitle;
    }

    public static List<ColumnSpec> parseColumnSpecs(String configJson) {
        List<ColumnSpec> result = new ArrayList<>();
        if (StrUtil.isBlank(configJson)) {
            return result;
        }
        JSONObject root = JSONUtil.parseObj(configJson);
        JSONArray items = root.getJSONArray("items");
        if (items == null || items.isEmpty()) {
            return result;
        }
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
            result.add(spec);
        }
        return result;
    }
}
