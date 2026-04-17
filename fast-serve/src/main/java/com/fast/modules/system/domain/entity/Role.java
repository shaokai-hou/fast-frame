package com.fast.modules.system.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fast.framework.web.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体
 *
 * @author fast-frame
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class Role extends BaseEntity {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色权限字符串
     */
    private String roleKey;

    /**
     * 显示顺序
     */
    private Integer roleSort;

    /**
     * 数据范围(1全部 2自定义 3本部门 4本部门及以下 5仅本人)
     */
    private String dataScope;

    /**
     * 状态(0正常 1禁用)
     */
    private String status;

    /**
     * 删除标志(0正常 1删除)
     */
    @TableLogic
    private String delFlag;

    /**
     * 备注
     */
    private String remark;
}