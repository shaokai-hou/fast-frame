package com.fast.modules.home.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.fast.modules.home.domain.dto.HomeVO;
import com.fast.modules.home.domain.dto.QuickLinkVO;
import com.fast.modules.home.service.HomeService;
import com.fast.modules.system.domain.dto.MenuVO;
import com.fast.modules.system.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页Service实现
 *
 * @author fast-frame
 */
@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final MenuService menuService;

    // 快捷入口最大数量
    private static final int MAX_QUICK_LINKS = 6;

    /**
     * 获取首页数据
     *
     * @return 首页数据VO
     */
    @Override
    public HomeVO getHomeData() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<MenuVO> menus = menuService.listMenusByUserId(userId);

        HomeVO vo = new HomeVO();
        vo.setQuickLinks(buildQuickLinks(menus));
        return vo;
    }

    /**
     * 构建快捷入口列表
     *
     * @param menus 用户菜单树
     * @return 快捷入口列表
     */
    private List<QuickLinkVO> buildQuickLinks(List<MenuVO> menus) {
        List<QuickLinkVO> result = new ArrayList<>();
        if (menus == null || menus.isEmpty()) {
            return result;
        }

        // 扁平化菜单树，获取菜单类型为 'M' 的菜单
        List<MenuVO> flatMenus = flattenMenus(menus);

        // 取前 MAX_QUICK_LINKS 个
        int count = Math.min(flatMenus.size(), MAX_QUICK_LINKS);
        for (int i = 0; i < count; i++) {
            MenuVO menu = flatMenus.get(i);
            QuickLinkVO link = new QuickLinkVO();
            link.setId(menu.getId());
            link.setName(menu.getMenuName());
            link.setPath(menu.getPath());
            link.setIcon(menu.getIcon());
            result.add(link);
        }

        return result;
    }

    /**
     * 扁平化菜单树
     *
     * @param menus 菜单树
     * @return 扁平化的菜单列表（只包含菜单类型为 'M' 的）
     */
    private List<MenuVO> flattenMenus(List<MenuVO> menus) {
        List<MenuVO> result = new ArrayList<>();
        for (MenuVO menu : menus) {
            // 只取菜单类型为 'M'（菜单），排除目录 'D' 和按钮 'B'
            if ("M".equals(menu.getMenuType()) && menu.getPath() != null && !menu.getPath().isEmpty()) {
                // 排除隐藏的菜单
                if (!"1".equals(menu.getVisible())) {
                    result.add(menu);
                }
            }
            // 递归处理子菜单
            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                result.addAll(flattenMenus(menu.getChildren()));
            }
        }
        return result;
    }
}