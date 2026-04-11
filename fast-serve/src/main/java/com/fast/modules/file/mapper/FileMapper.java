package com.fast.modules.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fast.modules.file.entity.File;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件Mapper
 *
 * @author fast-frame
 */
@Mapper
public interface FileMapper extends BaseMapper<File> {

}