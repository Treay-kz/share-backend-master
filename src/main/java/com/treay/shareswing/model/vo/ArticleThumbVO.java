//package com.treay.shareswing.model.vo;
//
//import cn.hutool.json.JSONUtil;
//import com.treay.shareswing.model.entity.ArticleThumb;
//import lombok.Data;
//import org.springframework.beans.BeanUtils;
//
//import java.io.Serializable;
//import java.util.Date;
//import java.util.List;
//
///**
// * 文章收藏视图
// *
// * treay
// *
// */
//@Data
//public class ArticleThumbVO implements Serializable {
//
//    /**
//     * id
//     */
//    private Long id;
//
//    /**
//     * 标题
//     */
//    private String title;
//
//    /**
//     * 内容
//     */
//    private String content;
//
//    /**
//     * 创建用户 id
//     */
//    private Long userId;
//
//    /**
//     * 创建时间
//     */
//    private Date createTime;
//
//    /**
//     * 更新时间
//     */
//    private Date updateTime;
//
//    /**
//     * 标签列表
//     */
//    private List<String> tagList;
//
//    /**
//     * 创建用户信息
//     */
//    private UserVO user;
//
//    /**
//     * 封装类转对象
//     *
//     * @param articleThumbVO
//     * @return
//     */
//    public static ArticleThumb voToObj(ArticleThumbVO articleThumbVO) {
//        if (articleThumbVO == null) {
//            return null;
//        }
//        ArticleThumb articleThumb = new ArticleThumb();
//        BeanUtils.copyProperties(articleThumbVO, articleThumb);
//        List<String> tagList = articleThumbVO.getTagList();
//        articleThumb.setTags(JSONUtil.toJsonStr(tagList));
//        return articleThumb;
//    }
//
//    /**
//     * 对象转封装类
//     *
//     * @param articleThumb
//     * @return
//     */
//    public static ArticleThumbVO objToVo(ArticleThumb articleThumb) {
//        if (articleThumb == null) {
//            return null;
//        }
//        ArticleThumbVO articleThumbVO = new ArticleThumbVO();
//        BeanUtils.copyProperties(articleThumb, articleThumbVO);
//        articleThumbVO.setTagList(JSONUtil.toList(articleThumb.getTags(), String.class));
//        return articleThumbVO;
//    }
//}
