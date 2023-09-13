package com.gk.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.reggie.entity.OrderDetail;
import com.gk.reggie.mapper.OrderDetailMapper;
import com.gk.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author 你是海蜇吗？
 * @version 1.0
 */
@Service
public class OrderDetailServiceImpl  extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
