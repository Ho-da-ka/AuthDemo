package com.shuzi.userservice.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * Propagate Seata XID to downstream services so that they can join the same global transaction.
     */
    @Bean
    public feign.RequestInterceptor seataFeignRequestInterceptor() {
        return requestTemplate -> {
            String xid = io.seata.core.context.RootContext.getXID();
            if (xid != null && !xid.isEmpty()) {
                requestTemplate.header(io.seata.core.context.RootContext.KEY_XID, xid);
            }
        };
    }
}
