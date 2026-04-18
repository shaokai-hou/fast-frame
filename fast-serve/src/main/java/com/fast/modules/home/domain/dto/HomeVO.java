package com.fast.modules.home.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 首页数据VO
 *
 * @author fast-frame
 */
@Data
public class HomeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 快捷入口列表
     */
    private List<QuickLinkVO> quickLinks;
}