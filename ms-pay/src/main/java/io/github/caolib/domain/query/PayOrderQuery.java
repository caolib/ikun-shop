package io.github.caolib.domain.query;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class PayOrderQuery implements Serializable {
    @Min(value = 1, message = "页码不能小于1")
    @NotNull(message = "页码不能为空")
    private Integer pageNo; // 页码

    @Min(value = 1, message = "每页查询数量不能小于1")
    @NotNull(message = "每页查询数量不能为空")
    private Integer pageSize; // 每页大小

    private Long orderId;
    private Long payOrderId;
    private Long userId;
    private LocalDateTime createTime;
    private int isDeleted;


    public <T> Page<T> toPage() {
        return new Page<>(this.pageNo, this.pageSize);
    }
}