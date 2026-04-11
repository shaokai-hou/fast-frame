package com.fast.modules.file.service;

import com.fast.common.result.PageResult;
import com.fast.modules.file.domain.dto.FileQuery;
import com.fast.modules.file.domain.vo.FileVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 文件Service
 *
 * @author fast-frame
 */
public interface FileService {


    /**
     * 分页查询文件列表
     *
     * @param query 查询参数
     * @return 文件分页结果
     */
    PageResult<FileVO> pageFiles(FileQuery query);


    /**
     * 上传文件
     * <p>
     * 默认上传到 file 桶，按日期分目录存储
     *
     * @param file 文件
     * @return 文件信息
     */
    FileVO uploadFile(MultipartFile file);

    /**
     * 批量上传文件
     * <p>
     * 默认上传到 file 桶
     *
     * @param files 文件数组
     * @return 文件信息列表
     */
    List<FileVO> uploadFiles(MultipartFile[] files);

    /**
     * 上传文件到指定桶
     *
     * @param file       文件
     * @param bucketType 桶类型（avatar/file）
     * @return 文件信息
     */
    FileVO uploadToBucket(MultipartFile file, String bucketType);

    /**
     * 批量上传文件到指定桶
     *
     * @param files      文件数组
     * @param bucketType 桶类型（avatar/file）
     * @return 文件信息列表
     */
    List<FileVO> uploadFilesToBucket(MultipartFile[] files, String bucketType);


    /**
     * 根据ID删除文件
     * <p>
     * 同步删除 MinIO 文件和数据库记录
     *
     * @param id 文件ID
     */
    void deleteFileById(Long id);

    /**
     * 删除文件
     *
     * @param bucketType 桶类型（avatar/file）
     * @param objectName 对象名称
     */
    void deleteFromBucket(String bucketType, String objectName);


    /**
     * 初始化桶
     * <p>
     * 应用启动时自动调用，创建 avatar 和 file 桶
     */
    void initBuckets();

    /**
     * 流式输出文件
     * <p>
     * 默认从 file 桶读取
     *
     * @param objectName 对象名称（yyyy/MM/dd/uuid.ext）
     * @param response   HTTP 响应
     * @param asDownload 是否作为下载（true: 下载模式，false: 预览模式）
     */
    void streamFile(String objectName, HttpServletResponse response, boolean asDownload);

    /**
     * 流式输出文件
     *
     * @param bucketType 桶类型（avatar/file）
     * @param objectName 对象名称（yyyy/MM/dd/uuid.ext）
     * @param response   HTTP 响应
     * @param asDownload 是否作为下载
     */
    void streamFromBucket(String bucketType, String objectName, HttpServletResponse response, boolean asDownload);
}