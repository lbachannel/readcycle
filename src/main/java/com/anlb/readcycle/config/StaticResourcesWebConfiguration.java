package com.anlb.readcycle.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class StaticResourcesWebConfiguration implements WebMvcConfigurer {

    private final MaintenanceInterceptor maintenanceInterceptor;

    @Value("${anlb.upload-file.base-uri}")
    private String baseURI;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(baseURI);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(maintenanceInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/admin/maintenance")
                .excludePathPatterns("/api/v1/auth/login")
                .excludePathPatterns("/api/v1/auth/logout");
    }
}
