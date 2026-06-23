package com.example.dockb.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法级权限注解：声明接口所需的最低角色。
 *
 * <p>示例：
 * <pre>
 * &#64;RequireRole(Role.ADMIN)
 * &#64;GetMapping("/admin/users")
 * public Result<?> listUsers(...) { ... }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {

    /** 所需最低角色。默认为 USER（登录即可）。 */
    Role value() default Role.USER;

    enum Role {
        /** 匿名可访问（不校验）。 */
        ANONYMOUS,
        /** 必须登录。 */
        USER,
        /** 必须管理员。 */
        ADMIN
    }
}
