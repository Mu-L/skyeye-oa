/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.print.service;

import com.skyeye.print.entity.PrintTemplate;

import java.util.Map;

/**
 * @ClassName: PrintHtmlGenerator
 * @Description: 打印HTML生成器接口
 * @author: skyeye云系列--卫志强
 * @date: 2025/5/15 8:49
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface PrintHtmlGenerator {

    /**
     * 生成打印HTML
     * @param template 模板信息
     * @param data 打印数据
     * @return HTML内容
     */
    String generateHtml(PrintTemplate template, Map<String, Object> data);
    
    /**
     * 生成预览图片
     * @param html HTML内容
     * @return 预览图片URL
     */
    String generatePreviewImage(String html);

}
