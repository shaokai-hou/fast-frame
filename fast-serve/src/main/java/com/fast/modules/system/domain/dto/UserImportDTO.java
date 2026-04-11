package com.fast.modules.system.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 用户导入 DTO
 * 用于 Excel 导入用户数据
 *
 * @author fast-frame
 */
@Data
public class UserImportDTO {

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
     * 部门名称（导入时用于查找部门ID）
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
     * 性别（导入时转换：未知/男/女 → 0/1/2）
     */
    @ExcelProperty("性别")
    private String gender;

    /**
     * 状态（导入时转换：正常/禁用 → 0/1）
     */
    @ExcelProperty("状态")
    private String status;

    /**
     * 密码（可选，不填则使用默认密码）
     */
    @ExcelProperty("密码")
    private String password;
}