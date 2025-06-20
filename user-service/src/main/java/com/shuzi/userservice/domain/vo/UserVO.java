package com.shuzi.userservice.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
//用户信息返回实体
public class UserVO {
    private Long userId;

    private String username;

    private String phone;

    private String email;

    private LocalDateTime gmtCreate;

    private String userRole;
}
