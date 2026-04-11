package com.fast.framework.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * MinIO配置属性
 *
 * @author fast-frame
 */
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    /**
     * 服务地址
     */
    private String endpoint;

    /**
     * 访问密钥
     */
    private String accessKey;

    /**
     * 私有密钥
     */
    private String secretKey;

    /**
     * 桶配置
     */
    private Buckets buckets;

    /**
     * 桶类型常量
     */
    public static final String BUCKET_TYPE_AVATAR = "avatar";
    public static final String BUCKET_TYPE_FILE = "file";

    @Data
    public static class Buckets {
        /**
         * 头像桶名称
         */
        private String avatar;

        /**
         * 文件桶名称
         */
        private String file;
    }

    /**
     * 获取头像桶名称
     */
    public String getAvatarBucket() {
        return buckets.getAvatar();
    }

    /**
     * 获取文件桶名称
     */
    public String getFileBucket() {
        return buckets.getFile();
    }

    /**
     * 根据桶类型获取实际桶名称
     *
     * @param bucketType 桶类型（avatar/file）
     * @return 实际桶名称
     */
    public String getBucketName(String bucketType) {
        if (BUCKET_TYPE_AVATAR.equals(bucketType)) {
            return buckets.getAvatar();
        } else if (BUCKET_TYPE_FILE.equals(bucketType)) {
            return buckets.getFile();
        }
        throw new IllegalArgumentException("无效的桶类型: " + bucketType);
    }
}