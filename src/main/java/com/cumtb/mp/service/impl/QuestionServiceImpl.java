package com.cumtb.mp.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cumtb.mp.entity.*;
import com.cumtb.mp.vo.QuestionTagVO;
import com.cumtb.mp.entity.Question;
import com.cumtb.mp.mapper.QuestionMapper;
import com.cumtb.mp.service.IQuestionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zheng
 * @since 2021-04-22
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements IQuestionService {
    @Autowired
    QuestionTagServiceImpl questionTagService;
    @Autowired
    TagServiceImpl tagService;
    @Autowired
    AnswerServiceImpl answerService;
    @Autowired
    UserServiceImpl userService;
    @Autowired
    QuestionFollowServiceImpl questionFollowService;
    @Autowired
    AnswerCollectServiceImpl answerCollectService;
    @Autowired
    AnswerLikeServiceImpl answerLikeService;
    @Autowired
    AnswerDislikeServiceImpl answerDislikeService;



    /*获取问题，代码重用*/
    //传参：标签id,搜索框字符串,排序方式,当前页,每页个数
    //返回值：vo集合，总页数，总记录数
    @Override
    public Map<String,Object> getAllQuestionDemo(Integer tagId, String searchStr, String orderStr, Integer curPage, Integer countPerPage){
        HashMap<String,Object> mp = new HashMap<>();
        ArrayList<QuestionTagVO> voList = new ArrayList<>();

        System.out.println(tagId);
        System.out.println(searchStr);
        System.out.println(orderStr);

        QueryWrapper<com.cumtb.mp.entity.Question> queryWrapper = new QueryWrapper<>();

        /*根据标签id筛选*/
        if(tagId != null){
            ArrayList<Integer> questionIdList = this.getAllQuestionIdByTagId(tagId);
            if(!questionIdList.isEmpty())
                queryWrapper.in("id",questionIdList);
            else queryWrapper.eq("id",0);
        }
        /*根据搜索内容筛选*/
        if(searchStr != null){
            queryWrapper.and(wrapper -> wrapper.like("title",searchStr).or().like("content",searchStr));
        }
        /*根据排序方式查找*/
        if(orderStr.equals("排序方式") || orderStr.equals("最新时间")){
            queryWrapper.orderBy(true,false,"update_time");
        }else if(orderStr.equals("最多浏览")){
            queryWrapper.orderBy(true,false,"view_count");
        } else if (orderStr.equals("最多收藏")){
            queryWrapper.orderBy(true,false,"collect_count");
        } else if(orderStr.equals("最多回答")){
            queryWrapper.orderBy(true,false,"answer_count");
        }

        //mybatis-plus的分页查询获取question集合
        IPage<com.cumtb.mp.entity.Question> page = new Page<>(); //创建IPage对象
        page.setCurrent(curPage);  //当前页
        page.setSize(countPerPage); //每页个数
        this.selectAllByPage((Page<com.cumtb.mp.entity.Question>) page, queryWrapper); //分页，数据都在page对象中
        List<com.cumtb.mp.entity.Question> questionList = page.getRecords(); //page获取所有vo对象
        long total = page.getTotal(); //总记录数
        Integer pageCount = Math.toIntExact(total % countPerPage == 0 ? total / countPerPage : total / countPerPage + 1);

        //遍历每个question获取标签集合并加进QuestionTagVO数组
        for (com.cumtb.mp.entity.Question question : questionList) {
            QuestionTagVO vo = new QuestionTagVO();
            ArrayList<Tag> tagList = getAllTagByQuestionId(question.getId());
            vo.setQuestion(question);
            vo.setTagList(tagList);
            voList.add(vo);
        }

        mp.put("voList",voList);
        mp.put("pageCount",pageCount);
        mp.put("total",total);
        return mp;
    }


    /*Question分页*/
    @Override
    public IPage<com.cumtb.mp.entity.Question> selectAllByPage(Page<com.cumtb.mp.entity.Question> page, Wrapper<com.cumtb.mp.entity.Question> wrapper) {
        return baseMapper.selectAllByPage(page,wrapper);
    }

    /*根据问题id获取Tag标签*/
    @Override
    public ArrayList<Tag> getAllTagByQuestionId(Integer questionId) {
        //根据问题id获取qt集合 1->n
        QueryWrapper<QuestionTag> questionTagWrapper = new QueryWrapper<QuestionTag>();
        questionTagWrapper.eq("question_id", questionId);
        ArrayList<QuestionTag> questionTagList = (ArrayList<QuestionTag>) questionTagService.list(questionTagWrapper);
        //根据每个qt获取对应的标签 1->1
        ArrayList<Tag> tagList = new ArrayList<>();
        for (QuestionTag questionTag : questionTagList) {
            Integer tagId = questionTag.getTagId();
            Tag tag = tagService.getById(tagId);
            tagList.add(tag);
        }
        return tagList;
    }

    /*根据标签id获取所有问题id*/
    @Override
    public ArrayList<Integer> getAllQuestionIdByTagId(Integer tagId){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("question_id");
        queryWrapper.eq("tag_id",tagId);

        ArrayList<Integer> list = (ArrayList<Integer>) questionTagService.listObjs(queryWrapper);
        return list;
    }

    /*获取所有回答*/
    //传参：问题id，当前页，排序列,sessionId
    //返回值：question,user,answer,tagList,total,是否关注问题,是否收藏
    public Map<String,Object> getAllAnswerDemo(Integer questionId, Integer curPage, String orderColumn, Integer sessionId) {
        HashMap<String,Object> mp = new HashMap<>();

        Question question = this.getById(questionId);
        ArrayList<Tag> tagList = this.getAllTagByQuestionId(questionId);

        QueryWrapper<Answer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("question_id",questionId);
        queryWrapper.orderBy(true,false,orderColumn);

        //mybatis-plus的分页查询获取answer集合
        IPage<Answer> page = new Page<>(); //创建IPage对象
        page.setCurrent(curPage);  //当前页
        page.setSize(1); //每页个数
        answerService.selectAllByPage((Page<Answer>) page, queryWrapper); //分页，数据都在page对象中
        List<Answer> answerList = page.getRecords(); //page获取所有answer对象
        long total = page.getTotal(); //总记录数
        if(total > 0) {
            User user = userService.getById(answerList.get(0).getAuthorId());
            mp.put("user",user);
        }
        //是否关注问题
        boolean questionFlag = false;
        if(sessionId != null) {
            QueryWrapper<QuestionFollow> questionFollowQueryWrapper = new QueryWrapper<>();
            questionFollowQueryWrapper.select("state").eq("uid",sessionId).eq("question_id",questionId);
            List<Object> list = questionFollowService.listObjs(questionFollowQueryWrapper);
            QuestionFollow questionFollow;
            if (list.isEmpty()) {
                questionFollow = new QuestionFollow().setUid(sessionId).setQuestionId(questionId).setState(false);
                questionFollowService.save(questionFollow);
            } else {
                questionFlag = (boolean) list.get(0);
            }
        }
        //如果存在回答
        if(!answerList.isEmpty()) {
            //判断sessionId是否在该问题下有回答
            QueryWrapper<Answer> answerQueryWrapper = new QueryWrapper<>();
            answerQueryWrapper.eq("question_id",questionId).eq("author_id",sessionId);
            Answer answer = answerService.getOne(answerQueryWrapper);
            if(answer != null) {
                mp.put("sessionAnswerId",answer.getId());
            }

            //是否收藏
            boolean collectFlag = false;
            if (sessionId != null) {
                QueryWrapper<AnswerCollect> answerCollectQueryWrapper = new QueryWrapper<>();
                //通过uid和answer_id唯一确认收藏表记录，并取出state判断是否收藏
                answerCollectQueryWrapper.select("state").eq("uid", sessionId).eq("answer_id", answerList.get(0).getId());
                List<Object> list = answerCollectService.listObjs(answerCollectQueryWrapper);
                AnswerCollect answerCollect;
                //如果记录不存在，则创建
                if (list.isEmpty()) {
                    answerCollect = new AnswerCollect();
                    answerCollect.setUid(sessionId).setAnswerId(answerList.get(0).getId()).setState(false);
                    answerCollectService.save(answerCollect);
                } else {
                    collectFlag = (boolean) list.get(0);
                }
            }

            //是否点赞回答
            boolean answerLikeFlag = false;
            if (sessionId != null) {
                QueryWrapper<AnswerLike> answerLikeQueryWrapper = new QueryWrapper<>();
                answerLikeQueryWrapper.select("state").eq("uid", sessionId).eq("answer_id", answerList.get(0).getId());
                List<Object> list = answerLikeService.listObjs(answerLikeQueryWrapper);
                AnswerLike answerLike;
                if (list.isEmpty()) {
                    answerLike = new AnswerLike();
                    answerLike.setUid(sessionId).setAnswerId(answerList.get(0).getId()).setState(false);
                    answerLikeService.save(answerLike);
                } else {
                    answerLikeFlag = (boolean) list.get(0);
                }
            }

            //是否踩回答
            boolean answerDislikeFlag = false;
            if (sessionId != null) {
                QueryWrapper<AnswerDislike> answerDislikeQueryWrapper = new QueryWrapper<>();
                answerDislikeQueryWrapper.select("state").eq("uid", sessionId).eq("answer_id", answerList.get(0).getId());
                List<Object> list = answerDislikeService.listObjs(answerDislikeQueryWrapper);
                AnswerDislike answerDislike;
                if (list.isEmpty()) {
                    answerDislike = new AnswerDislike();
                    answerDislike.setUid(sessionId).setAnswerId(answerList.get(0).getId()).setState(false);
                    answerDislikeService.save(answerDislike);
                } else {
                    answerDislikeFlag = (boolean) list.get(0);
                }
            }

            mp.put("collectFlag",collectFlag);
            mp.put("answerLikeFlag",answerLikeFlag);
            mp.put("answerDislikeFlag",answerDislikeFlag);
        }

        mp.put("question",question);
        mp.put("answer",answerList);
        mp.put("tagList",tagList);
        mp.put("total",total);
        mp.put("questionFlag",questionFlag);
        return mp;
    }
}
