package com.fast.framework.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j OpenAPI 3 配置类
 *
 * @author fast-frame
 */
@Configuration
public class Knife4jConfig {

    /**
     * 创建 OpenAPI 文档配置
     */
    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Fast Frame API 文档")
                .description("Fast Frame 后台管理系统接口文档")
                .version("1.0.0")
                .contact(new Contact()
                    .name("fast-frame")));
    }
}