package com.shuzi.userservice.interceptor;

import com.shuzi.commonapi.utils.JwtTool;
import com.shuzi.userservice.context.BaseContext;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;


/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {


    /**
     * 校验jwt
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
        //1、从请求头中获取令牌
        String token = request.getHeader("Authorization");
        // token a Bearer 开头的判断
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        //2、校验令牌
        try {
            log.info("jwt校验:{}", token);
            if (!JwtTool.verifyJwt(token)){
                log.info("令牌过期");
                throw new RuntimeException("令牌过期");
            }
            Map<String, Object> payload = JwtTool.getJwtPayload(token);
            Long userId = Long.valueOf(payload.get("userId").toString());
            log.info("当前用户id：{}", userId);
            BaseContext.setCurrentId(userId);
            //3、通过，放行
            return true;
        } catch (Exception ex) {
            //4、不通过，响应401状态码
            response.setStatus(401);
            return false;
        }
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContext.removeCurrentId();

    }
}
