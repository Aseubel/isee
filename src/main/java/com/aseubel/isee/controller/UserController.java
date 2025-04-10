package com.aseubel.isee.controller;

import com.aseubel.isee.common.Response;
import com.aseubel.isee.common.annotation.RequirePermission;
import com.aseubel.isee.pojo.dto.LoginRequest;
import com.aseubel.isee.pojo.entity.User;
import com.aseubel.isee.redis.IRedisService;
import com.aseubel.isee.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户相关接口
 * 
 * @author aseubel
 * @date 2025-03-29 10:16
 */
@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final IRedisService redissonService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public Response<String> login(@RequestBody LoginRequest loginRequest) throws Exception {
        return Response.success(userService.login(loginRequest.getUsername(), loginRequest.getPassword()));
    }

    /**
     * 创建用户
     */
    @PostMapping("/create")
    @RequirePermission(minAdminLevel = 1, checkArea = true)
    public Response<Void> createUser(@RequestBody User user) {
        userService.createUser(user);
        return Response.success();
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    @RequirePermission(minAdminLevel = 1)
    public Response<Void> updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return Response.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/delete/{userId}")
    @RequirePermission(minAdminLevel = 2)
    public Response<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return Response.success();
    }

    /**
     * 重置密码（设置新密码）
     */
    @PostMapping("/reset-password/{userId}")
    @RequirePermission(minAdminLevel = 0, allowSelf = true)
    public Response<Void> resetPassword(@PathVariable String userId, @RequestParam String newPassword) {
        userService.resetPassword(userId, newPassword);
        return Response.success();
    }

    /**
     * 获取区域用户列表
     */
    @GetMapping("/list/{areaId}")
    @RequirePermission(minAdminLevel = 1, checkArea = true)
    public Response<List<User>> getUsersByArea(@PathVariable Integer areaId) {
        return Response.success(userService.getUsersByArea(areaId));
    }

    /**
     * 分配区域
     */
    @PostMapping("/assign-area")
    @RequirePermission(minAdminLevel = 1, checkArea = true)
    public Response<Void> assignArea(@RequestParam String userId, @RequestParam Integer areaId) {
        userService.assignArea(userId, areaId);
        return Response.success();
    }

    /**
     * 获取用户个人信息
     */
    @GetMapping("/info")
    public Response<User> getUserInfo(@RequestHeader("Authorization") String token) {
        return Response.success(redissonService.getValue("token:" + token));
    }

    /**
     * 刷新token
     */
    @GetMapping("/token/refresh")
    public Response<String> refreshToken(@RequestHeader("Authorization") String token) throws Exception {
        return Response.success(userService.refreshToken(token));
    }
}
