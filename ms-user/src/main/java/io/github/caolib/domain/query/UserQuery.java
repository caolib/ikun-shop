package io.github.caolib.domain.query;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UserQuery implements Serializable {
    @Min(value = 1, message = "页码不能小于1")
    @NotNull(message = "页码不能为空")
    private Integer pageNo; // 页码

    @Min(value = 1, message = "每页查询数量不能小于1")
    @NotNull(message = "每页查询数量不能为空")
    private Integer pageSize; // 每页大小

    private Long id;
    private String username;
    private String phone;
    private Integer status;

    public <T> Page<T> toPage() {
        return new Page<>(this.pageNo, this.pageSize);
    }
}