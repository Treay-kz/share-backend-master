package com.treay.shareswing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.treay.shareswing.model.entity.Article;
import com.treay.shareswing.model.entity.ArticleThumb;

import java.util.List;


/**
* @author 16799
* @description 针对表【article_thumb(文章点赞)】的数据库操作Service
* @createDate 2024-06-01 11:50:40
*/
public interface ArticleThumbService extends IService<ArticleThumb> {

    ArticleThumb change(Long articleId,Long userid);

    List<Article> findByUserId(Long userId);
}
