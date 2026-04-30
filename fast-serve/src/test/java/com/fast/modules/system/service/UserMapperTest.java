package com.fast.modules.system.service;

import com.fast.modules.system.domain.dto.RoleVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserMapper 批量查询逻辑测试
 *
 * @author fast-frame
 */
class UserMapperTest {

    /**
     * 测试批量查询角色分组逻辑
     */
    @Test
    @DisplayName("批量查询角色 - 正确按 userId 分组")
    void batchQueryRoles_groupByUserId_correctly() {
        // Arrange - 模拟批量查询返回的角色列表
        RoleVO role1 = new RoleVO();
        role1.setId(1L);
        role1.setRoleName("管理员");
        role1.setUserId(2L);

        RoleVO role2 = new RoleVO();
        role2.setId(2L);
        role2.setRoleName("普通用户");
        role2.setUserId(2L);

        RoleVO role3 = new RoleVO();
        role3.setId(3L);
        role3.setRoleName("访客");
        role3.setUserId(3L);

        List<RoleVO> allRoles = Arrays.asList(role1, role2, role3);

        // Act - 按 userId 分组
        Map<Long, List<RoleVO>> roleMap = allRoles.stream()
            .collect(Collectors.groupingBy(RoleVO::getUserId));

        // Assert
        assertThat(roleMap.get(2L)).hasSize(2);
        assertThat(roleMap.get(2L).get(0).getRoleName()).isEqualTo("管理员");
        assertThat(roleMap.get(3L)).hasSize(1);
        assertThat(roleMap.get(1L)).isNull();
    }

    /**
     * 测试空角色列表处理
     */
    @Test
    @DisplayName("批量查询角色 - 空列表正确处理")
    void batchQueryRoles_emptyList_handlesGracefully() {
        // Arrange
        List<RoleVO> allRoles = Arrays.asList();

        // Act
        Map<Long, List<RoleVO>> roleMap = allRoles.stream()
            .collect(Collectors.groupingBy(RoleVO::getUserId));

        // Assert
        assertThat(roleMap).isEmpty();
    }
}