package io.github.caolib.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.caolib.config.AddrProperties;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.AddressDTO;
import io.github.caolib.domain.po.Address;
import io.github.caolib.enums.Code;
import io.github.caolib.exception.BadRequestException;
import io.github.caolib.mapper.AddressMapper;
import io.github.caolib.service.IAddressService;
import io.github.caolib.utils.BeanUtils;
import io.github.caolib.utils.CollUtils;
import io.github.caolib.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements IAddressService {

    private final AddrProperties addrProperties;

    @Override
    public List<AddressDTO> getUserAddresses() {
        // 查询列表
        LambdaQueryWrapper<Address> eq = new LambdaQueryWrapper<Address>().eq(Address::getUserId, UserContext.getUserId());
        List<Address> list = list(eq);

        // 判空
        if (CollUtils.isEmpty(list)) return CollUtils.emptyList();

        return BeanUtils.copyList(list, AddressDTO.class);
    }

    /**
     * 添加地址
     *
     * @param addressDTO 地址信息
     */
    @Override
    public R<Void> addAddress(AddressDTO addressDTO) {
        // 校验参数
        if (addressDTO == null) throw new BadRequestException(Code.PARAME_ERROR);

        // 查询用户地址个数
        Long userId = UserContext.getUserId();
        long count = count(new LambdaQueryWrapper<Address>().eq(Address::getUserId, userId));
        // 如果地址数量已达上限，抛出异常
        if (count >= addrProperties.getMaxCount()) {
            throw new BadRequestException(Code.ADDR_ALREADY_MAX);
        }
        // 转换为po
        Address address = BeanUtils.copyBean(addressDTO, Address.class);
        // 如果是第一个地址，设置为默认地址
        if (count == 0) address.setIsDefault(1);
        // 设置用户id
        address.setUserId(userId);
        // 保存
        save(address);

        return R.ok();
    }

    @Override
    public void updateAddress(AddressDTO addressDTO) {
        Long userId = UserContext.getUserId();
        Address address = BeanUtils.copyBean(addressDTO, Address.class);
        // 设置用户id
        address.setUserId(userId);

        updateById(address);
    }

    @Override
    public void setDefaultAddress(Long addressId) {
        Long userId = UserContext.getUserId();
        // 查询地址
        Address address = getById(addressId);
        // 判断是否是当前用户的地址
        if (!address.getUserId().equals(userId))
            throw new BadRequestException(Code.ADDR_NOT_BELONG_USER);

        // 查询用户的所有地址
        List<Address> list = list(new LambdaQueryWrapper<Address>().eq(Address::getUserId, userId));
        // 设置默认地址
        list.forEach(a -> a.setIsDefault(a.getId().equals(addressId) ? 1 : 0));
        // 更新
        updateBatchById(list);
    }
}
