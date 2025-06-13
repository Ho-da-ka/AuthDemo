package com.shuzi.userservice.context;

public class BaseContext {

    public static ThreadLocal<Long> userId = new ThreadLocal<>();
    public static ThreadLocal<String> userIp = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        userId.set(id);
    }
    public static void setCurrentIp(String ip) {
        userIp.set(ip);
    }

    public static Long getCurrentId() {
        return userId.get();
    }
    public static String getCurrentIp() {
        return userIp.get();
    }

    public static void removeCurrentId() {
        userId.remove();
    }
    public static void removeCurrentIp() {
        userIp.remove();
    }

}
