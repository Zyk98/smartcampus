package com.cumtb.mp.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cumtb.mp.entity.Tag;
import com.cumtb.mp.service.impl.TagServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zheng
 * @since 2021-04-22
 */
@Controller
public class TagController {
    @Autowired
    TagServiceImpl tagService;

    @PostMapping("/user/getAllTag")
    @ResponseBody
    public List<Tag> getAllTag(){
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.orderBy(true,false,"id");
        return tagService.list(wrapper);
    }
}
