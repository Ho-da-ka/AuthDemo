package com.shuzi.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shuzi.userservice.domain.dto.LoginFormDTO;

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

    /**
     * 仅执行本地库写操作，不涉及 RPC；由 AccountFacadeService 在全局事务中调用。
     */
    UserLoginVO register(RegisterFormDTO user);

    PageResult listUsers(Integer page, Integer pageSize);

    UserVO selectUser(String userid);

    /**
     * 仅执行本地库更新（users 表），不涉及权限角色调整的远程调用。
     */
    void updateUser(String userid, UserDTO userDTO);

    boolean resetPassword();
}
