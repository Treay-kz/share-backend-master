package com.treay.shareswing.config;

import com.treay.shareswing.utils.AliOssUtil;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @auther Treay_kz
 * @Date 2024/5/17 15:30
 */
@Configuration
@ConfigurationProperties(prefix = "alioss.client")
@Data
public class AliOssClientConfig {

    /**
     * endpoint
     */
    private String endpoint;
    /**
     * accessKeyId
     */
    private String accessKeyId;
    /**
     * accessKeySecret
     */
    private String accessKeySecret;
    /**
     * bucketName
     */
    private String bucketName;


    @Bean
    @ConditionalOnMissingBean//表示当没有这种Bean时创建
    public AliOssUtil aliOssUtil() {
        return new AliOssUtil(endpoint,
                accessKeyId,
                accessKeySecret,
                bucketName);
    }
}
