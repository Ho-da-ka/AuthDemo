package com.shuzi.userservice.service.impl;

import cn.hutool.core.bean.BeanUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.shuzi.userservice.client.PermissionService;
import com.shuzi.userservice.context.BaseContext;
import com.shuzi.userservice.domain.dto.LoginFormDTO;
import com.shuzi.userservice.domain.dto.PageQueryDTO;
import com.shuzi.userservice.domain.po.Users;
import com.shuzi.userservice.domain.vo.UserLoginVO;
import com.shuzi.userservice.domain.vo.UserVO;
import com.shuzi.userservice.mapper.UserMapper;
import com.shuzi.userservice.result.PageResult;
import com.shuzi.userservice.service.IUserService;
import com.shuzi.userservice.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.shuzi.userservice.annotation.OpLog;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

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
    @Override
    public PageResult listUsers(PageQueryDTO pageQueryDTO) {
        Long currentUserId = BaseContext.getCurrentId();
        String currentRole = permissionService.getUserRoleCode(currentUserId);

        int pageNum = pageQueryDTO.getPage();
        int pageSize = pageQueryDTO.getPageSize();

        // 超级管理员：直接分页查询全部
        if ("super_admin".equals(currentRole)) {
            Page<Users> mpPage = new Page<>(pageNum, pageSize);
            IPage<UserVO> result = page(mpPage, new QueryWrapper<>())
                    .convert(u -> BeanUtil.copyProperties(u, UserVO.class));
            return new PageResult(result.getTotal(), result.getRecords());
        }

        // 普通用户：只返回自己
        if ("user".equals(currentRole)) {
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
    public UserVO selectUser(String userid) {

        return null;
    }

    /**
     * 更新用户信息
     *
     * @param userid
     * @return UserLoginVO
     */
    @Override
    public boolean updateUser(String userid) {
        return true;
    }

    /**
     * 重置密码
     *
     * @return UserLoginVO
     */
    @Override
    public boolean resetPassword() {
        return true;
    }

}
