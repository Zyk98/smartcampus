package com.cumtb.mp.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatVO {

    private Integer id;

    private String userName;

    private String photoUrl;

    private String userUrl;

    private String content;

    private LocalDateTime updateTime;

}
