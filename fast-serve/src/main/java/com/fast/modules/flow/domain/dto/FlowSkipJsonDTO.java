package com.fast.modules.flow.domain.dto;

/**
 * 流程跳转JSON结构DTO（用于生成def_json.nodeList[].skipList）
 *
 * @author fast-frame
 */
public class FlowSkipJsonDTO {

    private String coordinate;

    private String nowNodeCode;

    private String nextNodeCode;

    private String skipName;

    private String skipType;

    private String skipCondition;

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public String getNowNodeCode() {
        return nowNodeCode;
    }

    public void setNowNodeCode(String nowNodeCode) {
        this.nowNodeCode = nowNodeCode;
    }

    public String getNextNodeCode() {
        return nextNodeCode;
    }

    public void setNextNodeCode(String nextNodeCode) {
        this.nextNodeCode = nextNodeCode;
    }

    public String getSkipName() {
        return skipName;
    }

    public void setSkipName(String skipName) {
        this.skipName = skipName;
    }

    public String getSkipType() {
        return skipType;
    }

    public void setSkipType(String skipType) {
        this.skipType = skipType;
    }

    public String getSkipCondition() {
        return skipCondition;
    }

    public void setSkipCondition(String skipCondition) {
        this.skipCondition = skipCondition;
    }
}