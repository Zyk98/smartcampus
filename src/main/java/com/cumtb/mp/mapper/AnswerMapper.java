package com.cumtb.mp.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cumtb.mp.entity.Answer;
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
public interface AnswerMapper extends BaseMapper<Answer> {
    public com.cumtb.mp.entity.Answer selectAnswerByOrder(@Param("questionId") Integer questionId, @Param("orderColumn") String orderColumn);

    public IPage<com.cumtb.mp.entity.Answer> selectAllByPage(Page<com.cumtb.mp.entity.Answer> page, @Param(Constants.WRAPPER) Wrapper<com.cumtb.mp.entity.Answer> wrapper);
}
