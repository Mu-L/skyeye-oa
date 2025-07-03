package com.skyeye;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {
        //用于启用Spring Boot的自动配置功能并排除SecurityAutoConfiguration类，即跳过默认的安全自动配置
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@EnableTransactionManagement//启用注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@ComponentScan(basePackages = {"com.skyeye"})
@EnableDiscoveryClient // 开启服务发现
@EnableFeignClients
public class SkyPortalApplication {

    public static void main(String[] args) {
        // 该行代码的作用是禁用Spring Boot DevTools的自动重启功能，防止项目在开发时因类路径变化而自动重启应用。
        System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(SkyPortalApplication.class, args);
    }
}
