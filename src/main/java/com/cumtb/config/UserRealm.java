package com.cumtb.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cumtb.mp.entity.User;
import com.cumtb.mp.service.impl.UserServiceImpl;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

/*自定义UserRealm，继承一个认证类*/
public class UserRealm extends AuthorizingRealm {

    @Autowired
    UserServiceImpl userService;

    /*授权*/
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行授权");
        return null;
    }

    /*认证*/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("执行认证");
        UsernamePasswordToken userToken = (UsernamePasswordToken) token; //UsernamePasswordToken,Controller里的令牌

        /*根据用户名查找记录*/
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("user_name",userToken.getUsername());
        User user = userService.getOne(wrapper);

        if (user==null){    //如果没有这个用户
            throw new UnknownAccountException(); //抛出UnknownAccountException
        }

        ByteSource salt = ByteSource.Util.bytes(user.getUserName()); //加盐(此处盐为用户名)

        //用户对象，用户数据库中的密文密码，盐，Realm名
        return new SimpleAuthenticationInfo(user,user.getPassword(),salt,getName());
    }
}