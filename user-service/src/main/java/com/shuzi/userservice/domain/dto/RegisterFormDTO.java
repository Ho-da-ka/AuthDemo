package com.shuzi.userservice.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
//注册表单实体
public class RegisterFormDTO {

    private String username;

    private String password;

    private String phone;

    private String email;

}
