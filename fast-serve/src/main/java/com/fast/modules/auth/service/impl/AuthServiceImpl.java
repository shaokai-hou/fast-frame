package com.fast.modules.auth.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.fast.common.constant.ConfigConstants;
import com.fast.common.constant.Constants;
import com.fast.common.constant.RedisConstants;
import com.fast.common.exception.BusinessException;
import com.fast.common.util.IpUtils;
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
     * 获取客户端浏览器信息
     *
     * @param request HTTP请求
     * @return 浏览器信息(ip+ua)
     */
    private String getBrowserInfo(HttpServletRequest request) {
        String xfwd = request.getHeader("X-Forwarded-For");
        String ip = null;
        if (StrUtil.isNotBlank(xfwd)) {
            ip = xfwd.split(",")[0].trim();
        }
        if (ip == null) {
            ip = request.getRemoteHost();
        }
        String ua = request.getHeader("user-agent");
        return ip + ua;
    }

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

        String lockKey = RedisConstants.LOGIN_LOCK_PREFIX + dto.getUsername();
        if (RedisHelper.hasKey(lockKey)) {
            recordLoginLog(dto.getUsername(), ip, Constants.DISABLE, "账户已锁定");
            throw new BusinessException("账户已锁定，请联系管理员解锁");
        }

        // 校验滑块验证码
        boolean captchaEnabled = ConfigHelper.getBooleanValue(ConfigConstants.CAPTCHA_ENABLED, true);
        if (captchaEnabled) {
            CaptchaVO captchaVo = new CaptchaVO();
            captchaVo.setCaptchaVerification(dto.getCaptchaVerification());
            captchaVo.setBrowserInfo(getBrowserInfo(request));
            ResponseModel response = captchaService.verification(captchaVo);
            if (!response.isSuccess()) {
                recordLoginLog(dto.getUsername(), ip, Constants.DISABLE, "滑块验证码错误");
                throw new BusinessException("滑块验证码验证失败");
            }
        }

        // 校验用户
        User user = userService.getByUsername(dto.getUsername());

        // 用户不存在
        if (Objects.isNull(user)) {
            recordLoginLog(dto.getUsername(), ip, Constants.DISABLE, "用户不存在");
            throw new BusinessException("用户名或密码错误");
        }

        // 检查账号禁用状态
        if (Constants.DISABLE.equals(user.getStatus())) {
            recordLoginLog(dto.getUsername(), ip, Constants.DISABLE, "账号已被禁用");
            throw new BusinessException("账号已被禁用");
        }

        // 校验密码（密码错误才增加失败计数）
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            incrementLoginFailCount(dto.getUsername());
            recordLoginLog(dto.getUsername(), ip, Constants.DISABLE, "密码错误");
            int remaining = getRemainingAttempts(dto.getUsername());
            if (remaining > 0) {
                throw new BusinessException("用户名或密码错误，剩余" + remaining + "次机会");
            } else {
                throw new BusinessException("账户已锁定，请联系管理员解锁");
            }
        }

        // 登录成功，清除失败计数
        clearLoginFailCount(dto.getUsername());

        // 登录
        StpUtil.login(user.getId());

        // 将 IP 地址存入 token session
        StpUtil.getTokenSession()
                .set("ip", ip)
                .set("username", user.getUsername());

        // 记录登录日志
        recordLoginLog(dto.getUsername(), ip, Constants.NORMAL, "登录成功");

        // 返回登录信息
        LoginVO vo = new LoginVO();
        vo.setAccessToken(StpUtil.getTokenValue());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        if (StrUtil.isNotBlank(user.getAvatar())) {
            vo.setAvatar("/api/file/avatar/" + user.getAvatar());
        }
        return vo;
    }

    private void incrementLoginFailCount(String username) {
        String failKey = RedisConstants.LOGIN_FAIL_PREFIX + username;
        long failCount = RedisHelper.incr(failKey);
        // 设置失败计数key过期时间（30分钟）
        if (failCount == 1) {
            RedisHelper.expire(failKey, 1800);
        }
        if (failCount >= getMaxFailCount()) {
            // 设置锁定key（30分钟自动解锁）
            String lockKey = RedisConstants.LOGIN_LOCK_PREFIX + username;
            RedisHelper.set(lockKey, "1", 1800);
            RedisHelper.delete(failKey);
        }
    }

    private void clearLoginFailCount(String username) {
        RedisHelper.delete(RedisConstants.LOGIN_FAIL_PREFIX + username);
    }

    private int getRemainingAttempts(String username) {
        if (RedisHelper.hasKey(RedisConstants.LOGIN_LOCK_PREFIX + username)) {
            return 0;
        }
        String failKey = RedisConstants.LOGIN_FAIL_PREFIX + username;
        String count = RedisHelper.get(failKey);
        int currentCount = count == null ? 0 : Integer.parseInt(count);
        return getMaxFailCount() - currentCount;
    }

    private int getMaxFailCount() {
        return ConfigHelper.getIntValue(ConfigConstants.LOGIN_MAX_FAIL_COUNT, 5);
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
        List<Menu> menus = menuMapper.selectMenusByUserId(userId);
        List<String> permissions = menus.stream()
                .map(Menu::getPerms)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
        vo.setPermissions(permissions);
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