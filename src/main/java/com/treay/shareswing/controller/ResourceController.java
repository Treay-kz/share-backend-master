package com.treay.shareswing.controller;

import com.treay.shareswing.common.BaseResponse;
import com.treay.shareswing.common.ErrorCode;
import com.treay.shareswing.common.ResultUtils;
import com.treay.shareswing.exception.ThrowUtils;
import com.treay.shareswing.model.entity.Resource;
import com.treay.shareswing.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 标签接口
 *
 */
@RestController
@RequestMapping("/notice")
@Slf4j
public class ResourceController {
    @javax.annotation.Resource
    private ResourceService resourceService;
    /**
     * 创建通知
     * @param resource
     */
    @PostMapping("/add")
    public BaseResponse<String> addResource(@RequestBody Resource resource, HttpServletRequest request) {
        ThrowUtils.throwIf(resource == null, ErrorCode.PARAMS_ERROR);
        String result = resourceService.addResource(resource,request);
        return ResultUtils.success(result);
    }

    /**
     * 删除通知
     * @param resourceid
     * @return
     */
    @PostMapping("/delete/{resourceid}")
    public BaseResponse<String> deleteResource(@PathVariable("resourceid") Integer resourceid, HttpServletRequest request) {
        ThrowUtils.throwIf(resourceid == null, ErrorCode.PARAMS_ERROR);
        String result = resourceService.deleteResource(resourceid,request);
        return ResultUtils.success(result);
    }

    /**
     * 通知搜索
     * @param userid
     * @return
     */
    @PostMapping("/searchByUserid/{userid}")
    public BaseResponse<List<Resource>> searchResourceByUserid(@PathVariable("userid") Integer userid, HttpServletRequest request) {
        ThrowUtils.throwIf(userid == null, ErrorCode.PARAMS_ERROR);
        List<Resource> resourceList = resourceService.searchResourceByUserid(userid);
        return ResultUtils.success(resourceList);
    }

    /**
     *
     * @param request
     * @return
     */
    @PostMapping("/searchAll")
    public BaseResponse<List<Resource>> searchAll(HttpServletRequest request) {

        List<Resource> resourceList = resourceService.searchAll(request);
        return ResultUtils.success(resourceList);
    }
    /**
     * 通知更新状态
     * @param resource
     */
    @PostMapping("/change")
    public BaseResponse<Integer> rewriteResource(@RequestBody Resource resource, HttpServletRequest request) {
        ThrowUtils.throwIf(resource == null, ErrorCode.PARAMS_ERROR);
        Integer result = resourceService.rewriteResource(resource);
        return ResultUtils.success(result);
    }

    /**
     * 根据资源状态查询
     * @param status
     * @return
     */

    @PostMapping("/searchByStatus/{status}")
    public BaseResponse<List<Resource>> searchResourcesByStatus(@PathVariable("status") Integer status, HttpServletRequest request){
        ThrowUtils.throwIf(status == null, ErrorCode.PARAMS_ERROR);
        List<Resource> resourceList = resourceService.searchResourcesByStatus(status);
        return ResultUtils.success(resourceList);
    }



}
