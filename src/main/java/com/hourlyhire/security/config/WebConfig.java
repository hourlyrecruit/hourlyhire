package com.hourlyhire.security.config;

import com.hourlyhire.shared.config.StringToEnumConverterFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final StringToEnumConverterFactory stringToEnumConverterFactory;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");
    }

    @Override
    public void addFormatters(FormatterRegistry registry){
        registry.addConverterFactory(stringToEnumConverterFactory);
    }

    @Override
    public void configureMessageConverters(HttpMessageConverters.ServerBuilder builder){
        JacksonJsonHttpMessageConverter jsonHttpMessageConverter = new JacksonJsonHttpMessageConverter();
        List<MediaType> supportMediaTypes = new ArrayList<>(jsonHttpMessageConverter.getSupportedMediaTypes());
        supportMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
        jsonHttpMessageConverter.setSupportedMediaTypes(supportMediaTypes);
        builder.withJsonConverter(jsonHttpMessageConverter);

    }
}
