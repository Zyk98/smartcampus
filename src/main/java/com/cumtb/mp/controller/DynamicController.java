package com.cumtb.mp.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cumtb.mp.entity.Answer;
import com.cumtb.mp.entity.Dynamic;
import com.cumtb.mp.entity.Question;
import com.cumtb.mp.entity.User;
import com.cumtb.mp.service.impl.AnswerServiceImpl;
import com.cumtb.mp.service.impl.DynamicServiceImpl;
import com.cumtb.mp.service.impl.QuestionServiceImpl;
import com.cumtb.mp.service.impl.UserServiceImpl;
import com.cumtb.mp.vo.DynamicVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zheng
 * @since 2021-04-22
 */
@Controller
public class DynamicController {
    @Autowired
    DynamicServiceImpl dynamicService;
    @Autowired
    QuestionServiceImpl questionService;
    @Autowired
    AnswerServiceImpl answerService;
    @Autowired
    UserServiceImpl userService;

    @PostMapping("/user/getDynamic")
    @ResponseBody
    public List<DynamicVO> getDynamic(Integer userId) {
        List<DynamicVO> voList=  new ArrayList<>();

        QueryWrapper<Dynamic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("follower_id",userId);
        List<Dynamic> dynamicList = dynamicService.list(queryWrapper); //所有userId关注的用户
        for (Dynamic dynamic : dynamicList) {
            DynamicVO dynamicVO = new DynamicVO();
            /*问题*/
            if(dynamic.getType() == 1) {
                Question question = questionService.getById(dynamic.getObjectId());
                User user = userService.getById(question.getAuthorId());
                dynamicVO.setTitle(question.getTitle());
                dynamicVO.setTitleUrl("/user/question/"+question.getId());
                dynamicVO.setContent("提问");
                dynamicVO.setUserName(user.getNickName());
                dynamicVO.setUserUrl("/user/homepage/"+user.getId());
                dynamicVO.setState(dynamic.getState());
                dynamicVO.setType(dynamic.getType());
                dynamicVO.setUpdateTime(dynamic.getUpdateTime());
            } else {
                Answer answer = answerService.getById(dynamic.getObjectId());
                User user = userService.getById(answer.getAuthorId());
                Question question = questionService.getById(answer.getQuestionId());
                dynamicVO.setTitle(question.getTitle());
                dynamicVO.setTitleUrl("/user"+answer.getAnswerUrl());
                dynamicVO.setContent("回答");
                dynamicVO.setUserName(user.getNickName());
                dynamicVO.setUserUrl("/user/homepage/"+user.getId());
                dynamicVO.setState(dynamic.getState());
                dynamicVO.setType(dynamic.getType());
                dynamicVO.setUpdateTime(dynamic.getUpdateTime());
            }
            voList.add(dynamicVO);
        }
        return voList;
    }
}
