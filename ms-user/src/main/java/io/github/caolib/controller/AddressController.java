package io.github.caolib.controller;

import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.AddressDTO;
import io.github.caolib.domain.po.Address;
import io.github.caolib.exception.BadRequestException;
import io.github.caolib.service.IAddressService;
import io.github.caolib.utils.BeanUtils;
import io.github.caolib.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址管理接口
 */
@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final IAddressService addressService;

    /**
     * 根据id查询地址
     *
     * @param id 地址id
     * @return 地址信息
     */
    @GetMapping("/{addressId}")
    public AddressDTO findAddressById(@PathVariable("addressId") Long id) {
        // 1.根据id查询
        Address address = addressService.getById(id);
        // 2.判断当前用户
        Long userId = UserContext.getUserId();
        if (!address.getUserId().equals(userId)) {
            throw new BadRequestException("地址不属于当前登录用户");
        }
        return BeanUtils.copyBean(address, AddressDTO.class);
    }

    /**
     * 查询当前用户地址列表
     *
     * @return 地址列表
     */
    @GetMapping
    public List<AddressDTO> findMyAddresses() {
        return addressService.getUserAddresses();
    }


    /**
     * 添加地址
     * @param addressDTO 地址信息
     */
    @PostMapping
    public R<Void> addAddress(AddressDTO addressDTO) {
        // 1.校验参数
        if (addressDTO == null) {
            throw new BadRequestException("参数不能为空");
        }
        // 2.转换为po
        Address address = BeanUtils.copyBean(addressDTO, Address.class);
        // 3.设置用户id
        address.setUserId(UserContext.getUserId());
        // 4.保存
        addressService.save(address);
        return R.ok();
    }
}