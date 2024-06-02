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
import com.treay.shareswing.model.dto.articleThumb.ArticleThumbAddRequest;
import com.treay.shareswing.model.dto.articleThumb.ArticleThumbEditRequest;
import com.treay.shareswing.model.dto.articleThumb.ArticleThumbQueryRequest;
import com.treay.shareswing.model.dto.articleThumb.ArticleThumbUpdateRequest;
import com.treay.shareswing.model.entity.ArticleThumb;
import com.treay.shareswing.model.entity.User;
import com.treay.shareswing.model.vo.ArticleThumbVO;
import com.treay.shareswing.service.ArticleThumbService;
import com.treay.shareswing.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 文章收藏接口
 *
 * treay
 * 
 */
@RestController
@RequestMapping("/articleThumb")
@Slf4j
public class ArticleThumbController {

    @Resource
    private ArticleThumbService articleThumbService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建文章收藏
     *
     * @param articleThumbAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addArticleThumb(@RequestBody ArticleThumbAddRequest articleThumbAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(articleThumbAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        ArticleThumb articleThumb = new ArticleThumb();
        BeanUtils.copyProperties(articleThumbAddRequest, articleThumb);
        // 数据校验
        articleThumbService.validArticleThumb(articleThumb, true);
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
        articleThumb.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = articleThumbService.save(articleThumb);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newArticleThumbId = articleThumb.getId();
        return ResultUtils.success(newArticleThumbId);
    }

    /**
     * 删除文章收藏
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteArticleThumb(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        ArticleThumb oldArticleThumb = articleThumbService.getById(id);
        ThrowUtils.throwIf(oldArticleThumb == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldArticleThumb.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = articleThumbService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新文章收藏（仅管理员可用）
     *
     * @param articleThumbUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateArticleThumb(@RequestBody ArticleThumbUpdateRequest articleThumbUpdateRequest) {
        if (articleThumbUpdateRequest == null || articleThumbUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        ArticleThumb articleThumb = new ArticleThumb();
        BeanUtils.copyProperties(articleThumbUpdateRequest, articleThumb);
        // 数据校验
        articleThumbService.validArticleThumb(articleThumb, false);
        // 判断是否存在
        long id = articleThumbUpdateRequest.getId();
        ArticleThumb oldArticleThumb = articleThumbService.getById(id);
        ThrowUtils.throwIf(oldArticleThumb == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = articleThumbService.updateById(articleThumb);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取文章收藏（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<ArticleThumbVO> getArticleThumbVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        ArticleThumb articleThumb = articleThumbService.getById(id);
        ThrowUtils.throwIf(articleThumb == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(articleThumbService.getArticleThumbVO(articleThumb, request));
    }

    /**
     * 分页获取文章收藏列表（仅管理员可用）
     *
     * @param articleThumbQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<ArticleThumb>> listArticleThumbByPage(@RequestBody ArticleThumbQueryRequest articleThumbQueryRequest) {
        long current = articleThumbQueryRequest.getCurrent();
        long size = articleThumbQueryRequest.getPageSize();
        // 查询数据库
        Page<ArticleThumb> articleThumbPage = articleThumbService.page(new Page<>(current, size),
                articleThumbService.getQueryWrapper(articleThumbQueryRequest));
        return ResultUtils.success(articleThumbPage);
    }

    /**
     * 分页获取文章收藏列表（封装类）
     *
     * @param articleThumbQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<ArticleThumbVO>> listArticleThumbVOByPage(@RequestBody ArticleThumbQueryRequest articleThumbQueryRequest,
                                                               HttpServletRequest request) {
        long current = articleThumbQueryRequest.getCurrent();
        long size = articleThumbQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<ArticleThumb> articleThumbPage = articleThumbService.page(new Page<>(current, size),
                articleThumbService.getQueryWrapper(articleThumbQueryRequest));
        // 获取封装类
        return ResultUtils.success(articleThumbService.getArticleThumbVOPage(articleThumbPage, request));
    }

    /**
     * 分页获取当前登录用户创建的文章收藏列表
     *
     * @param articleThumbQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<ArticleThumbVO>> listMyArticleThumbVOByPage(@RequestBody ArticleThumbQueryRequest articleThumbQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(articleThumbQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        articleThumbQueryRequest.setUserId(loginUser.getId());
        long current = articleThumbQueryRequest.getCurrent();
        long size = articleThumbQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<ArticleThumb> articleThumbPage = articleThumbService.page(new Page<>(current, size),
                articleThumbService.getQueryWrapper(articleThumbQueryRequest));
        // 获取封装类
        return ResultUtils.success(articleThumbService.getArticleThumbVOPage(articleThumbPage, request));
    }

    /**
     * 编辑文章收藏（给用户使用）
     *
     * @param articleThumbEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editArticleThumb(@RequestBody ArticleThumbEditRequest articleThumbEditRequest, HttpServletRequest request) {
        if (articleThumbEditRequest == null || articleThumbEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        ArticleThumb articleThumb = new ArticleThumb();
        BeanUtils.copyProperties(articleThumbEditRequest, articleThumb);
        // 数据校验
        articleThumbService.validArticleThumb(articleThumb, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = articleThumbEditRequest.getId();
        ArticleThumb oldArticleThumb = articleThumbService.getById(id);
        ThrowUtils.throwIf(oldArticleThumb == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldArticleThumb.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = articleThumbService.updateById(articleThumb);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion
}
