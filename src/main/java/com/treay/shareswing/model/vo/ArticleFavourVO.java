//package com.treay.shareswing.model.vo;
//
//import cn.hutool.json.JSONUtil;
//import com.treay.shareswing.model.entity.ArticleFavour;
//import lombok.Data;
//import org.springframework.beans.BeanUtils;
//
//import java.io.Serializable;
//import java.util.Date;
//import java.util.List;
//
///**
// * 文章点赞视图
// *
// * treay
// *
// */
//@Data
//public class ArticleFavourVO implements Serializable {
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
//     * @param articleFavourVO
//     * @return
//     */
//    public static ArticleFavour voToObj(ArticleFavourVO articleFavourVO) {
//        if (articleFavourVO == null) {
//            return null;
//        }
//        ArticleFavour articleFavour = new ArticleFavour();
//        BeanUtils.copyProperties(articleFavourVO, articleFavour);
//        List<String> tagList = articleFavourVO.getTagList();
//        articleFavour.setTags(JSONUtil.toJsonStr(tagList));
//        return articleFavour;
//    }
//
//    /**
//     * 对象转封装类
//     *
//     * @param articleFavour
//     * @return
//     */
//    public static ArticleFavourVO objToVo(ArticleFavour articleFavour) {
//        if (articleFavour == null) {
//            return null;
//        }
//        ArticleFavourVO articleFavourVO = new ArticleFavourVO();
//        BeanUtils.copyProperties(articleFavour, articleFavourVO);
//        articleFavourVO.setTagList(JSONUtil.toList(articleFavour.getTags(), String.class));
//        return articleFavourVO;
//    }
//}
