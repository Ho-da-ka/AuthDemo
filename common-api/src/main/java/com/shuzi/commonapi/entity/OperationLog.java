// 实体类（MyBatis Plus映射）
package com.shuzi.commonapi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("operation_logs")
public class OperationLog {
    
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;
    
    private Long userId;
    
    private String action;
    
    private String ip;
    
    private String detail;


}
