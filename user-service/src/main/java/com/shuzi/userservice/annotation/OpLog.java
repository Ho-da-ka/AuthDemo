package com.shuzi.userservice.annotation;

import java.lang.annotation.*;

/**
 * 标记需要记录操作日志的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OpLog {
    /** 动作名称 */
    String value();
}