package com.treay.shareswing.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.treay.shareswing.model.dto.tag.TagQueryRequest;
import com.treay.shareswing.model.entity.Tag;

/**
* @author 16799
* @description 针对表【tag(标签表)】的数据库操作Service
* @createDate 2024-06-01 11:50:40
*/
public interface TagService extends IService<Tag> {

    /**
     * 添加标签
     * @param tag
     * @return
     */
    String addTag(Tag tag);


    /**
     * 修改标签
     * @param tag
     * @return
     */
    Boolean changeTag(Tag tag);

    /**
     * 删除标签
     * @param id
     * @return
     */
    Boolean deleteTag(Long id);

    /**
     * 获取标签列表
     * @param tagQueryRequest
     * @return
     */
    Wrapper<Tag> getQueryWrapper(TagQueryRequest tagQueryRequest);
}
