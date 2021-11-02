package com.cumtb.mp.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cumtb.mp.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zheng
 * @since 2021-04-22
 */
public interface QuestionMapper extends BaseMapper<Question> {

    //QuestionMapper，注意返回值为IPage，第一个参数为Page，第二个参数封装的wrapper要带上@Param(Constants.WRAPPER)
    public IPage<com.cumtb.mp.entity.Question> selectAllByPage(Page<com.cumtb.mp.entity.Question> page, @Param(Constants.WRAPPER) Wrapper<com.cumtb.mp.entity.Question> wrapper);

}
