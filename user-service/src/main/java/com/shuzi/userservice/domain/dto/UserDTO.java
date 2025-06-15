package com.shuzi.userservice.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
//用户修改实体
public class UserDTO {

    private String username;

    private String phone;

    private String email;

    private String userRole;
}
