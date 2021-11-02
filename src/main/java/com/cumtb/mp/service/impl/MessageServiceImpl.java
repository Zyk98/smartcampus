package com.cumtb.mp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cumtb.mp.entity.Message;
import com.cumtb.mp.mapper.MessageMapper;
import com.cumtb.mp.service.IMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zheng
 * @since 2021-04-22
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {
    /*创建通知*/
    public boolean createMessage(Integer userId, Integer stateType, Integer objectType, Integer objectId, String msg, Integer msgUid, String url, LocalDateTime updateTime) {
        QueryWrapper<com.cumtb.mp.entity.Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid",userId).eq("state_type",stateType).eq("object_type",objectType).eq("object_id",objectId).eq("message_uid",msgUid);
        Message message = this.getOne(queryWrapper);
        if(message == null) {
            message = new Message();
            message.setUid(userId).setStateType(stateType).setObjectType(objectType).setObjectId(objectId).setMessage(msg).setMessageUid(msgUid).setState(false).setUrl(url).setUpdateTime(updateTime);
            boolean save = this.save(message);
            return save;
        } else {
            message.setUpdateTime(updateTime);
            boolean update = this.updateById(message);
            return update;
        }
    }
}
