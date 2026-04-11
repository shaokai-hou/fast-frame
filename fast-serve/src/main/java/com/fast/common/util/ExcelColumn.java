package com.fast.common.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Excel 列注解
 * 用于标注导出/导入字段的列名、顺序、格式等
 *
 * @author fast-frame
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {

    /**
     * 列名
     *
     * @return 列名
     */
    String name();

    /**
     * 排序（数字越小越靠前）
     *
     * @return 排序值
     */
    int sort() default 0;

    /**
     * 格式化
     * 支持以下格式：
     * - 日期格式：yyyy-MM-dd HH:mm:ss
     * - 字典转换：dict:gender（性别 0/1/2 → 未知/男/女）
     * - 字典转换：dict:status（状态 0/1 → 正常/禁用）
     *
     * @return 格式化规则
     */
    String format() default "";

    /**
     * 是否导出
     *
     * @return 是否导出
     */
    boolean export() default true;

    /**
     * 是否导入
     *
     * @return 是否导入
     */
    boolean importable() default true;
}