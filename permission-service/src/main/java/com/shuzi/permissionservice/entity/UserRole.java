package com.shuzi.permissionservice.entity; // 请根据您的权限服务包结构调整

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;

/**
 * 用户-角色关系实体类
 * 对应数据库中的 user_roles 表
 */
@Data
@TableName("user_roles")
public class UserRole {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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

}
