package com.cumtb.mp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author zheng
 * @since 2021-04-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 作者id
     */
    private Integer authorId;

    /**
     * 正文
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 踩数
     */
    private Integer dislikeCount;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 回答id
     */
    private Integer answerId;


}
