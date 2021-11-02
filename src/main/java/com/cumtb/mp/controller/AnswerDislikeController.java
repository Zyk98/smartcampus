package com.cumtb.mp.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cumtb.mp.entity.Answer;
import com.cumtb.mp.entity.AnswerDislike;
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
public class AnswerDislikeController {

    @Autowired
    AnswerServiceImpl answerService;
    @Autowired
    UserServiceImpl userService;
    @Autowired
    AnswerDislikeServiceImpl answerDislikeService;
    @Autowired
    MessageServiceImpl messageService;
    @Autowired
    QuestionServiceImpl questionService;

    @PostMapping("/user/answerDislikeClick")
    @ResponseBody
    public boolean answerDislikeClick(Integer answerId, Integer sessionId){
        QueryWrapper<AnswerDislike> answerDislikeQueryWrapper = new QueryWrapper<>();
        answerDislikeQueryWrapper.eq("uid",sessionId).eq("answer_id",answerId);
        AnswerDislike answerDislike = answerDislikeService.getOne(answerDislikeQueryWrapper);
        if (answerDislike != null) {
            boolean state = answerDislike.getState();
            //点击前是未踩状态
            if(state == false) {
                //更新回答的踩数
                Answer answer = answerService.getById(answerId);
                answer.setDislikeCount(answer.getDislikeCount()+1);
                answerService.updateById(answer);
                answerDislike.setState(true);
                //创建/更新踩通知
                Question question = questionService.getById(answer.getQuestionId());
                LocalDateTime time = LocalDateTime.now();
                messageService.createMessage(answer.getAuthorId(),2,2,answerId,question.getTitle(),sessionId,"/user"+answer.getAnswerUrl(),time);
                //更新踩中间表
                answerDislikeService.updateById(answerDislike);
            } else {
                Answer answer = answerService.getById(answerId);
                answer.setDislikeCount(answer.getDislikeCount()-1);
                answerService.updateById(answer);
                answerDislike.setState(false);
                answerDislikeService.updateById(answerDislike);
            }
            return answerDislike.getState();
        }
        return false;
    }

}
