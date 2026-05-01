package com.fast.modules.flow.service;

import com.fast.modules.flow.domain.dto.FlowDefJsonDTO;
import com.fast.modules.flow.domain.query.FlowDefQuery;
import com.fast.modules.flow.domain.vo.FlowDefVO;

import java.util.List;

/**
 * 流程定义Service接口
 *
 * @author fast-frame
 */
public interface FlowDefService {

    /**
     * 查询流程定义列表
     *
     * @param query    查询条件
     * @return 流程定义列表
     */
    List<FlowDefVO> listDefs(FlowDefQuery query);

    /**
     * 根据ID获取流程定义详情
     *
     * @param id 流程定义ID
     * @return 流程定义详情
     */
    FlowDefVO getDefById(Long id);

    /**
     * 发布流程定义
     *
     * @param id 流程定义ID
     */
    void publishDef(Long id);

    /**
     * 取消发布流程定义
     *
     * @param id 流程定义ID
     */
    void unpublishDef(Long id);

    /**
     * 删除流程定义
     *
     * @param id 流程定义ID
     */
    void removeDef(Long id);

    /**
     * 获取流程定义的JSON结构（用于流程图渲染）
     *
     * @param defId 流程定义ID
     * @return 流程定义JSON结构
     */
    FlowDefJsonDTO getDefJsonByDefId(Long defId);
}