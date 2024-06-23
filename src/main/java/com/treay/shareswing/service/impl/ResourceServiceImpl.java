package com.treay.shareswing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.treay.shareswing.common.ErrorCode;
import com.treay.shareswing.exception.BusinessException;
import com.treay.shareswing.mapper.ResourceMapper;
import com.treay.shareswing.mapper.UserMapper;
import com.treay.shareswing.model.entity.Resource;
import com.treay.shareswing.model.entity.User;
import com.treay.shareswing.service.ResourceService;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author
* @description 针对表【Resource(文件表)】的数据库操作Service实现
* @createDate 2024-06-01 11:50:40
*/
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource>
    implements ResourceService {
    @javax.annotation.Resource
    private ResourceMapper resourceMapper;
    @javax.annotation.Resource
    private UserMapper userMapper;
    private static final Set<String> ALLOWED_FILE_EXTENSIONS = Collections.unmodifiableSet(
            new HashSet<String>() {{
                add("word");
                add("ppt");
                add("pdf");
                add("txt");
            }} );
   public void isAnyBlank(Object... params) {
        for (Object param : params) {
            if (param == null || (param instanceof String && StringUtils.isBlank((String) param))) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "必要参数为空");
            }
        }
    }

    @Override
    public String addResource(Resource resource, HttpServletRequest request) {
        Long size = resource.getFileSize();
        String url  = resource.getFileUrl();
        String type = resource.getFileType();
        Long userid = resource.getUserId();
        /**
         * 判空
         */
        isAnyBlank(size,url,type,userid);

        User user = userMapper.selectById(userid);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
//        // 从会话中获取当前登录的用户信息
//        User currentUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
//        if (currentUser == null) {
//            throw new SecurityException("User not authenticated");
//        }
//
//        if (!currentUser.getId().equals(userid) {
//            throw new SecurityException("用户id不匹配");
//        }
        QueryWrapper<Resource> queryWrapperUrl = new QueryWrapper<>();
        queryWrapperUrl.eq("fileUrl",url);
        long count = resourceMapper.selectCount(queryWrapperUrl);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件重复");
        }


        // 验证文件类型是否合法
        if (!ALLOWED_FILE_EXTENSIONS.contains(type.toLowerCase())) {
            throw new IllegalArgumentException("文件类型不合法");
        }
        Resource renotice = new Resource();
        renotice.setFileSize(size);
        renotice.setFileType(type);
        renotice.setFileUrl(url);
        renotice.setUserId(userid);
        renotice.setFileStatus(0);
//        renotice.setUserId(currentUser.getId());
        boolean saveResult = this.save(renotice);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "提交失败");
        }
        return renotice.toString();
    }

    @Override
    public String deleteResource(Integer resourceid,HttpServletRequest request) {
        if (resourceid == null || resourceid.equals(0)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "UnuseableParams:id");
        }

        QueryWrapper<Resource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", resourceid);
        Resource resource = resourceMapper.selectOne(queryWrapper);

        if (resource == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无记录");
        }

//        // 从会话中获取当前登录的用户信息
//        User currentUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
//        if (currentUser == null) {
//            throw new SecurityException("User not authenticated");
//        }
//
//        if (!currentUser.getId().equals(resource.getUserId())) {
//            throw new SecurityException("用户id不匹配");
//        }

        int count = resourceMapper.deleteById(resource);
        if (count<=0){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除失败");
        }
        return "删除成功"+resource;

    }
    @Override
    public List<Resource> searchResourcesByStatus(Integer status) {
        if (status == null || status.equals(0)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "UnuseableParams:status");
        }
        // 创建查询条件，限定资源状态
        QueryWrapper<Resource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fileStatus", status);

        // 查询资源列表
        List<Resource> resources = resourceMapper.selectList(queryWrapper);
        if (resources == null || resources.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不存在的资源");
        }

        // 返回资源列表
        return resources;
    }
    @Override
    public List<Resource> searchResourceByUserid(Integer userid) {
        if (userid == null || userid.equals(0)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "UnuseableParams:userid");
        }

        // 检查用户是否存在
        User user = userMapper.selectById(userid);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        QueryWrapper<Resource> queryWrapperuserid = new QueryWrapper<>();
        queryWrapperuserid.eq("userId",userid);

        // 用户存在，继续查询资源列表
        List<Resource> resources = resourceMapper.selectList(queryWrapperuserid);
        if (resources == null || resources.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户无相关数据");
        }

        // 返回资源列表
        return resources;
    }

    @Override
    public Integer rewriteResource(Resource resource) {
        Long size = resource.getFileSize();
        String url  = resource.getFileUrl();
        String type = resource.getFileType();
        Long userid = resource.getUserId();
        Integer status = resource.getFileStatus();

        // 判空
        isAnyBlank(size,url,type,userid,status);

        if (status != 0 && status != 1 && status != 2) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "状态只能为0、1或2");
        }

        QueryWrapper<Resource> queryWrapperUrl = new QueryWrapper<>();
        queryWrapperUrl.eq("fileUrl",url).ne("id", resource.getId());
        long count = resourceMapper.selectCount(queryWrapperUrl);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件重复");
        }

        Resource existingResource = resourceMapper.selectById(resource.getId());
        Integer existingStatus = existingResource.getFileStatus();
        // 如果数据库中的状态为2，则不允许回退
        if (existingStatus != null && existingStatus == 2) {
            resource.setFileStatus(existingStatus);
        }

        int updateCount = resourceMapper.updateById(resource);
        if (updateCount <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "修改失败");
        }

        return updateCount;
    }

    @Override
    public List<Resource> searchAll(HttpServletRequest request) {
//        // 从会话中获取当前登录的用户信息
//        User currentUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
//        if (currentUser == null) {
//            throw new SecurityException("User not authenticated");
//        }
//
//        if (!currentUser.getUserRole().equals("admin")) {
//            throw new SecurityException("用户权限不匹配");
//        }

        List<Resource> resourceList = resourceMapper.selectList(null);
        if (resourceList == null || resourceList.isEmpty()){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "暂无数据");
        }
        return resourceList;
    }
}




