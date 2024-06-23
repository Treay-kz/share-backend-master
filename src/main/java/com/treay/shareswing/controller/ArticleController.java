package com.treay.shareswing.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.treay.shareswing.annotation.AuthCheck;
import com.treay.shareswing.common.BaseResponse;
import com.treay.shareswing.common.DeleteRequest;
import com.treay.shareswing.common.ErrorCode;
import com.treay.shareswing.common.ResultUtils;
import com.treay.shareswing.constant.UserConstant;
import com.treay.shareswing.exception.BusinessException;
import com.treay.shareswing.exception.ThrowUtils;
import com.treay.shareswing.model.dto.admin.ArticleReviewRequest;
import com.treay.shareswing.model.dto.article.ArticleAddRequest;
import com.treay.shareswing.model.dto.article.ArticleEditRequest;
import com.treay.shareswing.model.dto.article.ArticleQueryRequest;
import com.treay.shareswing.model.dto.article.ArticleUpdateRequest;
import com.treay.shareswing.model.entity.Article;
import com.treay.shareswing.model.entity.User;
import com.treay.shareswing.model.vo.ArticleVO;
import com.treay.shareswing.service.ArticleService;
import com.treay.shareswing.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 文章接口
 *
 * treay
 * 
 */
@RestController
@RequestMapping("/article")
@Slf4j
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @Resource
    private UserService userService;



    /**
     * 创建文章
     *
     * @param articleAddRequest
     * @param request
     * @return  id
     */
    @PostMapping("/add")
    public BaseResponse<Long> addArticle(@RequestBody ArticleAddRequest articleAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(articleAddRequest == null, ErrorCode.PARAMS_ERROR);
        // DTO -> entity
        Article article = new Article();
        BeanUtils.copyProperties(articleAddRequest, article);

        // 数据校验
        articleService.validArticle(article, true);

        User loginUser = userService.getLoginUser(request);
        // 初始化
        article.setUserId(loginUser.getId());
        article.setFavourNum(0);
        article.setThumbNum(0);

        // 写入数据库
        boolean result = articleService.save(article);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newArticleId = article.getId();
        return ResultUtils.success(newArticleId);
    }

    /**
     * 删除文章(仅管理员和本人可用)
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteArticle(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Article oldArticle = articleService.getById(id);
        ThrowUtils.throwIf(oldArticle == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldArticle.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = articleService.deleteArticle(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新文章（仅本人可用）
     *
     * @param articleUpdateRequest
     * @return
     */
    @PostMapping("/update")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateArticle(@RequestBody ArticleUpdateRequest articleUpdateRequest,HttpServletRequest request) {
        if (articleUpdateRequest == null || articleUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        // 实体类和 DTO 进行转换
        Article article = new Article();
        BeanUtils.copyProperties(articleUpdateRequest, article);
        // 数据校验
        articleService.validArticle(article, false);

        // 判断是否存在
        long id = articleUpdateRequest.getId();
        Article oldArticle = articleService.getById(id);
        ThrowUtils.throwIf(oldArticle == null, ErrorCode.NOT_FOUND_ERROR);

        // 仅本人或管理员可更新
        if (!oldArticle.getUserId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = articleService.updateById(article);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 查询文章详细信息
     * 根据 id 获取文章（封装类）
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<ArticleVO> getArticleVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Article article = articleService.getById(id);
        ThrowUtils.throwIf(article == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(articleService.getArticleVO(article, request));
    }


    /**
     * 分页获取文章列表和 根据关键字查询 所有用户可用
     *
     * @param articleQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Article>> listArticleByPage(@RequestBody ArticleQueryRequest articleQueryRequest) {
        long current = articleQueryRequest.getCurrent();
        long size = articleQueryRequest.getPageSize();
        // 查询数据库
        Page<Article> articlePage = articleService.page(new Page<>(current, size),articleService.queryArticles(articleQueryRequest));

        return ResultUtils.success(articlePage);
    }


    /**
     * 审核文章
     * @param articleReviewRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Boolean> reviewArticle(@RequestBody ArticleReviewRequest articleReviewRequest,HttpServletRequest request) {
        ThrowUtils.throwIf(articleReviewRequest == null, ErrorCode.PARAMS_ERROR);
        // 鉴权
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 操作数据库
        boolean result = articleService.reviewArticle(articleReviewRequest,request);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


//
//    /**
//     * 分页获取文章列表（封装类）
//     *
//     * @param articleQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/list/page/vo")
//    public BaseResponse<Page<ArticleVO>> listArticleVOByPage(@RequestBody ArticleQueryRequest articleQueryRequest,
//                                                               HttpServletRequest request) {
//        long current = articleQueryRequest.getCurrent();
//        long size = articleQueryRequest.getPageSize();
//        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//        // 查询数据库
//        Page<Article> articlePage = articleService.page(new Page<>(current, size),
//                articleService.getQueryWrapper(articleQueryRequest));
//        // 获取封装类
//        return ResultUtils.success(articleService.getArticleVOPage(articlePage, request));
//    }
//
//    /**
//     * 分页获取当前登录用户创建的文章列表
//     *
//     * @param articleQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/my/list/page/vo")
//    public BaseResponse<Page<ArticleVO>> listMyArticleVOByPage(@RequestBody ArticleQueryRequest articleQueryRequest,
//                                                                 HttpServletRequest request) {
//        ThrowUtils.throwIf(articleQueryRequest == null, ErrorCode.PARAMS_ERROR);
//        // 补充查询条件，只查询当前登录用户的数据
//        User loginUser = userService.getLoginUser(request);
//        articleQueryRequest.setUserId(loginUser.getId());
//        long current = articleQueryRequest.getCurrent();
//        long size = articleQueryRequest.getPageSize();
//        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//        // 查询数据库
//        Page<Article> articlePage = articleService.page(new Page<>(current, size),
//                articleService.getQueryWrapper(articleQueryRequest));
//        // 获取封装类
//        return ResultUtils.success(articleService.getArticleVOPage(articlePage, request));
//    }
//
//    /**
//     * 编辑文章（给用户使用）
//     *
//     * @param articleEditRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/edit")
//    public BaseResponse<Boolean> editArticle(@RequestBody ArticleEditRequest articleEditRequest, HttpServletRequest request) {
//        if (articleEditRequest == null || articleEditRequest.getId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        // todo 在此处将实体类和 DTO 进行转换
//        Article article = new Article();
//        BeanUtils.copyProperties(articleEditRequest, article);
//        // 数据校验
//        articleService.validArticle(article, false);
//        User loginUser = userService.getLoginUser(request);
//        // 判断是否存在
//        long id = articleEditRequest.getId();
//        Article oldArticle = articleService.getById(id);
//        ThrowUtils.throwIf(oldArticle == null, ErrorCode.NOT_FOUND_ERROR);
//        // 仅本人或管理员可编辑
//        if (!oldArticle.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
//            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
//        }
//        // 操作数据库
//        boolean result = articleService.updateById(article);
//        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
//        return ResultUtils.success(true);
//    }


}
