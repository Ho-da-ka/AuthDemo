// 实体类（MyBatis Plus映射）
package com.shuzi.loggingservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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

    /** 是否成功 */
    private Boolean success;

    /** 失败时的错误信息 */
    @TableField("error_msg")
    private String errorMsg;

}
