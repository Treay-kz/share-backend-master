package com.treay.shareswing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.treay.shareswing.common.ErrorCode;
import com.treay.shareswing.exception.BusinessException;
import com.treay.shareswing.mapper.ArticleFavourMapper;
import com.treay.shareswing.mapper.ArticleMapper;
import com.treay.shareswing.mapper.ArticleThumbMapper;
import com.treay.shareswing.mapper.UserMapper;
import com.treay.shareswing.model.entity.Article;
import com.treay.shareswing.model.entity.ArticleFavour;
import com.treay.shareswing.model.entity.ArticleThumb;
import com.treay.shareswing.model.entity.User;
import com.treay.shareswing.service.ArticleFavourService;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 16799
* @description 针对表【article_favour(文章收藏)】的数据库操作Service实现
* @createDate 2024-06-01 11:50:40
*/
@Service
public class ArticleFavourServiceImpl extends ServiceImpl<ArticleFavourMapper, ArticleFavour>
    implements ArticleFavourService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private ArticleFavourMapper articleFavourMapper;
    @Resource
    private ArticleMapper articleMapper;

    @Override
    public List<Article> findByUserId(Long userid) {
        if(userid==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }

        User user = userMapper.selectById(userid);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        // 查询收藏表中该用户收藏的所有文章ID
        QueryWrapper<ArticleFavour> favourQueryWrapper = new QueryWrapper<>();
        favourQueryWrapper.eq("userId", userid);
        List<ArticleFavour> articleFavours = articleFavourMapper.selectList(favourQueryWrapper);

        if (articleFavours.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> articleIds = articleFavours.stream()
                .map(ArticleFavour::getArticleId)
                .collect(Collectors.toList());

        List<Article> articles = articleMapper.selectBatchIds(articleIds);
        //已经判断收藏文章id不为空了则直接抛错
        if (articles.isEmpty()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文章表查询失败");
        }

        return articles;
    }

    @Override
    public ArticleFavour change(Long articleId, Long userid) {

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
        // 如果已存在则取消收藏（删除记录）
        QueryWrapper<ArticleFavour> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userid).eq("articleId", articleId);
        ArticleFavour existingFavour = articleFavourMapper.selectOne(queryWrapper);

        if (existingFavour != null) {
            articleFavourMapper.delete(queryWrapper);
        } else {
            ArticleFavour newFavour= new ArticleFavour();
            newFavour.setUserId(userid);
            newFavour.setArticleId(articleId);
            articleFavourMapper.insert(newFavour);
        }

        // 更新 article 表中的 favourNum 属性
        QueryWrapper<ArticleFavour> countWrapper = new QueryWrapper<>();
        countWrapper.eq("id", articleId);
        Long favourCount = articleFavourMapper.selectCount(countWrapper);

        article.setFavourNum(Math.toIntExact(favourCount));
        articleMapper.updateById(article);

        return existingFavour;
    }
}




