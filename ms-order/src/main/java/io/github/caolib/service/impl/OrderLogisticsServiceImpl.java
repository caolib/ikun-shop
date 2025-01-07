package io.github.caolib.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.caolib.domain.po.OrderLogistics;
import io.github.caolib.mapper.OrderLogisticsMapper;
import io.github.caolib.service.IOrderLogisticsService;
import org.springframework.stereotype.Service;

@Service
public class OrderLogisticsServiceImpl extends ServiceImpl<OrderLogisticsMapper, OrderLogistics> implements IOrderLogisticsService {

}
