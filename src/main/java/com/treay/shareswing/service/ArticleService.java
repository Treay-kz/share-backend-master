package com.treay.shareswing.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.treay.shareswing.model.dto.article.ArticleQueryRequest;
import com.treay.shareswing.model.entity.Article;
import com.treay.shareswing.model.vo.ArticleVO;

import java.util.List;


/**
* @author 16799
* @description 针对表【article(文章)】的数据库操作Service
* @createDate 2024-06-01 11:50:40
*/
public interface ArticleService extends IService<Article> {

    /**
     * 条件查询文章
     * @param articleQueryRequest
     * @return
     */
    Wrapper<Article> queryArticles(ArticleQueryRequest articleQueryRequest);

    /**
     * 参数校验
     * @param article
     * @param b
     */
    void validArticle(Article article, boolean b);
}
