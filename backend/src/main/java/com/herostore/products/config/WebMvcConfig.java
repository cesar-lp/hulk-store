package com.herostore.products.config;

import com.herostore.products.mapper.custom.FileTypeMapper;
import com.herostore.products.mapper.custom.ProductStockConditionMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Configuration
public class WebMvcConfig {

    @Bean
    public WebMvcConfigurer myWebMvcConfig(ProductStockConditionMapper stockConditionMapper, FileTypeMapper fileTypeMapper) {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders(
                                CONTENT_TYPE,
                                ACCESS_CONTROL_ALLOW_HEADERS,
                                ACCESS_CONTROL_EXPOSE_HEADERS,
                                CONTENT_DISPOSITION)
                        .exposedHeaders(CONTENT_DISPOSITION);
            }

            @Override
            public void addFormatters(FormatterRegistry registry) {
                registry.addConverter(stockConditionMapper);
                registry.addConverter(fileTypeMapper);
            }
        };
    }
}
