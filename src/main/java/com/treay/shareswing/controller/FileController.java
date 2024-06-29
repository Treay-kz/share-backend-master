package com.treay.shareswing.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import co.elastic.clients.elasticsearch.sql.QueryRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
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
import com.treay.shareswing.model.dto.file.FileQueryRequest;
import com.treay.shareswing.model.dto.tag.TagQueryRequest;
import com.treay.shareswing.model.entity.File;
import com.treay.shareswing.model.entity.Tag;
import com.treay.shareswing.model.entity.User;
import com.treay.shareswing.model.vo.FileVO;
import com.treay.shareswing.service.FileService;
import com.treay.shareswing.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import static com.treay.shareswing.constant.FileConstant.DEV_FILEPATH;

/**
 * 文件接口
 *
 * treay
 *
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {



    @Resource
    private FileService fileService;

    @Resource
    private UserService userService;


    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public BaseResponse<Long> uploadFile(@RequestParam MultipartFile file, HttpServletRequest request) {

        String originalFilename = file.getOriginalFilename();
        String type = FileUtil.extName(originalFilename);
        long size = file.getSize();
        // 定义保存文件的目录
        java.io.File fileParent = new java.io.File(DEV_FILEPATH);

        if (!fileParent.exists()) {
            fileParent.mkdirs();
        }

        // 定义文件的唯一标识符，就是存储的文件名
        String uuid = IdUtil.fastSimpleUUID();
        String fileUuid = uuid + "." + type;

        java.io.File oldFile = new java.io.File(DEV_FILEPATH + java.io.File.separator + fileUuid);

        try {
            // 将文件保存到指定位置
            file.transferTo(oldFile);
            //获取文件的md5
            String md5 = SecureUtil.md5(oldFile);
            //查询文件是否存在
            QueryWrapper<File> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(File::getMd5, md5);
            // avatarList是uuid加密后与上传的文件相同的文件列表
            List<File> fileList = fileService.list(queryWrapper);
            String url = "";
            if (CollectionUtils.isNotEmpty(fileList)) {
                // 如果有相同文件则只在数据库中写入，并将传入的文件删除
                url = fileList.get(0).getFileUrl();
                oldFile.delete();
            } else {
                // 服务器
                //  url = "http://yujian-backend.treay.cn/api/file/" + fileUuid;
                // 本地
                url = "http://localhost:8101/api/file/" + fileUuid;
            }

            User loginUser = userService.getLoginUser(request);
            if (loginUser == null){
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
            }
            // 写入数据库
            File newFile = new File();
            newFile.setUserId(loginUser.getId());
            newFile.setFileStatus(0);
            newFile.setFileUrl(url);
            newFile.setFileName(originalFilename);
            newFile.setFileSize(size/1024);
            newFile.setFileType(type);
            boolean result = fileService.save(newFile);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            // 返回新写入的数据 id
            long newFileId = newFile.getId();
            return ResultUtils.success(newFileId);
    } catch (IOException e) {
            // 处理文件上传过程中的IO异常
            oldFile.delete();
            log.error("上传失败: ", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"上传失败");
        }
    }

    /**
     * 文件下载
     * @param fileUUID
     * @param response
     * @throws IOException
     */
    @GetMapping("/{fileUUID}")
    public void down(@PathVariable String fileUUID, HttpServletResponse response) throws IOException {
        // 获取要下载的文件
        java.io.File file = new java.io.File(DEV_FILEPATH + "\\" + fileUUID);
        // 构造响应
        response.addHeader("Content-Disposition", "attachment;filename" + URLEncoder.encode(fileUUID, "UTF-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream os = response.getOutputStream();
        try {
            os.write(jodd.io.FileUtil.readBytes(file));
        }
        catch (IOException e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        } finally {
            os.flush();
            os.close();
        }
    }
    /**
     * 删除文件
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteFile(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        File oldFile = fileService.getById(id);
        ThrowUtils.throwIf(oldFile == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldFile.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = fileService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


    /**
     * 根据 id 获取文件（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<FileVO> getFileVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        File file = fileService.getById(id);
        ThrowUtils.throwIf(file == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(fileService.getFileVO(file, request));
    }

    /**
     * 分页获取文件列表（封装类）
     *
     * @param fileQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<FileVO>> listFileVOByPage(@RequestBody FileQueryRequest fileQueryRequest,
                                                               HttpServletRequest request) {
        long current = fileQueryRequest.getCurrent();
        long size = fileQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<File> filePage = fileService.page(new Page<>(current, size),
                fileService.getQueryWrapper(fileQueryRequest));
        // 获取封装类
        return ResultUtils.success(fileService.getFileVOPage(filePage, request));
    }

    /**
     * 分页获取当前登录用户创建的文件列表
     *
     * @param fileQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<FileVO>> listMyFileVOByPage(@RequestBody FileQueryRequest fileQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(fileQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        fileQueryRequest.setUserId(loginUser.getId());
        long current = fileQueryRequest.getCurrent();
        long size = fileQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<File> filePage = fileService.page(new Page<>(current, size),
                fileService.getQueryWrapper(fileQueryRequest));
        // 获取封装类
        return ResultUtils.success(fileService.getFileVOPage(filePage, request));
    }
    /**
     * 审核文件
     * @param fileReviewRequest
     * @param request
     * @return
     */
    @PostMapping("/admin/review/file")
    public BaseResponse<Boolean> reviewFile(@RequestBody ArticleReviewRequest fileReviewRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(fileReviewRequest == null, ErrorCode.PARAMS_ERROR);
        // 鉴权
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 操作数据库
        boolean result = fileService.reviewFile(fileReviewRequest,request);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 分页获取文件列表（仅管理员可用）
     *
     * @param fileQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<File>> listFileByPage(@RequestBody FileQueryRequest fileQueryRequest) {
        long current = fileQueryRequest.getCurrent();
        long size = fileQueryRequest.getPageSize();
        // 查询数据库
        Page<File> tagPage = fileService.page(new Page<>(current, size),
                fileService.getQueryWrapper(fileQueryRequest));
        return ResultUtils.success(tagPage);
    }
}
