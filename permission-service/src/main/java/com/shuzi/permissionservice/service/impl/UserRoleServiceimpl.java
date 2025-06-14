package com.shuzi.permissionservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuzi.permissionservice.entity.UserRole;
import com.shuzi.permissionservice.mapper.RoleMapper;
import com.shuzi.permissionservice.mapper.UserRoleMapper;
import com.shuzi.permissionservice.service.IUserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleServiceimpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    @Override
    public void bindDefaultRole(Long userId) {
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(2);
        save(userRole);
    }

    @Override
    public String getUserRoleCode(Long userId) {
        UserRole userRole = lambdaQuery().eq(UserRole::getUserId, userId).one();
        return roleMapper.selectById(userRole.getRoleId()).getRoleCode();
    }

    @Override
    public void upgradeToAdmin(Long userId) {
        UserRole userRole = lambdaQuery().eq(UserRole::getUserId, userId).one();
        if (userRole.getRoleId() == 3) {
            return;
        }
        userRole.setRoleId(3);
        lambdaUpdate()
                .eq(UserRole::getUserId, userId)
                .update(userRole);
    }

    @Override
    public void downgradeToUser(Long userId) {
        UserRole userRole = lambdaQuery().eq(UserRole::getUserId, userId).one();
        if (userRole.getRoleId() == 2) {
            return;
        }
        userRole.setRoleId(2);
        lambdaUpdate()
                .eq(UserRole::getUserId, userId)
                .update(userRole);
    }

    @Override
    public List<Long> listUserIdsByRole(String roleCode) {
        return userRoleMapper.selectUserIdsByRoleCode(roleCode);
    }
}
