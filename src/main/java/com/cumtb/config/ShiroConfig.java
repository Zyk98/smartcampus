package com.cumtb.config;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    //shiroFilterFactoryBean
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager")DefaultWebSecurityManager defaultWebSecurityManager){
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        //设置安全管理器
        bean.setSecurityManager(defaultWebSecurityManager);

        Map<String, String> filterMap = new LinkedHashMap<>();
//        filterMap.put("/user/*","authc"); //必须经过认证才能访问user目录下所有页面
        filterMap.put("/logout","logout");


        bean.setFilterChainDefinitionMap(filterMap);

        //设置登录请求(拦截后跳转的页面)
        bean.setLoginUrl("/toLogin");



        return bean;
    }

    //DafaultWebSecurituManager
    @Bean(name="securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //关联UserRealm
        securityManager.setRealm(userRealm);

        return securityManager;
    }

    //创建UserRealm对象
    @Bean(name = "userRealm")
    public UserRealm userRealm(){
        UserRealm userRealm = new UserRealm();

        //加密配置
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        //设置加密算法名称
        matcher.setHashAlgorithmName("md5");
        //设置加密次数
        matcher.setHashIterations(1024);
        userRealm.setCredentialsMatcher(matcher);

        return userRealm;
    }

}