package com.shuzi.userservice.service.impl;

import cn.hutool.core.bean.BeanUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.shuzi.commonapi.client.PermissionService;
import com.shuzi.commonapi.utils.JwtTool;
import com.shuzi.userservice.context.BaseContext;
import com.shuzi.userservice.domain.dto.LoginFormDTO;
import com.shuzi.userservice.domain.dto.RegisterFormDTO;
import com.shuzi.userservice.domain.dto.UserDTO;
import com.shuzi.userservice.domain.po.Users;
import com.shuzi.userservice.domain.vo.UserLoginVO;
import com.shuzi.userservice.domain.vo.UserVO;
import com.shuzi.userservice.mapper.UserMapper;
import com.shuzi.userservice.result.PageResult;
import com.shuzi.userservice.service.IUserService;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.shuzi.userservice.annotation.OpLog;

import java.util.ArrayList;
import java.util.List;

import static com.shuzi.commonapi.constants.PermissionConstants.*;

import io.seata.spring.annotation.GlobalTransactional;

/**
 * 用户表 服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, Users> implements IUserService {

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final PermissionService permissionService;

    /**
     * 用户登录
     *
     * @param loginDTO
     * @return 登录信息
     */
    @OpLog("用户登录")
    @Override
    public UserLoginVO login(LoginFormDTO loginDTO) {
        // 1.数据校验
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            log.error("用户名或密码不能为空");
            throw new RuntimeException("用户名或密码不能为空");
        }
        // 2.根据用户名查询
        Users user = lambdaQuery().eq(Users::getUsername, username).one();
        BaseContext.setCurrentId(user.getUserId());
        // 3.校验密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.error("用户名或密码错误");
            throw new RuntimeException("用户名或密码错误");
        }
        // 5.生成TOKEN
        String token = JwtTool.generateJwt(user.getUserId(), user.getUsername());
        BaseContext.setCurrentId(user.getUserId());
        // 6.封装VO返回
        UserLoginVO vo = new UserLoginVO();
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setToken(token);
        return vo;
    }

    @OpLog("用户注册")
    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    public UserLoginVO register(RegisterFormDTO registerFormDTO) {
        // 1.检查是否已存在,如果存在直接返回登录信息
        LoginFormDTO loginFormDTO = BeanUtil.copyProperties(registerFormDTO, LoginFormDTO.class);
        Users existUser = lambdaQuery().eq(Users::getUsername, registerFormDTO.getUsername()).one();
        if (existUser != null) {
            log.info("用户已存在");
            return login(loginFormDTO);
        }
        // 2.保存用户
        Users user = BeanUtil.copyProperties(registerFormDTO, Users.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setGmtCreate(LocalDateTime.now(ZoneOffset.UTC));
        save(user);
        log.info("注册的用户id:{}", user.getUserId());
        BaseContext.setCurrentId(user.getUserId());
        // 3. 远程绑定默认角色（Feign 已透传 XID）
        permissionService.bindDefaultRole(user.getUserId());
        int i=1/0;
        // 4.注册完成后直接复用登录逻辑生成 token
        return login(loginFormDTO);
    }


    /**
     * 获取用户列表
     *
     * @param page,pageSize
     * @param pageSize
     * @return PageResult
     */
    @OpLog(value = "获取用户列表")
    @Override
    public PageResult listUsers(Integer page, Integer pageSize) {
        Long currentUserId = BaseContext.getCurrentId();
        String currentRole = permissionService.getUserRoleCode(currentUserId);
        // 超级管理员：直接分页查询全部
        if (ROLE_SUPER_ADMIN.equals(currentRole)) {
            Page<Users> mpPage = new Page<>(page, pageSize);
            IPage<UserVO> result = page(mpPage, new QueryWrapper<>())
                    .convert(u -> {
                        UserVO vo = BeanUtil.copyProperties(u, UserVO.class);
                        vo.setUserRole(permissionService.getUserRoleCode(u.getUserId()));
                        return vo;
                    });
            return new PageResult(result.getTotal(), result.getRecords());
        }

        // 普通用户：只返回自己
        if (ROLE_USER.equals(currentRole)) {
            Users user = getById(currentUserId);
            List<UserVO> list = new ArrayList<>();
            if (user != null) {
                UserVO vo = BeanUtil.copyProperties(user, UserVO.class);
                vo.setUserRole(permissionService.getUserRoleCode(user.getUserId()));
                list.add(vo);
            }
            return new PageResult(list.size(), list);
        }

        // 管理员：只看普通用户
        List<Long> userIds = permissionService.listUserIdsByRole("user");
        if (userIds.isEmpty()) {
            return new PageResult(0, java.util.Collections.emptyList());
        }
        Page<Users> mpPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Users> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Users::getUserId, userIds);
        IPage<UserVO> result = page(mpPage, wrapper)
                .convert(u -> {
                    UserVO vo = BeanUtil.copyProperties(u, UserVO.class);
                    vo.setUserRole(permissionService.getUserRoleCode(u.getUserId()));
                    return vo;
                });
        return new PageResult(result.getTotal(), result.getRecords());
    }

    /**
     * 获取用户信息
     *
     * @param userid
     * @return UserVO
     */
    @Override
    @OpLog(value = "获取用户信息")
    public UserVO selectUser(String userid) {
        // 将字符串userid转换为Long避免分片算法类型不匹配
        if (!org.apache.commons.lang3.math.NumberUtils.isCreatable(userid)) {
            throw new RuntimeException("用户ID格式错误");
        }
        Long userIdLong = Long.parseLong(userid);
        Long currentUserId = BaseContext.getCurrentId();
        String currentRole = permissionService.getUserRoleCode(currentUserId);
        Users user = lambdaQuery().eq(Users::getUserId, userIdLong).one();

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        String targetUserRole = permissionService.getUserRoleCode(user.getUserId());

        if (ROLE_SUPER_ADMIN.equals(currentRole)) {
            UserVO vo = BeanUtil.copyProperties(user, UserVO.class);
            vo.setUserRole(targetUserRole);
            return vo;
        }

        if (ROLE_ADMIN.equals(currentRole)) {
            if (ROLE_SUPER_ADMIN.equals(targetUserRole) ||
                    (ROLE_ADMIN.equals(targetUserRole) && !user.getUserId().equals(currentUserId))) {
                throw new RuntimeException("权限不足");
            }
            UserVO vo = BeanUtil.copyProperties(user, UserVO.class);
            vo.setUserRole(targetUserRole);
            return vo;
        }

        if (ROLE_USER.equals(currentRole)) {
            if (!ROLE_USER.equals(targetUserRole) || !user.getUserId().equals(currentUserId)) {
                throw new RuntimeException("权限不足");
            }
            UserVO vo = BeanUtil.copyProperties(user, UserVO.class);
            vo.setUserRole(targetUserRole);
            return vo;
        }

        throw new RuntimeException("权限不足：未知角色");
    }

    @OpLog(value = "更新用户信息")
    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(String userid, UserDTO userDTO) {
        // 输入校验
        if (!NumberUtils.isCreatable(userid)) {
            throw new RuntimeException("用户ID格式错误");
        }
        Long currentUserId = BaseContext.getCurrentId();
        Long targetUserId = Long.parseLong(userid);

        // 角色获取与缓存
        String currentRole = permissionService.getUserRoleCode(currentUserId);
        String targetRole = permissionService.getUserRoleCode(targetUserId);
        String newRole = userDTO.getUserRole();
        // 权限校验
        switch (currentRole) {
            case ROLE_SUPER_ADMIN -> {
                if (newRole.equals(ROLE_SUPER_ADMIN)) {
                    throw new RuntimeException("不能新增超级管理员");
                }
                if (ROLE_ADMIN.equals(newRole) && !ROLE_ADMIN.equals(targetRole)) {
                    // 角色升降级
                    permissionService.upgradeToAdmin(targetUserId);
                } else if (ROLE_USER.equals(newRole) && !ROLE_USER.equals(targetRole)) {
                    permissionService.downgradeToUser(targetUserId);
                }
            }
            case ROLE_ADMIN -> {
                if (!newRole.isEmpty()) {
                    throw new RuntimeException("无权限修改用户权限");
                }
                if (targetRole.equals(ROLE_SUPER_ADMIN)) {
                    throw new RuntimeException("无权限修改超级管理员");
                }
                if (!(targetRole.equals(ROLE_ADMIN) && currentUserId.equals(targetUserId))) {
                    throw new RuntimeException("管理员不能修改其他管理员");
                }
            }
            case ROLE_USER -> {
                if (!newRole.isEmpty()) {
                    throw new RuntimeException("无权限修改用户权限");
                }
                if (!(targetRole.equals(ROLE_USER) && currentUserId.equals(targetUserId))) {
                    throw new RuntimeException("只能修改自己的信息");
                }
            }
            default -> throw new RuntimeException("未知用户角色");
        }

        // 数据更新
        boolean changed = false;
        var wrapper = lambdaUpdate().eq(Users::getUserId, targetUserId);
        if (StringUtils.isNotBlank(userDTO.getUsername())) {
            wrapper.set(Users::getUsername, userDTO.getUsername());
            changed = true;
        }
        if (StringUtils.isNotBlank(userDTO.getPhone())) {
            wrapper.set(Users::getPhone, userDTO.getPhone());
            changed = true;
        }
        if (StringUtils.isNotBlank(userDTO.getEmail())) {
            wrapper.set(Users::getEmail, userDTO.getEmail());
            changed = true;
        }
        if (changed) {
            wrapper.update();
        }
    }

    /**
     * 重置密码
     *
     * @return UserLoginVO
     */
    @OpLog(value = "重置密码")
    @Override
    @Transactional
    public boolean resetPassword() {
        Long currentUserId = BaseContext.getCurrentId();
        String currentRole = permissionService.getUserRoleCode(currentUserId);
        if (ROLE_SUPER_ADMIN.equals(currentRole)) {
            lambdaUpdate().set(Users::getPassword, passwordEncoder.encode(DEFAULT_PASSWORD)).update();
        }
        if (ROLE_ADMIN.equals(currentRole)) {
            lambdaUpdate().set(Users::getPassword, passwordEncoder.encode(DEFAULT_PASSWORD))
                    .in(Users::getUserId, permissionService.listUserIdsByRole(ROLE_USER)).update();
        }
        if (ROLE_USER.equals(currentRole)) {
            lambdaUpdate().set(Users::getPassword, passwordEncoder.encode(DEFAULT_PASSWORD))
                    .eq(Users::getUserId, currentUserId).update();
        }
        return true;
    }

}
