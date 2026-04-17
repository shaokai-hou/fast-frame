package com.fast.modules.flow.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.fast.modules.flow.domain.dto.FlowDefJsonDTO;
import com.fast.modules.flow.domain.dto.FlowInstanceQueryDTO;
import com.fast.modules.flow.domain.dto.FlowInstanceStartDTO;
import com.fast.modules.flow.domain.vo.FlowHisTaskVO;
import com.fast.modules.flow.domain.vo.FlowInstanceVO;
import com.fast.modules.flow.service.FlowDefService;
import com.fast.modules.flow.service.FlowInstanceService;
import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.HisTask;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.enums.FlowStatus;
import org.dromara.warm.flow.core.service.DefService;
import org.dromara.warm.flow.core.service.HisTaskService;
import org.dromara.warm.flow.core.service.InsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程实例Service实现
 *
 * @author fast-frame
 */
@Service
public class FlowInstanceServiceImpl implements FlowInstanceService {

    @Resource
    private InsService insService;

    @Resource
    private DefService defService;

    @Resource
    private FlowDefService flowDefService;

    @Override
    public List<FlowInstanceVO> listInstances(FlowInstanceQueryDTO dto) {
        String userId = StpUtil.getLoginIdAsString();
        Instance query = FlowEngine.newIns();
        query.setCreateBy(userId);

        List<Instance> instances = insService.list(query);
        return instances.stream()
                // 业务ID过滤
                .filter(ins -> dto.getBusinessId() == null || dto.getBusinessId().isEmpty()
                        || ins.getBusinessId().contains(dto.getBusinessId()))
                // 流程状态过滤
                .filter(ins -> dto.getFlowStatus() == null || dto.getFlowStatus().isEmpty()
                        || ins.getFlowStatus().equals(dto.getFlowStatus()))
                .map(ins -> {
                    FlowInstanceVO vo = BeanUtil.copyProperties(ins, FlowInstanceVO.class);
                    vo.setFlowStatusText(FlowStatus.getValueByKey(ins.getFlowStatus()));
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public FlowInstanceVO getInstanceById(Long id) {
        Instance instance = insService.getById(id);
        FlowInstanceVO vo = BeanUtil.copyProperties(instance, FlowInstanceVO.class);
        vo.setFlowStatusText(FlowStatus.getValueByKey(instance.getFlowStatus()));
        return vo;
    }

    @Override
    public Long startInstance(FlowInstanceStartDTO dto) {
        String userId = StpUtil.getLoginIdAsString();

        List<Definition> definitions = defService.getByFlowCode(dto.getFlowCode());
        if (definitions.isEmpty()) {
            throw new RuntimeException("流程定义不存在: " + dto.getFlowCode());
        }
        Definition definition = definitions.get(0);

        FlowDefJsonDTO defJsonDto = flowDefService.getDefJsonByDefId(definition.getId());
        String defJson = JSONUtil.toJsonStr(defJsonDto);

        FlowParams params = FlowParams.build()
                .flowCode(dto.getFlowCode())
                .handler(userId);

        Instance instance = insService.start(dto.getBusinessId(), params);

        instance.setDefJson(defJson);
        insService.updateById(instance);

        return instance.getId();
    }

    @Override
    public void terminateInstance(Long id) {
        insService.remove(Collections.singletonList(id));
    }

    @Override
    public Object getDiagram(Long id) {
        Instance instance = insService.getById(id);
        return instance;
    }

    @Override
    public List<FlowHisTaskVO> getApprovalHistory(Long instanceId) {
        HisTaskService hisTaskService = FlowEngine.hisTaskService();
        HisTask query = FlowEngine.newHisTask();
        query.setInstanceId(instanceId);

        List<HisTask> hisTasks = hisTaskService.list(query);
        if (CollUtil.isEmpty(hisTasks)) {
            return Collections.emptyList();
        }

        // 过滤掉开始节点(nodeType=0)，只保留审批节点的历史记录
        // 按 createTime 正序排列，形成时间线
        return hisTasks.stream()
                .filter(hisTask -> hisTask.getNodeType() != null && hisTask.getNodeType() != 0)
                .sorted(Comparator.comparing(HisTask::getCreateTime))
                .map(hisTask -> {
                    FlowHisTaskVO vo = BeanUtil.copyProperties(hisTask, FlowHisTaskVO.class);
                    // 转换 skipType 为文本
                    String skipType = hisTask.getSkipType();
                    if ("PASS".equals(skipType)) {
                        vo.setSkipTypeText("通过");
                    } else if ("REJECT".equals(skipType)) {
                        vo.setSkipTypeText("驳回");
                    } else {
                        vo.setSkipTypeText("无动作");
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }
}