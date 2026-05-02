package com.fast.modules.system.domain.vo;

import lombok.Data;

/**
 * 用户简化信息VO
 *
 * @author fast-frame
 */
@Data
public class UserSimpleVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String nickname;
}