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
    private Integer fontSize;  // 字体大小

    // 文本元素属性
    private Boolean bold;
    private Boolean italic;
    private Boolean underline;
    private String align;
    private String content;

    // 图片元素属性
    private String sourceType;  // 图片来源类型：upload/variable
    private String url;         // 上传图片的URL
    private String src;         // 最终的图片URL
    private String alt;         // 图片替代文本
    private String fit;         // 图片填充方式

    // 表格元素属性
    private Boolean zebra;      // 斑马纹
    private Integer rowHeight;  // 行高
    private Boolean showHeader; // 显示表头
    private List<TableColumn> columns; // 列配置
    private transient java.util.List<java.util.Map<String, Object>> rows; // 运行时数据，不序列化

    // 条码元素属性
    private String barcodeType;    // 条码类型：barcode/qrcode
    private String format;         // 条码格式：CODE128/CODE39/EAN13等
    private String foreground;     // 前景色
    private String background;     // 背景色
    private String errorLevel;     // 二维码纠错级别：L/M/Q/H
    private Boolean showLabel;     // 是否显示文本
    private String textPosition;   // 文本位置：bottom/top
    private Integer barWidth;      // 条码宽度
    private String valueType;      // 条码值类型：variable/static

    // 新增边框和样式相关属性
    private Integer borderWidth;
    private String borderStyle;
    private String borderColor;
    private String backgroundColor;
    private Integer borderRadius;
    private String borderRadiusUnit;
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