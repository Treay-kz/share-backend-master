package com.treay.shareswing.controller;

import com.treay.shareswing.common.BaseResponse;
import com.treay.shareswing.common.ErrorCode;
import com.treay.shareswing.common.ResultUtils;
import com.treay.shareswing.exception.ThrowUtils;
import com.treay.shareswing.model.dto.article.ArticleAddRequest;
import com.treay.shareswing.model.entity.Article;
import com.treay.shareswing.model.entity.Tag;
import com.treay.shareswing.model.entity.User;
import com.treay.shareswing.service.TagService;
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
    /**
     * 标签添加
     * @param tag
     */
    @PostMapping("/add")
    public BaseResponse<String> addArticle(@RequestBody Tag tag, HttpServletRequest request) {
        ThrowUtils.throwIf(tag == null, ErrorCode.PARAMS_ERROR);
        String result = tagService.addTag(tag);
        return ResultUtils.success(result);
    }

    /**
     * 标签删除
     * @param tagid
     * @return
     */
    @PostMapping("/delete/{tagid}")
    public BaseResponse<String> deleteTag(@PathVariable("tagid") Integer tagid, HttpServletRequest request) {
        ThrowUtils.throwIf(tagid == null, ErrorCode.PARAMS_ERROR);
        String result = tagService.deleteTag(tagid);
        return ResultUtils.success(result);
    }

    /**
     * 标签搜索
     * @param tagid
     * @return
     */
    @PostMapping("/search/{tagid}")
    public BaseResponse<Tag> searchTag(@PathVariable("tagid") Integer tagid, HttpServletRequest request) {
        ThrowUtils.throwIf(tagid == null, ErrorCode.PARAMS_ERROR);
        Tag tag = tagService.searchTag(tagid);
        return ResultUtils.success(tag);
    }

    /**
     * 标签重写
     * @param tag
     */
    @PostMapping("/change")
    public BaseResponse<Integer> changeTag(@RequestBody Tag tag ,HttpServletRequest request) {
        ThrowUtils.throwIf(tag == null, ErrorCode.PARAMS_ERROR);
        Integer result = tagService.changeTag(tag);
        return ResultUtils.success(result);
    }

}
