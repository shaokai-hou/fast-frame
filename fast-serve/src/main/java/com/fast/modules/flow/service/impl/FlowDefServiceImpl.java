package com.fast.modules.flow.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.fast.modules.flow.domain.dto.FlowDefJsonDTO;
import com.fast.modules.flow.domain.dto.FlowDefQueryDTO;
import com.fast.modules.flow.domain.dto.FlowNodeJsonDTO;
import com.fast.modules.flow.domain.dto.FlowSkipJsonDTO;
import com.fast.modules.flow.domain.vo.FlowDefVO;
import com.fast.modules.flow.service.FlowDefService;
import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.Skip;
import org.dromara.warm.flow.core.service.DefService;
import org.dromara.warm.flow.core.service.NodeService;
import org.dromara.warm.flow.core.service.SkipService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程定义Service实现
 *
 * @author fast-frame
 */
@Service
public class FlowDefServiceImpl implements FlowDefService {

    @Resource
    private DefService defService;

    @Override
    public List<FlowDefVO> listDefs(FlowDefQueryDTO dto) {
        List<Definition> definitions;

        if (StrUtil.isNotBlank(dto.getFlowCode())) {
            definitions = defService.getByFlowCode(dto.getFlowCode());
        } else {
            Definition query = FlowEngine.newDef();
            definitions = defService.list(query);
        }

        return definitions.stream()
                .filter(def -> StrUtil.isBlank(dto.getFlowName())
                        || def.getFlowName().contains(dto.getFlowName()))
                .filter(def -> dto.getIsPublish() == null
                        || def.getIsPublish().equals(dto.getIsPublish()))
                .filter(def -> dto.getActivityStatus() == null
                        || def.getActivityStatus().equals(dto.getActivityStatus()))
                .map(def -> BeanUtil.copyProperties(def, FlowDefVO.class))
                .collect(Collectors.toList());
    }

    @Override
    public FlowDefVO getDefById(Long id) {
        Definition definition = defService.getById(id);
        return BeanUtil.copyProperties(definition, FlowDefVO.class);
    }

    @Override
    public void publishDef(Long id) {
        defService.publish(id);
    }

    @Override
    public void unpublishDef(Long id) {
        defService.unActive(id);
    }

    @Override
    public void removeDef(Long id) {
        defService.removeById(id);
    }

    @Override
    public FlowDefJsonDTO getDefJsonByDefId(Long defId) {
        Definition definition = defService.getById(defId);

        NodeService nodeService = FlowEngine.nodeService();
        Node nodeQuery = FlowEngine.newNode();
        nodeQuery.setDefinitionId(defId);
        List<Node> nodes = nodeService.list(nodeQuery);

        SkipService skipService = FlowEngine.skipService();
        Skip skipQuery = FlowEngine.newSkip();
        skipQuery.setDefinitionId(defId);
        List<Skip> skips = skipService.list(skipQuery);

        Map<String, List<Skip>> skipMap = skips.stream()
                .collect(Collectors.groupingBy(Skip::getNowNodeCode));

        FlowDefJsonDTO defJson = new FlowDefJsonDTO();
        defJson.setFlowCode(definition.getFlowCode());
        defJson.setFlowName(definition.getFlowName());
        defJson.setCategory(definition.getCategory());
        defJson.setFormCustom(definition.getFormCustom());
        defJson.setFormPath(definition.getFormPath());
        defJson.setListenerPath(definition.getListenerPath());
        defJson.setListenerType(definition.getListenerType());
        defJson.setVersion(definition.getVersion());
        defJson.setExt(definition.getExt());

        defJson.setNodeList(nodes.stream()
                .map(node -> {
                    FlowNodeJsonDTO nodeJson = new FlowNodeJsonDTO();
                    nodeJson.setCoordinate(node.getCoordinate());
                    nodeJson.setNodeCode(node.getNodeCode());
                    nodeJson.setNodeName(node.getNodeName());
                    nodeJson.setNodeType(node.getNodeType());
                    nodeJson.setPermissionFlag(node.getPermissionFlag());
                    nodeJson.setNodeRatio(node.getNodeRatio());
                    nodeJson.setAnyNodeSkip(node.getAnyNodeSkip());
                    nodeJson.setListenerPath(node.getListenerPath());
                    nodeJson.setListenerType(node.getListenerType());
                    nodeJson.setFormCustom(node.getFormCustom());
                    nodeJson.setFormPath(node.getFormPath());
                    nodeJson.setExt(node.getExt());

                    List<Skip> nodeSkips = skipMap.getOrDefault(node.getNodeCode(), new ArrayList<>());
                    nodeJson.setSkipList(nodeSkips.stream()
                            .map(skip -> {
                                FlowSkipJsonDTO skipJson = new FlowSkipJsonDTO();
                                skipJson.setCoordinate(skip.getCoordinate());
                                skipJson.setNowNodeCode(skip.getNowNodeCode());
                                skipJson.setNextNodeCode(skip.getNextNodeCode());
                                skipJson.setSkipName(skip.getSkipName());
                                skipJson.setSkipType(skip.getSkipType());
                                skipJson.setSkipCondition(skip.getSkipCondition());
                                return skipJson;
                            })
                            .collect(Collectors.toList()));
                    return nodeJson;
                })
                .collect(Collectors.toList()));

        return defJson;
    }
}