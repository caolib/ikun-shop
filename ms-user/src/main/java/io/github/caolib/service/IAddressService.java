package io.github.caolib.service;


import com.baomidou.mybatisplus.extension.service.IService;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.AddressDTO;
import io.github.caolib.domain.po.Address;

import java.util.List;


public interface IAddressService extends IService<Address> {

    List<AddressDTO> getUserAddresses();

    R<Void> addAddress(AddressDTO addressDTO);

    void updateAddress(AddressDTO addressDTO);

    void setDefaultAddress(Long addressId);
}
