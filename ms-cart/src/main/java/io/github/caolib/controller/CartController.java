package io.github.caolib.controller;

import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.CartFormDTO;
import io.github.caolib.domain.po.Cart;
import io.github.caolib.domain.vo.CartVO;
import io.github.caolib.service.ICartService;
import io.github.caolib.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 购物车相关接口
 */

@Slf4j
@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {
    private final ICartService cartService;

    /**
     * 查询购物车列表
     *
     * @return 购物车视图对象列表
     */
    @GetMapping
    public List<CartVO> queryMyCarts() {
        return cartService.queryMyCarts(UserContext.getUserId());
    }

    /**
     * 添加商品到购物车
     *
     * @param cartFormDTO 购物车表单数据传输对象
     */
    @PostMapping
    public void addItem2Cart(@Valid @RequestBody CartFormDTO cartFormDTO) {
        cartService.addToCart(UserContext.getUserId(),cartFormDTO);
    }

    /**
     * 更新购物车数据
     *
     * @param cart 购物车实体
     */
    @PutMapping
    public void updateCart(@RequestBody Cart cart) {
        cartService.updateById(cart);
    }

    /**
     * 更新购物车中商品数量
     * @param id 购物车条目id
     * @param num 商品数量
     */
    @PutMapping("/{id}/{num}")
    public R<Void> updateCartItemNum(@PathVariable int id, @PathVariable int num) {
        return cartService.updateCartItemNum(id, num, UserContext.getUserId());
    }


    /**
     * 删除购物车中商品
     *
     * @param id 购物车条目id
     */
    @DeleteMapping("/{id}")
    public void deleteCartItem(@PathVariable("id") Long id) {
        cartService.removeById(id);
    }

    /**
     * 批量删除购物车中商品
     *
     * @param ids 购物车条目id集合
     */
    @DeleteMapping
    public void deleteCartItemByIds(@RequestParam("ids") List<Long> ids) {
        cartService.removeByItemIds(ids);
    }

    /**
     * 批量删除购物车中商品II
     *
     * @param ids 购物车条目id集合
     */
    @DeleteMapping("/batch")
    public void deleteBatchCartItem(@RequestBody List<Long> ids) {
        cartService.removeBatchByIds(ids);
    }

    @DeleteMapping("/cancel/{userId}")
    public R<Void> deleteCartByUserId(@PathVariable Long userId) {
        //log.debug("删除用户 {} 的购物车信息", userId);
       return cartService.deleteByUserId(userId);
    }


}