package com.treay.shareswing.controller;

import com.treay.shareswing.common.BaseResponse;
import com.treay.shareswing.common.DeleteRequest;
import com.treay.shareswing.common.ErrorCode;
import com.treay.shareswing.common.ResultUtils;
import com.treay.shareswing.exception.ThrowUtils;
import com.treay.shareswing.model.dto.article.ArticleAddRequest;
import com.treay.shareswing.model.entity.Article;
import com.treay.shareswing.model.entity.Tag;
import com.treay.shareswing.model.entity.User;
import com.treay.shareswing.service.TagService;
import com.treay.shareswing.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 标签接口
 *
 */
@RestController
@RequestMapping("/tag")
@Slf4j
public class TagController {
    @Resource
    private TagService tagService;
    @Resource
    private UserService userService;
    /**
     * 标签添加
     * @param tag
     */
    @PostMapping("/add")
    public BaseResponse<String> addArticle(@RequestBody Tag tag, HttpServletRequest request) {
        ThrowUtils.throwIf(tag == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf( !userService.isAdmin(request), ErrorCode.PARAMS_ERROR);
        String result = tagService.addTag(tag);
        return ResultUtils.success(result);
    }

    /**
     * 标签删除
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTag(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf( !userService.isAdmin(request), ErrorCode.PARAMS_ERROR);
        Boolean result = tagService.deleteTag(deleteRequest.getId());
        return ResultUtils.success(result);
    }

    /**
     * 标签搜索
     * @param id
     * @return
     */
    @PostMapping("/search/{id}")
    public BaseResponse<Tag> searchTag(Integer id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf( !userService.isAdmin(request), ErrorCode.PARAMS_ERROR);
        Tag tag = tagService.getById(id);
        return ResultUtils.success(tag);
    }

    /**
     * 标签重写
     * @param tag
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTag(@RequestBody Tag tag ,HttpServletRequest request) {
        ThrowUtils.throwIf(tag == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf( !userService.isAdmin(request), ErrorCode.PARAMS_ERROR);
        Boolean result = tagService.changeTag(tag);
        return ResultUtils.success(result);
    }

}
