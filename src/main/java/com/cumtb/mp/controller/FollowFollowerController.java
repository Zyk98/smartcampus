package com.cumtb.mp.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cumtb.mp.entity.Answer;
import com.cumtb.mp.entity.FollowFollower;
import com.cumtb.mp.entity.Question;
import com.cumtb.mp.entity.User;
import com.cumtb.mp.service.impl.*;
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
public class FollowFollowerController {

    @Autowired
    FollowFollowerServiceImpl followFollowerService;
    @Autowired
    UserServiceImpl userService;
    @Autowired
    MessageServiceImpl messageService;
    @Autowired
    QuestionServiceImpl questionService;
    @Autowired
    AnswerServiceImpl answerService;


    @PostMapping("/user/getAuthorPhotoInfo")
    @ResponseBody
    public Map<String,Object> getAuthorPhotoInfo(Integer userId) {
        Map<String,Object> mp = new HashMap<>();

        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.eq("author_id",userId);
        int questionCount = questionService.count(questionQueryWrapper);
        QueryWrapper<Answer> answerQueryWrapper = new QueryWrapper<>();
        answerQueryWrapper.eq("author_id",userId);
        int answerCount = answerService.count(answerQueryWrapper);
        QueryWrapper<FollowFollower> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("follow_id",userId);
        int followerCount = followFollowerService.count(queryWrapper1);

        mp.put("questionCount",questionCount);
        mp.put("answerCount",answerCount);
        mp.put("followerCount",followerCount);
        return mp;
    }

    @PostMapping("/user/isFollower")
    @ResponseBody
    public boolean isFollower(Integer sessionId, Integer userId){
        QueryWrapper<FollowFollower> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("follow_id",userId).eq("follower_id",sessionId);
        FollowFollower one = followFollowerService.getOne(queryWrapper);
        if(one == null) return false;
        else return true;
    }

    @PostMapping("/user/addFollow")
    @ResponseBody
    public boolean addFollow(Integer sessionId, Integer userId) {
        FollowFollower followFollower = new FollowFollower();
        followFollower.setFollowerId(sessionId).setFollowId(userId);
        boolean save = followFollowerService.save(followFollower);
        if(save) {
            messageService.createMessage(userId,9,4,sessionId,"关注了你",sessionId,"#", LocalDateTime.now());
        }
        return save;
    }

    @PostMapping("/user/deleteFollow")
    @ResponseBody
    public boolean deleteFollow(Integer sessionId, Integer userId){
        QueryWrapper<FollowFollower> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("follow_id",userId).eq("follower_id",sessionId);
        boolean remove = followFollowerService.remove(queryWrapper);
        return remove;
    }

    @PostMapping("/user/getFollow")
    @ResponseBody
    public List<User> getFollow(Integer userId){
        QueryWrapper<FollowFollower> followFollowerQueryWrapper = new QueryWrapper<>();
        followFollowerQueryWrapper.select("follow_id").eq("follower_id",userId);
        List<Object> list = followFollowerService.listObjs(followFollowerQueryWrapper);
        ArrayList<Integer> idList = new ArrayList<>();
        for (Object o : list) {
            idList.add((Integer)o);
        }
        List<User> userList = null;
        if(!idList.isEmpty()) userList = userService.listByIds(idList);
        return userList;
    }

    @PostMapping("/user/getFollower")
    @ResponseBody
    public List<User> getFollower(Integer userId){
        QueryWrapper<FollowFollower> followFollowerQueryWrapper = new QueryWrapper<>();
        followFollowerQueryWrapper.select("follower_id").eq("follow_id",userId);
        List<Object> list = followFollowerService.listObjs(followFollowerQueryWrapper);
        ArrayList<Integer> idList = new ArrayList<>();
        for (Object o : list) {
            idList.add((Integer)o);
        }
        List<User> userList = null;
        if(!idList.isEmpty()) userList = userService.listByIds(idList);
        return userList;
    }

}
