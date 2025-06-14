// 实体类（MyBatis Plus映射）
package com.shuzi.userservice.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

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
