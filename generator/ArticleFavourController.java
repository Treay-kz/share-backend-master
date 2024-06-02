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
import com.treay.shareswing.model.dto.articleFavour.ArticleFavourAddRequest;
import com.treay.shareswing.model.dto.articleFavour.ArticleFavourEditRequest;
import com.treay.shareswing.model.dto.articleFavour.ArticleFavourQueryRequest;
import com.treay.shareswing.model.dto.articleFavour.ArticleFavourUpdateRequest;
import com.treay.shareswing.model.entity.ArticleFavour;
import com.treay.shareswing.model.entity.User;
import com.treay.shareswing.model.vo.ArticleFavourVO;
import com.treay.shareswing.service.ArticleFavourService;
import com.treay.shareswing.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 文章点赞接口
 *
 * treay
 * 
 */
@RestController
@RequestMapping("/articleFavour")
@Slf4j
public class ArticleFavourController {

    @Resource
    private ArticleFavourService articleFavourService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建文章点赞
     *
     * @param articleFavourAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addArticleFavour(@RequestBody ArticleFavourAddRequest articleFavourAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(articleFavourAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        ArticleFavour articleFavour = new ArticleFavour();
        BeanUtils.copyProperties(articleFavourAddRequest, articleFavour);
        // 数据校验
        articleFavourService.validArticleFavour(articleFavour, true);
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
        articleFavour.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = articleFavourService.save(articleFavour);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newArticleFavourId = articleFavour.getId();
        return ResultUtils.success(newArticleFavourId);
    }

    /**
     * 删除文章点赞
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteArticleFavour(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        ArticleFavour oldArticleFavour = articleFavourService.getById(id);
        ThrowUtils.throwIf(oldArticleFavour == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldArticleFavour.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = articleFavourService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新文章点赞（仅管理员可用）
     *
     * @param articleFavourUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateArticleFavour(@RequestBody ArticleFavourUpdateRequest articleFavourUpdateRequest) {
        if (articleFavourUpdateRequest == null || articleFavourUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        ArticleFavour articleFavour = new ArticleFavour();
        BeanUtils.copyProperties(articleFavourUpdateRequest, articleFavour);
        // 数据校验
        articleFavourService.validArticleFavour(articleFavour, false);
        // 判断是否存在
        long id = articleFavourUpdateRequest.getId();
        ArticleFavour oldArticleFavour = articleFavourService.getById(id);
        ThrowUtils.throwIf(oldArticleFavour == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = articleFavourService.updateById(articleFavour);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取文章点赞（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<ArticleFavourVO> getArticleFavourVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        ArticleFavour articleFavour = articleFavourService.getById(id);
        ThrowUtils.throwIf(articleFavour == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(articleFavourService.getArticleFavourVO(articleFavour, request));
    }

    /**
     * 分页获取文章点赞列表（仅管理员可用）
     *
     * @param articleFavourQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<ArticleFavour>> listArticleFavourByPage(@RequestBody ArticleFavourQueryRequest articleFavourQueryRequest) {
        long current = articleFavourQueryRequest.getCurrent();
        long size = articleFavourQueryRequest.getPageSize();
        // 查询数据库
        Page<ArticleFavour> articleFavourPage = articleFavourService.page(new Page<>(current, size),
                articleFavourService.getQueryWrapper(articleFavourQueryRequest));
        return ResultUtils.success(articleFavourPage);
    }

    /**
     * 分页获取文章点赞列表（封装类）
     *
     * @param articleFavourQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<ArticleFavourVO>> listArticleFavourVOByPage(@RequestBody ArticleFavourQueryRequest articleFavourQueryRequest,
                                                               HttpServletRequest request) {
        long current = articleFavourQueryRequest.getCurrent();
        long size = articleFavourQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<ArticleFavour> articleFavourPage = articleFavourService.page(new Page<>(current, size),
                articleFavourService.getQueryWrapper(articleFavourQueryRequest));
        // 获取封装类
        return ResultUtils.success(articleFavourService.getArticleFavourVOPage(articleFavourPage, request));
    }

    /**
     * 分页获取当前登录用户创建的文章点赞列表
     *
     * @param articleFavourQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<ArticleFavourVO>> listMyArticleFavourVOByPage(@RequestBody ArticleFavourQueryRequest articleFavourQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(articleFavourQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        articleFavourQueryRequest.setUserId(loginUser.getId());
        long current = articleFavourQueryRequest.getCurrent();
        long size = articleFavourQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<ArticleFavour> articleFavourPage = articleFavourService.page(new Page<>(current, size),
                articleFavourService.getQueryWrapper(articleFavourQueryRequest));
        // 获取封装类
        return ResultUtils.success(articleFavourService.getArticleFavourVOPage(articleFavourPage, request));
    }

    /**
     * 编辑文章点赞（给用户使用）
     *
     * @param articleFavourEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editArticleFavour(@RequestBody ArticleFavourEditRequest articleFavourEditRequest, HttpServletRequest request) {
        if (articleFavourEditRequest == null || articleFavourEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        ArticleFavour articleFavour = new ArticleFavour();
        BeanUtils.copyProperties(articleFavourEditRequest, articleFavour);
        // 数据校验
        articleFavourService.validArticleFavour(articleFavour, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = articleFavourEditRequest.getId();
        ArticleFavour oldArticleFavour = articleFavourService.getById(id);
        ThrowUtils.throwIf(oldArticleFavour == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldArticleFavour.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = articleFavourService.updateById(articleFavour);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion
}
