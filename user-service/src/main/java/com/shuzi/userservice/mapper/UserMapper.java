package com.shuzi.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.shuzi.userservice.domain.po.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表 Mapper 接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
