package com.cumtb.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//使用WebMvcConfigurer扩展SpringMVC功能
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    //添加一个视图映射，只是实现跳转页面就没必要在Controller实现一个空方法了
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //测试
        registry.addViewController("/test").setViewName("test");

        //起始页面
        registry.addViewController("/").setViewName("index");

        //登录
        registry.addViewController("/toLogin").setViewName("user/login");

        //register
        registry.addViewController("/toRegister").setViewName("user/register");


        //index
        registry.addViewController("/user/index").setViewName("user/index");

        //ask_question
        registry.addViewController("/user/toAsk").setViewName("user/ask_question");

        //question
        registry.addViewController("/user/question").setViewName("user/question");

        //answer
        registry.addViewController("/user/answer").setViewName("user/answer");

        //settings_info
        registry.addViewController("/user/settings_info").setViewName("user/settings_info");

        //homepage
        registry.addViewController("/user/homepage").setViewName("user/homepage");


    }


}
