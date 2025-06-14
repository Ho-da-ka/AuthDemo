/*
 * @Author: Hodaka 2841006960@qq.com
 * @Date: 2025-06-13 10:56:15
 * @LastEditors: Hodaka 2841006960@qq.com
 * @LastEditTime: 2025-06-14 11:19:29
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.shuzi.userservice.client;

import com.shuzi.userservice.client.fallback.PermissionServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

// RPC接口定义
@Component
@FeignClient(name = "permission-service", path = "/Permission", fallback = PermissionServiceFallback.class)
public interface PermissionService {

    /** 绑定默认角色（普通用户） */
    @PutMapping("/{userId}")
    void bindDefaultRole(@PathVariable("userId") Long userId);

    /** 查询用户角色码（返回role_code） */
    @GetMapping("/{userId}")
    String getUserRoleCode(@PathVariable("userId") Long userId);

    /** 超管调用：升级用户为管理员 */
    @PutMapping("/upgrade/{userId}")
    void upgradeToAdmin(@PathVariable("userId") Long userId);

    /** 超管调用：降级用户为普通角色 */
    @PutMapping("/downgrade/{userId}")
    void downgradeToUser(@PathVariable("userId") Long userId);

    /** 根据角色编码查询用户id列表 */
    @GetMapping("/users/{roleCode}")
    java.util.List<Long> listUserIdsByRole(@PathVariable("roleCode") String roleCode);
}