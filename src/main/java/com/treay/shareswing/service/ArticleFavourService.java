package com.treay.shareswing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.treay.shareswing.model.entity.Article;
import com.treay.shareswing.model.entity.ArticleFavour;

import java.util.List;


/**
* @author 16799
* @description 针对表【article_favour(文章收藏)】的数据库操作Service
* @createDate 2024-06-01 11:50:40
*/
public interface ArticleFavourService extends IService<ArticleFavour> {
    List<Article> findByUserId(Long userid);
    ArticleFavour change(Long articleId, Long userid);

}
