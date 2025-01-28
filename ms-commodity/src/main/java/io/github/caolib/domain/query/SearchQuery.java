package io.github.caolib.domain.query;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;

@Data
public class SearchQuery implements Serializable {
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNo; // 页码
    @Min(value = 1, message = "每页查询数量不能小于1")
    private Integer pageSize; // 每页大小
    private Boolean isAsc = true; // 是否升序
    private String sortBy; // 排序字段

    private String key; // 搜索关键字
    private String category; // 商品分类
    private String brand; // 商品品牌
    private Integer minPrice; // 价格最小值
    private Integer maxPrice; // 价格最大值

    // 将当前对象转换为MP的Page对象, 并设置默认排序字段和排序方式
    public <T> Page<T> toMpPage(String defaultSortBy, boolean isAsc) {
        if (StringUtils.isBlank(sortBy)) {
            sortBy = defaultSortBy;
            this.isAsc = isAsc;
        }
        Page<T> page = new Page<>(pageNo, pageSize);
        OrderItem orderItem = new OrderItem();
        orderItem.setAsc(this.isAsc);
        orderItem.setColumn(sortBy);
        page.addOrder(orderItem);
        return page;
    }
}
