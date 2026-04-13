package com.fast.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fast.modules.system.domain.entity.File;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件Mapper
 *
 * @author fast-frame
 */
@Mapper
public interface FileMapper extends BaseMapper<File> {
}