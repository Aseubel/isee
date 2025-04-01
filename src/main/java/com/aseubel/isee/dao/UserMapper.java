package com.aseubel.isee.dao;

import com.aseubel.isee.pojo.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Aseubel
 * @description 用户数据库操作接口
 * @date 2025-03-29 10:17
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    /**
     * 根据地区ID查询用户列表
     */
    @Select("SELECT * FROM user WHERE area_id = #{areaId}")
    List<User> findByAreaId(@Param("areaId") Integer areaId);

    /**
     * 查询指定权限级别的用户
     */
    @Select("SELECT * FROM user WHERE admin = #{adminLevel}")
    List<User> findByAdminLevel(@Param("adminLevel") Integer adminLevel);

    /**
     * 根据用户ID查询用户
     */
    @Select("SELECT * FROM user WHERE user_id = #{userId}")
    User findByUserId(@Param("userId") String userId);
}
