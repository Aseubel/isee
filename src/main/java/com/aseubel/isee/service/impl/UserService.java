package com.aseubel.isee.service.impl;

import com.aseubel.isee.dao.UserMapper;
import com.aseubel.isee.pojo.entity.User;
import com.aseubel.isee.redis.IRedisService;
import com.aseubel.isee.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.crypto.digest.BCrypt;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * @author Aseubel
 * @description UserService
 * @date 2025-03-29 10:20
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService extends ServiceImpl<UserMapper, User> implements IUserService {

    private final UserMapper userMapper;
    private final IRedisService redissonService;

    @Override
    public String login(String username, String password) throws Exception {
        User user = userMapper.findByUsername(username);

        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            throw new AuthException("用户名或密码错误");
        }

        if (!user.getIsEnabled()) {
            throw new AuthException("账号已被禁用");
        }

        // 生成token并存入Redis
        String token = UUID.randomUUID().toString();
        redissonService.setValue("token:" + token, user, 3600 * 1000); // 1小时过期

        return token;
    }

    @Override
    public String refreshToken(String token) throws Exception{
        User user = redissonService.getValue("token:" + token);
        if (user == null) {
            log.warn("未登录或token已过期");
            throw new AuthException("未登录或token已过期");
        }
        String newToken = UUID.randomUUID().toString();
        redissonService.setValue("token:" + newToken, user, 3600 * 1000); // 1小时过期

        return newToken;
    }

    @Override
    public void createUser(User user) {
        // 检查用户名是否已存在
        if (getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername())) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 加密密码
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        user.setUserId(UUID.randomUUID().toString());
        user.setIsEnabled(true);

        save(user);
    }

    @Override
    public void updateUser(User user) {
        User existingUser = getById(user.getId());
        if (existingUser == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 如果修改了用户名，检查是否与其他用户重复
        if (!existingUser.getUsername().equals(user.getUsername()) &&
                getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername())) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }

        updateById(user);
    }

    @Override
    public void deleteUser(String userId) {
        remove(new LambdaQueryWrapper<User>().eq(User::getUserId, userId));
    }

    @Override
    public void resetPassword(String userId, String newPassword) {
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUserId, userId));
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        updateById(user);
    }

    @Override
    public List<User> getUsersByArea(Integer areaId) {
        return list(new LambdaQueryWrapper<User>().eq(User::getAreaId, areaId));
    }

    @Override
    public void assignArea(String userId, Integer areaId) {
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUserId, userId));
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        user.setAreaId(areaId);
        updateById(user);
    }
}
