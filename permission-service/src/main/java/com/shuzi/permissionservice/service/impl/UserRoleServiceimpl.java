package com.shuzi.permissionservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuzi.permissionservice.entity.UserRole;
import com.shuzi.permissionservice.mapper.RoleMapper;
import com.shuzi.permissionservice.mapper.UserRoleMapper;
import com.shuzi.permissionservice.service.IUserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleServiceimpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    /**
     * 绑定用户默认角色
     *
     * @param userId
     */
    @Override
    @Transactional
    public void bindDefaultRole(Long userId) {
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(2);
        save(userRole);
    }

    /**
     * 获取用户角色
     *
     * @param userId
     * @return
     */
    @Override
    public String getUserRoleCode(Long userId) {
        UserRole userRole = lambdaQuery().eq(UserRole::getUserId, userId).one();
        if (userRole == null) {
            return null;
        }
        return roleMapper.selectById(userRole.getRoleId()).getRoleCode();
    }

    /**
     * 升级为管理员
     *
     * @param userId
     */
    @Override
    @Transactional
    public void upgradeToAdmin(Long userId) {
        UserRole userRole = lambdaQuery().eq(UserRole::getUserId, userId).one();
        if (userRole == null) {
            throw new RuntimeException("用户不存在");
        }
        if (userRole.getRoleId() == 3) {
            return;
        }
        userRole.setRoleId(3);
        lambdaUpdate()
                .eq(UserRole::getUserId, userId)
                .update(userRole);
    }

    /**
     * 降级为普通用户
     *
     * @param userId
     */
    @Override
    @Transactional
    public void downgradeToUser(Long userId) {
        UserRole userRole = lambdaQuery().eq(UserRole::getUserId, userId).one();
        if (userRole == null) {
            throw new RuntimeException("用户不存在");
        }
        if (userRole.getRoleId() == 2) {
            return;
        }
        userRole.setRoleId(2);
        lambdaUpdate()
                .eq(UserRole::getUserId, userId)
                .update(userRole);
    }

    /**
     * 根据用户编码批量获取用户角色
     *
     * @param roleCode
     * @return List<Long>
     */
    @Override
    public List<Long> listUserIdsByRole(String roleCode) {
        return userRoleMapper.selectUserIdsByRoleCode(roleCode);
    }
}
