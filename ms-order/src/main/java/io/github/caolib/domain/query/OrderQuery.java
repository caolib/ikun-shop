package io.github.caolib.domain.query;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class OrderQuery {
    @Min(value = 1, message = "页码不能小于1")
    @NotNull(message = "页码不能为空")
    private Integer pageNo; // 页码

    @Min(value = 1, message = "每页查询数量不能小于1")
    @NotNull(message = "每页查询数量不能为空")
    private Integer pageSize; // 每页大小

    private Long id;
    private Integer status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createStartTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createEndTime;

    public <T> Page<T> toPage() {
        return new Page<>(this.pageNo, this.pageSize);
    }
}
