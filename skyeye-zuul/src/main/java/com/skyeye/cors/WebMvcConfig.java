/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.cors;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName: WebMvcConfig
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2024/11/24 9:26
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 配置跨域
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")// 设置允许跨域的路径
            .allowedOriginPatterns("*")// 设置允许跨域请求的域名
            .allowCredentials(true)// 是否允许证书 不再默认开启
            .allowedMethods("GET", "POST", "PUT", "DELETE")// 设置允许的方法
            .maxAge(3600);// 跨域允许时间
    }
}
