package com.SaasRRHH.main.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads/documentos}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String rutaAbsoluta = new File(uploadDir).getAbsolutePath();

        registry.addResourceHandler("/uploads/documentos/**")
                .addResourceLocations("file:" + rutaAbsoluta + File.separator);
    }
}