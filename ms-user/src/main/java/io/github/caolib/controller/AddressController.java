package io.github.caolib.controller;

import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.AddressDTO;
import io.github.caolib.domain.po.Address;
import io.github.caolib.exception.BadRequestException;
import io.github.caolib.service.IAddressService;
import io.github.caolib.utils.BeanUtils;
import io.github.caolib.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址管理接口
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final IAddressService addressService;

    /**
     * 根据id查询地址
     *
     * @param addressId 地址id
     * @return 地址信息
     */
    @GetMapping("/{addressId}")
    public AddressDTO findAddressById(@PathVariable Long addressId) {
        // 1.根据id查询
        Address address = addressService.getById(addressId);
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
        return addressService.getAddresses(UserContext.getUserId());
    }


    /**
     * 添加地址
     *
     * @param addressDTO 地址信息
     */
    @PostMapping
    public R<Void> addAddress(@RequestBody AddressDTO addressDTO) {
        //log.debug("添加地址: {}", addressDTO);
        return addressService.addAddress(UserContext.getUserId(), addressDTO);
    }

    /**
     * 设置默认地址
     *
     * @param addressId 地址id
     */
    @PutMapping("/{addressId}")
    public R<Void> setDefaultAddress(@PathVariable Long addressId) {
        log.debug("设置默认地址: {}", addressId);
        addressService.setDefaultAddress(UserContext.getUserId(),addressId);
        return R.ok();
    }

    /**
     * 更新地址信息
     *
     * @param addressDTO 地址信息
     */
    @PutMapping
    public R<Void> updateAddress(@RequestBody AddressDTO addressDTO) {
        log.debug("更新地址信息: {}", addressDTO);

        addressService.updateAddress(UserContext.getUserId(), addressDTO);
        return R.ok();
    }

    /**
     * 删除地址
     *
     * @param addressId 地址id
     */
    @DeleteMapping("/{addressId}")
    public R<Void> deleteAddress(@PathVariable Long addressId) {
        log.debug("删除地址: {}", addressId);
        addressService.removeById(addressId);
        return R.ok();
    }


}