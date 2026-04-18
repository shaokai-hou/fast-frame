package com.fast.modules.monitor.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 服务器信息VO
 *
 * @author fast-frame
 */
@Data
public class ServerVO {

    /**
     * CPU信息
     */
    private CpuVO cpu;

    /**
     * 内存信息
     */
    private MemVO mem;

    /**
     * JVM信息
     */
    private JvmVO jvm;

    /**
     * 系统信息
     */
    private SysVO sys;

    /**
     * 磁盘信息
     */
    private List<SysFileVO> sysFiles;
}