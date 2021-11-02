package com.cumtb.mp.service.impl;

import com.cumtb.mp.entity.Comment;
import com.cumtb.mp.mapper.CommentMapper;
import com.cumtb.mp.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zheng
 * @since 2021-04-22
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

}
