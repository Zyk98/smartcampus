package com.cumtb.mp.controller;


import com.cumtb.mp.entity.Feedback;
import com.cumtb.mp.entity.User;
import com.cumtb.mp.service.impl.FeedbackServiceImpl;
import com.cumtb.mp.service.impl.UserServiceImpl;
import com.cumtb.mp.vo.FeedbackVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
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
public class FeedbackController {
    @Autowired
    UserServiceImpl userService;
    @Autowired
    FeedbackServiceImpl feedbackService;

    @PostMapping("/user/addFeedback")
    @ResponseBody
    public boolean addFeedback(Integer sessionId, String title, String content) {
        Feedback feedback = new Feedback();
        feedback.setContent(content).setTitle(title).setUid(sessionId).setUpdateTime(LocalDateTime.now());
        return feedbackService.save(feedback);
    }

    @PostMapping("/user/getFeedback")
    @ResponseBody
    public List<FeedbackVO> getFeedback() {
        List<FeedbackVO> voList = new ArrayList<>();
        List<Feedback> feedbackList = feedbackService.list();
        for (Feedback feedback : feedbackList) {
            User user = userService.getById(feedback.getUid());
            FeedbackVO feedbackVO = new FeedbackVO();
            feedbackVO.setId(feedback.getId());
            feedbackVO.setTitle(feedback.getTitle());
            feedbackVO.setContent(feedback.getContent());
            feedbackVO.setUserName(user.getNickName());
            feedbackVO.setUserUrl("/user/homepage/"+user.getId());
            feedbackVO.setUpdateTime(feedback.getUpdateTime());
            voList.add(feedbackVO);
        }
        return voList;
    }

    @PostMapping("/user/deleteFeecback")
    @ResponseBody
    public boolean deleteFeedback(Integer feedbackId) {
        return feedbackService.removeById(feedbackId);
    }
}
