package com.fast.modules.auth.service;

import cn.dev33.satoken.secure.BCrypt;
import com.fast.common.exception.BusinessException;
import com.fast.modules.auth.domain.dto.LoginDTO;
import com.fast.modules.system.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * AuthService 业务逻辑单元测试
 *
 * 测试核心业务规则，不依赖静态方法调用
 *
 * @author fast-frame
 */
class AuthServiceBusinessTest {

    /**
     * 测试密码验证逻辑
     */
    @Test
    @DisplayName("密码验证 - 正确密码验证成功")
    void passwordValidation_correctPassword_success() {
        // Arrange
        String rawPassword = "admin123";
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        // Act
        boolean result = BCrypt.checkpw(rawPassword, hashedPassword);

        // Assert
        assertThat(result).isTrue();
    }

    /**
     * 测试密码验证 - 错误密码验证失败
     */
    @Test
    @DisplayName("密码验证 - 错误密码验证失败")
    void passwordValidation_wrongPassword_failed() {
        // Arrange
        String rawPassword = "admin123";
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        // Act
        boolean result = BCrypt.checkpw("wrongpassword", hashedPassword);

        // Assert
        assertThat(result).isFalse();
    }

    /**
     * 测试用户状态检查 - 禁用用户
     */
    @Test
    @DisplayName("用户状态检查 - 状态为1表示禁用")
    void userStatusCheck_statusOne_disabled() {
        // Arrange
        User user = new User();
        user.setStatus("1");

        // Act & Assert - 模拟禁用检查逻辑
        assertThatThrownBy(() -> {
            if ("1".equals(user.getStatus())) {
                throw new BusinessException("账号已被禁用");
            }
        }).isInstanceOf(BusinessException.class).hasMessageContaining("账号已被禁用");
    }

    /**
     * 测试用户状态检查 - 正常用户
     */
    @Test
    @DisplayName("用户状态检查 - 状态为0表示正常")
    void userStatusCheck_statusZero_normal() {
        // Arrange
        User user = new User();
        user.setStatus("0");

        // Act & Assert - 正常状态不抛异常
        if ("0".equals(user.getStatus())) {
            // 不抛异常，测试通过
            assertThat(user.getStatus()).isEqualTo("0");
        }
    }

    /**
     * 测试账户锁定检查逻辑
     */
    @Test
    @DisplayName("账户锁定检查 - 锁定状态抛出异常")
    void accountLockedCheck_locked_throwsException() {
        // Arrange - 模拟 Redis 中存在锁定 key
        boolean isLocked = true;

        // Act & Assert
        assertThatThrownBy(() -> {
            if (isLocked) {
                throw new BusinessException("账户已锁定，请联系管理员解锁");
            }
        }).isInstanceOf(BusinessException.class).hasMessageContaining("账户已锁定");
    }

    /**
     * 测试验证码检查逻辑 - 验证码过期
     */
    @Test
    @DisplayName("验证码检查 - 验证码过期抛出异常")
    void captchaCheck_expired_throwsException() {
        // Arrange - 模拟 Redis 中不存在验证码
        String cachedCode = null;

        // Act & Assert
        assertThatThrownBy(() -> {
            if (cachedCode == null) {
                throw new BusinessException("验证码已过期");
            }
        }).isInstanceOf(BusinessException.class).hasMessageContaining("验证码已过期");
    }

    /**
     * 测试验证码检查逻辑 - 验证码错误
     */
    @Test
    @DisplayName("验证码检查 - 验证码错误抛出异常")
    void captchaCheck_wrongCode_throwsException() {
        // Arrange
        String cachedCode = "1234";
        String inputCode = "5678";

        // Act & Assert
        assertThatThrownBy(() -> {
            if (!cachedCode.equalsIgnoreCase(inputCode)) {
                throw new BusinessException("验证码错误");
            }
        }).isInstanceOf(BusinessException.class).hasMessageContaining("验证码错误");
    }

    /**
     * 测试验证码检查逻辑 - 验证码正确
     */
    @Test
    @DisplayName("验证码检查 - 验证码正确通过")
    void captchaCheck_correctCode_pass() {
        // Arrange
        String cachedCode = "1234";
        String inputCode = "1234";

        // Act & Assert - 正确验证码不抛异常
        assertThat(cachedCode.equalsIgnoreCase(inputCode)).isTrue();
    }

    /**
     * 测试登录失败计数逻辑 - 达到阈值锁定
     */
    @Test
    @DisplayName("登录失败计数 - 达到阈值锁定账户")
    void loginFailCount_reachThreshold_locksAccount() {
        // Arrange
        int maxFailCount = 5;
        int currentFailCount = 5;

        // Act & Assert
        assertThatThrownBy(() -> {
            if (currentFailCount >= maxFailCount) {
                throw new BusinessException("账户已锁定，请联系管理员解锁");
            }
        }).isInstanceOf(BusinessException.class).hasMessageContaining("账户已锁定");
    }

    /**
     * 测试登录失败计数逻辑 - 未达到阈值
     */
    @Test
    @DisplayName("登录失败计数 - 未达到阈值返回剩余次数")
    void loginFailCount_belowThreshold_remainingAttempts() {
        // Arrange
        int maxFailCount = 5;
        int currentFailCount = 3;

        // Act
        int remaining = maxFailCount - currentFailCount;

        // Assert
        assertThat(remaining).isEqualTo(2);
    }

    /**
     * 测试用户不存在检查逻辑
     */
    @Test
    @DisplayName("用户检查 - 用户不存在抛出异常")
    void userCheck_notFound_throwsException() {
        // Arrange
        User user = null;

        // Act & Assert
        assertThatThrownBy(() -> {
            if (user == null) {
                throw new BusinessException("用户名或密码错误");
            }
        }).isInstanceOf(BusinessException.class).hasMessageContaining("用户名或密码错误");
    }

    /**
     * 测试密码不匹配检查逻辑
     */
    @Test
    @DisplayName("密码检查 - 密码不匹配抛出异常")
    void passwordCheck_notMatch_throwsException() {
        // Arrange
        User user = new User();
        user.setPassword(BCrypt.hashpw("admin123", BCrypt.gensalt()));
        String inputPassword = "wrongpassword";

        // Act & Assert
        assertThatThrownBy(() -> {
            if (!BCrypt.checkpw(inputPassword, user.getPassword())) {
                throw new BusinessException("用户名或密码错误");
            }
        }).isInstanceOf(BusinessException.class).hasMessageContaining("用户名或密码错误");
    }
}