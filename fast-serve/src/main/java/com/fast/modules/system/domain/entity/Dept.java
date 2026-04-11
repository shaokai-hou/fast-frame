package com.fast.modules.system.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fast.framework.web.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门实体
 *
 * @author fast-frame
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class Dept extends BaseEntity {

    /**
     * 部门ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 祖级列表
     */
    private String ancestors;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 部门标识
     */
    private String deptKey;

    /**
     * 负责人
     */
    private String leader;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 状态(0正常 1禁用)
     */
    private String status;

    /**
     * 删除标志(0正常 1删除)
     */
    @TableLogic
    private String delFlag;
}