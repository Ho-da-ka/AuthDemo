package com.shuzi.loggingservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.shuzi.commonapi.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}
