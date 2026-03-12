package com.campus.system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/Knife4j API 文档配置
 * 访问地址：http://localhost:8080/doc.html
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("一体化智慧校园系统 API")
                        .version("1.0.0")
                        .description("涵盖系统管理(sys)、教研管理(edu)、校园服务(svc)三大业务域的完整后端接口文档")
                        .contact(new Contact()
                                .name("Campus Platform Team")
                                .email("Linxiaowuuuuu@gmail.com")));
    }
}
