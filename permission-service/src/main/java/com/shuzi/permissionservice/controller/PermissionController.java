package com.shuzi.permissionservice.controller;

import com.shuzi.permissionservice.entity.UserRole;
import com.shuzi.permissionservice.service.IUserRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Api(tags = "权限管理相关接口")
@RestController
@RequestMapping("/Permission")
@RequiredArgsConstructor
public class PermissionController {

    private final IUserRoleService userRoleService;

    /**
     * 绑定默认角色（普通用户）
     *
     * @param userId
     */
    @ApiOperation("绑定默认角色（普通用户）")
    @PutMapping("/{userId}")
    void bindDefaultRole(@PathVariable Long userId) {
        userRoleService.bindDefaultRole(userId);
    }

    /**
     * 获取用户角色
     *
     * @param userId
     * @return role_code
     */
    @ApiOperation("查询用户角色码（返回role_code）")
    @GetMapping("/{userId}")
    String getUserRoleCode(@PathVariable Long userId) {
        return userRoleService.getUserRoleCode(userId);
    }

    /**
     * 超管调用：升级用户为管理员
     *
     * @param userId
     */
    @ApiOperation("超管调用：升级用户为管理员")
    @PutMapping("/upgrade/{userId}")
    void upgradeToAdmin(@PathVariable Long userId) {
        userRoleService.upgradeToAdmin(userId);
    }

    /**
     * 超管调用：降级用户为普通角色
     *
     * @param userId
     */
    @ApiOperation("超管调用：降级用户为普通角色")
    @PutMapping("/downgrade/{userId}")
    void downgradeToUser(@PathVariable Long userId) {
        userRoleService.downgradeToUser(userId);
    }

    /**
     * 根据角色查询用户id列表
     *
     * @param roleCode 角色编码
     * @return 用户id列表
     */
    @ApiOperation("根据角色查询用户id列表")
    @GetMapping("/users/{roleCode}")
    java.util.List<Long> listUserIdsByRole(@PathVariable String roleCode) {
        return userRoleService.listUserIdsByRole(roleCode);
    }

    /**
     * 绑定超级管理员角色
     *
     * @param userId 用户id
     *               默认密码 123456
     */
    @Transactional
    @PutMapping("/super_admin/{userId}")
    void bindSuperAdminRole(@PathVariable Long userId) {
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(1);
        userRoleService.save(userRole);
    }


}
