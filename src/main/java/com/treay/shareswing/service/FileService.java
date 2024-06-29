package com.treay.shareswing.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.treay.shareswing.model.dto.admin.ArticleReviewRequest;
import com.treay.shareswing.model.dto.file.FileQueryRequest;
import com.treay.shareswing.model.entity.File;
import com.treay.shareswing.model.entity.Tag;
import com.treay.shareswing.model.vo.FileVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 文件服务
 *
 * treay
 *
 */
public interface FileService extends IService<File> {



    /**
     * 获取查询条件
     *
     * @param fileQueryRequest
     * @return
     */
    QueryWrapper<File> getQueryWrapper(FileQueryRequest fileQueryRequest);
    
    /**
     * 获取文件封装
     *
     * @param file
     * @param request
     * @return
     */
    FileVO getFileVO(File file, HttpServletRequest request);

    /**
     * 分页获取文件封装
     *
     * @param filePage
     * @param request
     * @return
     */
    Page<FileVO> getFileVOPage(Page<File> filePage, HttpServletRequest request);

    /**
     * 审核文件
     * @param fileReviewRequest
     * @param request
     * @return
     */
    boolean reviewFile(ArticleReviewRequest fileReviewRequest, HttpServletRequest request);

}
