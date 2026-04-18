package com.fast.framework.helper;

import cn.hutool.extra.spring.SpringUtil;
import com.fast.framework.properties.MinioProperties;
import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * MinIO辅助类
 * 提供便捷的MinIO操作静态方法
 *
 * @author fast-frame
 */
@Slf4j
public class MinioHelper {

    private static MinioClient getMinioClient() {
        return SpringUtil.getBean(MinioClient.class);
    }

    private static MinioProperties getMinioProperties() {
        return SpringUtil.getBean(MinioProperties.class);
    }

    private static String getBucketName(String bucketType) {
        return getMinioProperties().getBucketName(bucketType);
    }

    /**
     * 上传文件
     *
     * @param bucketType 桶类型（avatar/file）
     * @param objectName 对象名称
     * @param inputStream 文件流
     * @param size 文件大小
     * @param contentType 内容类型
     */
    public static void upload(String bucketType, String objectName,
            InputStream inputStream, long size, String contentType) {
        try {
            getMinioClient().putObject(PutObjectArgs.builder()
                    .bucket(getBucketName(bucketType))
                    .object(objectName)
                    .stream(inputStream, size, -1)
                    .contentType(contentType)
                    .build());
        } catch (Exception e) {
            log.error("上传文件失败: {}", e.getMessage());
            throw new RuntimeException("上传文件失败: " + e.getMessage());
        }
    }

    /**
     * 上传 MultipartFile
     *
     * @param bucketType 桶类型
     * @param objectName 对象名称
     * @param file 上传的文件
     */
    public static void upload(String bucketType, String objectName, MultipartFile file) {
        try {
            getMinioClient().putObject(PutObjectArgs.builder()
                    .bucket(getBucketName(bucketType))
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (Exception e) {
            log.error("上传文件失败: {}", e.getMessage());
            throw new RuntimeException("上传文件失败: " + e.getMessage());
        }
    }

    /**
     * 下载文件
     *
     * @param bucketType 桶类型
     * @param objectName 对象名称
     * @return 文件流
     */
    public static InputStream download(String bucketType, String objectName) {
        try {
            return getMinioClient().getObject(GetObjectArgs.builder()
                    .bucket(getBucketName(bucketType))
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            log.error("下载文件失败: {}", e.getMessage());
            throw new RuntimeException("下载文件失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     *
     * @param bucketType 桶类型
     * @param objectName 对象名称
     */
    public static void delete(String bucketType, String objectName) {
        try {
            getMinioClient().removeObject(RemoveObjectArgs.builder()
                    .bucket(getBucketName(bucketType))
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            log.error("删除文件失败: {}", e.getMessage());
            throw new RuntimeException("删除文件失败: " + e.getMessage());
        }
    }

    /**
     * 获取临时访问URL
     *
     * @param bucketType 桶类型
     * @param objectName 对象名称
     * @param expires 过期时间（秒）
     * @return 访问URL
     */
    public static String getUrl(String bucketType, String objectName, int expires) {
        try {
            return getMinioClient().getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(getBucketName(bucketType))
                    .object(objectName)
                    .expiry(expires, TimeUnit.SECONDS)
                    .build());
        } catch (Exception e) {
            log.error("获取URL失败: {}", e.getMessage());
            throw new RuntimeException("获取URL失败: " + e.getMessage());
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param bucketType 桶类型
     * @param objectName 对象名称
     * @return 是否存在
     */
    public static boolean exists(String bucketType, String objectName) {
        try {
            getMinioClient().statObject(StatObjectArgs.builder()
                    .bucket(getBucketName(bucketType))
                    .object(objectName)
                    .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断桶是否存在
     *
     * @param bucketType 桶类型
     * @return 是否存在
     */
    public static boolean bucketExists(String bucketType) {
        try {
            return getMinioClient().bucketExists(BucketExistsArgs.builder()
                    .bucket(getBucketName(bucketType))
                    .build());
        } catch (Exception e) {
            log.error("判断桶是否存在失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 创建桶
     *
     * @param bucketType 桶类型
     */
    public static void createBucket(String bucketType) {
        try {
            String bucketName = getBucketName(bucketType);
            if (!bucketExists(bucketType)) {
                getMinioClient().makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                log.info("创建桶: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("创建桶失败: {}", e.getMessage());
            throw new RuntimeException("创建桶失败: " + e.getMessage());
        }
    }
}