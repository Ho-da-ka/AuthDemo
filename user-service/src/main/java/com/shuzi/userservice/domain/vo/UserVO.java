package com.shuzi.userservice.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class UserVO {
    private Long userId;

    private String username;

    private String phone;

    private String email;

    private LocalDateTime gmtCreate;
}
