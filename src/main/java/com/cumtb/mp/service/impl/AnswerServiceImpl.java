package com.cumtb.mp.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cumtb.mp.entity.*;
import com.cumtb.mp.entity.Answer;
import com.cumtb.mp.mapper.AnswerMapper;
import com.cumtb.mp.service.IAnswerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AnswerServiceImpl extends ServiceImpl<AnswerMapper, Answer> implements IAnswerService {
    @Autowired
    UserServiceImpl userService;
    @Autowired
    QuestionServiceImpl questionService;
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


    @Override
    public com.cumtb.mp.entity.Answer getAnswerByOrder(Integer questionId, String orderColumn){
        return baseMapper.selectAnswerByOrder(questionId, orderColumn);
    }

    @Override
    public IPage<com.cumtb.mp.entity.Answer> selectAllByPage(Page<com.cumtb.mp.entity.Answer> page, Wrapper<com.cumtb.mp.entity.Answer> wrapper) {
        return baseMapper.selectAllByPage(page,wrapper);
    }

    public boolean deleteAnswer(Integer answerId, String message) {
        Answer answer = this.getById(answerId);
        Question question = questionService.getById(answer.getQuestionId());
        Integer authorId = answer.getAuthorId();
        User user = userService.getById(answer.getAuthorId());
        /*先删掉回答中所有的评论和评论通知*/
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("answer_id",answerId);
        commentService.remove(commentQueryWrapper);
        boolean remove = this.removeById(answerId);
        if(remove) {
            /*更新问题的回答数*/
            question.setAnswerCount(question.getAnswerCount()-1);
            questionService.updateById(question);
            /*删除回答点赞/踩/收藏/评论表相关回答信息*/
            QueryWrapper wrapper = new QueryWrapper();
            wrapper.eq("answer_id",answerId);
            answerLikeService.remove(wrapper);
            answerDislikeService.remove(wrapper);
            answerCollectService.remove(wrapper);
            /*删除回答相关的通知*/
            QueryWrapper<Message> messageQueryWrapper = new QueryWrapper<>();
            messageQueryWrapper.eq("object_id",answerId).eq("object_type",2);
            messageService.remove(messageQueryWrapper);
            /*删除关注动态的回答通知*/
            QueryWrapper<Dynamic> dynamicQueryWrapper = new QueryWrapper<>();
            dynamicQueryWrapper.eq("type",2).eq("object_id",answerId);
            dynamicService.remove(dynamicQueryWrapper);
            /*创建删除回答通知*/
            messageService.createMessage(authorId,6,2,answerId,question.getTitle()+"|"+message,0,"/user/question/"+question.getId(), LocalDateTime.now());
        }
        return remove;
    }
}
