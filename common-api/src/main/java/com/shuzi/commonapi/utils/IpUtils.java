package com.shuzi.commonapi.utils;

import jakarta.servlet.http.HttpServletRequest;
/**
 * 此类由ai辅助生成
 */
public class IpUtils {
    public static String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        } else {
            // X-Forwarded-For可能包含多个IP，第一个为真实客户端IP
            int commaIndex = ip.indexOf(',');
            if (commaIndex > 0) {
                ip = ip.substring(0, commaIndex).trim();
            }
        }
        return ip;
    }
}
