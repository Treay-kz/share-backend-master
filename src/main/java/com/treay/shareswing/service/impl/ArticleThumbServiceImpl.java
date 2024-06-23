package com.treay.shareswing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.treay.shareswing.common.ErrorCode;
import com.treay.shareswing.exception.BusinessException;
import com.treay.shareswing.mapper.ArticleMapper;
import com.treay.shareswing.mapper.ArticleThumbMapper;
import com.treay.shareswing.mapper.UserMapper;
import com.treay.shareswing.model.entity.Article;
import com.treay.shareswing.model.entity.ArticleFavour;
import com.treay.shareswing.model.entity.ArticleThumb;
import com.treay.shareswing.model.entity.User;
import com.treay.shareswing.service.ArticleThumbService;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 16799
* @description 针对表【article_thumb(文章点赞)】的数据库操作Service实现
* @createDate 2024-06-01 11:50:40
*/
@Service
public class ArticleThumbServiceImpl extends ServiceImpl<ArticleThumbMapper, ArticleThumb>
    implements ArticleThumbService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private ArticleThumbMapper articleThumbMapper;
    @Resource
    private ArticleMapper articleMapper;

    @Override
    public ArticleThumb change(Long articleId, Long userid) {
        // 判空
        if (articleId == null || userid == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }

        User user = userMapper.selectById(userid);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        }
        // 检查文章状态
        if (article.getArticleStatus() != 2) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文章状态不允许收藏");
        }
        // 如果已存在则取消点赞（删除记录）
        QueryWrapper<ArticleThumb> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userid).eq("articleId", articleId);
        ArticleThumb existingThumb = articleThumbMapper.selectOne(queryWrapper);

        if (existingThumb != null) {
            articleThumbMapper.delete(queryWrapper);
        } else {
            // 如果不存在则添加一条点赞记录
            ArticleThumb newThumb = new ArticleThumb();
            newThumb.setUserId(userid);
            newThumb.setArticleId(articleId);
            articleThumbMapper.insert(newThumb);
        }

        // 更新 article 表中的 thumb 属性
        QueryWrapper<ArticleThumb> countWrapper = new QueryWrapper<>();
        countWrapper.eq("id", articleId);
        Long thumbCount = articleThumbMapper.selectCount(countWrapper);

        article.setThumbNum(Math.toIntExact(thumbCount));
        articleMapper.updateById(article);

        return existingThumb;
    }

    @Override
    public List<Article> findByUserId(Long userId) {
        if(userId==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        // 查询收藏表中该用户收藏的所有文章ID
        QueryWrapper<ArticleThumb> thumbQueryWrapper = new QueryWrapper<>();
        thumbQueryWrapper.eq("userId", userId);
        List<ArticleThumb> articleThumbs= articleThumbMapper.selectList(thumbQueryWrapper);

        if (articleThumbs.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> articleIds = articleThumbs.stream()
                .map(ArticleThumb::getArticleId)
                .collect(Collectors.toList());

        List<Article> articles = articleMapper.selectBatchIds(articleIds);
        //已经判断收藏文章id不为空了则直接抛错
        if (articles.isEmpty()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文章表查询失败");
        }

        return articles;
    }
}




