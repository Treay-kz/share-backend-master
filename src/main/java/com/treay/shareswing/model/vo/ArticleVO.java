package com.treay.shareswing.model.vo;

import cn.hutool.json.JSONUtil;
import com.treay.shareswing.model.entity.Article;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 文章视图
 *
 * treay
 * 
 */
@Data
public class ArticleVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 标签列表
     */
    private List<String> tagList;

    /**
     * 创建用户信息
     */
    private UserVO user;

    /**
     * 封装类转对象
     *
     * @param articleVO
     * @return
     */
    public static Article voToObj(ArticleVO articleVO) {
        if (articleVO == null) {
            return null;
        }
        Article article = new Article();
        BeanUtils.copyProperties(articleVO, article);
        List<String> tagList = articleVO.getTagList();
        article.setTags(JSONUtil.toJsonStr(tagList));
        return article;
    }

    /**
     * 对象转封装类
     *
     * @param article
     * @return
     */
    public static ArticleVO objToVo(Article article) {
        if (article == null) {
            return null;
        }
        ArticleVO articleVO = new ArticleVO();
        BeanUtils.copyProperties(article, articleVO);
        articleVO.setTagList(JSONUtil.toList(article.getTags(), String.class));
        return articleVO;
    }
}
