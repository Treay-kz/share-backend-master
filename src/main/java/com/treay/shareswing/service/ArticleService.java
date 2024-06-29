package com.treay.shareswing.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.treay.shareswing.model.dto.admin.ArticleReviewRequest;
import com.treay.shareswing.model.dto.article.ArticleQueryRequest;
import com.treay.shareswing.model.entity.Article;
import com.treay.shareswing.model.vo.ArticleVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
* @author 16799
* @description 针对表【article(文章)】的数据库操作Service
* @createDate 2024-06-01 11:50:40
*/
public interface ArticleService extends IService<Article> {

    /**
     * 条件查询文章（管理员）
     * @param articleQueryRequest
     * @return
     */
    QueryWrapper<Article> queryArticles(ArticleQueryRequest articleQueryRequest);

    /**
     * 参数校验
     * @param article
     * @param b
     */
    void validArticle(Article article, boolean b);

    /**
     * 根据id查询文章 返回封装类（VO）
     * @param article
     * @param request
     * @return
     */
    ArticleVO getArticleVO(Article article, HttpServletRequest request);

    /**
     * 删除文章
     * @param id
     * @return
     */
    boolean deleteArticle(long id);

    /**
     * 审核文章
     * @param articleReviewRequest
     * @return
     */
    boolean reviewArticle(ArticleReviewRequest articleReviewRequest,HttpServletRequest request);


    /**
     * 获取文章列表(用户)
     * @param articleQueryRequest
     * @return
     */
    QueryWrapper<Article> getQueryWrapper(ArticleQueryRequest articleQueryRequest);

    /**
     *
     * @param articlePage
     * @param request
     * @return
     */
    Page<ArticleVO> getArticleVOPage(Page<Article> articlePage, HttpServletRequest request);
}
