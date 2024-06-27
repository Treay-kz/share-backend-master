package com.treay.shareswing.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.treay.shareswing.common.ErrorCode;
import com.treay.shareswing.constant.CommonConstant;
import com.treay.shareswing.exception.ThrowUtils;
import com.treay.shareswing.mapper.FileMapper;
import com.treay.shareswing.model.dto.admin.ArticleReviewRequest;
import com.treay.shareswing.model.dto.file.FileQueryRequest;
import com.treay.shareswing.model.entity.Article;
import com.treay.shareswing.model.entity.File;

import com.treay.shareswing.model.entity.Review;
import com.treay.shareswing.model.entity.User;
import com.treay.shareswing.model.enums.ReviewStatusEnum;
import com.treay.shareswing.model.vo.FileVO;
import com.treay.shareswing.model.vo.UserVO;
import com.treay.shareswing.service.FileService;
import com.treay.shareswing.service.ReviewService;
import com.treay.shareswing.service.UserService;
import com.treay.shareswing.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 文件服务实现
 *
 * treay
 *
 */
@Service
@Slf4j
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

    @Resource
    private UserService userService;

    @Resource
    private ReviewService reviewService;

    /**
     * 获取查询条件
     *
     * @param fileQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<File> getQueryWrapper(FileQueryRequest fileQueryRequest) {
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        if (fileQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = fileQueryRequest.getId();

        String sortField = fileQueryRequest.getSortField();
        String sortOrder = fileQueryRequest.getSortOrder();
        Long userId = fileQueryRequest.getUserId();

        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取文件封装
     *
     * @param file
     * @param request
     * @return
     */
    @Override
    public FileVO getFileVO(File file, HttpServletRequest request) {
        // 对象转封装类
        FileVO fileVO = FileVO.objToVo(file);
        Long userId = file.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        fileVO.setUser(userVO);


        return fileVO;
    }

    /**
     * 分页获取文件封装
     *
     * @param filePage
     * @param request
     * @return
     */
    @Override
    public Page<FileVO> getFileVOPage(Page<File> filePage, HttpServletRequest request) {
        List<File> fileList = filePage.getRecords();
        Page<FileVO> fileVOPage = new Page<>(filePage.getCurrent(), filePage.getSize(), filePage.getTotal());
        if (CollUtil.isEmpty(fileList)) {
            return fileVOPage;
        }
        // 对象列表 => 封装对象列表
        List<FileVO> fileVOList = fileList.stream().map(file -> {
            return FileVO.objToVo(file);
        }).collect(Collectors.toList());


        // 关联查询用户信息
        Set<Long> userIdSet = fileList.stream().map(File::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        fileVOList.forEach(fileVO -> {
            Long userId = fileVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            fileVO.setUser(userService.getUserVO(user));
        });
        fileVOPage.setRecords(fileVOList);
        return fileVOPage;
    }

    @Override
    public boolean reviewFile(ArticleReviewRequest fileReviewRequest, HttpServletRequest request) {
        // 获取参数
        Long id = fileReviewRequest.getId();
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        Boolean isPass = fileReviewRequest.getIsPass();
        File oldFile = this.getById(id);
        // 审核通过
        if (isPass) {
            oldFile.setFileStatus(ReviewStatusEnum.SUCCESS.getValue());
            return this.updateById(oldFile);
        }
        // 审核未通过
        String reviewMessage = fileReviewRequest.getReviewMessage();
        String reviewDescription = fileReviewRequest.getReviewDescription();
        User user = userService.getLoginUser(request);
        Long userId = user.getId();
        // 向审核表中加数据
        Review articleReview = null;
        articleReview.setResourceId(id);
        articleReview.setResourceType(1);
        articleReview.setUserId(userId);
        articleReview.setMessage(reviewMessage);
        articleReview.setDescription(reviewDescription);
        boolean result = reviewService.save(articleReview);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR);
        // 更新文章状态
        oldFile.setFileStatus(ReviewStatusEnum.FAIL.getValue());
        return this.updateById(oldFile);
    }

}
