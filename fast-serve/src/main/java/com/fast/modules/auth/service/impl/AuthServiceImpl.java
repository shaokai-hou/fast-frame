package com.fast.modules.auth.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.fast.common.constant.RedisKeyConstants;
import com.fast.common.exception.BusinessException;
import com.fast.modules.auth.domain.dto.*;
import com.fast.modules.auth.service.AuthService;
import com.fast.modules.log.domain.entity.LoginLog;
import com.fast.modules.log.service.LoginLogService;
import com.fast.modules.system.domain.dto.MenuVO;
import com.fast.modules.system.domain.dto.RoleVO;
import com.fast.modules.system.domain.entity.Menu;
import com.fast.modules.system.domain.entity.User;
import com.fast.modules.system.mapper.MenuMapper;
import com.fast.modules.system.service.ConfigService;
import com.fast.modules.system.service.MenuService;
import com.fast.modules.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 认证Service实现
 *
 * @author fast-frame
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final MenuService menuService;
    private final MenuMapper menuMapper;
    private final LoginLogService loginLogService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ConfigService configService;

    /**
     * 生成验证码
     *
     * @return 验证码信息 VO
     */
    @Override
    public CaptchaVO generateCaptcha() {
        // 生成验证码
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(120, 40, 4, 20);
        String uuid = IdUtil.fastSimpleUUID();
        String code = captcha.getCode();

        // 存入Redis，5分钟过期
        String key = RedisKeyConstants.CAPTCHA_CODE_PREFIX + uuid;
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);

        // 返回验证码信息
        CaptchaVO vo = new CaptchaVO();
        vo.setUuid(uuid);
        vo.setImg(captcha.getImageBase64Data());
        vo.setExpireTime(LocalDateTime.now().plusMinutes(5));
        return vo;
    }

    /**
     * 用户登录
     *
     * @param dto 登录参数 DTO
     * @param ip 用户 IP 地址
     * @return 登录信息 VO
     */
    @Override
    public LoginVO login(LoginDTO dto, String ip) {
        String lockKey = RedisKeyConstants.LOGIN_LOCK_PREFIX + dto.getUsername();
        if (redisTemplate.hasKey(lockKey)) {
            recordLoginLog(dto.getUsername(), ip, "1", "账户已锁定");
            throw new BusinessException("账户已锁定，请联系管理员解锁");
        }

        // 校验验证码
        String captchaKey = RedisKeyConstants.CAPTCHA_CODE_PREFIX + dto.getUuid();
        Object cacheCode = redisTemplate.opsForValue().get(captchaKey);
        if (cacheCode == null) {
            recordLoginLog(dto.getUsername(), ip, "1", "验证码已过期");
            throw new BusinessException("验证码已过期");
        }
        if (!cacheCode.toString().equalsIgnoreCase(dto.getCaptcha())) {
            recordLoginLog(dto.getUsername(), ip, "1", "验证码错误");
            throw new BusinessException("验证码错误");
        }
        redisTemplate.delete(captchaKey);

        // 校验用户
        User user = userService.getByUsername(dto.getUsername());

        if (user == null || !BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            // 增加失败计数
            incrementLoginFailCount(dto.getUsername());
            recordLoginLog(dto.getUsername(), ip, "1", "认证失败");
            int remaining = getRemainingAttempts(dto.getUsername());
            if (remaining > 0) {
                throw new BusinessException("用户名或密码错误，剩余" + remaining + "次机会");
            } else {
                throw new BusinessException("账户已锁定，请联系管理员解锁");
            }
        }
        if ("1".equals(user.getStatus())) {
            recordLoginLog(dto.getUsername(), ip, "1", "账号已被禁用");
            throw new BusinessException("账号已被禁用");
        }

        // 登录成功，清除失败计数
        clearLoginFailCount(dto.getUsername());

        // 登录
        StpUtil.login(user.getId());

        // 将 IP 地址存入 token session,用于在线用户显示
        StpUtil.getTokenSession()
                .set("ip", ip)
                .set("username", user.getUsername());

        // 记录登录日志
        recordLoginLog(dto.getUsername(), ip, "0", "登录成功");

        // 返回登录信息
        LoginVO vo = new LoginVO();
        vo.setAccessToken(StpUtil.getTokenValue());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        // avatar 存储格式: 2026/04/11/abc.jpg（不带桶名前缀）
        // 访问 URL: /api/file/avatar/2026/04/11/abc.jpg
        if (StrUtil.isNotBlank(user.getAvatar())) {
            vo.setAvatar("/api/file/avatar/" + user.getAvatar());
        }
        return vo;
    }

    /**
     * 增加登录失败计数
     * 达到阈值后锁定账户
     *
     * @param username 用户名
     */
    private void incrementLoginFailCount(String username) {
        String failKey = RedisKeyConstants.LOGIN_FAIL_PREFIX + username;
        Long failCount = redisTemplate.opsForValue().increment(failKey);

        if (failCount != null && failCount >= getMaxFailCount()) {
            // 永久锁定账户（需管理员手动解锁）
            redisTemplate.opsForValue().set(RedisKeyConstants.LOGIN_LOCK_PREFIX + username, "1");
            redisTemplate.delete(failKey);
        }
    }

    /**
     * 清除登录失败计数
     *
     * @param username 用户名
     */
    private void clearLoginFailCount(String username) {
        redisTemplate.delete(RedisKeyConstants.LOGIN_FAIL_PREFIX + username);
    }

    /**
     * 获取剩余尝试次数
     *
     * @param username 用户名
     * @return 剩余次数，已锁定返回 0
     */
    private int getRemainingAttempts(String username) {
        if (redisTemplate.hasKey(RedisKeyConstants.LOGIN_LOCK_PREFIX + username)) {
            return 0;
        }
        String failKey = RedisKeyConstants.LOGIN_FAIL_PREFIX + username;
        Object count = redisTemplate.opsForValue().get(failKey);
        int currentCount = count == null ? 0 : Integer.parseInt(count.toString());
        return getMaxFailCount() - currentCount;
    }

    /**
     * 获取登录失败锁定阈值
     *
     * @return 最大失败次数
     */
    private int getMaxFailCount() {
        String value = configService.getConfigValue("sys.login.maxFailCount");
        if (StrUtil.isBlank(value)) {
            throw new BusinessException("系统参数缺失：sys.login.maxFailCount");
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new BusinessException("系统参数配置异常：sys.login.maxFailCount=" + value);
        }
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息 VO
     */
    @Override
    public UserInfoVO getUserInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(userId);

        UserInfoVO vo = BeanUtil.copyProperties(user, UserInfoVO.class);

        // avatar 存储格式: 2026/04/11/abc.jpg
        // 访问 URL: /api/system/file/avatar/2026/04/11/abc.jpg
        if (StrUtil.isNotBlank(user.getAvatar())) {
            vo.setAvatar("/api/system/file/avatar/" + user.getAvatar());
        }

        // 获取角色列表
        List<RoleVO> roles = userService.listRolesByUserId(userId);
        vo.setRoles(roles);

        // 获取权限列表
        List<Menu> menus = menuMapper.selectMenusByUserId(userId);
        List<String> permissions = menus.stream()
                .map(Menu::getPerms)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
        vo.setPermissions(permissions);

        return vo;
    }

    /**
     * 获取当前用户路由菜单
     *
     * @return 路由信息 VO
     */
    @Override
    public RoutesVO getRoutes() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<MenuVO> menus = menuService.listMenusByUserId(userId);

        RoutesVO vo = new RoutesVO();
        vo.setRoutes(buildRoutes(menus));
        return vo;
    }

    /**
     * 用户登出
     */
    @Override
    public void logout() {
        StpUtil.logout();
    }

    /**
     * 记录登录日志
     *
     * @param username 用户名
     * @param ip IP 地址
     * @param status 登录状态（0: 成功, 1: 失败）
     * @param msg 登录消息
     */
    private void recordLoginLog(String username, String ip, String status, String msg) {
        LoginLog log = new LoginLog();
        log.setUsername(username);
        log.setIpAddress(ip);
        log.setStatus(status);
        log.setMsg(msg);
        log.setLoginTime(LocalDateTime.now());
        loginLogService.save(log);
    }

    /**
     * 构建路由
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    private List<RoutesVO.RouteItem> buildRoutes(List<MenuVO> menus) {
        return menus.stream()
                .filter(menu -> !"B".equals(menu.getMenuType()))
                .map(menu -> {
                    RoutesVO.RouteItem item = new RoutesVO.RouteItem();
                    item.setPath(menu.getPath());
                    item.setName(menu.getMenuName());
                    item.setComponent(menu.getComponent());

                    RoutesVO.RouteMeta meta = new RoutesVO.RouteMeta();
                    meta.setTitle(menu.getMenuName());
                    meta.setIcon(menu.getIcon());
                    meta.setHidden("1".equals(menu.getVisible()));
                    meta.setLink(menu.getLink());
                    item.setMeta(meta);

                    if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                        item.setChildren(buildRoutes(menu.getChildren()));
                    }
                    return item;
                })
                .collect(Collectors.toList());
    }
}