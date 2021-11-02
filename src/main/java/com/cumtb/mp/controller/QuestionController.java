package com.cumtb.mp.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cumtb.mp.entity.*;
import com.cumtb.mp.service.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class QuestionController {
    @Autowired
    QuestionServiceImpl questionService;
    @Autowired
    QuestionTagServiceImpl questionTagService;
    @Autowired
    AnswerServiceImpl answerService;
    @Autowired
    UserServiceImpl userService;
    @Autowired
    FollowFollowerServiceImpl followFollowerService;
    @Autowired
    DynamicServiceImpl dynamicService;
    @Autowired
    MessageServiceImpl messageService;
    @Autowired
    QuestionFollowServiceImpl questionFollowService;

    /*前往首页*/
    @GetMapping("/user/index")
    public String toIndexPage(String searchStr, Model model) {
        model.addAttribute("searchStr", searchStr);
        return "user/index";
    }

    /*添加问题*/
    @PostMapping("/user/addQuestion")
    @ResponseBody
    public boolean addQuestion(@RequestBody Map<String, Object> map) {

        ArrayList<String> list = (ArrayList<String>) map.get("list");
        for (String i : list) {
            System.out.println(i);
        }
        String title = (String) map.get("title");
        String content = (String) map.get("content");
        String idStr = (String) map.get("userId");
        Integer id = Integer.parseInt(idStr);   //问题作者id

        Question question = new Question();
        question.setTitle(title);
        question.setContent(content);
        question.setAuthorId(id);
        question.setUpdateTime(LocalDateTime.now());
        System.out.println(question);

        boolean b = questionService.save(question);
        System.out.println(question);
        /*如果问题保存成功，更新questionTag和创建动态*/
        if (b) {
            //更新QuestionTag
            Integer questionId = question.getId();
            for (String tagId : list) {
                QuestionTag questionTag = new QuestionTag();
                questionTag.setQuestionId(questionId);
                questionTag.setTagId(Integer.parseInt(tagId));
                questionTagService.save(questionTag);
            }
            //创建动态
            QueryWrapper<FollowFollower> followFollowerQueryWrapper = new QueryWrapper<>();
            followFollowerQueryWrapper.eq("follow_id", id);
            List<FollowFollower> followerList = followFollowerService.list(followFollowerQueryWrapper);
            for (FollowFollower follower : followerList) {
                Integer followerId = follower.getFollowerId();
                Dynamic dynamic = new Dynamic();
                dynamic.setFollowerId(followerId).setFollowId(id).setType(1).setObjectId(questionId).
                        setUrl("/user/question/" + questionId).setUpdateTime(LocalDateTime.now());
                dynamicService.save(dynamic);
            }
        }
        return b;
    }

    /*获取问题*/
    @PostMapping("/user/getAllQuestions")
    @ResponseBody
    public Map<String, Object> getAllQuestion(Integer tagId, String searchStr, String orderStr, Integer curPage, Integer countPerPage) {
        return questionService.getAllQuestionDemo(tagId, searchStr, orderStr, curPage, countPerPage);
    }

    /*前往问题界面*/
    @GetMapping("/user/question/{id}")
    public String toQuestionPage(@PathVariable("id") Integer id) {
        Question question = questionService.getById(id);
        question.setViewCount(question.getViewCount() + 1);
        questionService.updateById(question);
        return "user/question";
    }

    /*问题界面*/
    @PostMapping("/user/question/{questionId}")
    @ResponseBody
    public Map<String, Object> questionPage(@PathVariable("questionId") Integer questionId, Integer curPage, String orderColumn, Integer sessionId) {
        return questionService.getAllAnswerDemo(questionId, curPage, orderColumn, sessionId);
    }

    /*通过回答跳转至问题界面*/
    @GetMapping("/user/question/{questionId}/answer/{answerId}")
    public String toQuestionPageByAnswer(@PathVariable("questionId") Integer id) {
        Question question = questionService.getById(id);
        question.setViewCount(question.getViewCount() + 1);
        questionService.updateById(question);
        return "user/question";
    }


    /*获取回答对应问题界面的页数*/
    @PostMapping("/user/getCurPageByAnswerId")
    @ResponseBody
    public Integer getCurPageByAnswerId(Integer questionId, Integer answerId) {
        QueryWrapper<Answer> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id");
        queryWrapper.eq("question_id", questionId);
        queryWrapper.orderBy(true, false, "like_count");
        List<Object> list = answerService.listObjs(queryWrapper); //按照点赞数倒序得到answer的id集合
        for (int i = 0; i < list.size(); i++) { //获取当前answerId所在索引+1作为分页的当前页
            if ((Integer) list.get(i) == answerId) return i + 1;
        }
        return 1;
    }

    /*前往回答界面*/
    @GetMapping("/user/answer/{id}")
    public String toAnswerPage(@PathVariable Integer id, Model model) {
        Question question = questionService.getById(id);
        model.addAttribute("question", question);
        return "user/answer";
    }

    /*前往重写回答界面*/
    @GetMapping("/user/answer/{questionId}/{answerId}")
    public String toAnswerRewritePage(@PathVariable("questionId") Integer questionId, @PathVariable("answerId") Integer answerId, Model model) {
        Question question = questionService.getById(questionId);
        Answer answer = answerService.getById(answerId);
        model.addAttribute("question", question).addAttribute("answer", answer);
        return "user/answer";
    }

    /*警告问题*/
    @PostMapping("/user/questionWarn")
    @ResponseBody
    public boolean questionWarn(Integer questionId, String message) {
        Question question = questionService.getById(questionId);
        boolean b = messageService.createMessage(question.getAuthorId(), 5, 1, questionId, question.getTitle() + "|" + message, 0, "/user/question/" + question.getId(), LocalDateTime.now());
        return b;
    }

    /*删除问题*/
    @PostMapping("/user/deleteQuestion")
    @ResponseBody
    public boolean deleteQuestion(Integer questionId, String message) {
        Question question = questionService.getById(questionId);
        QueryWrapper<Answer> answerQueryWrapper = new QueryWrapper<>();
        answerQueryWrapper.eq("question_id", questionId);
        List<Answer> answerList = answerService.list(answerQueryWrapper);
        for (Answer answer : answerList) {
            /*删除问题下所有的回答*/
            answerService.deleteAnswer(answer.getId(), "该问题已被删除");
        }
        /*删除QuestionTag表，问题关注表相关记录*/
        QueryWrapper questionTagQueryWrapper = new QueryWrapper();
        questionTagQueryWrapper.eq("question_id", questionId);
        questionTagService.remove(questionTagQueryWrapper);
        questionFollowService.remove(questionTagQueryWrapper);

        /*删除objectId为问题的所有通知和动态*/
        QueryWrapper<Message> messageQueryWrapper = new QueryWrapper<>();
        messageQueryWrapper.eq("object_type", 1).eq("object_id", questionId);
        messageService.remove(messageQueryWrapper);
        QueryWrapper<Dynamic> dynamicQueryWrapper = new QueryWrapper<>();
        dynamicQueryWrapper.eq("type", 1).eq("object_id", questionId);
        dynamicService.remove(dynamicQueryWrapper);
        /*创建删除问题通知*/
        messageService.createMessage(question.getAuthorId(), 6, 1, questionId, question.getTitle() + "|" + message, 0, "#", LocalDateTime.now());
        /*删除问题*/
        boolean b = questionService.removeById(questionId);
        return b;
    }

    /*重新编辑问题*/
    @PostMapping("/user/editQuestion")
    @ResponseBody
    public boolean editQuestion(Integer questionId, String title, String content) {
        Question question = questionService.getById(questionId);
        question.setTitle(title).setContent(content);
        boolean update = questionService.updateById(question);
        return update;
    }

}
