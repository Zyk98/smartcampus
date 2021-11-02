package com.cumtb.mp.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cumtb.mp.entity.*;
import com.cumtb.mp.service.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
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
public class AnswerController {
    @Autowired
    UserServiceImpl userService;
    @Autowired
    QuestionServiceImpl questionService;
    @Autowired
    AnswerServiceImpl answerService;
    @Autowired
    FollowFollowerServiceImpl followFollowerService;
    @Autowired
    DynamicServiceImpl dynamicService;
    @Autowired
    MessageServiceImpl messageService;
    @Autowired
    CommentServiceImpl commentService;
    @Autowired
    AnswerLikeServiceImpl answerLikeService;
    @Autowired
    AnswerDislikeServiceImpl answerDislikeService;
    @Autowired
    AnswerCollectServiceImpl answerCollectService;

    /*添加回答*/
    @PostMapping("/user/addAnswer")
    @ResponseBody
    public Map<String,Object> addAnswer(Integer authorId, Integer questionId, String content){
        HashMap<String,Object> mp = new HashMap<>();

        Question question = questionService.getById(questionId);
        Answer answer = new Answer();
        answer.setAuthorId(authorId);
        answer.setQuestionId(questionId);
        answer.setContent(content);
        answer.setAnswerUrl("");
        answer.setUpdateTime(LocalDateTime.now());
        boolean save = answerService.save(answer);
        if(save) {
            String answerUrl = "/question/" + questionId + "/answer/" + answer.getId();
            answer.setAnswerUrl(answerUrl);
            boolean b = answerService.updateById(answer);
            question.setAnswerCount(question.getAnswerCount()+1);
            questionService.updateById(question);
            /*创建通知*/
            messageService.createMessage(question.getAuthorId(),7,2,answer.getId(),"回答了你的问题",authorId,"/user"+answerUrl,LocalDateTime.now());
            /*添加动态*/
            QueryWrapper<FollowFollower> followFollowerQueryWrapper = new QueryWrapper<>();
            followFollowerQueryWrapper.eq("follow_id",authorId);
            List<FollowFollower> followerList = followFollowerService.list(followFollowerQueryWrapper);
            for (FollowFollower follower : followerList) {
                Integer followerId = follower.getFollowerId();
                Dynamic dynamic = new Dynamic();
                dynamic.setFollowerId(followerId).setFollowId(authorId).setType(2).setObjectId(answer.getId()).
                        setUrl("/user"+answerUrl).setUpdateTime(LocalDateTime.now());
                dynamicService.save(dynamic);
            }

            mp.put("answerUrl", answerUrl);
            mp.put("flag", b);
        }
        return mp;
    }

    /*修改回答*/
    @PostMapping("/user/editAnswer")
    @ResponseBody
    public Map<String,Object> addAnswer(Integer authorId, Integer questionId, Integer answerId, String content){
        HashMap<String,Object> mp = new HashMap<>();

        Answer answer = answerService.getById(answerId);
        answer.setAuthorId(authorId);
        answer.setQuestionId(questionId);
        answer.setContent(content);
        answer.setUpdateTime(LocalDateTime.now());
        boolean update = answerService.updateById(answer);
        if(update) {
            mp.put("answerUrl", answer.getAnswerUrl());
            mp.put("flag", update);
        }
        return mp;
    }

    /*回答排序*/
    @PostMapping("/user/changeAnswerByOrder")
    public String changeAnswerByOrder(Integer questionId, String orderStr){
        Answer answer;
        if(orderStr.equals("排序方式") || orderStr.equals("最多点赞")){
            answer = answerService.getAnswerByOrder(questionId,"like_count");
        } else if(orderStr.equals("最多收藏")) {
            answer = answerService.getAnswerByOrder(questionId,"collect_count");
        } else if (orderStr.equals("最多评论")) {
            answer = answerService.getAnswerByOrder(questionId,"comment_count");
        } else {
            answer = answerService.getAnswerByOrder(questionId,"update_time");
        }
        return "redirect:/user/question/"+questionId.toString()+"/answer/"+answer.getId().toString();
    }

    /*删除回答*/
    @PostMapping("/user/deleteAnswer")
    @ResponseBody
    public boolean deleteAnswer(Integer answerId, String message) {
        return answerService.deleteAnswer(answerId,message);
    }

    /*警告回答*/
    @PostMapping("/user/warnAnswer")
    @ResponseBody
    public boolean warnAnswer(Integer answerId, String message) {
        Answer answer = answerService.getById(answerId);
        Question question = questionService.getById(answer.getQuestionId());
        User user = userService.getById(answer.getAuthorId());
        boolean b = messageService.createMessage(user.getId(), 5, 2, answerId, question.getTitle()+"|"+message, 0, "/user" + answer.getAnswerUrl(), LocalDateTime.now());
        return b;
    }

    /*判断当前用户在该问题是否有回答并返回回答id*/
    @PostMapping("/user/getAnswerIdBySession")
    @ResponseBody
    public Integer getAnswerIdBySession(Integer sessionId, Integer questionId) {
        QueryWrapper<Answer> answerQueryWrapper = new QueryWrapper<>();
        answerQueryWrapper.eq("question_id",questionId).eq("author_id",sessionId);
        Answer answer = answerService.getOne(answerQueryWrapper);
        if(answer == null) return null;
        else return answer.getId();
    }
}
