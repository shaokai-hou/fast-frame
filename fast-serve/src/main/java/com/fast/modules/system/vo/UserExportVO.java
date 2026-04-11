package com.fast.modules.system.vo;

import com.fast.common.util.ExcelColumn;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户导出 VO
 * 用于 Excel 导出用户数据
 *
 * @author fast-frame
 */
@Data
public class UserExportVO {

    /**
     * 用户名
     */
    @ExcelColumn(name = "用户名", sort = 1)
    private String username;

    /**
     * 昵称
     */
    @ExcelColumn(name = "昵称", sort = 2)
    private String nickname;

    /**
     * 部门名称
     */
    @ExcelColumn(name = "部门", sort = 3)
    private String deptName;

    /**
     * 手机号
     */
    @ExcelColumn(name = "手机号", sort = 4)
    private String phone;

    /**
     * 邮箱
     */
    @ExcelColumn(name = "邮箱", sort = 5)
    private String email;

    /**
     * 性别
     */
    @ExcelColumn(name = "性别", sort = 6, format = "dict:gender")
    private String gender;

    /**
     * 状态
     */
    @ExcelColumn(name = "状态", sort = 7, format = "dict:status")
    private String status;

    /**
     * 创建时间
     */
    @ExcelColumn(name = "创建时间", sort = 8, format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}