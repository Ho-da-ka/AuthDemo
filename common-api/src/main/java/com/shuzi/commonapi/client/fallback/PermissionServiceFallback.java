package com.shuzi.commonapi.client.fallback;

import com.shuzi.commonapi.client.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PermissionServiceFallback implements FallbackFactory<PermissionService> {
    @Override
    public PermissionService create(Throwable cause) {
        return new PermissionService() {
            @Override
            public void bindDefaultRole(Long userId) {
                log.error("远程调用PermissionService#bindDefaultRole方法出现异常，参数：{}", userId, cause);
            }

            @Override
            public String getUserRoleCode(Long userId) {
                log.error("远程调用PermissionService#getUserRoleCode方法出现异常，参数：{}", userId, cause);
                return "error";
            }

            @Override
            public void upgradeToAdmin(Long userId) {
                log.error("远程调用PermissionService#upgradeToAdmin方法出现异常，参数：{}", userId, cause);
            }

            @Override
            public void downgradeToUser(Long userId) {
                log.error("远程调用PermissionService#downgradeToUser方法出现异常，参数：{}", userId, cause);
            }

            @Override
            public List<Long> listUserIdsByRole(String roleCode) {
                log.error("远程调用PermissionService#listUserIdsByRole方法出现异常，参数：{}", roleCode, cause);
                return List.of();
            }

            @Override
            public void bindSuperAdminRole(Long userId) {
                log.error("远程调用PermissionService#bindSuperAdminRole方法出现异常，参数：{}", userId, cause);
            }
        };
    }
}