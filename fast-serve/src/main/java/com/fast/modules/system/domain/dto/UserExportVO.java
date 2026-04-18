package com.fast.modules.system.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户导出 VO
 * 用于 Excel 导出用户数据
 * 注：字典转换（性别、状态）在业务层处理，此 VO 直接存储转换后的值
 *
 * @author fast-frame
 */
@Data
public class UserExportVO {

    /**
     * 用户名
     */
    @ExcelProperty("用户名")
    private String username;

    /**
     * 昵称
     */
    @ExcelProperty("昵称")
    private String nickname;

    /**
     * 部门名称
     */
    @ExcelProperty("部门")
    private String deptName;

    /**
     * 手机号
     */
    @ExcelProperty("手机号")
    private String phone;

    /**
     * 邮箱
     */
    @ExcelProperty("邮箱")
    private String email;

    /**
     * 性别（已转换为中文：未知/男/女）
     */
    @ExcelProperty("性别")
    private String gender;

    /**
     * 状态（已转换为中文：正常/禁用）
     */
    @ExcelProperty("状态")
    private String status;

    /**
     * 创建时间
     */
    @ExcelProperty("创建时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}