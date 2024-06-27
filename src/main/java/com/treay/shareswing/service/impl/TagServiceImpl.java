package com.treay.shareswing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.treay.shareswing.common.ErrorCode;
import com.treay.shareswing.exception.BusinessException;
import com.treay.shareswing.exception.ThrowUtils;
import com.treay.shareswing.mapper.TagMapper;
import com.treay.shareswing.mapper.UserMapper;
import com.treay.shareswing.model.entity.Tag;
import com.treay.shareswing.model.entity.User;
import com.treay.shareswing.service.TagService;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author 16799
 * @description 针对表【tag(标签表)】的数据库操作Service实现
 * @createDate 2024-06-01 11:50:40
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
        implements TagService {
    @Resource
    private TagMapper tagMapper;

    @Override
    public String addTag(Tag tag) {
        String name = tag.getTagName();
        Integer isp = tag.getIsParent();
        Long pid = tag.getParentId();
        if (StringUtils.isBlank(name)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "必要参数为空");
        }
        QueryWrapper<Tag> queryWrappername = new QueryWrapper<>();
        queryWrappername.eq("tagName", name);
        long count = tagMapper.selectCount(queryWrappername);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签重复");
        }
        if (isp.equals(1)) {
            // 检查父标签是否存在
            ThrowUtils.throwIf(pid == null || pid.equals(0), ErrorCode.PARAMS_ERROR, "父标签不存在");
            QueryWrapper<Tag> queryWrapperpid = new QueryWrapper<>();
            queryWrapperpid.eq("id", pid);
            long countpid = tagMapper.selectCount(queryWrappername);
            if (countpid < 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "父标签不存在");
            }

        }

        Tag pretag = new Tag();
        pretag.setTagName(name);
        pretag.setIsParent(isp);
        pretag.setParentId(pid);
        boolean saveResult = this.save(pretag);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加失败");
        }
        return tag.toString();
    }



    @Override
    public Boolean changeTag(Tag tag) {
        String name = tag.getTagName();
        Integer isp = tag.getIsParent();
        Long pid = tag.getParentId();


        // 检查标签名是否重复（排除自身）
        QueryWrapper<Tag> queryWrapperName = new QueryWrapper<>();
        queryWrapperName.eq("tagName", name).ne("id", tag.getId());
        long count = tagMapper.selectCount(queryWrapperName);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签重复");
        }

        // 检查父标签是否存在
        if (pid != 0) {
            QueryWrapper<Tag> queryWrapperPid = new QueryWrapper<>();
            queryWrapperPid.eq("id", pid);
            long countPid = tagMapper.selectCount(queryWrapperPid);
            if (countPid <= 0) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "父标签不存在");
            }
        }

        // 更新标签
        Tag changeTag = new Tag();
        changeTag.setId(tag.getId()); // 确保设置正确的ID
        changeTag.setTagName(name);
        changeTag.setIsParent(isp);
        changeTag.setParentId(pid);
        int updateCount = tagMapper.updateById(changeTag);
        if (updateCount <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "修改失败");
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteTag(Long id) {
        if (id == null || id.equals(0)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        Tag tag = tagMapper.selectOne(queryWrapper);
        if (tag.getIsParent().equals(1)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "父级标签删除失败");
        }
        int count = tagMapper.deleteById(tag);
        if (count <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除失败");
        }
        return true;
    }

}




