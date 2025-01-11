package io.github.caolib.controller;

import io.github.caolib.domain.dto.CartFormDTO;
import io.github.caolib.domain.po.Cart;
import io.github.caolib.domain.vo.CartVO;
import io.github.caolib.service.ICartService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 购物车相关接口
 */
@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {
    private final ICartService cartService;

    /**
     * 添加商品到购物车
     * @param cartFormDTO 购物车表单数据传输对象
     */
    @PostMapping
    public void addItem2Cart(@Valid @RequestBody CartFormDTO cartFormDTO) {
        cartService.addItem2Cart(cartFormDTO);
    }

    /**
     * 更新购物车数据
     * @param cart 购物车实体
     */
    @PutMapping
    public void updateCart(@RequestBody Cart cart) {
        cartService.updateById(cart);
    }

    /**
     * 删除购物车中商品
     * @param id 购物车条目id
     */
    @DeleteMapping("{id}")
    public void deleteCartItem(@Param("购物车条目id") @PathVariable("id") Long id) {
        cartService.removeById(id);
    }

    /**
     * 查询购物车列表
     * @return 购物车视图对象列表
     */
    @GetMapping
    public List<CartVO> queryMyCarts() {
        return cartService.queryMyCarts();
    }

    /**
     * 批量删除购物车中商品
     * @param ids 购物车条目id集合
     */
    @DeleteMapping
    public void deleteCartItemByIds(@RequestParam("ids") List<Long> ids) {
        cartService.removeByItemIds(ids);
    }
}