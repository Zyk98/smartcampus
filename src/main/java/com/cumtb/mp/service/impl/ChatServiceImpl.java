package com.cumtb.mp.service.impl;

import com.cumtb.mp.entity.Chat;
import com.cumtb.mp.mapper.ChatMapper;
import com.cumtb.mp.service.IChatService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zheng
 * @since 2021-04-22
 */
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat> implements IChatService {

}
