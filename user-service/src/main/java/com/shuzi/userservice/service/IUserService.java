package com.shuzi.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shuzi.userservice.domain.dto.LoginFormDTO;
import com.shuzi.userservice.domain.po.User;
import com.shuzi.userservice.domain.vo.UserLoginVO;
import com.shuzi.userservice.domain.vo.UserVO;

import java.util.List;


/**
 * 用户表 服务类
 */
public interface IUserService extends IService<User> {

    UserLoginVO login(LoginFormDTO loginFormDTO);

    UserLoginVO register(LoginFormDTO loginFormDTO);

    List<UserVO> listUsers(LoginFormDTO loginFormDTO);

    UserVO selectUser(String userid);

    UserLoginVO updateUser(String userid);

    UserLoginVO resetPassword();
}
