package com.shuzi.userservice.init;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;


import com.shuzi.commonapi.client.PermissionService;
import com.shuzi.commonapi.constants.PermissionConstants;
import com.shuzi.userservice.domain.po.Users;
import com.shuzi.userservice.service.IUserService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.transaction.api.TransactionType;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.shuzi.commonapi.constants.PermissionConstants.DEFAULT_PASSWORD;
import static com.shuzi.commonapi.constants.PermissionConstants.ROLE_SUPER_ADMIN;

@Slf4j
@Component
@RequiredArgsConstructor
public class SuperAdminInitializer implements ApplicationRunner {

    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final PermissionService permissionService;

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    public void run(ApplicationArguments args) {
        String superAdminName = ROLE_SUPER_ADMIN;
        Users exist = userService.getOne(
                new LambdaQueryWrapper<Users>().eq(Users::getUsername, superAdminName)
        );
        if (exist != null) {
            log.info("超级管理员已存在，跳过初始化");
            return;
        }

        Users admin = new Users();
        admin.setUsername(superAdminName);
        admin.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        admin.setPhone("00000000000");
        admin.setEmail("super_admin@example.com");
        admin.setGmtCreate(java.time.LocalDateTime.now());
        userService.save(admin);

        // 给超级管理员绑定角色
        permissionService.bindSuperAdminRole(admin.getUserId());

        log.info("已自动初始化超级管理员，默认密码 123456，userid={}", admin.getUserId());
    }
}