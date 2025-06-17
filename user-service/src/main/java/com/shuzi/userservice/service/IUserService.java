package com.shuzi.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shuzi.userservice.domain.dto.LoginFormDTO;

import com.shuzi.userservice.domain.dto.PageQueryDTO;
import com.shuzi.userservice.domain.dto.RegisterFormDTO;
import com.shuzi.userservice.domain.dto.UserDTO;
import com.shuzi.userservice.domain.po.Users;
import com.shuzi.userservice.domain.vo.UserLoginVO;
import com.shuzi.userservice.domain.vo.UserVO;
import com.shuzi.userservice.result.PageResult;


/**
 * 用户表 服务类
 */
public interface IUserService extends IService<Users> {

    UserLoginVO login(LoginFormDTO loginFormDTO);

    UserLoginVO register(RegisterFormDTO user);

    PageResult listUsers(PageQueryDTO loginFormDTO);

    UserVO selectUser(String userid);

    void updateUser(String userid, UserDTO userDTO);

    boolean resetPassword();
}
