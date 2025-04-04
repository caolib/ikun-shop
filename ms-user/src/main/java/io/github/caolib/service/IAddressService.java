package io.github.caolib.service;


import com.baomidou.mybatisplus.extension.service.IService;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.AddressDTO;
import io.github.caolib.domain.po.Address;

import java.util.List;


public interface IAddressService extends IService<Address> {

    List<AddressDTO> getAddresses(Long userId);

    R<Void> addAddress(Long userId,AddressDTO addressDTO);

    void updateAddress(Long userId,AddressDTO addressDTO);

    void setDefaultAddress(Long userId,Long addressId);

    R<Void> deleteAddress(Long userId, Long addressId);
}
