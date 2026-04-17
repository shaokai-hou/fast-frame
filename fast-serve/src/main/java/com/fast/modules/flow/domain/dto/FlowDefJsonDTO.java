package com.fast.modules.flow.domain.dto;

import java.util.List;

/**
 * 流程定义JSON结构DTO（用于生成flow_instance.def_json）
 *
 * @author fast-frame
 */
public class FlowDefJsonDTO {

    private String flowCode;

    private String flowName;

    private String category;

    private String formCustom;

    private String formPath;

    private String listenerPath;

    private String listenerType;

    private String version;

    private String ext;

    private List<FlowNodeJsonDTO> nodeList;

    public String getFlowCode() {
        return flowCode;
    }

    public void setFlowCode(String flowCode) {
        this.flowCode = flowCode;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFormCustom() {
        return formCustom;
    }

    public void setFormCustom(String formCustom) {
        this.formCustom = formCustom;
    }

    public String getFormPath() {
        return formPath;
    }

    public void setFormPath(String formPath) {
        this.formPath = formPath;
    }

    public String getListenerPath() {
        return listenerPath;
    }

    public void setListenerPath(String listenerPath) {
        this.listenerPath = listenerPath;
    }

    public String getListenerType() {
        return listenerType;
    }

    public void setListenerType(String listenerType) {
        this.listenerType = listenerType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public List<FlowNodeJsonDTO> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<FlowNodeJsonDTO> nodeList) {
        this.nodeList = nodeList;
    }
}