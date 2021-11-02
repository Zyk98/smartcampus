package com.cumtb.mp.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cumtb.mp.entity.*;
import com.cumtb.mp.service.impl.*;
import com.cumtb.mp.vo.MessageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zheng
 * @since 2021-04-22
 */
@Controller
public class UserController {

    @Autowired
    QuestionServiceImpl questionService;
    @Autowired
    UserServiceImpl userService;
    @Autowired
    QuestionFollowServiceImpl questionFollowService;
    @Autowired
    AnswerServiceImpl answerService;
    @Autowired
    AnswerCollectServiceImpl answerCollectService;
    @Autowired
    MessageServiceImpl messageService;
    @Autowired
    FollowFollowerServiceImpl followFollowerService;
    @Autowired
    CommentServiceImpl commentService;


    /*个人主页链接跳转*/
    @GetMapping("/user/homepage/{id}")
    public String toHomePage(@PathVariable("id") Integer id) {
        return "user/homepage";
    }

    /*获取用户信息*/
    @PostMapping("/user/getUserInfo")
    @ResponseBody
    public Map<String,Object> getUserInfo(Integer userId){
        Map<String,Object> mp = new HashMap<>();
        User user = userService.getById(userId);
        QueryWrapper<FollowFollower> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("follow_id",userId);
        List<FollowFollower> followers = followFollowerService.list(wrapper1);
        Integer followerCount = followers.size();
        QueryWrapper<FollowFollower> wrapper2 = new QueryWrapper<>();
        wrapper2.eq("follower_id",userId);
        List<FollowFollower> follows = followFollowerService.list(wrapper2);
        Integer followCount = follows.size();
        mp.put("user",user);
        mp.put("followCount",followCount);
        mp.put("followerCount",followerCount);
        return mp;
    }

    /*获取用户关注的问题*/
    @PostMapping("/user/getQuestionAttend")
    @ResponseBody
    public List<Question> getQuestionAttend(Integer userId){
        /*在关注问题表中获取所有uid为用户id并且状态为1的所有questionId*/
        QueryWrapper<QuestionFollow> questionFollowWrapper = new QueryWrapper<>();
        questionFollowWrapper.select("question_id");
        questionFollowWrapper.eq("uid",userId);
        questionFollowWrapper.eq("state",1);

        List<Object> list = questionFollowService.listObjs(questionFollowWrapper);
        ArrayList<Integer> idList = new ArrayList<>();
        for (Object o : list) {
            idList.add((Integer)o);
        }

        List<Question> questionList = new ArrayList<>();
        if(!idList.isEmpty()) {
            questionList = questionService.listByIds(idList);
        }
        return questionList;
    }

    /*获取用户的所有提问*/
    @PostMapping("/user/getQuestionAsk")
    @ResponseBody
    public List<Question> getQuestionAsk(Integer userId){
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.eq("author_id",userId).orderBy(true,false,"update_time");
        List<Question> list = questionService.list(questionQueryWrapper);
        return list;
    }

    /*获取用户的所有回答*/
    //返回问题的title和answer的url
    @PostMapping("/user/getAnswerMake")
    @ResponseBody
    public Map<String,Object> getAnswerMake(Integer userId) {
        Map<String,Object> mp = new HashMap<>();

        QueryWrapper<Answer> answerQueryWrapper = new QueryWrapper<>();
        answerQueryWrapper.eq("author_id",userId);
        List<Answer> answerList = answerService.list(answerQueryWrapper);
        List<String> urlList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        List<LocalDateTime> timeList = new ArrayList<>();
        for (Answer answer : answerList) {
            urlList.add("/user"+answer.getAnswerUrl());
            timeList.add(answer.getUpdateTime());
            Question question = questionService.getById(answer.getQuestionId());
            titleList.add(question.getTitle());
        }
        mp.put("urlList",urlList);
        mp.put("titleList",titleList);
        mp.put("timeList",timeList);
        return mp;
    }

    /*获取用户收藏的回答*/
    //返回问题的title，answer的url，作者昵称，作者的个人主页url
    @PostMapping("/user/getAnswerCollect")
    @ResponseBody
    public Map<String,Object> getAnswerCollect(Integer userId) {
        Map<String,Object> mp = new HashMap<>();

        QueryWrapper<AnswerCollect> answerCollectQueryWrapper = new QueryWrapper<>();
        answerCollectQueryWrapper.select("answer_id");
        answerCollectQueryWrapper.eq("uid",userId);
        answerCollectQueryWrapper.eq("state",1);
        List<Object> list = answerCollectService.listObjs(answerCollectQueryWrapper); //获取所有收藏的answerId

        if (list.isEmpty()) return null;
        List<String> answerUrlList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        List<String> nameList = new ArrayList<>();
        List<String> userUrlList = new ArrayList<>();
        List<LocalDateTime> timeList = new ArrayList<>();
        for (Object o : list) {
            Answer answer = answerService.getById((Integer) o);
            Question question = questionService.getById(answer.getQuestionId());
            User user = userService.getById(answer.getAuthorId());
            answerUrlList.add("/user"+answer.getAnswerUrl());
            titleList.add(question.getTitle());
            nameList.add(user.getNickName());
            userUrlList.add("/user/homepage/"+answer.getAuthorId());
            timeList.add(answer.getUpdateTime());
        }
        mp.put("answerUrlList",answerUrlList);
        mp.put("titleList",titleList);
        mp.put("nameList",nameList);
        mp.put("userUrlList",userUrlList);
        mp.put("timeList",timeList);
        return mp;
    }

    /*获取用户接收的通知*/
    //如果该通知指向的对象被删除了，则删除该通知
    @PostMapping("/user/getInfo")
    @ResponseBody
    public List<MessageVO> getInfo(Integer userId){
        List<MessageVO> list = new ArrayList<>();

        QueryWrapper<Message> messageQueryWrapper = new QueryWrapper<>();
        messageQueryWrapper.eq("uid",userId).orderBy(true,false,"update_time");
        List<Message> messageList = messageService.list(messageQueryWrapper);
        for (Message message : messageList) {
            if(!message.getState()) {
                message.setState(true); messageService.updateById(message);
            }
            MessageVO messageVO = new MessageVO();
            messageVO.setId(message.getId());

            /*收藏*/
            if(message.getStateType() == 3) {
                Answer answer = answerService.getById(message.getObjectId());
                if(answer == null) continue;
                Question question = questionService.getById(answer.getQuestionId());
                User user = userService.getById(message.getMessageUid());
                messageVO.setIcon("fa-star");
                messageVO.setColor("burlywood");
                messageVO.setTitle(question.getTitle());
                messageVO.setTitleUrl(message.getUrl());
                messageVO.setUserName(user.getNickName());
                messageVO.setUserUrl("/user/homepage/"+user.getId());
                messageVO.setContent("收藏了你的回答");
                messageVO.setStateType(message.getStateType());
                messageVO.setObjectType(message.getObjectType());
                messageVO.setUpdateTime(message.getUpdateTime());
                list.add(messageVO);
            }
            /*回答问题*/
            else if(message.getStateType() == 7 && message.getObjectType() == 2) {
                Answer answer = answerService.getById(message.getObjectId());
                if(answer == null) continue;
                Question question = questionService.getById(answer.getQuestionId());
                User user = userService.getById(message.getMessageUid());
                messageVO.setIcon("fa-comment");
                messageVO.setColor("blue");
                messageVO.setTitle(question.getTitle());
                messageVO.setTitleUrl(message.getUrl());
                messageVO.setUserName(user.getNickName());
                messageVO.setUserUrl("/user/homepage/"+user.getId());
                messageVO.setContent("回答了你的问题");
                messageVO.setStateType(message.getStateType());
                messageVO.setObjectType(message.getObjectType());
                messageVO.setUpdateTime(message.getUpdateTime());
                list.add(messageVO);
            }
            /*回答点赞*/
            else if(message.getStateType() == 1 && message.getObjectType() == 2) {
                Answer answer = answerService.getById(message.getObjectId());
                if(answer == null) continue;
                Question question = questionService.getById(answer.getQuestionId());
                User user = userService.getById(message.getMessageUid());
                messageVO.setIcon("fa-thumbs-up");
                messageVO.setColor("cornflowerblue");
                messageVO.setTitle(question.getTitle());
                messageVO.setTitleUrl(message.getUrl());
                messageVO.setUserName(user.getNickName());
                messageVO.setUserUrl("/user/homepage/"+user.getId());
                messageVO.setContent("点赞了你的回答");
                messageVO.setStateType(message.getStateType());
                messageVO.setObjectType(message.getObjectType());
                messageVO.setUpdateTime(message.getUpdateTime());
                list.add(messageVO);
            }
            /*回答踩*/
            else if(message.getStateType() == 2 && message.getObjectType() == 2) {
                Answer answer = answerService.getById(message.getObjectId());
                if(answer == null) continue;
                Question question = questionService.getById(answer.getQuestionId());
                User user = userService.getById(message.getMessageUid());
                messageVO.setIcon("fa-thumbs-down");
                messageVO.setColor("cornflowerblue");
                messageVO.setTitle(question.getTitle());
                messageVO.setTitleUrl(message.getUrl());
                messageVO.setUserName(user.getNickName());
                messageVO.setUserUrl("/user/homepage/"+user.getId());
                messageVO.setContent("踩了你的回答");
                messageVO.setStateType(message.getStateType());
                messageVO.setObjectType(message.getObjectType());
                messageVO.setUpdateTime(message.getUpdateTime());
                list.add(messageVO);
            }
            /*评论*/
            else if(message.getStateType() == 4) {
                Comment comment = commentService.getById(message.getObjectId());
                if(comment == null) continue;
                Answer answer = answerService.getById(comment.getAnswerId());
                Question question = questionService.getById(answer.getQuestionId());
                User user = userService.getById(message.getMessageUid());
                messageVO.setIcon("fa-comments");
                messageVO.setColor("blue");
                messageVO.setTitle(question.getTitle());
                messageVO.setTitleUrl(message.getUrl());
                messageVO.setUserName(user.getNickName());
                messageVO.setUserUrl("/user/homepage/"+user.getId());
                messageVO.setContent("评论了你的回答：");
                messageVO.setMsg(comment.getContent()); //附加信息
                messageVO.setStateType(message.getStateType());
                messageVO.setObjectType(message.getObjectType());
                messageVO.setUpdateTime(message.getUpdateTime());
                System.out.println(messageVO);
                list.add(messageVO);
            }
            /*关注用户*/
            else if(message.getStateType() == 9 && message.getObjectType() == 4) {
                User user = userService.getById(message.getObjectId());
                messageVO.setIcon("fa-heart");
                messageVO.setColor("deeppink");
                messageVO.setTitle("用户关注通知");
                messageVO.setTitleUrl(message.getUrl());
                messageVO.setUserName(user.getNickName());
                messageVO.setUserUrl("/user/homepage/"+user.getId());
                messageVO.setContent("关注了你");
                messageVO.setStateType(message.getStateType());
                messageVO.setObjectType(message.getObjectType());
                messageVO.setUpdateTime(message.getUpdateTime());
                list.add(messageVO);
            }
            /*删除用户评论*/
            else if(message.getStateType() == 6 && message.getObjectType() == 3) {
                String contentAndMessage = message.getMessage();
                Integer index = contentAndMessage.length();
                for(int i = contentAndMessage.length()-1;i >= 0;i--) {
                    if(contentAndMessage.charAt(i) == '|') {
                        index = i;
                        break;
                    }
                }
                String content = contentAndMessage.substring(0,index);
                String messageStr = contentAndMessage.substring(index+1,contentAndMessage.length());

                messageVO.setIcon("fa-trash");
                messageVO.setColor("red");
                messageVO.setTitle(content);
                messageVO.setTitleUrl(message.getUrl());
                messageVO.setUserName("管理员");
                messageVO.setUserUrl("#");
                messageVO.setContent("删除了你的评论");
                messageVO.setMsg(messageStr);
                messageVO.setStateType(message.getStateType());
                messageVO.setObjectType(message.getObjectType());
                messageVO.setUpdateTime(message.getUpdateTime());
                list.add(messageVO);
            }
            /*删除用户回答*/
            else if(message.getStateType() == 6 && message.getObjectType() == 2) {
                String contentAndMessage = message.getMessage();
                Integer index = contentAndMessage.length();
                for(int i = contentAndMessage.length()-1;i >= 0;i--) {
                    if(contentAndMessage.charAt(i) == '|') {
                        index = i;
                        break;
                    }
                }
                String title = contentAndMessage.substring(0,index);
                String messageStr = contentAndMessage.substring(index+1,contentAndMessage.length());

                messageVO.setIcon("fa-trash");
                messageVO.setColor("red");
                messageVO.setTitle(title);
                messageVO.setTitleUrl(message.getUrl());
                messageVO.setUserName("管理员");
                messageVO.setUserUrl("#");
                messageVO.setContent("删除了你在该问题下的回答");
                messageVO.setMsg(messageStr);
                messageVO.setStateType(message.getStateType());
                messageVO.setObjectType(message.getObjectType());
                messageVO.setUpdateTime(message.getUpdateTime());
                list.add(messageVO);
            }
            /*警告用户回答*/
            else if(message.getStateType() == 5 && message.getObjectType() == 2) {
                String contentAndMessage = message.getMessage();
                Integer index = contentAndMessage.length();
                for(int i = contentAndMessage.length()-1;i >= 0;i--) {
                    if(contentAndMessage.charAt(i) == '|') {
                        index = i;
                        break;
                    }
                }
                String title = contentAndMessage.substring(0,index);
                String messageStr = contentAndMessage.substring(index+1,contentAndMessage.length());

                messageVO.setIcon("fa-exclamation-triangle");
                messageVO.setColor("sandybrown");
                messageVO.setTitle(title);
                messageVO.setTitleUrl(message.getUrl());
                messageVO.setUserName("管理员");
                messageVO.setUserUrl("#");
                messageVO.setContent("警告了你在该问题下的回答");
                messageVO.setMsg(messageStr);
                messageVO.setStateType(message.getStateType());
                messageVO.setObjectType(message.getObjectType());
                messageVO.setUpdateTime(message.getUpdateTime());
                list.add(messageVO);
            }
            /*警告用户问题*/
            else if(message.getStateType() == 5 && message.getObjectType() == 1) {
                String contentAndMessage = message.getMessage();
                Integer index = contentAndMessage.length();
                for(int i = contentAndMessage.length()-1;i >= 0;i--) {
                    if(contentAndMessage.charAt(i) == '|') {
                        index = i;
                        break;
                    }
                }
                String title = contentAndMessage.substring(0,index);
                String messageStr = contentAndMessage.substring(index+1,contentAndMessage.length());

                messageVO.setIcon("fa-exclamation-triangle");
                messageVO.setColor("sandybrown");
                messageVO.setTitle(title);
                messageVO.setTitleUrl(message.getUrl());
                messageVO.setUserName("管理员");
                messageVO.setUserUrl("#");
                messageVO.setContent("警告了你提的问题");
                messageVO.setMsg(messageStr);
                messageVO.setStateType(message.getStateType());
                messageVO.setObjectType(message.getObjectType());
                messageVO.setUpdateTime(message.getUpdateTime());
                list.add(messageVO);
            }
            /*删除用户问题*/
            else if(message.getStateType() == 6 && message.getObjectType() == 1) {
                String contentAndMessage = message.getMessage();
                Integer index = contentAndMessage.length();
                for(int i = contentAndMessage.length()-1;i >= 0;i--) {
                    if(contentAndMessage.charAt(i) == '|') {
                        index = i;
                        break;
                    }
                }
                String title = contentAndMessage.substring(0,index);
                String messageStr = contentAndMessage.substring(index+1,contentAndMessage.length());

                messageVO.setIcon("fa-trash");
                messageVO.setColor("red");
                messageVO.setTitle(title);
                messageVO.setTitleUrl(message.getUrl());
                messageVO.setUserName("管理员");
                messageVO.setUserUrl("#");
                messageVO.setContent("删除了你提出的问题");
                messageVO.setMsg(messageStr);
                messageVO.setStateType(message.getStateType());
                messageVO.setObjectType(message.getObjectType());
                messageVO.setUpdateTime(message.getUpdateTime());
                list.add(messageVO);
            }
        }
        return list;

    }


    /*前往设置界面*/
    @GetMapping("user/settings_info/{id}")
    public String toSettings_info(@PathVariable("id") Integer id, Model model){
        User user = userService.getById(id);
        model.addAttribute("user",user);
        return "user/settings_info";
    }

    /*上传压缩头像*/
    @PostMapping("/user/update_settings_info_avatar")
    @ResponseBody
    public Map<String,Object> uploadAvatar(@RequestParam(value = "file") MultipartFile file,
                                           HttpServletRequest request) throws IllegalStateException, IOException {

        HashMap<String,Object> mp = new HashMap<>();

        //1.确定保存的文件夹
        String dirPath = request.getServletContext().getRealPath("upload");//获取到项目部署的绝对路径
        System.out.println("dirPath="+dirPath);

        File dir = new File(dirPath);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        //2.确定保存的文件名
        String orginalFilename = file.getOriginalFilename();
        int beginIndex = orginalFilename.lastIndexOf(".");
        String suffix ="";
        if(beginIndex!=-1) {
            suffix = orginalFilename.substring(beginIndex);
        }
        String filename = UUID.randomUUID().toString()+suffix; //随机生成新文件名，防止冲突和中文乱码
        System.out.println(filename);
        //创建文件对象，表示要保存的头像文件,第一个参数表示存储的文件夹，第二个参数表示存储的文件
        File dest = new File(dir,filename);
        //执行保存
        file.transferTo(dest);

        System.out.println("图片保存成功");

        //更新数据表
        String avatar = "/upload/"+filename;
        User user_session = (User) request.getSession().getAttribute("user");
        Integer id = user_session.getId();

        System.out.println("获取Id成功");
        System.out.println(id);

        // 通过id找到用户,并更改头像路径
        User user = userService.getById(id);
        user.setPhotoUrl(avatar);
        userService.updateById(user);

        System.out.println("数据库更新成功");

        mp.put("state",1);
        return mp;
    }

    /*保存设置信息*/
    @PostMapping("/user/update_settings_info")
    @ResponseBody
    public boolean updateUser(User user) {
        System.out.println(user);
        boolean b = userService.updateById(user);
        System.out.println(userService.getById(user.getId()));
        System.out.println(b);
        return b;
    }

    @PostMapping("/user/getNavPhoto")
    @ResponseBody
    public String getNavPhoto(Integer sessionId) {
        User user = userService.getById(sessionId);
        return user.getPhotoUrl();
    }

}
