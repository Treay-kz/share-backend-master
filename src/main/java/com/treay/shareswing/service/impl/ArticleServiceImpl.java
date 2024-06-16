package com.treay.shareswing.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.treay.shareswing.common.ErrorCode;
import com.treay.shareswing.exception.ThrowUtils;
import com.treay.shareswing.mapper.ArticleMapper;
import com.treay.shareswing.model.dto.article.ArticleQueryRequest;
import com.treay.shareswing.model.entity.Article;
import com.treay.shareswing.model.vo.ArticleVO;
import com.treay.shareswing.service.ArticleService;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 16799
* @description 针对表【article(文章)】的数据库操作Service实现
* @createDate 2024-06-01 11:50:40
*/
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService {
    /**
     * 根据条件查询文章列表
     * @param articleQueryRequest
     * @return
     */
    @Override
    public Wrapper<Article> queryArticles(ArticleQueryRequest articleQueryRequest) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();

        // 动态SQL构建，根据不同的查询条件进行过滤
        queryWrapper.lambda()
                .eq(articleQueryRequest.getId() != null && articleQueryRequest.getId() > 0, Article::getId, articleQueryRequest.getId()) // 根据ID查询
                .eq(articleQueryRequest.getUserId() != null && articleQueryRequest.getUserId() > 0, Article::getUserId, articleQueryRequest.getUserId());// 根据作者ID查询
        // 根据关键字查询
        if (StringUtils.isNotBlank(articleQueryRequest.getSearchText())) {
            queryWrapper.lambda()
                    .like(Article::getTitle, articleQueryRequest.getSearchText())
                    .or()
                    .like(Article::getContent, articleQueryRequest.getSearchText());
        }
        return queryWrapper;
    }

    @Override
    public void validArticle(Article article, boolean b) {
//        ThrowUtils.throwIf(article.getId() == null || article.getId() <= 0 , ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isAnyBlank(article.getTitle(),article.getContent())  , ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(article.getId() == null || article.getId() <= 0, ErrorCode.PARAMS_ERROR);
    }
}




