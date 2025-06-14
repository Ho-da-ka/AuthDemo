package com.shuzi.permissionservice.entity; // 请根据您的权限服务包结构调整

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户-角色关系实体类
 * 对应数据库中的 user_roles 表
 */
@Data // Lombok 注解，自动生成 getter, setter, toString, equals, hashCode 方法
@TableName("user_roles") // MyBatis-Plus 注解，指定实体类对应的数据库表名
public class UserRole {

    /**
     * 主键ID
     */
    @TableId // MyBatis-Plus 注解，标记为主键，默认使用数据库自增
    private Long id;

    /**
     * 用户ID
     * 对应用户服务中的 user_id
     */
    private Long userId;

    /**
     * 角色ID
     * 对应 roles 表中的 role_id
     */
    private Integer roleId;

    // 注意：数据库中定义的 UNIQUE KEY uk_user_role (user_id) 确保了每个用户只有一个角色绑定。
    // 这在实体类中无法直接通过注解体现，需要在业务逻辑层或数据库层面进行约束。
}
