package com.shuzi.userservice.service.impl;

import cn.hutool.core.bean.BeanUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.shuzi.userservice.client.PermissionService;
import com.shuzi.userservice.constants.PermissionConstants;
import com.shuzi.userservice.context.BaseContext;
import com.shuzi.userservice.domain.dto.LoginFormDTO;
import com.shuzi.userservice.domain.dto.PageQueryDTO;
import com.shuzi.userservice.domain.dto.UserDTO;
import com.shuzi.userservice.domain.po.Users;
import com.shuzi.userservice.domain.vo.UserLoginVO;
import com.shuzi.userservice.domain.vo.UserVO;
import com.shuzi.userservice.mapper.UserMapper;
import com.shuzi.userservice.result.PageResult;
import com.shuzi.userservice.service.IUserService;
import com.shuzi.userservice.utils.JwtTool;
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

import static com.shuzi.userservice.constants.PermissionConstants.*;

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

    @OpLog("用户登录")
    @Override
    public UserLoginVO login(LoginFormDTO loginDTO) {
        // 1.数据校验
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
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
        // 6.封装VO返回
        UserLoginVO vo = new UserLoginVO();
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setToken(token);
        return vo;
    }

    /**
     * 注册
     *
     * @param user
     * @return 登录VO
     */
    @OpLog("用户注册")
    @Override
    @Transactional
    public UserLoginVO register(Users user) {
        // 1.检查是否已存在
        Users existUser = lambdaQuery().eq(Users::getUsername, user.getUsername()).one();
        if (existUser != null) {
            log.info("用户已存在");
            return login(BeanUtil.copyProperties(existUser, LoginFormDTO.class));
        }
        // 2.保存用户
        LoginFormDTO loginFormDTO = BeanUtil.copyProperties(user, LoginFormDTO.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setGmtCreate(LocalDateTime.now(ZoneOffset.UTC));
        save(user);
        log.info("用户id:{}", user.getUserId());
        BaseContext.setCurrentId(user.getUserId());
        //  3.绑定角色
        permissionService.bindDefaultRole(user.getUserId());
        return login(loginFormDTO);
    }

    /**
     * 获取用户列表
     *
     * @param pageQueryDTO
     * @return PageResult
     */
    @OpLog(value = "获取用户列表")
    @Override
    public PageResult listUsers(PageQueryDTO pageQueryDTO) {
        Long currentUserId = BaseContext.getCurrentId();
        String currentRole = permissionService.getUserRoleCode(currentUserId);

        int pageNum = pageQueryDTO.getPage();
        int pageSize = pageQueryDTO.getPageSize();

        // 超级管理员：直接分页查询全部
        if (ROLE_SUPER_ADMIN.equals(currentRole)) {
            Page<Users> mpPage = new Page<>(pageNum, pageSize);
            IPage<UserVO> result = page(mpPage, new QueryWrapper<>())
                    .convert(u -> BeanUtil.copyProperties(u, UserVO.class));
            return new PageResult(result.getTotal(), result.getRecords());
        }

        // 普通用户：只返回自己
        if (ROLE_USER.equals(currentRole)) {
            Users user = getById(currentUserId);
            List<UserVO> list = new ArrayList<>();
            if (user != null) {
                list.add(BeanUtil.copyProperties(user, UserVO.class));
            }
            return new PageResult(list.size(), list);
        }

        // 管理员：只看普通用户
        List<Long> userIds = permissionService.listUserIdsByRole("user");
        if (userIds.isEmpty()) {
            return new PageResult(0, java.util.Collections.emptyList());
        }
        Page<Users> mpPage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Users> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Users::getUserId, userIds);
        IPage<UserVO> result = page(mpPage, wrapper)
                .convert(u -> BeanUtil.copyProperties(u, UserVO.class));
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
        Long currentUserId = BaseContext.getCurrentId();
        String currentRole = permissionService.getUserRoleCode(currentUserId);
        Users user = lambdaQuery().eq(Users::getUserId, userid).one();

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        String targetUserRole = permissionService.getUserRoleCode(user.getUserId());

        if (ROLE_SUPER_ADMIN.equals(currentRole)) {
            return BeanUtil.copyProperties(user, UserVO.class);
        }

        if (ROLE_ADMIN.equals(currentRole)) {
            if (ROLE_SUPER_ADMIN.equals(targetUserRole) ||
                    (ROLE_ADMIN.equals(targetUserRole) && !user.getUserId().equals(currentUserId))) {
                throw new RuntimeException("权限不足");
            }
            return BeanUtil.copyProperties(user, UserVO.class);
        }

        if (ROLE_USER.equals(currentRole)) {
            if (!ROLE_USER.equals(targetUserRole) || !user.getUserId().equals(currentUserId)) {
                throw new RuntimeException("权限不足");
            }
            return BeanUtil.copyProperties(user, UserVO.class);
        }

        throw new RuntimeException("权限不足：未知角色");
    }


    /**
     * 更新用户信息
     *
     * @param userid
     * @param userDTO
     */
    @Transactional
    @Override
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

        // 权限校验
        switch (currentRole) {
            case ROLE_SUPER_ADMIN -> {
                if (userDTO.getUserRole().equals(ROLE_SUPER_ADMIN)) {
                    throw new RuntimeException("不能修改超级管理员角色");
                }
                String newRole = userDTO.getUserRole();
                if (newRole.equals(ROLE_ADMIN) && !targetRole.equals(ROLE_ADMIN)) {
                    permissionService.downgradeToUser(targetUserId);
                } else if (newRole.equals(ROLE_USER) && !targetRole.equals(ROLE_USER)) {
                    permissionService.upgradeToAdmin(targetUserId);
                }
            }
            case ROLE_ADMIN -> {
                if (targetRole.equals(ROLE_SUPER_ADMIN)) {
                    throw new RuntimeException("无权限修改超级管理员");
                }
                if (targetRole.equals(ROLE_ADMIN)) {
                    throw new RuntimeException("管理员不能修改其他管理员");
                }
            }
            case ROLE_USER -> {
                if (!(targetRole.equals(ROLE_USER) && currentUserId.equals(targetUserId))) {
                    throw new RuntimeException("只能修改自己的信息");
                }
            }
            default -> throw new RuntimeException("未知用户角色");
        }

        // 数据更新
        Users user = BeanUtil.copyProperties(userDTO, Users.class);
        user.setUserId(targetUserId);

        lambdaUpdate()
                .set(StringUtils.isNotBlank(userDTO.getUsername()), Users::getUsername, userDTO.getUsername())
                .set(StringUtils.isNotBlank(userDTO.getPhone()), Users::getPhone, userDTO.getPhone())
                .set(StringUtils.isNotBlank(userDTO.getEmail()), Users::getEmail, userDTO.getEmail())
                .eq(Users::getUserId, targetUserId)
                .update();
    }


    /**
     * 重置密码
     *
     * @return UserLoginVO
     */
    @Override
    @Transactional
    public boolean resetPassword() {
        Long currentUserId = BaseContext.getCurrentId();
        String currentRole = permissionService.getUserRoleCode(currentUserId);
        if (ROLE_SUPER_ADMIN.equals(currentRole)) {
            lambdaUpdate().set(Users::getPassword, passwordEncoder.encode(DEFAULT_PASSWORD));
        }
        if (ROLE_ADMIN.equals(currentRole)) {
            lambdaUpdate().set(Users::getPassword, passwordEncoder.encode(DEFAULT_PASSWORD))
                    .in(Users::getUserId, permissionService.listUserIdsByRole(ROLE_USER));
        }
        if (ROLE_USER.equals(currentRole)) {
            lambdaUpdate().set(Users::getPassword, passwordEncoder.encode(DEFAULT_PASSWORD))
                    .eq(Users::getUserId, currentUserId);
        }
        return true;
    }

}
