package io.github.caolib.controller.admin;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.UserDTO;
import io.github.caolib.domain.po.User;
import io.github.caolib.domain.query.UserQuery;
import io.github.caolib.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/users/manage")
@RequiredArgsConstructor
public class UserManageController {

    private final IUserService userService;

    /**
     * 获取用户列表-分页查询
     *
     * @param query 查询条件
     */
    @GetMapping
    public R<Page<User>> getUsers(UserQuery query) {
        return userService.getUsers(query);
    }


    /**
     * 冻结用户账号
     *
     * @param id 用户id
     */
    @PutMapping("/freeze/{id}")
    public R<Void> freezeUser(@PathVariable Long id) {
        return userService.freezeUser(id);
    }

    /**
     * 解冻用户账号
     *
     * @param id 用户id
     */
    @PutMapping("/recover/{id}")
    public R<Void> unfreezeUser(@PathVariable Long id) {
        return userService.recoverUser(id);
    }

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     */
    @PutMapping
    public void updateUser(@RequestBody UserDTO user) {
        userService.updateUser(user);
    }

}
