package com.shuzi.permissionservice.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色实体类
 * 对应数据库中的 roles 表
 */
@Data // Lombok 注解，自动生成 getter, setter, toString, equals, hashCode 方法
@TableName("roles") // MyBatis-Plus 注解，指定实体类对应的数据库表名
public class Role {

    /**
     * 角色ID，主键
     * 1: 超级管理员 (super_admin)
     * 2: 普通用户 (user)
     * 3: 管理员 (admin)
     */
    @TableId // MyBatis-Plus 注解，标记为主键
    private Integer roleId;

    /**
     * 角色编码，唯一
     * 例如: super_admin, user, admin
     */
    private String roleCode;
}
