package com.fast.modules.monitor.service.impl;

import com.fast.modules.monitor.domain.vo.CpuVO;
import com.fast.modules.monitor.domain.vo.JvmVO;
import com.fast.modules.monitor.domain.vo.MemVO;
import com.fast.modules.monitor.domain.vo.ServerVO;
import com.fast.modules.monitor.domain.vo.SysVO;
import com.fast.modules.monitor.domain.vo.SysFileVO;
import com.fast.modules.monitor.service.ServerService;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * 服务监控Service实现
 *
 * @author fast-frame
 */
@Service
public class ServerServiceImpl implements ServerService {

    @Override
    public ServerVO getServerInfo() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        OperatingSystem os = systemInfo.getOperatingSystem();

        ServerVO server = new ServerVO();

        // CPU信息
        server.setCpu(getCpuInfo(hardware));

        // 内存信息
        server.setMem(getMemInfo(hardware));

        // JVM信息
        server.setJvm(getJvmInfo());

        // 系统信息
        server.setSys(getSysInfo(os));

        // 磁盘信息
        server.setSysFiles(getSysFiles(os));

        return server;
    }

    /**
     * 获取CPU信息
     */
    private CpuVO getCpuInfo(HardwareAbstractionLayer hardware) {
        CentralProcessor processor = hardware.getProcessor();

        CpuVO cpu = new CpuVO();
        cpu.setCore(processor.getLogicalProcessorCount());

        // CPU使用率需要通过对比两次采样来计算
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        Util.sleep(1000);
        double usage = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;

        cpu.setUsed(Math.round(usage * 100) / 100.0);
        cpu.setIdle(Math.round((100 - usage) * 100) / 100.0);

        return cpu;
    }

    /**
     * 获取内存信息
     */
    private MemVO getMemInfo(HardwareAbstractionLayer hardware) {
        GlobalMemory memory = hardware.getMemory();

        MemVO mem = new MemVO();
        long total = memory.getTotal();
        long available = memory.getAvailable();

        mem.setTotal(Math.round(total / (1024.0 * 1024.0 * 1024.0) * 100) / 100.0);
        mem.setFree(Math.round(available / (1024.0 * 1024.0 * 1024.0) * 100) / 100.0);
        mem.setUsed(Math.round((total - available) / (1024.0 * 1024.0 * 1024.0) * 100) / 100.0);
        mem.setUsage(Math.round((double) (total - available) / total * 100 * 100) / 100.0);

        return mem;
    }

    /**
     * 获取JVM信息
     */
    private JvmVO getJvmInfo() {
        Properties props = System.getProperties();
        Runtime runtime = Runtime.getRuntime();

        JvmVO jvm = new JvmVO();
        long total = runtime.totalMemory();
        long free = runtime.freeMemory();
        long max = runtime.maxMemory();

        jvm.setTotal(Math.round(total / (1024.0 * 1024.0) * 100) / 100.0);
        jvm.setFree(Math.round(free / (1024.0 * 1024.0) * 100) / 100.0);
        jvm.setUsed(Math.round((total - free) / (1024.0 * 1024.0) * 100) / 100.0);
        jvm.setMax(Math.round(max / (1024.0 * 1024.0) * 100) / 100.0);
        jvm.setUsage(Math.round((double) (total - free) / max * 100 * 100) / 100.0);

        jvm.setVersion(props.getProperty("java.version"));
        jvm.setHome(props.getProperty("java.home"));
        jvm.setName(props.getProperty("java.vm.name"));

        // JVM启动时间
        long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jvm.setStartTime(sdf.format(new Date(startTime)));
        jvm.setRunTime(System.currentTimeMillis() - startTime);

        return jvm;
    }

    /**
     * 获取系统信息
     */
    private SysVO getSysInfo(OperatingSystem os) {
        SysVO sys = new SysVO();

        sys.setOsName(os.toString());
        sys.setOsArch(System.getProperty("os.arch"));
        sys.setOsVersion(System.getProperty("os.version"));
        sys.setUserName(System.getProperty("user.name"));

        try {
            InetAddress inet = InetAddress.getLocalHost();
            sys.setHostName(inet.getHostName());
            sys.setIp(inet.getHostAddress());
        } catch (UnknownHostException e) {
            sys.setHostName("unknown");
            sys.setIp("unknown");
        }

        return sys;
    }

    /**
     * 获取磁盘信息
     */
    private List<SysFileVO> getSysFiles(OperatingSystem os) {
        List<SysFileVO> sysFiles = new ArrayList<>();

        FileSystem fileSystem = os.getFileSystem();
        List<OSFileStore> fileStores = fileSystem.getFileStores();

        for (OSFileStore store : fileStores) {
            SysFileVO sysFile = new SysFileVO();
            long total = store.getTotalSpace();
            long free = store.getUsableSpace();
            long used = total - free;

            sysFile.setDirName(store.getName());
            sysFile.setSysTypeName(store.getType());
            sysFile.setTotal(Math.round(total / (1024.0 * 1024.0 * 1024.0) * 100) / 100.0);
            sysFile.setFree(Math.round(free / (1024.0 * 1024.0 * 1024.0) * 100) / 100.0);
            sysFile.setUsed(Math.round(used / (1024.0 * 1024.0 * 1024.0) * 100) / 100.0);
            sysFile.setUsage(Math.round((double) used / total * 100 * 100) / 100.0);

            sysFiles.add(sysFile);
        }

        return sysFiles;
    }
}