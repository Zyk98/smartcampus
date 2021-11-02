package com.cumtb.mp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cumtb.mp.entity.User;
import com.cumtb.mp.service.impl.UserServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Controller
public class LoginController {

    @Autowired
    UserServiceImpl userService;

    /*后端校验用户名唯一性*/
    @ResponseBody
    @GetMapping("/checkName")
    public Map<String, Object> checkName(String name) {
        /*先验证邮箱格式*/
        Map<String, Object> mp = new HashMap<>(); //mp封装结果和提示信息
        String reg = "^[a-zA-Z0-9_]{2,16}$";

        if (!Pattern.matches(reg,name)) {
            mp.put("flag", false);
            mp.put("msg", "用户名必须是2-16位英文,数字,下划线的组合");
            return mp;
        }

        /*通过用户名获取用户*/
        QueryWrapper wrapperName = new QueryWrapper();
        wrapperName.eq("user_name",name);
        User user = userService.getOne(wrapperName);

        /*判断用户是否存在*/
        if (user==null) {
            mp.put("flag", true);
            mp.put("msg", "用户名可用");
        } else {
            mp.put("flag", false);
            mp.put("msg", "该用户名已被注册！");
        }
        return mp;
    }

    /*后端校验邮箱唯一性*/
    @ResponseBody
    @GetMapping("/checkEmail")
    public Map<String, Object> checkEmail(String email) {
        /*先验证邮箱格式*/
        Map<String, Object> mp = new HashMap<>(); //mp封装结果和提示信息
        String reg = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";

        if (!Pattern.matches(reg,email)) {
            mp.put("flag", false);
            mp.put("msg", "邮箱格式不正确！");
            return mp;
        }

        /*通过邮箱获取用户*/
        QueryWrapper wrapperEmail = new QueryWrapper();
        wrapperEmail.eq("email",email);
        User user = userService.getOne(wrapperEmail);

        /*判断用户是否存在*/
        if (user==null) {
            mp.put("flag", true);
            mp.put("msg", "邮箱可用");
        } else {
            mp.put("flag", false);
            mp.put("msg", "该邮箱已被注册！");
        }
        return mp;
    }

    /*注册*/
    @PostMapping("/register")
    public String register(String email, String userName, String password){

        //将密码进行md5加盐加密并存到数据库中
        Object salt = ByteSource.Util.bytes(userName); //用户名作为盐
        //转为密文。参数为加密方式，明文密码，盐值，迭代次数
        SimpleHash simpleHash = new SimpleHash("md5", password, salt, 1024);

        User user = new User();
        user.setUserName(userName);
        user.setNickName(userName);
        user.setPassword(simpleHash.toString()); //密码通过密文存储
        user.setEmail(email);
        user.setPhotoUrl("/asserts/img/user-default.jpg");
        userService.save(user); //保存到数据库中
        return "user/login";
    }

    /*登录*/
    @PostMapping("/login")
    public String login(String username,String password,Model model){

        //封装用户名和密码到token
        UsernamePasswordToken token = new UsernamePasswordToken(username,password);

        //获取当前用户
        Subject subject = SecurityUtils.getSubject();

        try{
            subject.login(token); //执行登录方法

            //登录成功，将user放到session域中
            Session session = subject.getSession();
            User user = (User) subject.getPrincipal();
            session.setAttribute("user",user);
            System.out.println(user);

            return "redirect:/";
        }catch (UnknownAccountException e){ //用户名不存在
            model.addAttribute("msg","用户名不存在");
            return "user/login";
        }catch (IncorrectCredentialsException e){
            model.addAttribute("msg","用户名或密码错误");
            return "user/login";
        }
    }

}
