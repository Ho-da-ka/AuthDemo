package com.shuzi.permissionservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shuzi.permissionservice.entity.Role;
import com.shuzi.permissionservice.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
    /** 根据角色编码查询所有用户id */
    @Select("SELECT ur.user_id FROM user_roles ur JOIN roles r ON ur.role_id = r.role_id WHERE r.role_code = #{roleCode}")
    java.util.List<Long> selectUserIdsByRoleCode(String roleCode);
}
