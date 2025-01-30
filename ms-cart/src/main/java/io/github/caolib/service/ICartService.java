package io.github.caolib.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.CartFormDTO;
import io.github.caolib.domain.po.Cart;
import io.github.caolib.domain.vo.CartVO;

import java.util.Collection;
import java.util.List;



public interface ICartService extends IService<Cart> {

    void addToCart(Long userId, CartFormDTO cartFormDTO);

    List<CartVO> queryMyCarts(Long userId);

    void removeByItemIds(Collection<Long> itemIds);

    R<Void> updateCartItemNum(int id, int num, Long userId);

    R<Void> deleteByUserId(Long userId);
}
