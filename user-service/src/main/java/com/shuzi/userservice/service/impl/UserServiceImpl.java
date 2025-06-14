package com.shuzi.userservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.shuzi.userservice.client.PermissionService;
import com.shuzi.userservice.context.BaseContext;
import com.shuzi.userservice.domain.dto.LoginFormDTO;
import com.shuzi.userservice.domain.po.OperationLog;
import com.shuzi.userservice.domain.po.Users;
import com.shuzi.userservice.domain.vo.UserLoginVO;
import com.shuzi.userservice.domain.vo.UserVO;
import com.shuzi.userservice.mapper.UserMapper;
import com.shuzi.userservice.service.IUserService;
import com.shuzi.userservice.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
    private final RabbitTemplate rabbitTemplate;
    private final PermissionService permissionService;

    @Override
    public UserLoginVO login(LoginFormDTO loginDTO) {
        // 1.数据校验
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        // 2.根据用户名查询
        Users user = lambdaQuery().eq(Users::getUsername, username).one();
        Assert.notNull(user, "用户名错误");
        // 3.校验密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.error("用户名或密码错误");
            return null;
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
        // 4.发送日志消息至MQ
        user.setPassword("*********");
        OperationLog operationLog = new OperationLog();
        operationLog.setAction("用户注册");
        operationLog.setIp(BaseContext.getCurrentIp());
        operationLog.setDetail(JSON.toJSONString(user));
        operationLog.setUserId(BaseContext.getCurrentId());
        rabbitTemplate.convertAndSend("log.topic", operationLog);
        return login(loginFormDTO);
    }

    /**
     * 获取用户列表
     *
     * @param loginFormDTO
     * @return
     */
    @Override
    public List<UserVO> listUsers(LoginFormDTO loginFormDTO) {
        return List.of();
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
        return null;
    }

}
