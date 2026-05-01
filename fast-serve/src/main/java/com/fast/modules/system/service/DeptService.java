package com.fast.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fast.modules.system.domain.dto.DeptDTO;
import com.fast.modules.system.domain.query.DeptQuery;
import com.fast.modules.system.domain.vo.DeptTreeVO;
import com.fast.modules.system.domain.vo.DeptVO;
import com.fast.modules.system.domain.entity.Dept;

import java.util.List;

/**
 * 部门Service
 *
 * @author fast-frame
 */
public interface DeptService extends IService<Dept> {


    /**
     * 查询部门树
     *
     * @param query    查询条件
     * @return 部门树列表
     */
    List<DeptVO> listDeptTree(DeptQuery query);

    /**
     * 查询部门树选择器
     *
     * @return 部门树选择器数据
     */
    List<DeptTreeVO> listDeptTreeSelect();

    /**
     * 根据角色ID查询部门ID列表
     *
     * @param roleId 角色ID
     * @return 部门ID列表
     */
    List<Long> getDeptIdsByRoleId(Long roleId);

    /**
     * 根据部门ID查询部门及子部门ID列表
     *
     * @param deptId 部门ID
     * @return 部门ID列表
     */
    List<Long> getDeptAndChildrenIds(Long deptId);

    /**
     * 根据部门名称查询部门ID
     *
     * @param deptName 部门名称
     * @return 部门ID（不存在则返回 null）
     */
    Long getDeptIdByName(String deptName);


    /**
     * 新增部门
     *
     * @param dept 部门信息
     */
    void addDept(Dept dept);


    /**
     * 修改部门
     *
     * @param dept 部门信息
     */
    void updateDept(Dept dept);


    /**
     * 删除部门
     *
     * @param id 部门ID
     */
    void deleteDept(Long id);
}