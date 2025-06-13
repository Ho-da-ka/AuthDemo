package com.shuzi.userservice.interceptor;

import com.shuzi.userservice.context.BaseContext;
import com.shuzi.userservice.utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class IPInterceptor implements HandlerInterceptor {


    /**
     * 解析IP地址
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }
            IpUtils.getClientIP(request);
            String ip = IpUtils.getClientIP(request);
            log.info("当前用户ip：{}", ip);
            BaseContext.setCurrentIp(ip);
            //3、通过，放行
            return true;
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContext.removeCurrentIp();
    }
}
