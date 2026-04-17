package com.fast.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.modules.system.domain.dto.UserDTO;
import com.fast.modules.system.domain.entity.User;
import com.fast.modules.system.domain.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper
 *
 * @author fast-frame
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {


    /**
     * 分页查询用户列表（带部门名称和角色）
     *
     * @param page 分页对象
     * @param dto  查询参数DTO
     * @return 用户分页结果
     */
    IPage<UserVO> selectUserPage(IPage<UserVO> page, UserDTO dto);

    /**
     * 查询用户列表（不分页，用于导出）
     *
     * @param dto 查询参数DTO
     * @return 用户列表
     */
    List<UserVO> selectUserList(UserDTO dto);


    /**
     * 批量插入用户角色关联
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    void insertUserRole(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);


    /**
     * 删除用户角色关联
     *
     * @param userId 用户ID
     */
    void deleteUserRoleByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询用户ID列表
     *
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    List<Long> selectUserIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据部门ID查询用户ID列表
     *
     * @param deptId 部门ID
     * @return 用户ID列表
     */
    List<Long> selectUserIdsByDeptId(@Param("deptId") Long deptId);
}