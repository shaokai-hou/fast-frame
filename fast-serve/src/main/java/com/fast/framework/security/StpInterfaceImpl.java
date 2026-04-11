package com.fast.framework.security;

import cn.dev33.satoken.stp.StpInterface;
import com.fast.modules.system.entity.Menu;
import com.fast.modules.system.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sa-Token 权限实现
 *
 * @author fast-frame
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final MenuMapper menuMapper;

    /**
     * 返回一个账号所拥有的权限码集合
     *
     * @param loginId   登录ID
     * @param loginType 登录类型
     * @return 权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());
        List<Menu> menus = menuMapper.selectMenusByUserId(userId);
        return menus.stream()
                .filter(menu -> menu.getPerms() != null && !menu.getPerms().isEmpty())
                .map(Menu::getPerms)
                .collect(Collectors.toList());
    }

    /**
     * 返回一个账号所拥有的角色标识集合
     *
     * @param loginId   登录ID
     * @param loginType 登录类型
     * @return 角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 这里返回角色key列表，如果需要可以查询用户角色
        return new ArrayList<>();
    }
}