package com.aseubel.isee.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    /**
     * 所需的最小管理员级别
     * 0-普通用户, 1-地区管理员, 2-超级管理员
     */
    int minAdminLevel() default 0;

    /**
     * 是否需要验证地区权限
     */
    boolean checkArea() default false;

    /**
     * 是否允许用户本人操作
     */
    boolean allowSelf() default false;
}