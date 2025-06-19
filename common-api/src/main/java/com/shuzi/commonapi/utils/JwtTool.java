package com.shuzi.commonapi.utils;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;

import java.util.HashMap;
import java.util.Map;

public class JwtTool {

    // 密钥，用于签名和验证JWT，应保密且足够复杂
    private static final byte[] KEY = "1234567890abcdefgh123456789abcdefgh".getBytes();


    /**
     * 生成 JWT Token
     * @return JWT Token 字符串
     */
    public static String generateJwt(Long userId,  String username) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("username", username);
        payload.put("expire_time", System.currentTimeMillis() + 60 * 60 * 10000); // 10小时过期

        // 使用 HMAC 签名算法，并传入密钥
        return JWTUtil.createToken(payload, KEY);
    }

    /**
     * 校验 JWT Token
     * @param token JWT Token 字符串
     * @return 校验是否通过
     */
    public static boolean verifyJwt(String token) {
        // Hutool 默认使用 HS256 算法，并自动验证签名和过期时间
        // 如果 token 无效（签名不正确或已过期），verify() 方法会返回 false
        return JWTUtil.verify(token, KEY);
    }

    /**
     * 获取 JWT Token 中的载荷 (Payload)
     * @param token JWT Token 字符串
     * @return 载荷 Map
     */
    public static Map<String, Object> getJwtPayload(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        return jwt.getPayloads();
    }
}