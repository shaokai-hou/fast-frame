package com.fast.modules.flow.domain.dto;

import java.util.List;

/**
 * 流程节点JSON结构DTO（用于生成def_json.nodeList）
 *
 * @author fast-frame
 */
public class FlowNodeJsonDTO {

    private String coordinate;

    private String nodeCode;

    private String nodeName;

    private Integer nodeType;

    private String permissionFlag;

    private String nodeRatio;

    private String anyNodeSkip;

    private String listenerPath;

    private String listenerType;

    private String formCustom;

    private String formPath;

    private String ext;

    private List<FlowSkipJsonDTO> skipList;

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Integer getNodeType() {
        return nodeType;
    }

    public void setNodeType(Integer nodeType) {
        this.nodeType = nodeType;
    }

    public String getPermissionFlag() {
        return permissionFlag;
    }

    public void setPermissionFlag(String permissionFlag) {
        this.permissionFlag = permissionFlag;
    }

    public String getNodeRatio() {
        return nodeRatio;
    }

    public void setNodeRatio(String nodeRatio) {
        this.nodeRatio = nodeRatio;
    }

    public String getAnyNodeSkip() {
        return anyNodeSkip;
    }

    public void setAnyNodeSkip(String anyNodeSkip) {
        this.anyNodeSkip = anyNodeSkip;
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

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public List<FlowSkipJsonDTO> getSkipList() {
        return skipList;
    }

    public void setSkipList(List<FlowSkipJsonDTO> skipList) {
        this.skipList = skipList;
    }
}