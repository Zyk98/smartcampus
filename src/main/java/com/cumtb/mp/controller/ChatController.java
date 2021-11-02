package com.cumtb.mp.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cumtb.mp.entity.Chat;
import com.cumtb.mp.entity.User;
import com.cumtb.mp.service.impl.ChatServiceImpl;
import com.cumtb.mp.service.impl.UserServiceImpl;
import com.cumtb.mp.vo.ChatVO;
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
public class ChatController {

    @Autowired
    ChatServiceImpl chatService;
    @Autowired
    UserServiceImpl userService;

    @PostMapping("/user/addChat")
    @ResponseBody
    public boolean addChat(Integer sessionId, String content) {

        Chat chat = new Chat();
        chat.setUid(sessionId).setContent(content).setUpdateTime(LocalDateTime.now());
        boolean save = chatService.save(chat);
        return save;
    }

    @PostMapping("/user/getChat")
    @ResponseBody
    public List<ChatVO> getChat() {

        List<ChatVO> voList = new ArrayList<>();
        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderBy(true,false,"update_time").last("limit 0, 10");
        List<Chat> list = chatService.list(queryWrapper);
        if(list.isEmpty()) return null;
        for (Chat chat : list) {
            ChatVO chatVO = new ChatVO();
            User user = userService.getById(chat.getUid());
            chatVO.setUserUrl("/user/homepage/"+user.getId());
            chatVO.setUserName(user.getNickName());
            chatVO.setPhotoUrl(user.getPhotoUrl());
            chatVO.setContent(chat.getContent());
            chatVO.setUpdateTime(chat.getUpdateTime());
            voList.add(chatVO);
        }
        return voList;
    }
}
