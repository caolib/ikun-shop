package io.github.caolib.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.caolib.domain.PageDTO;
import io.github.caolib.domain.PageQuery;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.CommodityDTO;
import io.github.caolib.domain.dto.OrderDetailDTO;
import io.github.caolib.domain.po.Commodity;
import io.github.caolib.service.ICommodityService;
import io.github.caolib.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品管理相关接口
 */
@RestController
@RequestMapping("/commodity")
@RequiredArgsConstructor
public class CommodityController {

    private final ICommodityService commodityService;

    /**
     * 分页查询商品
     * @param query 分页查询条件
     * @return 分页结果
     */
    @GetMapping("/page")
    public PageDTO<CommodityDTO> queryItemByPage(PageQuery query) {
        // 1.分页查询
        Page<Commodity> result = commodityService.page(query.toMpPage("update_time", false));
        // 2.封装并返回
        return PageDTO.of(result, CommodityDTO.class);
    }

    /**
     * 根据id批量查询商品
     * @param ids 商品id集合
     * @return 商品列表
     */
    @GetMapping
    public List<CommodityDTO> queryItemByIds(@RequestParam("ids") List<Long> ids) {
        return commodityService.queryItemByIds(ids);
    }

    /**
     * 根据id查询商品
     * @param id 商品id
     * @return 商品信息
     */
    @GetMapping("{id}")
    public CommodityDTO queryItemById(@PathVariable("id") Long id) {
        return BeanUtils.copyBean(commodityService.getById(id), CommodityDTO.class);
    }

    /**
     * 新增商品
     * @param item 商品信息
     */
    @PostMapping
    public void saveItem(@RequestBody CommodityDTO item) {
        // 新增
        commodityService.save(BeanUtils.copyBean(item, Commodity.class));
    }

    /**
     * 更新商品状态
     * @param id 商品id
     * @param status 商品状态
     */
    @PutMapping("/status/{id}/{status}")
    public void updateItemStatus(@PathVariable("id") Long id, @PathVariable("status") Integer status) {
        Commodity item = new Commodity();
        item.setId(id);
        item.setStatus(status);
        commodityService.updateById(item);
    }

    /**
     * 更新商品
     * @param item 商品信息
     */
    @PutMapping
    public void updateItem(@RequestBody CommodityDTO item) {
        // 不允许修改商品状态，所以强制设置为null，更新时，就会忽略该字段
        item.setStatus(null);
        // 更新
        commodityService.updateById(BeanUtils.copyBean(item, Commodity.class));
    }

    /**
     * 根据id删除商品
     * @param id 商品id
     */
    @DeleteMapping("{id}")
    public void deleteItemById(@PathVariable("id") Long id) {
        commodityService.removeById(id);
    }

    /**
     * 批量扣减库存
     * @param items 订单详情列表
     */
    @PutMapping("/stock/deduct")
    public R<Void> deductStock(@RequestBody List<OrderDetailDTO> items) {
       return commodityService.deductStock(items);
    }

    @PutMapping("/release")
    public void releaseStock(@RequestBody List<OrderDetailDTO> dtos) {
        commodityService.releaseStock(dtos);
    }
}