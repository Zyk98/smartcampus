package com.cumtb.mp.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cumtb.mp.entity.Answer;
import com.cumtb.mp.entity.AnswerLike;
import com.cumtb.mp.entity.Question;
import com.cumtb.mp.service.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zheng
 * @since 2021-04-22
 */
@Controller
public class AnswerLikeController {
    @Autowired
    AnswerServiceImpl answerService;
    @Autowired
    UserServiceImpl userService;
    @Autowired
    AnswerLikeServiceImpl answerLikeService;
    @Autowired
    MessageServiceImpl messageService;
    @Autowired
    QuestionServiceImpl questionService;

    @PostMapping("/user/answerLikeClick")
    @ResponseBody
    public boolean answerLikeClick(Integer answerId, Integer sessionId){
        QueryWrapper<AnswerLike> answerLikeQueryWrapper = new QueryWrapper<>();
        answerLikeQueryWrapper.eq("uid",sessionId).eq("answer_id",answerId);
        AnswerLike answerLike = answerLikeService.getOne(answerLikeQueryWrapper);
        if (answerLike != null) {
            boolean state = answerLike.getState();
            //点击前是未收藏状态
            if(state == false) {
                //更新回答的点赞数
                Answer answer = answerService.getById(answerId);
                answer.setLikeCount(answer.getLikeCount()+1);
                answerService.updateById(answer);
                answerLike.setState(true);
                //创建/更新点赞通知
                Question question = questionService.getById(answer.getQuestionId());
                LocalDateTime time = LocalDateTime.now();
                messageService.createMessage(answer.getAuthorId(),1,2,answerId,question.getTitle(),sessionId,"/user"+answer.getAnswerUrl(),time);
                //更新收藏中间表
                answerLikeService.updateById(answerLike);
            } else {
                Answer answer = answerService.getById(answerId);
                answer.setLikeCount(answer.getLikeCount()-1);
                answerService.updateById(answer);
                answerLike.setState(false);
                answerLikeService.updateById(answerLike);
            }
            return answerLike.getState();
        }
        return false;
    }
}
