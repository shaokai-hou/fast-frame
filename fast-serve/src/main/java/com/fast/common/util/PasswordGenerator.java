package com.fast.common.util;

import cn.dev33.satoken.secure.BCrypt;

/**
 * BCrypt 密码哈希生成工具
 * 用于生成数据库初始化脚本的密码
 *
 * @author fast-frame
 */
public class PasswordGenerator {

    /**
     * 生成 BCrypt 密码哈希
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        String adminPassword = "admin123";
        String userPassword = "123456";

        String adminHash = BCrypt.hashpw(adminPassword, BCrypt.gensalt());
        String userHash = BCrypt.hashpw(userPassword, BCrypt.gensalt());

        System.out.println("======================================");
        System.out.println("BCrypt 密码哈希值（用于 schema.sql）");
        System.out.println("======================================");
        System.out.println();
        System.out.println("admin 密码 (admin123):");
        System.out.println(adminHash);
        System.out.println();
        System.out.println("普通用户密码 (123456):");
        System.out.println(userHash);
        System.out.println();
        System.out.println("======================================");
    }
}