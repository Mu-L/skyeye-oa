/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.print;

/**
 * @ClassName: PrintTemplateConstants
 * @Description: 打印模板相关常量
 * @author: skyeye云系列--卫志强
 * @date: 2025/5/15 12:30
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface PrintTemplateConstants {

    /**
     * 纸张大小
     */
    interface PaperSize {
        String A3 = "A3";
        String A4 = "A4";
        String A5 = "A5";
        String LETTER = "LETTER";
    }

    /**
     * 纸张方向
     */
    interface Orientation {
        String PORTRAIT = "portrait";
        String LANDSCAPE = "landscape";
    }

    /**
     * 元素类型
     */
    interface ElementType {
        String TEXT = "text";
        String IMAGE = "image";
        String BARCODE = "barcode";
        String TABLE = "table";
    }

    /**
     * 条码类型
     */
    interface BarcodeType {
        String BARCODE = "barcode";
        String QRCODE = "qrcode";
    }

    /**
     * 条码格式
     */
    interface BarcodeFormat {
        String CODE39 = "CODE39";
        String CODE128 = "CODE128";
        String EAN13 = "EAN13";
        String EAN8 = "EAN8";
        String UPC = "UPC";
        String ITF = "ITF";
        String MSI = "MSI";
    }
} 