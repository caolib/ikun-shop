package io.github.caolib.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.caolib.client.CommodityClient;
import io.github.caolib.config.CartProperties;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.CartFormDTO;
import io.github.caolib.domain.dto.CommodityDTO;
import io.github.caolib.domain.po.Cart;
import io.github.caolib.domain.vo.CartVO;
import io.github.caolib.exception.AlreadyExistException;
import io.github.caolib.exception.BizIllegalException;
import io.github.caolib.mapper.CartMapper;
import io.github.caolib.service.ICartService;
import io.github.caolib.utils.BeanUtils;
import io.github.caolib.utils.CollUtils;
import io.github.caolib.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements ICartService {
    private final CommodityClient commodityClient;
    private final CartProperties cartProperties;
    private final CartMapper cartMapper;

    /**
     * 添加商品到购物车
     *
     * @param cartFormDTO 购物车表单
     */
    public void addToCart(CartFormDTO cartFormDTO) {
        // 获取登录用户id
        Long userId = UserContext.getUserId();

        // 判断是否已经存在
        if (checkItemExists(cartFormDTO.getItemId(), userId)) {
            // 已存在存在
            throw new AlreadyExistException("购物车中已存在该商品", 400);
        }
        // 不存在，判断是否超过购物车数量
        checkCartsFull(userId);

        // 新增购物车条目
        Cart cart = BeanUtils.copyBean(cartFormDTO, Cart.class); // 转换为PO
        // 设置当前用户ID
        cart.setUserId(userId);
        // 保存到数据库
        save(cart);
    }

    /**
     * 查询用户购物车列表
     */
    @Override
    public List<CartVO> queryMyCarts() {
        // 查询用户购物车列表
        List<Cart> carts = lambdaQuery().eq(Cart::getUserId, UserContext.getUserId()).list();
        if (CollUtils.isEmpty(carts)) {
            return CollUtils.emptyList();
        }

        // 转换为VO
        List<CartVO> vos = BeanUtils.copyList(carts, CartVO.class);

        // 更新购物车中的商品信息为最新信息
        updateCartItems(vos);

        return vos;
    }

    /**
     * 更新购物车中的商品信息
     *
     * @param vos 购物车VO列表
     */
    private void updateCartItems(List<CartVO> vos) {
        // 1.获取商品id
        Set<Long> CommodityIds = vos.stream().map(CartVO::getItemId).collect(Collectors.toSet());

        // 2.查询商品
        List<CommodityDTO> commodityDTOS = commodityClient.queryCommodityByIds(CommodityIds);
        if (CollUtils.isEmpty(commodityDTOS)) return;// 无商品直接返回

        // 3.转为 id 到 item的map
        Map<Long, CommodityDTO> itemMap = commodityDTOS.stream().collect(Collectors.toMap(CommodityDTO::getId, Function.identity()));
        // 4.写入vo
        for (CartVO v : vos) {
            CommodityDTO item = itemMap.get(v.getItemId());
            if (item == null) continue;
            v.setNewPrice(item.getPrice());
            v.setStatus(item.getStatus());
            v.setStock(item.getStock());
        }
    }

    /**
     * 批量删除购物车中的商品
     *
     * @param ids 商品ID集合
     */
    @Override
    @Transactional
    public void removeByItemIds(Collection<Long> ids) {
        // 1.构建删除条件，userId和itemId
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(Cart::getUserId, UserContext.getUserId())
                .in(Cart::getItemId, ids);
        // 2.删除
        remove(queryWrapper);
    }

    /**
     * 更新购物车中商品数量
     *
     * @param id  购物车条目ID
     * @param num 数量
     */
    @Override
    public R<Void> updateCartItemNum(int id, int num) {
        cartMapper.updateItemNum(id, num);
        return R.ok();
    }

    /**
     * 检查购物车是否已满
     *
     * @param userId 用户ID
     */
    private void checkCartsFull(Long userId) {
        long count = lambdaQuery().eq(Cart::getUserId, userId).count();
        int max = cartProperties.getMaxCommodityNum(); // 购物车中商品数量上限
        if (count >= max) {
            throw new BizIllegalException(StrUtil.format("用户购物车课程不能超过{}", max));
        }
    }

    /**
     * 检查购物车中是否已经存在该商品
     *
     * @param itemId 商品ID
     * @param userId 用户ID
     */
    private boolean checkItemExists(Long itemId, Long userId) {
        long count = lambdaQuery()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getItemId, itemId)
                .count();
        return count > 0;
    }
}
