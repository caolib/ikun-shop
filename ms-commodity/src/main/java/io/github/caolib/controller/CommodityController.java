package io.github.caolib.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.caolib.domain.PageDTO;
import io.github.caolib.domain.PageQuery;
import io.github.caolib.domain.dto.CommodityDTO;
import io.github.caolib.domain.dto.OrderDetailDTO;
import io.github.caolib.domain.po.Commodity;
import io.github.caolib.service.ICommodityService;
import io.github.caolib.utils.BeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品管理相关接口")
@RestController
@RequestMapping("/commodity")
@RequiredArgsConstructor
public class CommodityController {

    private final ICommodityService itemService;

    @ApiOperation("分页查询商品")
    @GetMapping("/page")
    public PageDTO<CommodityDTO> queryItemByPage(PageQuery query) {
        // 1.分页查询
        Page<Commodity> result = itemService.page(query.toMpPage("update_time", false));
        // 2.封装并返回
        return PageDTO.of(result, CommodityDTO.class);
    }

    @ApiOperation("根据id批量查询商品")
    @GetMapping
    public List<CommodityDTO> queryItemByIds(@RequestParam("ids") List<Long> ids) {
        return itemService.queryItemByIds(ids);
    }

    @ApiOperation("根据id查询商品")
    @GetMapping("{id}")
    public CommodityDTO queryItemById(@PathVariable("id") Long id) {
        return BeanUtils.copyBean(itemService.getById(id), CommodityDTO.class);
    }

    @ApiOperation("新增商品")
    @PostMapping
    public void saveItem(@RequestBody CommodityDTO item) {
        // 新增
        itemService.save(BeanUtils.copyBean(item, Commodity.class));
    }

    @ApiOperation("更新商品状态")
    @PutMapping("/status/{id}/{status}")
    public void updateItemStatus(@PathVariable("id") Long id, @PathVariable("status") Integer status) {
        Commodity item = new Commodity();
        item.setId(id);
        item.setStatus(status);
        itemService.updateById(item);
    }

    @ApiOperation("更新商品")
    @PutMapping
    public void updateItem(@RequestBody CommodityDTO item) {
        // 不允许修改商品状态，所以强制设置为null，更新时，就会忽略该字段
        item.setStatus(null);
        // 更新
        itemService.updateById(BeanUtils.copyBean(item, Commodity.class));
    }

    @ApiOperation("根据id删除商品")
    @DeleteMapping("{id}")
    public void deleteItemById(@PathVariable("id") Long id) {
        itemService.removeById(id);
    }

    @ApiOperation("批量扣减库存")
    @PutMapping("/stock/deduct")
    public void deductStock(@RequestBody List<OrderDetailDTO> items) {
        itemService.deductStock(items);
    }
}
