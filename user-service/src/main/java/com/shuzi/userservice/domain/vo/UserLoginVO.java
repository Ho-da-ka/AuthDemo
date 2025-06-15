package com.shuzi.userservice.domain.vo;

import lombok.Data;

@Data
//  用户登录返回结果
public class UserLoginVO {
    private String token;
    private Long userId;
    private String username;
}
