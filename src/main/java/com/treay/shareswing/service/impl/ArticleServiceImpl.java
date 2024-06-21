package com.treay.shareswing.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.treay.shareswing.common.ErrorCode;
import com.treay.shareswing.constant.CommonConstant;
import com.treay.shareswing.exception.ThrowUtils;
import com.treay.shareswing.mapper.ArticleFavourMapper;
import com.treay.shareswing.mapper.ArticleMapper;
import com.treay.shareswing.mapper.ArticleThumbMapper;
import com.treay.shareswing.model.dto.admin.ArticleReviewRequest;
import com.treay.shareswing.model.dto.article.ArticleQueryRequest;
import com.treay.shareswing.model.entity.Article;
import com.treay.shareswing.model.entity.ArticleFavour;
import com.treay.shareswing.model.entity.ArticleThumb;
import com.treay.shareswing.model.enums.ArticleStatusEnum;
import com.treay.shareswing.model.enums.UserRoleEnum;
import com.treay.shareswing.model.vo.ArticleVO;
import com.treay.shareswing.model.vo.UserVO;
import com.treay.shareswing.model.entity.User;
import com.treay.shareswing.service.ArticleService;

import com.treay.shareswing.service.UserService;
import com.treay.shareswing.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.beans.Transient;
import java.util.List;


/**
* @author 16799
* @description 针对表【article(文章)】的数据库操作Service实现
* @createDate 2024-06-01 11:50:40
*/
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService {

    @Resource
    private UserService userService;
    @Resource
    private ArticleThumbMapper articleThumbMapper;

    @Resource
    private ArticleFavourMapper articleFavourMapper;
    /**
     * 根据条件查询文章列表
     * @param articleQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Article> queryArticles(ArticleQueryRequest articleQueryRequest) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        // 获取所有文章
        if (articleQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = articleQueryRequest.getId();
        String searchText = articleQueryRequest.getSearchText();
        String title = articleQueryRequest.getTitle();
        String content = articleQueryRequest.getContent();
        Long userId = articleQueryRequest.getUserId();
        String tags = articleQueryRequest.getTags();

        Gson gson = new Gson();
        // 登录用户标签 把标签转换成list<string>
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 补充需要的查询条件
        // 根据关键字查询
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }

        // 模糊查询
        // 根据标题、内容查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 精确查询
        // 根据文章id、作者id查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 只获取审核通过的文章
        queryWrapper.eq("status", 2);
        return queryWrapper;
    }


    /**
     * 校验数据
     *
     * @param article
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validArticle(Article article, boolean add) {
        ThrowUtils.throwIf(article == null, ErrorCode.PARAMS_ERROR);
        String title = article.getTitle();
        String content = article.getContent();
        // 创建数据时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title,content) , ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        if (StringUtils.isNotBlank(title)) {
            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
        }
    }

    /**
     * 获取文章封装
     *
     * @param article
     * @param request
     * @return
     */
    @Override
    public ArticleVO getArticleVO(Article article, HttpServletRequest request) {
        // 对象转封装类
        ArticleVO articleVO = ArticleVO.objToVo(article);

        // region 可选
        // 1. 关联查询用户信息
        Long userId = article.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }

        UserVO userVO = UserVO.objToVo(user);
        articleVO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
        long articleId = article.getId();
//        User loginUser = userService.getLoginUserPermitNull(request);
        User loginUser = userService.getLoginUser(request);
        if (loginUser != null) {
            // 获取点赞
            QueryWrapper<ArticleThumb> articleThumbQueryWrapper = new QueryWrapper<>();
            articleThumbQueryWrapper.in("articleId", articleId);
            articleThumbQueryWrapper.eq("userId", loginUser.getId());
            ArticleThumb articleThumb = articleThumbMapper.selectOne(articleThumbQueryWrapper);
            articleVO.setHasThumb(articleThumb != null);
            // 获取收藏
            QueryWrapper<ArticleFavour> articleFavourQueryWrapper = new QueryWrapper<>();
            articleFavourQueryWrapper.in("articleId", articleId);
            articleFavourQueryWrapper.eq("userId", loginUser.getId());
            ArticleFavour articleFavour = articleFavourMapper.selectOne(articleFavourQueryWrapper);
            articleVO.setHasFavour(articleFavour != null);
        }

        return articleVO;
    }

    /**
     * 删除文章
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteArticle(long id) {
//        1. 先删点赞和收藏关系表
        articleThumbMapper.deleteById(id);
        articleFavourMapper.deleteById(id);
//        2. 再删实体表
        return this.removeById(id);
    }

    /**
     * 文章审核
     * @param articleReviewRequest
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reviewArticle(ArticleReviewRequest articleReviewRequest,HttpServletRequest request) {
        // 获取参数
        Long id = articleReviewRequest.getId();
        Boolean isPass = articleReviewRequest.getIsPass();
        Article oldArticle = this.getById(id);
        // 审核通过
        if (isPass) {
            oldArticle.setArticleStatus(ArticleStatusEnum.SUCCESS.getValue());
            return this.updateById(oldArticle);
        }
        // 审核未通过
        String reviewMessage = articleReviewRequest.getReviewMessage();
        String reviewDescription = articleReviewRequest.getReviewDescription();
        User user = userService.getLoginUser(request);
        Long userId = user.getId();
        // 向审核表中加数据
        oldArticle.setArticleStatus(ArticleStatusEnum.FAIL.getValue());
        return this.updateById(oldArticle);
    }
}





