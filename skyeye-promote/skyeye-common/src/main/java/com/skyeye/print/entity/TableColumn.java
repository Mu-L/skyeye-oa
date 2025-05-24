/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.print.entity;

import lombok.Data;

/**
 * @ClassName: TableColumn
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2025/5/24 10:55
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
public class TableColumn {
    private String key;          // 列标识
    private String field;        // 字段名
    private String title;        // 列标题
    private Integer width;       // 宽度(px)
    private String align;        // 对齐方式
    private Integer fontSize;    // 字体大小
    private Boolean wrap;        // 自动换行
}
