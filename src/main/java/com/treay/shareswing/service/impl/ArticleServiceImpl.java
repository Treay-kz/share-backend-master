package com.treay.shareswing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.treay.shareswing.mapper.ArticleMapper;
import com.treay.shareswing.model.entity.Article;
import com.treay.shareswing.service.ArticleService;

import org.springframework.stereotype.Service;

/**
* @author 16799
* @description 针对表【article(文章)】的数据库操作Service实现
* @createDate 2024-06-01 11:50:40
*/
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService {

}




