package com.treay.shareswing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.treay.shareswing.model.entity.Resource;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
* @author 16799
* @description 针对表【notice(文件表)】的数据库操作Service
* @createDate 2024-06-01 11:50:40
*/
public interface ResourceService extends IService<Resource> {
    String addResource(Resource notice, HttpServletRequest request);
    String deleteResource(Integer noticeid,HttpServletRequest request);

    List<Resource> searchResourcesByStatus(Integer status);

    List<Resource> searchResourceByUserid(Integer userid);
    Integer rewriteResource(Resource resource);

    List<Resource> searchAll(HttpServletRequest request);
}
