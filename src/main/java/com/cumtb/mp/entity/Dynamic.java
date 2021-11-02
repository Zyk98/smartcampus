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
public class Dynamic implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 接受动态的用户id
     */
    private Integer followerId;

    /**
     * 动态对象的用户id
     */
    private Integer followId;

    /**
     * 1为提问，2为回答
     */
    private Integer type;

    /**
     * 动态对象的id(问题和回答)
     */
    private Integer objectId;

    /**
     * 状态，默认未读
     */
    private Boolean state;

    /**
     * 跳转链接
     */
    private String url;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
