package com.treay.shareswing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.treay.shareswing.mapper.TagMapper;
import com.treay.shareswing.model.entity.Tag;
import com.treay.shareswing.service.TagService;

import org.springframework.stereotype.Service;

/**
* @author 16799
* @description 针对表【tag(标签表)】的数据库操作Service实现
* @createDate 2024-06-01 11:50:40
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService {

}




