package com.cumtb.mp.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cumtb.mp.entity.Tag;
import com.cumtb.mp.entity.Question;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zheng
 * @since 2021-04-22
 */
public interface IQuestionService extends IService<Question> {
    public IPage<com.cumtb.mp.entity.Question> selectAllByPage(Page<com.cumtb.mp.entity.Question> page, Wrapper<com.cumtb.mp.entity.Question> wrapper);

    public List<Tag> getAllTagByQuestionId(Integer questionId);


    public Map<String,Object> getAllQuestionDemo(Integer tagId, String searchStr, String orderStr, Integer curPage, Integer countPerPage);

    public ArrayList<Integer> getAllQuestionIdByTagId(Integer tagId);
}
