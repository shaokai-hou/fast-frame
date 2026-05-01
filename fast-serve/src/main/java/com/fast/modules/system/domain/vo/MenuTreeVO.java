package com.fast.modules.system.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 菜单树VO
 *
 * @author fast-frame
 */
@Data
public class MenuTreeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 节点ID
     */
    private Long id;

    /**
     * 父节点ID
     */
    private Long parentId;

    /**
     * 节点名称
     */
    private String label;

    /**
     * 子节点
     */
    private List<MenuTreeVO> children;
}