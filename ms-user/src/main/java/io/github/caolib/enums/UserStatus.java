package io.github.caolib.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.github.caolib.exception.BadRequestException;
import lombok.Getter;

@Getter
public enum UserStatus {
    FROZEN(0, "禁止使用"),
    NORMAL(1, "已激活"),
    ;
    @EnumValue
    final int value;
    final String desc;

    UserStatus(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static UserStatus of(int value) {
        if (value == 0)
            return FROZEN;
        if (value == 1)
            return NORMAL;
        throw new BadRequestException("账户状态错误");
    }
}