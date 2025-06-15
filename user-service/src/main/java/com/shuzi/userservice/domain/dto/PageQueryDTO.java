package com.shuzi.userservice.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
//分页查询参数
public class PageQueryDTO implements Serializable {
    //页码
    private int page;
    //每页记录数
    private int pageSize;


}