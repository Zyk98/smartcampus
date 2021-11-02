package com.cumtb.mp.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cumtb.mp.entity.Message;
import com.cumtb.mp.service.impl.MessageServiceImpl;
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
public class MessageController {
    @Autowired
    MessageServiceImpl messageService;

    @PostMapping("/user/deleteMessage")
    @ResponseBody
    public boolean deleteMessage(Integer messageId) {
        boolean remove = messageService.removeById(messageId);
        return remove;
    }

    @PostMapping("/user/getMessageTotal")
    @ResponseBody
    public Integer getMessageTotal(Integer sessionId) {
        if (sessionId == null) return 0;
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid",sessionId).eq("state",false);
        return messageService.count(queryWrapper);
    }
}
