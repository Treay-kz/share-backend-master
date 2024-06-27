package com.treay.shareswing.model.vo;

import cn.hutool.json.JSONUtil;
import com.treay.shareswing.model.entity.File;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 文件视图
 *
 * treay
 *
 */
@Data
public class FileVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String fileName;


    /**
     * 创建用户 id
     */
    private Long userId;


    /**
     * 创建用户信息
     */
    private UserVO user;

    /**
     * 封装类转对象
     *
     * @param fileVO
     * @return
     */
    public static File voToObj(FileVO fileVO) {
        if (fileVO == null) {
            return null;
        }
        File file = new File();
        BeanUtils.copyProperties(fileVO, file);
        return file;
    }

    /**
     * 对象转封装类
     *
     * @param file
     * @return
     */
    public static FileVO objToVo(File file) {
        if (file == null) {
            return null;
        }
        FileVO fileVO = new FileVO();
        BeanUtils.copyProperties(file, fileVO);
        return fileVO;
    }
}
