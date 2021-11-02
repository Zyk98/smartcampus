package com.cumtb.mp.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cumtb.mp.entity.Answer;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zheng
 * @since 2021-04-22
 */
public interface IAnswerService extends IService<Answer> {
    public com.cumtb.mp.entity.Answer getAnswerByOrder(Integer questionId, String orderColumn);

    public IPage<com.cumtb.mp.entity.Answer> selectAllByPage(Page<com.cumtb.mp.entity.Answer> page, Wrapper<com.cumtb.mp.entity.Answer> wrapper);
}
