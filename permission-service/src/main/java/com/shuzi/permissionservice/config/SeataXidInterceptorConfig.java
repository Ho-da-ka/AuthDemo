package com.shuzi.permissionservice.config;

import io.seata.core.context.RootContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Binds Seata XID carried by upstream HTTP header to the current thread so that
 * permission-service can participate in the same global transaction.
 * 此类由ai辅助生成
 */
@Configuration
public class SeataXidInterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull Object handler) {
                String xid = request.getHeader(RootContext.KEY_XID);
                if (StringUtils.hasText(xid)) {
                    RootContext.bind(xid);
                }
                return true;
            }

            @Override
            public void afterCompletion(@NonNull HttpServletRequest request,
                                        @NonNull HttpServletResponse response,
                                        @NonNull Object handler,
                                        Exception ex) {
                if (RootContext.inGlobalTransaction()) {
                    RootContext.unbind();
                }
            }
        });
    }
} 