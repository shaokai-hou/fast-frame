package com.fast.framework.annotation;

import java.lang.annotation.*;

/**
 * 权限注解
 *
 * @author fast-frame
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermission {

    /**
     * 权限标识
     */
    String value();

    /**
     * 逻辑关系：AND | OR
     */
    Logical logical() default Logical.AND;

    enum Logical {
        AND, OR
    }
}