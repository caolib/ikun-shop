package io.github.caolib.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.caolib.domain.po.Address;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface AddressMapper extends BaseMapper<Address> {

}
