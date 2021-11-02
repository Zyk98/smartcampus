package com.cumtb.mp.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DynamicVO {

    private Integer id;

    private String title;

    private String titleUrl;

    private String content;

    private String userName;

    private String userUrl;

    private Integer type;

    private boolean state;

    private LocalDateTime updateTime;

}
