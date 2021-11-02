package com.cumtb.mp.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeedbackVO {

    public Integer id;

    public String title;

    public String content;

    public String userName;

    public String userUrl;

    public LocalDateTime updateTime;

}
