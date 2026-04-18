package com.fast.modules.flow.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.fast.modules.flow.domain.dto.FlowTaskApproveDTO;
import com.fast.modules.flow.domain.dto.FlowTaskQuery;
import com.fast.modules.flow.domain.dto.FlowTaskVO;
import com.fast.modules.flow.service.FlowTaskService;
import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.entity.*;
import org.dromara.warm.flow.core.enums.FlowStatus;
import org.dromara.warm.flow.core.service.DefService;
import org.dromara.warm.flow.core.service.HisTaskService;
import org.dromara.warm.flow.core.service.TaskService;
import org.dromara.warm.flow.core.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程任务Service实现
 *
 * @author fast-frame
 */
@Service
public class FlowTaskServiceImpl implements FlowTaskService {

    @Resource
    private TaskService taskService;

    @Resource
    private DefService defService;

    @Override
    public List<FlowTaskVO> listTodoTasks(FlowTaskQuery query) {
        // 获取当前用户的完整权限标识列表（包括 userId 和 role:xxx）
        List<String> permissions = FlowEngine.permissionHandler().permissions();
        if (CollUtil.isEmpty(permissions)) {
            return new ArrayList<>();
        }

        // 用每个权限标识查询 flow_user 表中匹配的记录
        UserService userService = FlowEngine.userService();
        List<User> userList = new ArrayList<>();
        for (String permission : permissions) {
            User userQuery = FlowEngine.newUser();
            userQuery.setType("1");
            userQuery.setProcessedBy(permission);
            List<User> found = userService.list(userQuery);
            userList.addAll(found);
        }

        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }

        // 获取关联的任务ID列表（去重）
        List<Long> taskIds = userList.stream()
                .map(User::getAssociated)
                .distinct()
                .collect(Collectors.toList());

        // 查询待办任务详情
        List<Task> tasks = taskService.getByIds(taskIds);
        if (CollUtil.isEmpty(tasks)) {
            return new ArrayList<>();
        }

        // 获取所有实例ID
        List<Long> instanceIds = tasks.stream()
                .map(Task::getInstanceId)
                .distinct()
                .collect(Collectors.toList());
        List<Instance> instances = FlowEngine.insService().getByIds(instanceIds);
        java.util.Map<Long, Instance> instanceMap = instances.stream()
                .collect(Collectors.toMap(Instance::getId, i -> i));

        // 获取所有 definitionId，查询流程定义获取 flowName
        List<Long> definitionIds = instances.stream()
                .map(Instance::getDefinitionId)
                .distinct()
                .collect(Collectors.toList());
        List<Definition> definitions = defService.getByIds(definitionIds);
        java.util.Map<Long, Definition> definitionMap = definitions.stream()
                .collect(Collectors.toMap(Definition::getId, d -> d));

        return tasks.stream()
                .map(task -> {
                    FlowTaskVO vo = BeanUtil.copyProperties(task, FlowTaskVO.class);
                    vo.setFlowStatusText(FlowStatus.getValueByKey(task.getFlowStatus()));
                    Instance instance = instanceMap.get(task.getInstanceId());
                    if (instance != null) {
                        vo.setBusinessId(instance.getBusinessId());
                        Definition definition = definitionMap.get(instance.getDefinitionId());
                        if (definition != null) {
                            vo.setFlowName(definition.getFlowName());
                        }
                    }
                    return vo;
                })
                // 业务ID过滤
                .filter(vo -> query.getBusinessId() == null || query.getBusinessId().isEmpty()
                        || (vo.getBusinessId() != null && vo.getBusinessId().contains(query.getBusinessId())))
                // 流程名称过滤
                .filter(vo -> query.getFlowName() == null || query.getFlowName().isEmpty()
                        || (vo.getFlowName() != null && vo.getFlowName().contains(query.getFlowName())))
                .collect(Collectors.toList());
    }

    @Override
    public List<FlowTaskVO> listDoneTasks(FlowTaskQuery query) {
        // 获取当前用户的完整权限标识列表
        List<String> permissions = FlowEngine.permissionHandler().permissions();
        if (CollUtil.isEmpty(permissions)) {
            return new ArrayList<>();
        }

        // 查询已办任务（历史任务表）
        HisTaskService hisTaskService = FlowEngine.hisTaskService();
        List<HisTask> hisTasks = new ArrayList<>();
        for (String permission : permissions) {
            HisTask hisQuery = FlowEngine.newHisTask();
            hisQuery.setApprover(permission);
            List<HisTask> found = hisTaskService.list(hisQuery);
            hisTasks.addAll(found);
        }

        if (CollUtil.isEmpty(hisTasks)) {
            return new ArrayList<>();
        }

        // 获取所有实例ID
        List<Long> instanceIds = hisTasks.stream()
                .map(HisTask::getInstanceId)
                .distinct()
                .collect(Collectors.toList());
        List<Instance> instances = FlowEngine.insService().getByIds(instanceIds);
        java.util.Map<Long, Instance> instanceMap = instances.stream()
                .collect(Collectors.toMap(Instance::getId, i -> i));

        // 获取所有 definitionId，查询流程定义获取 flowName
        List<Long> definitionIds = instances.stream()
                .map(Instance::getDefinitionId)
                .distinct()
                .collect(Collectors.toList());
        List<Definition> definitions = defService.getByIds(definitionIds);
        java.util.Map<Long, Definition> definitionMap = definitions.stream()
                .collect(Collectors.toMap(Definition::getId, d -> d));

        return hisTasks.stream()
                // 过滤掉开始节点(node_type=0)，只保留审批节点的已办任务
                .filter(hisTask -> hisTask.getNodeType() != null && hisTask.getNodeType() != 0)
                .map(hisTask -> {
                    FlowTaskVO vo = BeanUtil.copyProperties(hisTask, FlowTaskVO.class);
                    vo.setFlowStatusText(FlowStatus.getValueByKey(hisTask.getFlowStatus()));
                    Instance instance = instanceMap.get(hisTask.getInstanceId());
                    if (instance != null) {
                        vo.setBusinessId(instance.getBusinessId());
                        Definition definition = definitionMap.get(instance.getDefinitionId());
                        if (definition != null) {
                            vo.setFlowName(definition.getFlowName());
                        }
                    }
                    vo.setApproveTime(hisTask.getUpdateTime());
                    vo.setMessage(hisTask.getMessage());
                    return vo;
                })
                // 业务ID过滤
                .filter(vo -> query.getBusinessId() == null || query.getBusinessId().isEmpty()
                        || (vo.getBusinessId() != null && vo.getBusinessId().contains(query.getBusinessId())))
                // 流程名称过滤
                .filter(vo -> query.getFlowName() == null || query.getFlowName().isEmpty()
                        || (vo.getFlowName() != null && vo.getFlowName().contains(query.getFlowName())))
                // 流程状态过滤
                .filter(vo -> query.getFlowStatus() == null || query.getFlowStatus().isEmpty()
                        || (vo.getFlowStatus() != null && vo.getFlowStatus().equals(query.getFlowStatus())))
                .collect(Collectors.toList());
    }

    @Override
    public FlowTaskVO getTaskById(Long id) {
        Task task = taskService.getById(id);
        return BeanUtil.copyProperties(task, FlowTaskVO.class);
    }

    @Override
    public void approveTask(FlowTaskApproveDTO dto) {
        // 获取当前办理人和权限标识
        String handler = FlowEngine.permissionHandler().getHandler();
        List<String> permissions = FlowEngine.permissionHandler().permissions();

        FlowParams params = FlowParams.build()
                .skipType("PASS")
                .handler(handler)
                .permissionFlag(permissions)
                .message(dto.getMessage());

        taskService.skip(dto.getTaskId(), params);
    }

    @Override
    public void rejectTask(FlowTaskApproveDTO dto) {
        // 获取当前办理人和权限标识
        String handler = FlowEngine.permissionHandler().getHandler();
        List<String> permissions = FlowEngine.permissionHandler().permissions();

        FlowParams params = FlowParams.build()
                .skipType("REJECT")
                .handler(handler)
                .permissionFlag(permissions)
                .message(dto.getMessage());

        taskService.skip(dto.getTaskId(), params);
    }

    @Override
    public void backTask(FlowTaskApproveDTO dto) {
        // 获取当前办理人和权限标识
        String handler = FlowEngine.permissionHandler().getHandler();
        List<String> permissions = FlowEngine.permissionHandler().permissions();

        FlowParams params = FlowParams.build()
                .skipType("REJECT")
                .handler(handler)
                .permissionFlag(permissions)
                .message(dto.getMessage());

        taskService.taskBack(dto.getTaskId(), params);
    }

    @Override
    public void delegateTask(FlowTaskApproveDTO dto) {
        // 获取当前办理人和权限标识
        String handler = FlowEngine.permissionHandler().getHandler();
        List<String> permissions = FlowEngine.permissionHandler().permissions();

        FlowParams params = FlowParams.build()
                .handler(handler)
                .permissionFlag(permissions)
                .message(dto.getMessage());

        taskService.depute(dto.getTaskId(), params);
    }
}