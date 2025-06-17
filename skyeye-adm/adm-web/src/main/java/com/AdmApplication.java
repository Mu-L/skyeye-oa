/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@EnableTransactionManagement//启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@ComponentScan(basePackages = {"com.skyeye"})
@EnableDiscoveryClient // 开启服务发现
@EnableFeignClients
public class AdmApplication {

    @Value("${IMAGES_PATH}")
    private String tPath;

    public static void main(String[] args) {
        System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(AdmApplication.class, args);
    }

    /**
     * 文件上传配置
     *
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setLocation(tPath);
        // 单个文件最大，KB,MB 100MB
        factory.setMaxFileSize(DataSize.ofMegabytes(100));
        // 设置总上传数据总大小，1000MB
        factory.setMaxRequestSize(DataSize.ofMegabytes(1000));
        return factory.createMultipartConfig();
    }

}
