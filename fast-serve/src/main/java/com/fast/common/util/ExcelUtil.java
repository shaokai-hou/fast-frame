package com.fast.common.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.fast.common.exception.BusinessException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel 导入导出工具类
 * 基于 Alibaba EasyExcel 实现
 *
 * @author fast-frame
 */
public class ExcelUtil {

    /**
     * 导出 Excel 到 HttpServletResponse
     *
     * @param data     数据列表
     * @param clazz    数据类型
     * @param filename 文件名（不含扩展名）
     * @param response HTTP 响应
     */
    public static <T> void exportExcel(List<T> data, Class<T> clazz, String filename, HttpServletResponse response) {
        setupExcelResponse(response, filename);
        try {
            EasyExcel.write(response.getOutputStream(), clazz)
                    .sheet(filename)
                    .doWrite(data);
        } catch (IOException e) {
            throw new BusinessException("导出 Excel 失败: " + e.getMessage());
        }
    }

    /**
     * 导出 Excel 模板（只有表头）
     *
     * @param clazz    数据类型
     * @param filename 文件名（不含扩展名）
     * @param response HTTP 响应
     */
    public static <T> void exportExcel(Class<T> clazz, String filename, HttpServletResponse response) {
        exportExcel(new ArrayList<>(), clazz, filename + "_模板", response);
    }

    /**
     * 导入 Excel 文件
     *
     * @param inputStream 输入流
     * @param clazz       目标类型
     * @return 解析后的数据列表
     */
    public static <T> List<T> importExcel(InputStream inputStream, Class<T> clazz) {
        if (inputStream == null) {
            throw new BusinessException("导入文件不能为空");
        }

        List<T> result = new ArrayList<>();
        EasyExcel.read(inputStream, clazz, new PageReadListener<T>(result::addAll)).sheet().doRead();
        return result;
    }

    /**
     * 设置 Excel 响应头
     *
     * @param response HTTP 响应
     * @param filename 文件名
     */
    private static void setupExcelResponse(HttpServletResponse response, String filename) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        try {
            String encodedFilename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFilename + ".xlsx");
        } catch (IOException e) {
            throw new BusinessException("设置响应头失败: " + e.getMessage());
        }
    }
}