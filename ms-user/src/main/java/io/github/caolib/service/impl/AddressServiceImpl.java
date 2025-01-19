package io.github.caolib.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.caolib.domain.dto.AddressDTO;
import io.github.caolib.domain.po.Address;
import io.github.caolib.mapper.AddressMapper;
import io.github.caolib.service.IAddressService;
import io.github.caolib.utils.BeanUtils;
import io.github.caolib.utils.CollUtils;
import io.github.caolib.utils.UserContext;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements IAddressService {

    @Override
    public List<AddressDTO> getUserAddresses() {
        // 查询列表
        LambdaQueryWrapper<Address> eq = new LambdaQueryWrapper<Address>().eq(Address::getUserId, UserContext.getUserId());
        List<Address> list = list(eq);

        // 判空
        if (CollUtils.isEmpty(list)) return CollUtils.emptyList();

        return BeanUtils.copyList(list, AddressDTO.class);
    }
}
