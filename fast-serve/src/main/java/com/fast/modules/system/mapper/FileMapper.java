package com.fast.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.modules.system.domain.query.FileQuery;
import com.fast.modules.system.domain.vo.FileVO;
import com.fast.modules.system.domain.entity.File;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件Mapper
 *
 * @author fast-frame
 */
@Mapper
public interface FileMapper extends BaseMapper<File> {

    /**
     * 分页查询文件列表
     *
     * @param page  分页对象
     * @param query 查询参数
     * @return 文件分页结果
     */
    IPage<FileVO> selectFilePage(IPage<FileVO> page, FileQuery query);
}