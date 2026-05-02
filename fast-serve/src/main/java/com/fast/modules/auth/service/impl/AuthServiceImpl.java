package com.fast.modules.auth.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.fast.common.constant.ConfigConstants;
import com.fast.common.constant.Constants;
import com.fast.common.constant.RedisConstants;
import com.fast.common.exception.BusinessException;
import com.fast.common.util.IpUtils;
import com.fast.framework.helper.AdminHelper;
import com.fast.framework.helper.ConfigHelper;
import com.fast.framework.helper.RedisHelper;
import com.fast.modules.auth.domain.dto.LoginDTO;
import com.fast.modules.auth.domain.vo.LoginVO;
import com.fast.modules.auth.domain.vo.RoutesVO;
import com.fast.modules.auth.domain.vo.UserInfoVO;
import com.fast.modules.auth.service.AuthService;
import com.fast.modules.log.domain.entity.LoginLog;
import com.fast.modules.log.service.LoginLogService;
import com.fast.modules.system.domain.entity.Menu;
import com.fast.modules.system.domain.entity.User;
import com.fast.modules.system.domain.vo.MenuVO;
import com.fast.modules.system.domain.vo.RoleVO;
import com.fast.modules.system.mapper.MenuMapper;
import com.fast.modules.system.service.MenuService;
import com.fast.modules.system.service.UserService;
import com.xingyuv.captcha.model.common.ResponseModel;
import com.xingyuv.captcha.model.vo.CaptchaVO;
import com.xingyuv.captcha.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
    private final CaptchaService captchaService;

    /**
     * 用户登录
     *
     * @param dto 登录参数 DTO
     * @param request HTTP请求
     * @return 登录信息 VO
     */
    @Override
    public LoginVO login(LoginDTO dto, HttpServletRequest request) {

        String ip = IpUtils.getClientIp(request);
        String username = dto.getUsername();

        // 检查账户是否已锁定
        Long lockRemaining = getLockRemainingTime(username);
        if (lockRemaining != null) {
            recordLoginLog(username, ip, Constants.DISABLE, "账户已锁定");
            throw new BusinessException("账户已锁定，" + DateUtil.formatBetween(lockRemaining * 1000) + "后自动解锁，或联系管理员");
        }

        // 校验滑块验证码
        CaptchaVO captchaVo = new CaptchaVO();
        captchaVo.setCaptchaVerification(dto.getCaptchaVerification());
        captchaVo.setBrowserInfo(IpUtils.getBrowserInfo(request));
        ResponseModel response = captchaService.verification(captchaVo);
        if (!response.isSuccess()) {
            recordLoginLog(username, ip, Constants.DISABLE, "滑块验证码错误");
            throw new BusinessException("滑块验证码验证失败");
        }

        // 校验用户
        User user = userService.getByUsername(username);

        // 用户不存在
        if (Objects.isNull(user)) {
            recordLoginLog(username, ip, Constants.DISABLE, "用户不存在");
            throw new BusinessException("用户名或密码错误");
        }

        // 检查账号禁用状态
        if (Constants.DISABLE.equals(user.getStatus())) {
            recordLoginLog(username, ip, Constants.DISABLE, "账号已被禁用");
            throw new BusinessException("账号已被禁用");
        }

        // 校验密码
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            int remaining = handleLoginFail(username);
            recordLoginLog(username, ip, Constants.DISABLE, "密码错误");
            if (remaining == 0) {
                throw new BusinessException("密码错误次数过多，账户已锁定" + (getLockDuration() / 60) + "分钟");
            }
            throw new BusinessException("用户名或密码错误，剩余" + remaining + "次机会");
        }

        // 登录成功，清除失败计数
        clearLoginFailCount(username);

        // 登录
        StpUtil.login(user.getId());

        // 将 IP 地址存入 token session（同一用户不同设备登录 IP 可能不同）
        StpUtil.getTokenSession()
                .set("ip", ip)
                .set("username", user.getUsername());

        // 记录登录日志
        recordLoginLog(username, ip, Constants.NORMAL, "登录成功");

        // 返回登录信息
        return buildLoginVO(user);
    }

    /**
     * 构建登录返回信息
     *
     * @param user 登录用户
     * @return 登录信息 VO
     */
    private LoginVO buildLoginVO(User user) {
        LoginVO vo = new LoginVO();
        vo.setAccessToken(StpUtil.getTokenValue());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        if (StrUtil.isNotBlank(user.getAvatar())) {
            vo.setAvatar("/api/file/avatar/" + user.getAvatar());
        }
        return vo;
    }

    /**
     * 获取锁定时长配置（秒）
     */
    private int getLockDuration() {
        return ConfigHelper.getIntValue(ConfigConstants.LOGIN_LOCK_DURATION, 1800);
    }

    /**
     * 获取最大失败次数配置
     */
    private int getMaxFailCount() {
        return ConfigHelper.getIntValue(ConfigConstants.LOGIN_MAX_FAIL_COUNT, 5);
    }

    /**
     * 获取账户锁定剩余时间（秒）
     *
     * @param username 用户名
     * @return 锁定剩余时间，未锁定返回null
     */
    private Long getLockRemainingTime(String username) {
        String lockKey = RedisConstants.LOGIN_LOCK_PREFIX + username;
        if (RedisHelper.hasKey(lockKey)) {
            return RedisHelper.getExpire(lockKey);
        }
        return null;
    }

    /**
     * 处理登录失败
     * 增加失败计数，触发锁定
     *
     * @param username 用户名
     * @return 剩余尝试次数，0表示已锁定
     */
    private int handleLoginFail(String username) {
        String failKey = RedisConstants.LOGIN_FAIL_PREFIX + username;
        long failCount = RedisHelper.incr(failKey);

        int lockDuration = getLockDuration();
        int maxFailCount = getMaxFailCount();

        // 设置失败计数key过期时间（与锁定时长一致）
        if (failCount == 1) {
            RedisHelper.expire(failKey, lockDuration);
        }

        // 达到阈值，触发锁定
        if (failCount >= maxFailCount) {
            String lockKey = RedisConstants.LOGIN_LOCK_PREFIX + username;
            RedisHelper.set(lockKey, "1", lockDuration);
            RedisHelper.delete(failKey);
            return 0;
        }

        return maxFailCount - (int) failCount;
    }

    /**
     * 清除登录失败计数
     */
    private void clearLoginFailCount(String username) {
        RedisHelper.delete(RedisConstants.LOGIN_FAIL_PREFIX + username);
    }

    @Override
    public UserInfoVO getUserInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(userId);
        UserInfoVO vo = BeanUtil.copyProperties(user, UserInfoVO.class);
        if (StrUtil.isNotBlank(user.getAvatar())) {
            vo.setAvatar("/api/system/file/avatar/" + user.getAvatar());
        }
        List<RoleVO> roles = userService.listRolesByUserId(userId);
        vo.setRoles(roles);

        // admin 用户返回上帝权限
        if (AdminHelper.isAdmin(userId)) {
            vo.setPermissions(Collections.singletonList(AdminHelper.getSuperPermission()));
        } else {
            List<Menu> menus = menuMapper.selectMenusByUserId(userId);
            List<String> permissions = menus.stream()
                    .map(Menu::getPerms)
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.toList());
            vo.setPermissions(permissions);
        }
        return vo;
    }

    @Override
    public RoutesVO getRoutes() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<MenuVO> menus = menuService.listMenusByUserId(userId);
        RoutesVO vo = new RoutesVO();
        vo.setRoutes(buildRoutes(menus));
        return vo;
    }

    private void recordLoginLog(String username, String ip, String status, String msg) {
        LoginLog log = new LoginLog();
        log.setUsername(username);
        log.setIpAddress(ip);
        log.setStatus(status);
        log.setMsg(msg);
        log.setLoginTime(LocalDateTime.now());
        loginLogService.save(log);
    }

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