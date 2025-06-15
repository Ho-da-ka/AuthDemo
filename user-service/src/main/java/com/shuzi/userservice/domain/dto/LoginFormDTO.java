package com.shuzi.userservice.domain.dto;

import lombok.Data;


@Data
//登录表单实体
public class LoginFormDTO {
    //用户名
    private String username;
    //密码
    private String password;
}
