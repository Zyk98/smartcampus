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
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 通知用户id
     */
    private Integer uid;

    /**
     * 1-like,2-dislike,3-collect,4-comment,5-warn,6-delete
     */
    private Integer stateType;

    /**
     * 1-question,2-answer,3-comment
     */
    private Integer objectType;

    /**
     * 通知对象的id
     */
    private Integer objectId;

    /**
     * 跳转链接
     */
    private String url;

    /**
     * 通知信息
     */
    private String message;

    /**
     * 通知信息中的用户id
     */
    private Integer messageUid;

    /**
     * 状态，默认未读
     */
    private Boolean state;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
