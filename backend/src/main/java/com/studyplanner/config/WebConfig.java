package com.studyplanner.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path frontendPath = Paths.get("../frontend").toAbsolutePath().normalize();
        String frontendUri = frontendPath.toUri().toString();

        Path localFrontendPath = Paths.get("frontend").toAbsolutePath().normalize();
        String localFrontendUri = localFrontendPath.toUri().toString();

        registry.addResourceHandler("/**")
                .addResourceLocations(
                        "classpath:/static/",
                        frontendUri,
                        localFrontendUri,
                        "file:./frontend/",
                        "file:../frontend/"
                );
    }
}
