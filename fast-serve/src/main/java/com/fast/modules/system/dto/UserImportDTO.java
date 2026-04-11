package com.fast.modules.system.dto;

import com.fast.common.util.ExcelColumn;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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
    @ExcelColumn(name = "用户名", sort = 1)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度必须在2-20个字符之间")
    private String username;

    /**
     * 昵称
     */
    @ExcelColumn(name = "昵称", sort = 2)
    @Size(max = 30, message = "昵称长度不能超过30个字符")
    private String nickname;

    /**
     * 部门名称（导入时用于查找部门ID）
     */
    @ExcelColumn(name = "部门", sort = 3)
    private String deptName;

    /**
     * 手机号
     */
    @ExcelColumn(name = "手机号", sort = 4)
    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 邮箱
     */
    @ExcelColumn(name = "邮箱", sort = 5)
    @Pattern(regexp = "^$|^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "邮箱格式不正确")
    private String email;

    /**
     * 性别（导入时转换：未知/男/女 → 0/1/2）
     */
    @ExcelColumn(name = "性别", sort = 6)
    private String gender;

    /**
     * 状态（导入时转换：正常/禁用 → 0/1）
     */
    @ExcelColumn(name = "状态", sort = 7)
    private String status;

    /**
     * 密码（可选，不填则使用默认密码）
     */
    @ExcelColumn(name = "密码", sort = 8, export = false)
    @Size(min = 5, max = 20, message = "密码长度必须在5-20个字符之间")
    private String password;
}