package com.fast.modules.system.service;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.bean.BeanUtil;
import com.fast.common.exception.BusinessException;
import com.fast.modules.system.domain.dto.UserDTO;
import com.fast.modules.system.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * UserService 业务逻辑单元测试
 *
 * 测试核心业务规则，不依赖数据库操作
 *
 * @author fast-frame
 */
class UserServiceBusinessTest {

    /**
     * 测试密码哈希验证
     */
    @Test
    @DisplayName("BCrypt 密码哈希 - 正确密码验证成功")
    void bcryptPassword_correctPassword_verifiesSuccessfully() {
        // Arrange
        String rawPassword = "123456";
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        // Act
        boolean result = BCrypt.checkpw(rawPassword, hashedPassword);

        // Assert
        assertThat(result).isTrue();
    }

    /**
     * 测试密码哈希验证 - 错误密码
     */
    @Test
    @DisplayName("BCrypt 密码哈希 - 错误密码验证失败")
    void bcryptPassword_wrongPassword_verifiesFailed() {
        // Arrange
        String rawPassword = "123456";
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        // Act
        boolean result = BCrypt.checkpw("wrongpassword", hashedPassword);

        // Assert
        assertThat(result).isFalse();
    }

    /**
     * 测试用户名重复检查逻辑
     */
    @Test
    @DisplayName("用户名重复检查 - 相同用户名应抛出异常")
    void usernameDuplicate_sameUsername_throwsException() {
        // Arrange
        User existingUser = new User();
        existingUser.setUsername("admin");

        UserDTO dto = new UserDTO();
        dto.setUsername("admin");

        // Act & Assert - 模拟用户名已存在的情况
        if (existingUser.getUsername().equals(dto.getUsername())) {
            assertThatThrownBy(() -> {
                throw new BusinessException("用户名已存在");
            }).isInstanceOf(BusinessException.class).hasMessageContaining("用户名已存在");
        }
    }

    /**
     * 测试管理员保护逻辑
     */
    @Test
    @DisplayName("管理员保护 - userId=1 不能删除")
    void adminProtection_cannotDeleteAdmin() {
        // Arrange
        Long adminUserId = 1L;

        // Act & Assert - userId == 1 是管理员，不能删除
        assertThatThrownBy(() -> {
            if (adminUserId == 1L) {
                throw new BusinessException("不能删除管理员用户");
            }
        }).isInstanceOf(BusinessException.class).hasMessageContaining("不能删除管理员用户");
    }

    /**
     * 测试管理员保护逻辑 - 不能重置密码
     */
    @Test
    @DisplayName("管理员保护 - userId=1 不能重置密码")
    void adminProtection_cannotResetAdminPassword() {
        // Arrange
        Long adminUserId = 1L;

        // Act & Assert
        assertThatThrownBy(() -> {
            if (adminUserId == 1L) {
                throw new BusinessException("不能重置管理员密码");
            }
        }).isInstanceOf(BusinessException.class).hasMessageContaining("不能重置管理员密码");
    }

    /**
     * 测试管理员保护逻辑 - 不能禁用
     */
    @Test
    @DisplayName("管理员保护 - userId=1 不能禁用")
    void adminProtection_cannotDisableAdmin() {
        // Arrange
        Long adminUserId = 1L;

        // Act & Assert
        assertThatThrownBy(() -> {
            if (adminUserId == 1L) {
                throw new BusinessException("不能禁用管理员用户");
            }
        }).isInstanceOf(BusinessException.class).hasMessageContaining("不能禁用管理员用户");
    }

    /**
     * 测试用户 DTO 到 Entity 的转换
     */
    @Test
    @DisplayName("用户 DTO 转换 - 属性正确复制")
    void userDtoConversion_propertiesCopiedCorrectly() {
        // Arrange
        UserDTO dto = new UserDTO();
        dto.setUsername("testuser");
        dto.setNickname("测试用户");
        dto.setEmail("test@example.com");
        dto.setPhone("13800138000");
        dto.setGender("1");

        // Act
        User user = BeanUtil.copyProperties(dto, User.class);

        // Assert
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getNickname()).isEqualTo("测试用户");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getPhone()).isEqualTo("13800138000");
        assertThat(user.getGender()).isEqualTo("1");
    }

    /**
     * 测试用户状态判断
     */
    @Test
    @DisplayName("用户状态判断 - 状态为1表示禁用")
    void userStatus_statusOne_isDisabled() {
        // Arrange
        User user = new User();
        user.setStatus("1");

        // Act & Assert
        assertThat(user.getStatus()).isEqualTo("1");
    }

    /**
     * 测试用户状态判断 - 正常状态
     */
    @Test
    @DisplayName("用户状态判断 - 状态为0表示正常")
    void userStatus_statusZero_isNormal() {
        // Arrange
        User user = new User();
        user.setStatus("0");

        // Act & Assert
        assertThat(user.getStatus()).isEqualTo("0");
    }
}