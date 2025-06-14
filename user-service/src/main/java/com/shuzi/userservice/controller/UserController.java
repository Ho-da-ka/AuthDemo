package com.shuzi.userservice.controller;


import com.shuzi.userservice.domain.dto.LoginFormDTO;
import com.shuzi.userservice.domain.po.Users;
import com.shuzi.userservice.domain.vo.UserLoginVO;
import com.shuzi.userservice.domain.vo.UserVO;
import com.shuzi.userservice.result.Result;
import com.shuzi.userservice.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "用户相关接口")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @ApiOperation("用户登录接口")
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody @Validated LoginFormDTO loginFormDTO) {
        UserLoginVO login = userService.login(loginFormDTO);
        if (login != null) {
            return Result.success(login);
        } else {
            return Result.error("用户名或密码错误");
        }
    }

    @ApiOperation("用户注册接口")
    @PostMapping("/register")
    public Result<UserLoginVO> register(@RequestBody @Validated Users user, HttpServletRequest request) {
        UserLoginVO register = userService.register(user);
        return Result.success(register);
    }

    @ApiOperation("分页用户列表接口")
    @GetMapping()
    public Result<List<UserVO>> listUsers(@RequestBody @Validated LoginFormDTO loginFormDTO, HttpServletRequest request) {
        List<UserVO> userVOS = userService.listUsers(loginFormDTO);
        return Result.success(userVOS);
    }

    @ApiOperation("查询用户信息接口")
    @GetMapping("/{userid}")
    public Result<UserVO> selectUser(@PathVariable String userid) {
        UserVO userVO = userService.selectUser(userid);
        return Result.success(userVO);
    }

    @ApiOperation("修改用户信息接口")
    @PutMapping("/{userid}")
    public Result updateUser(@PathVariable String userid) {
        if (userService.updateUser(userid)) {
            return Result.success();
        } else {
            return Result.error("修改用户信息失败,用户不存在或权限不够");
        }
    }

    @ApiOperation("密码重置接口")
    @PostMapping("/reset-password")
    public Result resetPassword() {
        if (userService.resetPassword()) {
            return Result.success();
        } else {
            return Result.error("修密码重置失败,用户不存在");
        }
    }

}

