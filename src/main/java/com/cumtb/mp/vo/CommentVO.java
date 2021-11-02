package com.cumtb.mp.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentVO {

    private Integer id;

    private String photoUrl;

    private String userUrl;

    private String userName;

    private String content;

    private Integer likeCount;

    private Integer dislikeCount;

    private LocalDateTime updateTime;

}
