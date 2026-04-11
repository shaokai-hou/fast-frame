package com.fast.modules.system.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 字典VO
 *
 * @author fast-frame
 */
@Data
public class DictVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 状态
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 字典数据列表
     */
    private List<DictDataVO> dataList;
}