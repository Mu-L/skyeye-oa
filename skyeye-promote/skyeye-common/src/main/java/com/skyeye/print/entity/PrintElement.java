package com.skyeye.print.entity;

import lombok.Data;

import java.util.List;

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
    private String position;  // 定位方式：absolute/relative
    private String marginLeft; // margin-left值
    private String marginTop;  // margin-top值
    private String value;  // 组件元素绑定的键。如果：图片组件、条码组件、表格组件等，该属性为必填项。

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
    private Boolean zebra;      // 斑马纹
    private Integer rowHeight;  // 行高
    private Boolean showHeader; // 显示表头
    private List<TableColumn> columns; // 列配置
    private transient java.util.List<java.util.Map<String, Object>> rows; // 运行时数据，不序列化

    // 条码元素属性
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