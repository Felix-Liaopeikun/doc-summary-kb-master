package com.example.dockb.interceptor;

import com.example.dockb.annotation.RequireRole;
import com.example.dockb.config.JwtAuthFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 权限拦截器：配合 {@link RequireRole} 注解，统一校验接口权限。
 *
 * <p>拦截规则：
 * <ul>
 *   <li>方法未标注 @RequireRole → 直接放行（向后兼容）</li>
 *   <li>ANONYMOUS → 直接放行</li>
 *   <li>USER → 未登录返回 401</li>
 *   <li>ADMIN → 非管理员返回 403</li>
 * </ul>
 */
@Slf4j
public class AuthorizationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        if (!(handler instanceof HandlerMethod method)) {
            return true;
        }

        RequireRole requireRole = method.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            return true; // 未标注，向后兼容
        }

        RequireRole.Role required = requireRole.value();
        if (required == RequireRole.Role.ANONYMOUS) {
            return true;
        }

        String role = (String) request.getAttribute(JwtAuthFilter.ATTR_ROLE);
        boolean loggedIn = request.getAttribute(JwtAuthFilter.ATTR_USER_ID) != null;

        if (required == RequireRole.Role.USER && !loggedIn) {
            writeJson(response, 401, "请先登录", "请先登录后访问");
            return false;
        }

        if (required == RequireRole.Role.ADMIN && !"ADMIN".equals(role)) {
            writeJson(response, 403, "权限不足", "需要管理员权限");
            return false;
        }

        return true;
    }

    private void writeJson(HttpServletResponse response, int code, String message, String userTip) {
        response.setStatus(200); // 统一 200，通过 code 区分
        response.setContentType("application/json;charset=UTF-8");
        try {
            String json = String.format(
                    "{\"code\":%d,\"message\":\"%s\",\"data\":null}",
                    code, escapeJson(message));
            response.getWriter().write(json);
            response.getWriter().flush();
        } catch (Exception e) {
            log.error("[AuthInterceptor] write response failed", e);
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
