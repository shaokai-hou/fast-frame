package com.fast.common.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.fast.common.exception.BusinessException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Excel 导入导出工具类
 * 基于 Alibaba EasyExcel 实现，支持注解驱动
 *
 * @author fast-frame
 */
public class ExcelUtil {

    /**
     * 性别字典
     */
    private static final Map<String, String> GENDER_DICT = new HashMap<>();

    /**
     * 状态字典
     */
    private static final Map<String, String> STATUS_DICT = new HashMap<>();

    static {
        GENDER_DICT.put("0", "未知");
        GENDER_DICT.put("1", "男");
        GENDER_DICT.put("2", "女");

        STATUS_DICT.put("0", "正常");
        STATUS_DICT.put("1", "禁用");
    }

    /**
     * 导出 Excel 到 HttpServletResponse
     *
     * @param data     数据列表
     * @param clazz    数据类型
     * @param filename 文件名（不含扩展名）
     * @param response HTTP 响应
     */
    public static <T> void exportExcel(List<T> data, Class<T> clazz, String filename, HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String encodedFilename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFilename + ".xlsx");

            // 获取带注解的字段列表
            List<Field> exportFields = getExportFields(clazz);

            // 创建动态列头
            List<List<String>> heads = exportFields.stream()
                    .map(f -> {
                        ExcelColumn annotation = f.getAnnotation(ExcelColumn.class);
                        return Arrays.asList(annotation.name());
                    })
                    .collect(Collectors.toList());

            // 转换数据为 List<List<Object>>
            List<List<Object>> dataList = convertData(data, exportFields);

            // 写入 Excel
            EasyExcel.write(response.getOutputStream())
                    .head(heads)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet(filename)
                    .doWrite(dataList);
        } catch (IOException e) {
            throw new BusinessException("导出 Excel 失败: " + e.getMessage());
        }
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
        List<String> errorMessages = new ArrayList<>();

        // 获取列名到字段的映射
        Map<String, Field> columnFieldMap = getImportFieldMap(clazz);

        EasyExcel.read(inputStream, new AnalysisEventListener<Map<Integer, String>>() {
            private List<String> headers = new ArrayList<>();

            @Override
            public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
                for (int i = 0; i < headMap.size(); i++) {
                    headers.add(headMap.getOrDefault(i, ""));
                }
            }

            @Override
            public void invoke(Map<Integer, String> data, AnalysisContext context) {
                try {
                    T obj = clazz.getDeclaredConstructor().newInstance();
                    for (int i = 0; i < headers.size(); i++) {
                        String columnName = headers.get(i);
                        String value = data.getOrDefault(i, "");
                        Field field = columnFieldMap.get(columnName);
                        if (field != null && StrUtil.isNotBlank(value)) {
                            field.setAccessible(true);
                            setFieldValue(obj, field, value);
                        }
                    }
                    result.add(obj);
                } catch (Exception e) {
                    errorMessages.add("第 " + (context.readRowHolder().getRowIndex() + 1) + " 行解析失败: " + e.getMessage());
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                // 解析完成
            }
        }).sheet().doRead();

        if (!errorMessages.isEmpty()) {
            throw new BusinessException("导入失败:\n" + String.join("\n", errorMessages));
        }

        return result;
    }

    /**
     * 导入 Excel 文件（带错误收集）
     *
     * @param inputStream 输入流
     * @param clazz       目标类型
     * @return 导入结果（成功列表 + 错误信息）
     */
    public static <T> ImportResult<T> importExcelWithErrors(InputStream inputStream, Class<T> clazz) {
        if (inputStream == null) {
            throw new BusinessException("导入文件不能为空");
        }

        List<T> successList = new ArrayList<>();
        List<ImportError> errorList = new ArrayList<>();

        Map<String, Field> columnFieldMap = getImportFieldMap(clazz);

        EasyExcel.read(inputStream, new AnalysisEventListener<Map<Integer, String>>() {
            private List<String> headers = new ArrayList<>();

            @Override
            public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
                for (int i = 0; i < headMap.size(); i++) {
                    headers.add(headMap.getOrDefault(i, ""));
                }
            }

            @Override
            public void invoke(Map<Integer, String> data, AnalysisContext context) {
                try {
                    T obj = clazz.getDeclaredConstructor().newInstance();
                    for (int i = 0; i < headers.size(); i++) {
                        String columnName = headers.get(i);
                        String value = data.getOrDefault(i, "");
                        Field field = columnFieldMap.get(columnName);
                        if (field != null && StrUtil.isNotBlank(value)) {
                            field.setAccessible(true);
                            setFieldValue(obj, field, value);
                        }
                    }
                    successList.add(obj);
                } catch (Exception e) {
                    errorList.add(new ImportError(context.readRowHolder().getRowIndex() + 1, e.getMessage()));
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                // 解析完成
            }
        }).sheet().doRead();

        return new ImportResult<>(successList, errorList);
    }

    /**
     * 下载导入模板
     *
     * @param clazz    数据类型
     * @param filename 文件名
     * @param response HTTP 响应
     */
    public static <T> void downloadTemplate(Class<T> clazz, String filename, HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String encodedFilename = URLEncoder.encode(filename + "_模板", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFilename + ".xlsx");

            // 获取带注解的字段列表
            List<Field> importFields = getImportFields(clazz);

            // 创建动态列头
            List<List<String>> heads = importFields.stream()
                    .map(f -> {
                        ExcelColumn annotation = f.getAnnotation(ExcelColumn.class);
                        return Arrays.asList(annotation.name());
                    })
                    .collect(Collectors.toList());

            // 写入空 Excel（只有表头）
            EasyExcel.write(response.getOutputStream())
                    .head(heads)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("模板")
                    .doWrite(new ArrayList<>());
        } catch (IOException e) {
            throw new BusinessException("下载模板失败: " + e.getMessage());
        }
    }

    /**
     * 获取导出字段列表（按 sort 排序）
     *
     * @param clazz 类型
     * @return 导出字段列表
     */
    private static <T> List<Field> getExportFields(Class<T> clazz) {
        List<Field> fields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
            if (annotation != null && annotation.export()) {
                fields.add(field);
            }
        }
        fields.sort(Comparator.comparingInt(f -> f.getAnnotation(ExcelColumn.class).sort()));
        return fields;
    }

    /**
     * 获取导入字段列表（按 sort 排序）
     *
     * @param clazz 类型
     * @return 导入字段列表
     */
    private static <T> List<Field> getImportFields(Class<T> clazz) {
        List<Field> fields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
            if (annotation != null && annotation.importable()) {
                fields.add(field);
            }
        }
        fields.sort(Comparator.comparingInt(f -> f.getAnnotation(ExcelColumn.class).sort()));
        return fields;
    }

    /**
     * 获取导入字段映射（列名 -> 字段）
     *
     * @param clazz 类型
     * @return 列名到字段的映射
     */
    private static <T> Map<String, Field> getImportFieldMap(Class<T> clazz) {
        Map<String, Field> map = new HashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
            if (annotation != null && annotation.importable()) {
                map.put(annotation.name(), field);
            }
        }
        return map;
    }

    /**
     * 转换数据为 EasyExcel 写入格式
     *
     * @param data  原始数据
     * @param fields 导出字段列表
     * @return 转换后的数据
     */
    private static <T> List<List<Object>> convertData(List<T> data, List<Field> fields) {
        List<List<Object>> result = new ArrayList<>();
        for (T item : data) {
            List<Object> row = new ArrayList<>();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(item);
                    ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
                    row.add(formatValue(value, annotation.format()));
                } catch (IllegalAccessException e) {
                    row.add("");
                }
            }
            result.add(row);
        }
        return result;
    }

    /**
     * 格式化值
     *
     * @param value  原始值
     * @param format 格式化规则
     * @return 格式化后的值
     */
    private static Object formatValue(Object value, String format) {
        if (value == null) {
            return "";
        }

        if (StrUtil.isBlank(format)) {
            return value;
        }

        // 字典转换
        if (format.startsWith("dict:")) {
            String dictType = format.substring(5);
            String key = value.toString();
            return switchDict(dictType, key);
        }

        // 日期格式化
        if (value instanceof LocalDateTime) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return ((LocalDateTime) value).format(formatter);
        }

        return value;
    }

    /**
     * 字典转换
     *
     * @param dictType 字典类型
     * @param key      键
     * @return 值
     */
    private static String switchDict(String dictType, String key) {
        switch (dictType) {
            case "gender":
                return GENDER_DICT.getOrDefault(key, key);
            case "status":
                return STATUS_DICT.getOrDefault(key, key);
            default:
                return key;
        }
    }

    /**
     * 设置字段值（导入时使用）
     *
     * @param obj   对象
     * @param field 字段
     * @param value 值
     */
    private static <T> void setFieldValue(T obj, Field field, String value) throws IllegalAccessException {
        Class<?> type = field.getType();

        if (type == String.class) {
            field.set(obj, value);
        } else if (type == Long.class || type == long.class) {
            field.set(obj, Long.parseLong(value));
        } else if (type == Integer.class || type == int.class) {
            field.set(obj, Integer.parseInt(value));
        } else if (type == Double.class || type == double.class) {
            field.set(obj, Double.parseDouble(value));
        } else if (type == Boolean.class || type == boolean.class) {
            field.set(obj, Boolean.parseBoolean(value));
        } else if (type == LocalDateTime.class) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            field.set(obj, LocalDateTime.parse(value, formatter));
        } else {
            field.set(obj, value);
        }
    }

    /**
     * 导入结果
     */
    public static class ImportResult<T> {
        private final List<T> successList;
        private final List<ImportError> errorList;

        public ImportResult(List<T> successList, List<ImportError> errorList) {
            this.successList = successList;
            this.errorList = errorList;
        }

        public List<T> getSuccessList() {
            return successList;
        }

        public List<ImportError> getErrorList() {
            return errorList;
        }

        public int getSuccessCount() {
            return successList.size();
        }

        public int getErrorCount() {
            return errorList.size();
        }
    }

    /**
     * 导入错误
     */
    public static class ImportError {
        private final int row;
        private final String message;

        public ImportError(int row, String message) {
            this.row = row;
            this.message = message;
        }

        public int getRow() {
            return row;
        }

        public String getMessage() {
            return message;
        }
    }
}