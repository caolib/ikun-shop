package io.github.caolib.controller;

import io.github.caolib.domain.PageDTO;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.CommodityDTO;
import io.github.caolib.domain.dto.OrderDetailDTO;
import io.github.caolib.domain.po.Commodity;
import io.github.caolib.domain.query.SearchQuery;
import io.github.caolib.service.ICommodityService;
import io.github.caolib.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品
 */
@Slf4j
@RestController
@RequestMapping("/commodity")
@RequiredArgsConstructor
public class CommodityController {

    private final ICommodityService commodityService;


    /**
     * 获取主页商品列表
     */
    @GetMapping("/home")
    public PageDTO<CommodityDTO> getHomeCommodity() {
        SearchQuery query = new SearchQuery();
        query.setPageNo(1);
        query.setPageSize(24);

        return commodityService.homePage(query);
    }


    /**
     * 搜索商品
     *
     * @param query 商品分页查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    public PageDTO<CommodityDTO> search(@Validated SearchQuery query) {
        log.debug("搜索条件: {}", query);
        return commodityService.pageQuery(query);
    }

    /**
     * 根据id批量查询商品
     *
     * @param ids 商品id集合
     * @return 商品列表
     */
    @GetMapping
    public List<CommodityDTO> getItemByIds(@RequestParam("ids") List<Long> ids) {
        return commodityService.getItemByIds(ids);
    }

    /**
     * 根据id查询商品
     *
     * @param id 商品id
     * @return 商品信息
     */
    @GetMapping("{id}")
    public CommodityDTO queryItemById(@PathVariable("id") Long id) {
        return BeanUtils.copyBean(commodityService.getCommodityById(id), CommodityDTO.class);
    }

    /**
     * 更新商品状态
     *
     * @param id     商品id
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
     *
     * @param item 商品信息
     */
    @PutMapping
    public void updateItem(@RequestBody CommodityDTO item) {
        // 不修改商品状态
        item.setStatus(null);
        // 更新
        commodityService.updateById(BeanUtils.copyBean(item, Commodity.class));
    }

    /**
     * 根据id删除商品
     *
     * @param id 商品id
     */
    @DeleteMapping("/{id}")
    public void deleteItemById(@PathVariable Long id) {
        commodityService.removeById(id);
    }

    /**
     * 批量扣减库存
     *
     * @param items 订单详情列表
     */
    @PutMapping("/stock/deduct")
    public R<Void> deductStock(@RequestBody List<OrderDetailDTO> items) {
        return commodityService.deductStock(items);
    }

    /**
     * 批量释放库存
     *
     * @param dtos 商品列表
     */
    @PutMapping("/release")
    public void releaseStock(@RequestBody List<OrderDetailDTO> dtos) {
        commodityService.releaseStock(dtos);
    }

    /**
     * 健康状态
     */
    @GetMapping("/health")
    public R<String> health() {
        return R.ok("ok");
    }
}