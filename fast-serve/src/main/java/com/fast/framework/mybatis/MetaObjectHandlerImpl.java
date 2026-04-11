package com.fast.framework.mybatis;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis Plus 字段自动填充处理器
 *
 * @author fast-frame
 */
@Slf4j
@Component
public class MetaObjectHandlerImpl implements MetaObjectHandler {

    /**
     * 插入时自动填充
     *
     * @param metaObject 元数据对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        try {
            // 创建时间
            this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
            // 修改时间
            this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            // 创建人ID
            this.strictInsertFill(metaObject, "createBy", Long.class, StpUtil.getLoginIdAsLong());
            // 修改人ID
            this.strictInsertFill(metaObject, "updateBy", Long.class, StpUtil.getLoginIdAsLong());
        } catch (Exception e) {
            log.error("自动填充异常: {}", e.getMessage());
        }
    }

    /**
     * 更新时自动填充
     *
     * @param metaObject 元数据对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            // 修改时间
            this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            // 修改人ID
            this.strictUpdateFill(metaObject, "updateBy", Long.class, StpUtil.getLoginIdAsLong());
        } catch (Exception e) {
            log.error("自动填充异常: {}", e.getMessage());
        }
    }

}