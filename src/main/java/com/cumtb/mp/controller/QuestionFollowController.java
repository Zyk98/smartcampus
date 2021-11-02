package com.cumtb.mp.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cumtb.mp.entity.Question;
import com.cumtb.mp.entity.QuestionFollow;
import com.cumtb.mp.service.impl.QuestionFollowServiceImpl;
import com.cumtb.mp.service.impl.QuestionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zheng
 * @since 2021-04-22
 */
@Controller
public class QuestionFollowController {

    @Autowired
    QuestionFollowServiceImpl questionFollowService;
    @Autowired
    QuestionServiceImpl questionService;

    @PostMapping("/user/questionFollowClick")
    @ResponseBody
    public boolean questionFollowClick(Integer questionId, Integer sessionId){
        QueryWrapper<QuestionFollow> questionFollowQueryWrapper = new QueryWrapper<>();
        questionFollowQueryWrapper.eq("question_id",questionId).eq("uid",sessionId);
        QuestionFollow questionFollow = questionFollowService.getOne(questionFollowQueryWrapper);
        if(questionFollow != null) {
            Question question = questionService.getById(questionId);
            if(questionFollow.getState() == false) {
                questionFollow.setState(true);
                question.setCollectCount(question.getCollectCount()+1);
            } else {
                questionFollow.setState(false);
                question.setCollectCount(question.getCollectCount()-1);
            }
            questionFollowService.updateById(questionFollow);
            questionService.updateById(question);
            return questionFollow.getState();
        }
        return false;
    }

}
