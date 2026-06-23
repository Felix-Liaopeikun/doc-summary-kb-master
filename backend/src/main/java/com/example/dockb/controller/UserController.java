package com.example.dockb.controller;

import com.example.dockb.annotation.RequireRole;
import com.example.dockb.common.Result;
import com.example.dockb.entity.User;
import com.example.dockb.mapper.UserMapper;
import com.example.dockb.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理接口（仅 ADMIN 可访问）。
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/users")
public class UserController {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final UserMapper userMapper;

    public UserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 列出所有用户（不含密码）。
     */
    @RequireRole(RequireRole.Role.ADMIN)
    @GetMapping
    public Result<List<UserVO>> list() {
        List<User> users = userMapper.selectList(
                new LambdaQueryWrapper<User>().orderByDesc(User::getCreatedAt));
        List<UserVO> voList = users.stream().map(this::toVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    /**
     * 更新用户角色。
     * @param id  用户 ID
     * @param role 新角色（USER / ADMIN）
     */
    @RequireRole(RequireRole.Role.ADMIN)
    @PutMapping("/{id}/role")
    public Result<?> updateRole(@PathVariable Long id,
                                @RequestParam String role) {
        if (!"USER".equals(role) && !"ADMIN".equals(role)) {
            return Result.fail(400, "角色只能是 USER 或 ADMIN");
        }
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.fail(404, "用户不存在");
        }
        user.setRole(role);
        userMapper.updateById(user);
        log.info("[UserCtrl] updated user {} role to {}", user.getUsername(), role);
        return Result.success(toVO(user));
    }

    private UserVO toVO(User u) {
        UserVO vo = new UserVO();
        vo.setId(u.getId());
        vo.setUsername(u.getUsername());
        vo.setRole(u.getRole());
        vo.setCreatedAt(u.getCreatedAt() == null ? "" : u.getCreatedAt().format(FMT));
        return vo;
    }
}
