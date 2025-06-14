package com.shuzi.permissionservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shuzi.permissionservice.entity.Role;
import com.shuzi.permissionservice.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
}
