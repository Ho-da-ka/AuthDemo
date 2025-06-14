package com.shuzi.permissionservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shuzi.permissionservice.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;


public interface IUserRoleService extends IService<UserRole> {
    void bindDefaultRole(Long userId);

    String getUserRoleCode(Long userId);

    void upgradeToAdmin(Long userId);

    void downgradeToUser(Long userId);
}
