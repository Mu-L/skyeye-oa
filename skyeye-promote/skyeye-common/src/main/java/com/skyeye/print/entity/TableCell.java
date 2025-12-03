/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.print.entity;

import lombok.Data;

import java.util.List;

/**
 * @ClassName: TableCell
 * @Description: 表格容器单元格实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/5/24 10:55
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
public class TableCell {
    private Integer row;          // 行索引
    private Integer col;          // 列索引
    private Integer rowspan;     // 行合并数
    private Integer colspan;      // 列合并数
    private Boolean isMerged;     // 是否被合并
    private String horizontalAlign; // 水平对齐：left/center/right
    private String verticalAlign;   // 垂直对齐：top/middle/bottom
    private List<PrintElement> elements; // 单元格内的元素列表
}

