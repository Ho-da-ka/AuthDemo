package com.shuzi.userservice.config;


import com.shuzi.userservice.interceptor.IPInterceptor;
import com.shuzi.userservice.interceptor.JwtTokenUserInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;


import java.util.List;

/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class WebMvcConfiguration extends WebMvcConfigurationSupport {


    private final IPInterceptor ipInterceptor;

    private final JwtTokenUserInterceptor jwtTokenUserInterceptor;

    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/users/login")
                .excludePathPatterns("/users/register");

        registry.addInterceptor(ipInterceptor)
                .addPathPatterns("/**");
    }
}
