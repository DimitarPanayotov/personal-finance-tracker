//package com.dimitar.financetracker.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
////allows backend to trust frontend
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/api/**") //alowing CORS for api endpoints
//            .allowedOrigins("http://localhost:5173") //the origin of the React app
//            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
//            .allowedHeaders("*")
//            .allowCredentials(true);
//    }
//}
