package com.cumtb.mp.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cumtb.mp.entity.*;
import com.cumtb.mp.service.impl.*;
import com.cumtb.mp.vo.CommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zheng
 * @since 2021-04-22
 */
@Controller
public class CommentController {

    @Autowired
    CommentServiceImpl commentService;
    @Autowired
    UserServiceImpl userService;
    @Autowired
    AnswerServiceImpl answerService;
    @Autowired
    MessageServiceImpl messageService;
    @Autowired
    QuestionServiceImpl questionService;

    /*展现评论*/
    @PostMapping("/user/showComment")
    @ResponseBody
    public Map<String,Object> showComment(Integer answerId) {
        Map<String,Object> mp = new HashMap<>();
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("answer_id",answerId).orderBy(true,false,"like_count");
        List<Comment> commentList = commentService.list(queryWrapper);
        int total = commentList.size();
        List<CommentVO> voList = new ArrayList<>();
        for (Comment comment : commentList) {
            User user = userService.getById(comment.getAuthorId());
            CommentVO commentVO = new CommentVO();
            commentVO.setId(comment.getId());
            commentVO.setContent(comment.getContent());
            commentVO.setDislikeCount(comment.getDislikeCount());
            commentVO.setLikeCount(comment.getLikeCount());
            commentVO.setUserUrl("/user/homepage/"+comment.getAuthorId());
            commentVO.setPhotoUrl(user.getPhotoUrl());
            commentVO.setUserName(user.getNickName());
            commentVO.setUpdateTime(comment.getUpdateTime());
            voList.add(commentVO);
        }
        mp.put("total",total);
        mp.put("list",voList);
        return mp;
    }

    /*创建评论*/
    @PostMapping("/user/createComment")
    @ResponseBody
    public boolean createComment(Integer sessionId, Integer answerId, String content){
        Comment comment = new Comment();
        LocalDateTime time = LocalDateTime.now();
        comment.setAnswerId(answerId).setAuthorId(sessionId).setContent(content).setUpdateTime(time);
        boolean save = commentService.save(comment);
        if(save) {
            //更新回答的评论数
            Answer answer = answerService.getById(comment.getAnswerId());
            answer.setCommentCount(answer.getCommentCount()+1);
            answerService.updateById(answer);
            //创建通知
            Question question = questionService.getById(answer.getQuestionId());
            messageService.createMessage(answer.getAuthorId(),4,3,comment.getId(),comment.getContent(),sessionId,"/user"+answer.getAnswerUrl(),comment.getUpdateTime());
        }
        return save;
    }

    /*删除评论*/
    @PostMapping("/user/deleteComment")
    @ResponseBody
    public boolean deleteComment(Integer commentId, String message) {
        Comment comment = commentService.getById(commentId);
        if(comment == null) return false;
        String content = comment.getContent();
        Answer answer = answerService.getById(comment.getAnswerId());
        User user = userService.getById(comment.getAuthorId());
        boolean remove = commentService.removeById(commentId);
        if(remove) {
            /*更新回答的评论数*/
            answer.setCommentCount(answer.getCommentCount()-1);
            answerService.updateById(answer);
            /*删除用到该评论的通知*/
            QueryWrapper<Message> messageQueryWrapper = new QueryWrapper<>();
            messageQueryWrapper.eq("object_id",commentId).eq("object_type",3);
            messageService.remove(messageQueryWrapper);
            /*创建通知*/
            messageService.createMessage(user.getId(),6,3,commentId,content+"|"+message,0,"/user"+answer.getAnswerUrl(),LocalDateTime.now());
        }
        return remove;
    }
}
