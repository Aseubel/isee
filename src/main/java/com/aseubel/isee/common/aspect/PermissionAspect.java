package com.aseubel.isee.common.aspect;

import com.aseubel.isee.common.Response;
import com.aseubel.isee.common.annotation.RequirePermission;
import com.aseubel.isee.pojo.entity.User;
import com.aseubel.isee.redis.IRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final IRedisService redissonService;

    @Around("@annotation(com.aseubel.isee.common.annotation.RequirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("未登录或token已过期");
        }

        // 从Redis获取用户信息
        User user = redissonService.getValue("token:" + token);
        if (user == null) {
            log.warn("未登录或token已过期");
            return Response.authFail("未登录或token已过期");
        }

        // 获取注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RequirePermission permission = signature.getMethod().getAnnotation(RequirePermission.class);

        // 检查管理员级别
        if (user.getAdmin() < permission.minAdminLevel()) {
            log.warn("用户" + user.getUsername() + "无权限访问");
            return Response.authFail("无权限访问");
        }

        // 检查地区权限
        if (permission.checkArea()) {
            Integer requestAreaId = null;
            // 从请求参数中获取地区ID
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof Integer) {
                    requestAreaId = (Integer) arg;
                    break;
                } else if (arg instanceof User) {
                    requestAreaId = ((User) arg).getAreaId();
                    break;
                }
            }

            if (requestAreaId != null && !user.getAdmin().equals(2) && !user.getAreaId().equals(requestAreaId)) {
                log.warn("用户" + user.getUsername() + "无权限访问该地区数据");
                return Response.authFail("无权限访问该地区数据");
            }
        }

        if (permission.allowSelf() && user.getAdmin().equals(0) && !user.getUserId().equals(request.getParameter("userId"))) {
            log.warn("用户" + user.getUsername() + "无权限访问该用户数据");
            return Response.authFail("无权限访问该用户数据");
        }

        return joinPoint.proceed();
    }
}