package com.shuzi.permissionservice.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色实体类
 * 对应数据库中的 roles 表
 */
@Data
@TableName("roles")
public class Role {

    /**
     * 角色ID，主键
     * 1: 超级管理员 (super_admin)
     * 2: 普通用户 (user)
     * 3: 管理员 (admin)
     */
    @TableId
    private Integer roleId;

    /**
     * 角色编码，唯一
     * 例如: super_admin, user, admin
     */
    private String roleCode;
}
