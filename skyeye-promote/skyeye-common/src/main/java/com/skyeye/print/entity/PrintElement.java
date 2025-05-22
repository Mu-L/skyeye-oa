package com.skyeye.print.entity;

import lombok.Data;

/**
 * @author skyeye云系列--卫志强
 * @description 打印元素基础实体类
 * @date 2025/5/22 10:30
 */
@Data
public class PrintElement {
    private String type;
    private Integer x;
    private Integer y;
    private String width;
    private String height;
    private Integer rotate;
    private String transformOrigin;
    private Integer opacity;
    private ElementShadow shadow;
    private ElementAnimation animation;

    // 文本元素属性
    private Integer fontSize;
    private Boolean bold;
    private Boolean italic;
    private Boolean underline;
    private String align;
    private String content;

    // 图片元素属性
    private String src;
    private String alt;
    private String fit;

    // 表格元素属性
    private Boolean zebra;
    private Integer rowHeight;
    private Boolean showHeader;
    private java.util.List<TableColumn> columns;
    private java.util.List<java.util.Map<String, Object>> rows;

    // 条码元素属性
    private String value;
    private String barcodeType;
    private String format;
    private String foreground;
    private String background;
    private String errorLevel;
    private Boolean showLabel;
}

@Data
class ElementShadow {
    private Boolean enabled;
    private Integer x;
    private Integer y;
    private Integer blur;
    private String color;
}

@Data
class ElementAnimation {
    private String type;
    private String direction;
    private Integer duration;
    private Integer delay;
}

@Data
class TableColumn {
    private String field;
    private String title;
    private String width;
    private String align;
    private Integer fontSize;
    private Boolean wrap;
} 