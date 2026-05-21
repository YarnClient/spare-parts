package com.example.spareparts.config;

import com.example.spareparts.filter.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtFilterRegistration() {
        FilterRegistrationBean<JwtAuthFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(jwtAuthFilter);
        reg.addUrlPatterns("/api/*");
        reg.setOrder(1);
        return reg;
    }
}
