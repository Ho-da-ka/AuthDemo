package com.shuzi.userservice.controller;


import com.shuzi.userservice.domain.dto.LoginFormDTO;
import com.shuzi.userservice.domain.po.Users;
import com.shuzi.userservice.domain.vo.UserLoginVO;
import com.shuzi.userservice.domain.vo.UserVO;
import com.shuzi.userservice.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.shuzi.userservice.utils.IpUtils;
import java.util.List;

@Api(tags = "用户相关接口")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @ApiOperation("用户登录接口")
    @PostMapping("/login")
    public UserLoginVO login(@RequestBody @Validated LoginFormDTO loginFormDTO) {

        return userService.login(loginFormDTO);
    }

    @ApiOperation("用户注册接口")
    @PostMapping("/register")
    public UserLoginVO register(@RequestBody @Validated Users user,HttpServletRequest request) {

        return userService.register(user);
    }

    @ApiOperation("分页用户列表接口")
    @GetMapping()
    public List<UserVO> listUsers(@RequestBody @Validated LoginFormDTO loginFormDTO,HttpServletRequest request) {

        return userService.listUsers(loginFormDTO);
    }

    @ApiOperation("查询用户信息接口")
    @GetMapping("/{userid}")
    public UserVO selectUser(@PathVariable String userid,HttpServletRequest request) {
        String clientIP = IpUtils.getClientIP(request);
        return userService.selectUser(userid);
    }

    @ApiOperation("修改用户信息接口")
    @PutMapping("/{userid}")
    public UserLoginVO updateUser(@PathVariable String userid,HttpServletRequest request) {
        String clientIP = IpUtils.getClientIP(request);
        return userService.updateUser(userid);
    }

    @ApiOperation("密码重置接口")
    @PostMapping("/reset-password")
    public UserLoginVO resetPassword(HttpServletRequest request) {
        String clientIP = IpUtils.getClientIP(request);
        return userService.resetPassword();
    }

}

