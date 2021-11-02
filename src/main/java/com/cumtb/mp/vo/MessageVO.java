package com.cumtb.mp.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageVO {

    private Integer id;

    private String icon;

    private String color;

    private String title;

    private String titleUrl;

    private String content;

    private String msg;

    private Integer stateType;

    private Integer objectType;

    private String userName;

    private String userUrl;

    private LocalDateTime updateTime;
}
