package com.shuzi.permissionservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shuzi.permissionservice.entity.UserRole;

import java.util.List;


public interface IUserRoleService extends IService<UserRole> {
    void bindDefaultRole(Long userId);

    String getUserRoleCode(Long userId);

    void upgradeToAdmin(Long userId);

    void downgradeToUser(Long userId);

    /** 根据角色编码批量查询用户id */
    List<Long> listUserIdsByRole(String roleCode);
}
