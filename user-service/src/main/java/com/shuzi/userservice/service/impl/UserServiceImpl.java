package com.shuzi.userservice.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.shuzi.userservice.domain.dto.LoginFormDTO;
import com.shuzi.userservice.domain.po.User;
import com.shuzi.userservice.domain.vo.UserLoginVO;
import com.shuzi.userservice.domain.vo.UserVO;
import com.shuzi.userservice.mapper.UserMapper;
import com.shuzi.userservice.service.IUserService;
import com.shuzi.userservice.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 用户表 服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserLoginVO login(LoginFormDTO loginDTO) {
        // 1.数据校验
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        // 2.根据用户名查询
        User user = lambdaQuery().eq(User::getUsername, username).one();
        Assert.notNull(user, "用户名错误");
        // 3.校验密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
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

    @Override
    public UserLoginVO register(LoginFormDTO loginFormDTO) {
        return null;
    }

    @Override
    public List<UserVO> listUsers(LoginFormDTO loginFormDTO) {
        return List.of();
    }

    @Override
    public UserVO selectUser(String userid) {
        return null;
    }

    @Override
    public UserLoginVO updateUser(String userid) {
        return null;
    }

    @Override
    public UserLoginVO resetPassword() {
        return null;
    }

}
