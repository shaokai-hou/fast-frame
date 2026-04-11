package com.fast.modules.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.common.exception.BusinessException;
import com.fast.modules.system.dto.DeptDTO;
import com.fast.modules.system.entity.Dept;
import com.fast.modules.system.entity.User;
import com.fast.modules.system.mapper.DeptMapper;
import com.fast.modules.system.mapper.UserMapper;
import com.fast.modules.system.service.DeptService;
import com.fast.modules.system.vo.DeptTreeVO;
import com.fast.modules.system.vo.DeptVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 部门Service实现
 *
 * @author fast-frame
 */
@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

    @Resource
    private UserMapper userMapper;

    /**
     * 查询部门树形列表
     *
     * @param dto 查询参数 DTO
     * @return 部门树形列表
     */
    @Override
    public List<DeptVO> listDeptTree(DeptDTO dto) {
        // 判断是否有搜索条件
        boolean hasSearchCondition = StrUtil.isNotBlank(dto.getDeptName()) || StrUtil.isNotBlank(dto.getStatus());

        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(dto.getDeptName()), Dept::getDeptName, dto.getDeptName())
               .eq(StrUtil.isNotBlank(dto.getStatus()), Dept::getStatus, dto.getStatus())
               .orderByAsc(Dept::getSort)
               .orderByAsc(Dept::getCreateTime);
        List<Dept> depts = list(wrapper);

        // 如果有搜索条件，补全祖先部门以保持树形结构
        if (hasSearchCondition && !depts.isEmpty()) {
            depts = fillAncestors(depts);
        }

        return buildDeptTree(depts, 0L);
    }

    /**
     * 补全祖先部门，确保搜索结果能正确构建树形结构
     *
     * @param matchedDepts 匹配搜索条件的部门列表
     * @return 包含祖先部门的完整部门列表
     */
    private List<Dept> fillAncestors(List<Dept> matchedDepts) {
        Set<Long> matchedIds = matchedDepts.stream()
                .map(Dept::getId)
                .collect(Collectors.toSet());

        Set<Dept> allDepts = new HashSet<>(matchedDepts);

        // 遍历每个匹配的部门，找出其祖先
        for (Dept dept : matchedDepts) {
            Dept current = dept;
            while (current.getParentId() != null && current.getParentId() != 0L) {
                // 如果祖先已在结果中，跳过
                if (matchedIds.contains(current.getParentId())) {
                    break;
                }
                // 查询祖先部门
                Dept parent = getById(current.getParentId());
                if (parent == null) {
                    break;
                }
                allDepts.add(parent);
                matchedIds.add(parent.getId());
                current = parent;
            }
        }

        return new ArrayList<>(allDepts);
    }

    /**
     * 获取部门树形选择器列表
     *
     * @return 部门树形选择器列表
     */
    @Override
    public List<DeptTreeVO> listDeptTreeSelect() {
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dept::getStatus, "0")
               .orderByAsc(Dept::getSort);
        List<Dept> depts = list(wrapper);
        return buildTreeSelect(depts, 0L);
    }

    /**
     * 新增部门
     *
     * @param dept 部门实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDept(Dept dept) {
        // 检查部门名称是否存在
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dept::getDeptName, dept.getDeptName())
               .eq(Dept::getParentId, dept.getParentId() == null ? 0L : dept.getParentId());
        if (count(wrapper) > 0) {
            throw new BusinessException("同级部门名称已存在");
        }
        // 设置祖级列表
        if (dept.getParentId() == null || dept.getParentId() == 0L) {
            dept.setParentId(0L);
            dept.setAncestors("0");
        } else {
            Dept parentDept = getById(dept.getParentId());
            if (parentDept == null) {
                throw new BusinessException("父部门不存在");
            }
            dept.setAncestors(parentDept.getAncestors() + "," + parentDept.getId());
        }
        save(dept);
    }

    /**
     * 更新部门
     *
     * @param dept 部门实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDept(Dept dept) {
        Dept existDept = getById(dept.getId());
        if (existDept == null) {
            throw new BusinessException("部门不存在");
        }
        // 检查部门名称是否重复
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dept::getDeptName, dept.getDeptName())
               .eq(Dept::getParentId, dept.getParentId() == null ? 0L : dept.getParentId())
               .ne(Dept::getId, dept.getId());
        if (count(wrapper) > 0) {
            throw new BusinessException("同级部门名称已存在");
        }
        // 不能将父部门设置为自己
        if (dept.getId().equals(dept.getParentId())) {
            throw new BusinessException("父部门不能是自己");
        }
        // 更新祖级列表
        if (dept.getParentId() != null && !dept.getParentId().equals(existDept.getParentId())) {
            if (dept.getParentId() == 0L) {
                dept.setAncestors("0");
            } else {
                Dept parentDept = getById(dept.getParentId());
                if (parentDept == null) {
                    throw new BusinessException("父部门不存在");
                }
                dept.setAncestors(parentDept.getAncestors() + "," + parentDept.getId());
            }
        }
        updateById(dept);
    }

    /**
     * 删除部门
     *
     * @param id 部门 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDept(Long id) {
        // 检查是否存在子部门
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dept::getParentId, id);
        if (count(wrapper) > 0) {
            throw new BusinessException("存在子部门，不能删除");
        }
        // 检查部门下是否存在用户
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getDeptId, id);
        if (userMapper.selectCount(userWrapper) > 0) {
            throw new BusinessException("部门下存在用户，不能删除");
        }
        removeById(id);
    }

    /**
     * 根据角色 ID 获取部门 ID 列表
     *
     * @param roleId 角色 ID
     * @return 部门 ID 列表
     */
    @Override
    public List<Long> getDeptIdsByRoleId(Long roleId) {
        return baseMapper.selectDeptIdsByRoleId(roleId);
    }

    /**
     * 获取部门及其子部门 ID 列表
     *
     * @param deptId 部门 ID
     * @return 部门 ID 列表
     */
    @Override
    public List<Long> getDeptAndChildrenIds(Long deptId) {
        return baseMapper.selectChildrenDeptIdsById(deptId);
    }

    /**
     * 根据部门名称查询部门ID
     *
     * @param deptName 部门名称
     * @return 部门ID（不存在则返回 null）
     */
    @Override
    public Long getDeptIdByName(String deptName) {
        if (StrUtil.isBlank(deptName)) {
            return null;
        }
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dept::getDeptName, deptName.trim());
        wrapper.last("LIMIT 1");
        Dept dept = getOne(wrapper);
        return dept != null ? dept.getId() : null;
    }

    /**
     * 构建部门树
     *
     * @param depts 部门列表
     * @param parentId 父级 ID
     * @return 部门树形列表
     */
    private List<DeptVO> buildDeptTree(List<Dept> depts, Long parentId) {
        List<DeptVO> result = new ArrayList<>();
        for (Dept dept : depts) {
            if (parentId.equals(dept.getParentId())) {
                DeptVO vo = BeanUtil.copyProperties(dept, DeptVO.class);
                vo.setChildren(buildDeptTree(depts, dept.getId()));
                result.add(vo);
            }
        }
        return result;
    }

    /**
     * 构建树形选择器
     *
     * @param depts 部门列表
     * @param parentId 父级 ID
     * @return 树形选择器列表
     */
    private List<DeptTreeVO> buildTreeSelect(List<Dept> depts, Long parentId) {
        List<DeptTreeVO> result = new ArrayList<>();
        for (Dept dept : depts) {
            if (parentId.equals(dept.getParentId())) {
                DeptTreeVO vo = new DeptTreeVO();
                vo.setId(dept.getId());
                vo.setParentId(dept.getParentId());
                vo.setLabel(dept.getDeptName());
                vo.setChildren(buildTreeSelect(depts, dept.getId()));
                result.add(vo);
            }
        }
        return result;
    }
}