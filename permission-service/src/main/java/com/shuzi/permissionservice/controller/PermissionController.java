package com.shuzi.permissionservice.controller;

import com.shuzi.permissionservice.service.IUserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Permission")
@RequiredArgsConstructor
public class PermissionController {

    private final IUserRoleService userRoleService;

    // 绑定默认角色（普通用户）
    @PutMapping("/{userId}")
    void bindDefaultRole(@PathVariable Long userId) {
        userRoleService.bindDefaultRole(userId);
    }

    // 查询用户角色码（返回role_code）
    @GetMapping("/{userId}")
    String getUserRoleCode(@PathVariable Long userId) {
        return userRoleService.getUserRoleCode(userId);
    }

    // 超管调用：升级用户为管理员
    @PutMapping("/upgrade/{userId}")
    void upgradeToAdmin(@PathVariable Long userId) {
        userRoleService.upgradeToAdmin(userId);
    }

    // 超管调用：降级用户为普通角色
    @PutMapping("/downgrade/{userId}")
    void downgradeToUser(@PathVariable Long userId) {
        userRoleService.downgradeToUser(userId);
    }

}
