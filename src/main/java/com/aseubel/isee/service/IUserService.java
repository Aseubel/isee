package com.aseubel.isee.service;

import com.aseubel.isee.pojo.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Aseubel
 * @description 用户服务接口
 * @date 2025-03-29 10:20
 */
public interface IUserService extends IService<User> {
    /**
     * 用户登录
     * 
     * @param username 用户名
     * @param password 密码
     * @return token
     */
    String login(String username, String password) throws Exception;

    /**
     * 刷新token
     *
     * @param token token
     * @return 新token
     */
    String refreshToken(String token) throws Exception;

    /**
     * 创建用户
     * 
     * @param user 用户信息
     */
    void createUser(User user);

    /**
     * 更新用户信息
     * 
     * @param user 用户信息
     */
    void updateUser(User user);

    /**
     * 删除用户
     * 
     * @param userId 用户ID
     */
    void deleteUser(String userId);

    /**
     * 重置用户密码
     * 
     * @param userId      用户ID
     * @param newPassword 新密码
     */
    void resetPassword(String userId, String newPassword);

    /**
     * 获取指定地区的用户列表
     * 
     * @param areaId 地区ID
     * @return 用户列表
     */
    List<User> getUsersByArea(Integer areaId);

    /**
     * 分配地区给用户
     * 
     * @param userId 用户ID
     * @param areaId 地区ID
     */
    void assignArea(String userId, Integer areaId);
}
