package com.fast.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.modules.system.entity.User;
import com.fast.modules.system.vo.UserVO;
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
     * 分页查询用户列表（带部门名称）
     */
    IPage<UserVO> selectUserPage(IPage<UserVO> page,
                                 @Param("deptId") Long deptId,
                                 @Param("username") String username,
                                 @Param("phone") String phone,
                                 @Param("status") String status,
                                 @Param("dataScope") String dataScope);

    /**
     * 删除用户角色关联
     */
    void deleteUserRoleByUserId(@Param("userId") Long userId);

    /**
     * 批量插入用户角色关联
     */
    void insertUserRole(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    /**
     * 查询用户列表（不分页，用于导出）
     *
     * @param deptId    部门ID
     * @param username  用户名
     * @param phone     手机号
     * @param status    状态
     * @param dataScope 数据权限SQL
     * @return 用户列表
     */
    List<UserVO> selectUserList(@Param("deptId") Long deptId,
                                @Param("username") String username,
                                @Param("phone") String phone,
                                @Param("status") String status,
                                @Param("dataScope") String dataScope);
}