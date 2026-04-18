package com.fast.framework.helper;

import cn.hutool.extra.spring.SpringUtil;
import com.fast.modules.system.domain.dto.DictDataVO;
import com.fast.modules.system.service.DictService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字典辅助类
 * 提供便捷的字典操作静态方法
 *
 * @author fast-frame
 */
public class DictHelper {

    private static DictService getDictService() {
        return SpringUtil.getBean(DictService.class);
    }

    /**
     * 根据字典类型和字典值获取字典标签
     *
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @return 字典标签，找不到返回空字符串
     */
    public static String getLabel(String dictType, String dictValue) {
        return getLabel(dictType, dictValue, "");
    }

    /**
     * 根据字典类型和字典值获取字典标签（带默认值）
     *
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @param defaultValue 默认值
     * @return 字典标签
     */
    public static String getLabel(String dictType, String dictValue, String defaultValue) {
        List<DictDataVO> list = list(dictType);
        return list.stream()
            .filter(d -> d.getDictValue().equals(dictValue))
            .findFirst()
            .map(DictDataVO::getDictLabel)
            .orElse(defaultValue);
    }

    /**
     * 根据字典类型和字典标签获取字典值
     *
     * @param dictType 字典类型
     * @param dictLabel 字典标签
     * @return 字典值，找不到返回空字符串
     */
    public static String getValue(String dictType, String dictLabel) {
        return getValue(dictType, dictLabel, "");
    }

    /**
     * 根据字典类型和字典标签获取字典值（带默认值）
     *
     * @param dictType 字典类型
     * @param dictLabel 字典标签
     * @param defaultValue 默认值
     * @return 字典值
     */
    public static String getValue(String dictType, String dictLabel, String defaultValue) {
        List<DictDataVO> list = list(dictType);
        return list.stream()
            .filter(d -> d.getDictLabel().equals(dictLabel))
            .findFirst()
            .map(DictDataVO::getDictValue)
            .orElse(defaultValue);
    }

    /**
     * 获取字典数据列表
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    public static List<DictDataVO> list(String dictType) {
        return getDictService().listDictDataByType(dictType);
    }

    /**
     * 批量获取字典标签
     *
     * @param dictType 字典类型
     * @param dictValues 字典值列表
     * @return 字典值-标签映射
     */
    public static Map<String, String> getLabelMap(String dictType, List<String> dictValues) {
        List<DictDataVO> list = list(dictType);
        return list.stream()
            .filter(d -> dictValues.contains(d.getDictValue()))
            .collect(Collectors.toMap(DictDataVO::getDictValue, DictDataVO::getDictLabel, (v1, v2) -> v1));
    }

    /**
     * 批量获取字典值
     *
     * @param dictType 字典类型
     * @param dictLabels 字典标签列表
     * @return 字典标签-值映射
     */
    public static Map<String, String> getValueMap(String dictType, List<String> dictLabels) {
        List<DictDataVO> list = list(dictType);
        return list.stream()
            .filter(d -> dictLabels.contains(d.getDictLabel()))
            .collect(Collectors.toMap(DictDataVO::getDictLabel, DictDataVO::getDictValue, (v1, v2) -> v1));
    }

    /**
     * 判断字典值是否有效
     *
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @return 是否有效
     */
    public static boolean isValid(String dictType, String dictValue) {
        List<DictDataVO> list = list(dictType);
        return list.stream().anyMatch(d -> d.getDictValue().equals(dictValue));
    }
}