package com.fast.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fast.modules.system.entity.Dept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门Mapper
 *
 * @author fast-frame
 */
@Mapper
public interface DeptMapper extends BaseMapper<Dept> {

    /**
     * 根据角色ID查询部门ID列表
     *
     * @param roleId 角色ID
     * @return 部门ID列表
     */
    List<Long> selectDeptIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据部门ID查询子部门ID列表
     *
     * @param deptId 部门ID
     * @return 子部门ID列表
     */
    List<Long> selectChildrenDeptIdsById(@Param("deptId") Long deptId);
}