package com.shuzi.userservice.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户表
 */
@Data
public class User implements Serializable {
    @TableId
    private Long userId;

    private String username;

    private String password;

    private String phone;

    private String email;

    private LocalDateTime gmtCreate;
}
