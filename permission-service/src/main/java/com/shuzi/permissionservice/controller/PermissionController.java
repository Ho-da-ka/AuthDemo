package com.shuzi.permissionservice.controller;

import com.shuzi.permissionservice.service.IUserRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "权限管理相关接口")
@RestController
@RequestMapping("/Permission")
@RequiredArgsConstructor
public class PermissionController {

    private final IUserRoleService userRoleService;

    // 绑定默认角色（普通用户）
    @ApiOperation("绑定默认角色（普通用户）")
    @PutMapping("/{userId}")
    void bindDefaultRole(@PathVariable Long userId) {
        userRoleService.bindDefaultRole(userId);
    }

    @ApiOperation("查询用户角色码（返回role_code）")
    // 查询用户角色码（返回role_code）
    @GetMapping("/{userId}")
    String getUserRoleCode(@PathVariable Long userId) {
        return userRoleService.getUserRoleCode(userId);
    }

    @ApiOperation("超管调用：升级用户为管理员")
    // 超管调用：升级用户为管理员
    @PutMapping("/upgrade/{userId}")
    void upgradeToAdmin(@PathVariable Long userId) {
        userRoleService.upgradeToAdmin(userId);
    }

    @ApiOperation("超管调用：降级用户为普通角色")
    // 超管调用：降级用户为普通角色
    @PutMapping("/downgrade/{userId}")
    void downgradeToUser(@PathVariable Long userId) {
        userRoleService.downgradeToUser(userId);
    }

    // 根据角色查询用户id列表
    @ApiOperation("根据角色查询用户id列表")
    @GetMapping("/users/{roleCode}")
    java.util.List<Long> listUserIdsByRole(@PathVariable String roleCode) {
        return userRoleService.listUserIdsByRole(roleCode);
    }

}
