package com.shuzi.userservice.controller;


import com.shuzi.userservice.domain.dto.RegisterFormDTO;
import com.shuzi.userservice.domain.dto.UserDTO;
import com.shuzi.userservice.result.PageResult;
import com.shuzi.userservice.domain.dto.LoginFormDTO;
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
        return Result.success(login);
    }

    @ApiOperation("用户注册接口")
    @PostMapping("/register")
    public Result<UserLoginVO> register(@RequestBody @Validated RegisterFormDTO registerFormDTO, HttpServletRequest request) {
        UserLoginVO register = userService.register(registerFormDTO);
        return Result.success(register);
    }

    @ApiOperation("分页用户列表接口")
    @GetMapping()
    public Result<PageResult> listUsers(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageResult pageResult = userService.listUsers(page, pageSize);
        return Result.success(pageResult);
    }

    @ApiOperation("查询用户信息接口")
    @GetMapping("/{userid}")
    public Result<UserVO> selectUser(@PathVariable String userid) {
        UserVO userVO = userService.selectUser(userid);
        return Result.success(userVO);
    }

    @ApiOperation("修改用户信息接口")
    @PutMapping("/{userid}")
    public Result updateUser(@PathVariable String userid, @RequestBody UserDTO userDTO) {
        userService.updateUser(userid, userDTO);
        return Result.success();
    }

    @ApiOperation("密码重置接口")
    @PostMapping("/reset-password")
    public Result resetPassword() {
        boolean reset = userService.resetPassword();
        return Result.success();
    }

}

